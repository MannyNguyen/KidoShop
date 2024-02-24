package vn.kido.shop.Fragment.Notify;


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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import vn.kido.shop.Adapter.NotifyAdapter;
import vn.kido.shop.Api.APIService;
import vn.kido.shop.Api.ISubscriber;
import vn.kido.shop.Bean.BeanNotify;
import vn.kido.shop.Fragment.BaseFragment;
import vn.kido.shop.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeNotifyFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {
    RecyclerView recyclerView;
    SwipeRefreshLayout refresher;
    RecyclerView.OnScrollListener onScrollListener;
    LinearLayoutManager layoutManager;
    public NotifyAdapter adapter;
    public List<BeanNotify> notifies;
    Subscription subscription;
    Gson gson;
    int page = 1;

    public HomeNotifyFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView != null) {
            return rootView;
        }
        rootView = inflater.inflate(R.layout.fragment_home_notify, container, false);
        return rootView;
    }

    public static HomeNotifyFragment newInstance() {
        Bundle args = new Bundle();
        HomeNotifyFragment fragment = new HomeNotifyFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (isLoaded) {
            return;
        }
        threadInit = new Thread(new Runnable() {
            @Override
            public void run() {
                recyclerView = rootView.findViewById(R.id.recycler);
                refresher = rootView.findViewById(R.id.refresher);
                layoutManager = new LinearLayoutManager(getContext());
                final TextView title = rootView.findViewById(R.id.title);
                gson = new Gson();
                notifies = new ArrayList<>();
                adapter = new NotifyAdapter(HomeNotifyFragment.this, recyclerView, notifies);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        title.setText(getString(R.string.notify));
                        refresher.setOnRefreshListener(HomeNotifyFragment.this);
                        recyclerView.setLayoutManager(layoutManager);
                        recyclerView.setAdapter(adapter);
                        getNotify();
                        onScrollListener = new RecyclerView.OnScrollListener() {
                            @Override
                            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                                super.onScrolled(recyclerView, dx, dy);
                                if (layoutManager.findLastCompletelyVisibleItemPosition() == notifies.size() - 1 && notifies.size() >= 10) {
                                    if (subscription == null) {
                                        return;
                                    }
                                    if (!subscription.isUnsubscribed()) {
                                        return;
                                    }
                                    page++;
                                    getNotify();
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
        if (subscription != null) {
            subscription.unsubscribe();
        }
    }

    private void getNotify() {
        showProgress();
        subscription = APIService.getInstance().getNotifies(page)
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
                                BeanNotify notify = gson.fromJson(data.getString(i), BeanNotify.class);
                                notifies.add(notify);
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


    @Override
    public void onRefresh() {
        try {
            if (!subscription.isUnsubscribed()) {
                subscription.unsubscribe();
            }
            recyclerView.removeOnScrollListener(onScrollListener);
            recyclerView.addOnScrollListener(onScrollListener);
            notifies.clear();
            page = 1;
            adapter.notifyDataSetChanged();
            refresher.setRefreshing(false);
            getNotify();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
