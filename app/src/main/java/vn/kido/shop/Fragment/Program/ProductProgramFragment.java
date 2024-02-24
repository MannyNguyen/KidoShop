package vn.kido.shop.Fragment.Program;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

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
public class ProductProgramFragment extends BaseFragment {
    Subscription subscriptionEvent;
    List<BeanProgram> programs;
    RecyclerView recyclerView;
    ProgramAdapter adapter;
    RecyclerView.OnScrollListener onScrollListener;
    LinearLayoutManager layoutManager;
    Gson gson;
    int page = 1;

    public ProductProgramFragment() {
        // Required empty public constructor
    }

    public static ProductProgramFragment newInstance(int id, String title) {
        Bundle args = new Bundle();
        args.putInt("id", id);
        args.putString("title", title);
        ProductProgramFragment fragment = new ProductProgramFragment();
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
        rootView = inflater.inflate(R.layout.fragment_product_program, container, false);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (isLoaded) {
            return;
        }
        TextView title = rootView.findViewById(R.id.title);
        title.setText(getArguments().getString("title"));
        threadInit = new Thread(new Runnable() {
            @Override
            public void run() {
                recyclerView = rootView.findViewById(R.id.recycler);
                layoutManager = new LinearLayoutManager(getContext());

                gson = new Gson();
                programs = new ArrayList<>();
                adapter = new ProgramAdapter(ProductProgramFragment.this, programs, recyclerView);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView.setLayoutManager(layoutManager);
                        recyclerView.setAdapter(adapter);
                        getEvent();
                        onScrollListener = new RecyclerView.OnScrollListener() {
                            @Override
                            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                                super.onScrolled(recyclerView, dx, dy);
                                if (layoutManager.findLastCompletelyVisibleItemPosition() == programs.size() - 1 && programs.size() >= 10) {
                                    if (subscriptionEvent == null) {
                                        return;
                                    }
                                    if (!subscriptionEvent.isUnsubscribed()) {
                                        return;
                                    }
                                    page++;
                                    getEvent();
                                }
                            }
                        };
                        recyclerView.addOnScrollListener(onScrollListener);
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
        if (subscriptionEvent != null) {
            subscriptionEvent.unsubscribe();
        }
    }

    private void getEvent() {
        showProgress();

        subscriptionEvent = APIService.getInstance().getEventRunning(getArguments().getInt("id"), page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ISubscriber() {
                    @Override
                    public void done() {
                        super.done();
                        hideProgress();
                    }

                    @Override
                    public void excute(JSONObject jsonObject) {
                        try {
                            JSONArray data = jsonObject.getJSONArray("data");
                            for (int i = 0; i < data.length(); i++) {
                                BeanProgram program = gson.fromJson(data.getString(i), BeanProgram.class);
                                programs.add(program);
                            }
                            if (data.length() < 10) {
                                recyclerView.removeOnScrollListener(onScrollListener);
                            }
                            adapter.notifyDataSetChanged();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

    }

}
