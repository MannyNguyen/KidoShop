package vn.kido.shop.Fragment.News;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import vn.kido.shop.Adapter.NewsAdapter;
import vn.kido.shop.Api.APIService;
import vn.kido.shop.Api.ISubscriber;
import vn.kido.shop.Bean.BeanNews;
import vn.kido.shop.Fragment.BaseFragment;
import vn.kido.shop.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChildNewsFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {
    RecyclerView recycler;
    SwipeRefreshLayout refresher;
    List<BeanNews> map;
    NewsAdapter newsAdapter;
    RecyclerView.OnScrollListener onScrollListener;
    Subscription subNews;

    public ChildNewsFragment() {
    }

    public static ChildNewsFragment newInstance(int id) {
        Bundle args = new Bundle();
        args.putInt("news_id", id);
        ChildNewsFragment fragment = new ChildNewsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView != null) {
            return rootView;
        }
        rootView = inflater.inflate(R.layout.fragment_child_news, container, false);
        return rootView;
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
                recycler = rootView.findViewById(R.id.recyclernews);
                refresher = rootView.findViewById(R.id.refresher);
                refresher.setOnRefreshListener(ChildNewsFragment.this);
                final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        recycler.setLayoutManager(layoutManager);

                        onScrollListener = new RecyclerView.OnScrollListener() {
                            @Override
                            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                                if (layoutManager.findLastCompletelyVisibleItemPosition() == map.size() - 1 && map.size() >= 10) {
                                    if (subNews == null) {
                                        return;
                                    }
                                    if (!subNews.isUnsubscribed()) {
                                        return;
                                    }
                                    getNews(getArguments().getInt("news_id"));

                                }
                            }
                        };
                        recycler.addOnScrollListener(onScrollListener);

                        getNews(getArguments().getInt("news_id"));
                    }
                });
            }
        });
        threadInit.start();
        isLoaded = true;

    }

    public void getNews(final int typeId) {
        //Check type of tab and clear list before request API
        if (typeId == 3) {
            ((HomeNewsFragment) getParentFragment()).listId.clear();
        }
        subNews = APIService.getInstance().getNews(typeId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ISubscriber() {
                    @Override
                    public void excute(JSONObject jsonObject) {
                        try {
                            JSONArray data = jsonObject.getJSONArray("data");
                            map = new ArrayList<>();

                            for (int i = 0; i < data.length(); i++) {
                                BeanNews beanNews = new Gson().fromJson(data.getString(i), BeanNews.class);
                                map.add(beanNews);
                                if (typeId == 3) {
                                    ((HomeNewsFragment) getParentFragment()).listId.add(beanNews.getId());
                                }
                            }
                            newsAdapter = new NewsAdapter(ChildNewsFragment.this, recycler, map, typeId);
                            recycler.setAdapter(newsAdapter);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    @Override
    public void onRefresh() {
        try {
            if (!subNews.isUnsubscribed()) {
                subNews.unsubscribe();
            }
            recycler.removeOnScrollListener(onScrollListener);
            recycler.addOnScrollListener(onScrollListener);
            map.clear();
            newsAdapter.notifyDataSetChanged();
            refresher.setRefreshing(false);
            getNews(getArguments().getInt("news_id"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
