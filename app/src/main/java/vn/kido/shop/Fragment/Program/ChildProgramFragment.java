package vn.kido.shop.Fragment.Program;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import vn.kido.shop.Adapter.ProgramAdapter;
import vn.kido.shop.Api.APIService;
import vn.kido.shop.Api.ISubscriber;
import vn.kido.shop.Bean.BeanProgram;
import vn.kido.shop.Fragment.BaseFragment;
import vn.kido.shop.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChildProgramFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {
    SwipeRefreshLayout refresher;
    RecyclerView recycler;
    List<BeanProgram> programs;
    ProgramAdapter programAdapter;
    Subscription subscription;
    TextView txtNoOrder;
    int page = 1;
    Gson gson;
    RecyclerView.OnScrollListener onScrollListener;
    LinearLayoutManager layoutManager;

    public ChildProgramFragment() {
        // Required empty public constructor
    }

    public static ChildProgramFragment newInstance(int companyId) {
        Bundle args = new Bundle();
        args.putInt("company_id", companyId);
        ChildProgramFragment fragment = new ChildProgramFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (rootView != null) {
            return rootView;
        }
        rootView = inflater.inflate(R.layout.fragment_child_program, container, false);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (isLoaded) {
            return;
        }
        refresher = rootView.findViewById(R.id.refresher);
        recycler = rootView.findViewById(R.id.recycler);
        txtNoOrder = rootView.findViewById(R.id.txt_no_order);
        programs = new ArrayList<>();
        gson = new Gson();
        programAdapter = new ProgramAdapter(ChildProgramFragment.this, programs, recycler);
        threadInit = new Thread(new Runnable() {
            @Override
            public void run() {
                layoutManager = new LinearLayoutManager(getContext());
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        recycler.setLayoutManager(layoutManager);
                        recycler.setAdapter(programAdapter);
                        getEvents(getArguments().getInt("company_id"));
                        refresher.setOnRefreshListener(ChildProgramFragment.this);
                        onScrollListener = new RecyclerView.OnScrollListener() {
                            @Override
                            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                                super.onScrolled(recyclerView, dx, dy);
                                if (layoutManager.findLastCompletelyVisibleItemPosition() == programs.size() - 1 && programs.size() >= 10) {
                                    if (subscription == null) {
                                        return;
                                    }
                                    if (!subscription.isUnsubscribed()) {
                                        return;
                                    }
                                    page++;
                                    getEvents(getArguments().getInt("company_id"));
                                }
                            }
                        };
                        recycler.addOnScrollListener(onScrollListener);
                    }
                });
            }
        });
        threadInit.start();
        isLoaded = true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (subscription != null) {
            subscription.unsubscribe();
        }
    }

    private void getEvents(int companyId) {
        subscription = APIService.getInstance().getEvent(companyId, page)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new ISubscriber() {
                    @Override
                    public void excute(JSONObject jsonObject) {
                        try {
                            JSONArray data = jsonObject.getJSONArray("data");
//                            Type listType = new TypeToken<List<BeanProgram>>() {
//                            }.getType();
//                            programs = gson.fromJson(data.toString(), listType);

                            for (int i = 0; i < data.length(); i++) {
                                BeanProgram program = gson.fromJson(data.getString(i), BeanProgram.class);
                                programs.add(program);
                            }

                            if (programs.size() == 0) {
                                txtNoOrder.setVisibility(View.VISIBLE);
                            } else {
                                txtNoOrder.setVisibility(View.GONE);
                            }
                            if (data.length() < 10) {
                                recycler.removeOnScrollListener(onScrollListener);
                            }
                            programAdapter.notifyDataSetChanged();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    @Override
    public void onRefresh() {
        try {
            if (!subscription.isUnsubscribed()) {
                subscription.unsubscribe();
            }
            programs.clear();
            page = 1;
            programAdapter.notifyDataSetChanged();
            refresher.setRefreshing(false);
            getEvents(getArguments().getInt("company_id"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
