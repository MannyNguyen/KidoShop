
package vn.kido.shop.Fragment.Search;


import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.gson.Gson;

import vn.kido.shop.Adapter.NewsAdapter;
import vn.kido.shop.Adapter.ProductAdapter;
import vn.kido.shop.Adapter.ProgramAdapter;
import vn.kido.shop.Adapter.StoreAdapter;
import vn.kido.shop.Bean.BeanNews;
import vn.kido.shop.Bean.BeanProduct;
import vn.kido.shop.Bean.BeanProgram;
import vn.kido.shop.Bean.BeanStore;
import vn.kido.shop.Class.StoreCart;
import vn.kido.shop.Fragment.BaseFragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import vn.kido.shop.Api.APIService;
import vn.kido.shop.Api.ISubscriber;
import vn.kido.shop.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChildSearchFragment extends BaseFragment {
    Subscription subscriptionSearch;
    int page = 1;
    RecyclerView.OnScrollListener onScrollListener;
    Gson gson;
    LinearLayoutManager layoutManager;
    RecyclerView recyclerView;
    LinkedHashMap map;
    List items;
    RecyclerView.Adapter adapter;
    LinearLayout llNoData;

    public ChildSearchFragment() {
        // Required empty public constructor
    }

    public static ChildSearchFragment newInstance(int type) {
        Bundle args = new Bundle();
        args.putInt("type", type);
        ChildSearchFragment fragment = new ChildSearchFragment();
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
        rootView = inflater.inflate(R.layout.fragment_child_search, container, false);
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
                recyclerView = rootView.findViewById(R.id.recycler);
                llNoData = rootView.findViewById(R.id.ll_no_data);
                layoutManager = new LinearLayoutManager(getContext());
                gson = new Gson();

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView.setLayoutManager(layoutManager);
                        switch (getArguments().getInt("type")) {
                            case HomeSearchFragment.PROGRAM:
                                items = new ArrayList();
                                adapter = new ProgramAdapter(ChildSearchFragment.this, items, recyclerView);
                                recyclerView.setAdapter(adapter);
                                break;
                            case HomeSearchFragment.PRODUCT:
                                map = new LinkedHashMap();
                                adapter = new ProductAdapter(ChildSearchFragment.this, recyclerView, map);
                                recyclerView.setAdapter(adapter);
                                break;
                            case HomeSearchFragment.NEWS:
                                items = new ArrayList();
                                adapter = new NewsAdapter(ChildSearchFragment.this, recyclerView, items, 2);
                                recyclerView.setAdapter(adapter);
                                break;
                            case HomeSearchFragment.STORE_NAME:
                                layoutManager = new GridLayoutManager(getContext(), 3);
                                recyclerView.setLayoutManager(layoutManager);
                                items = new ArrayList();
                                adapter = new StoreAdapter(ChildSearchFragment.this, recyclerView, items);
                                recyclerView.setAdapter(adapter);
                                break;
                        }
                        onScrollListener = new RecyclerView.OnScrollListener() {
                            @Override
                            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                                super.onScrolled(recyclerView, dx, dy);
                                int size = map == null ? items.size() : map.size();
                                if (layoutManager.findLastCompletelyVisibleItemPosition() == size - 1 && size >= 10) {
                                    if (subscriptionSearch == null) {
                                        return;
                                    }
                                    if (!subscriptionSearch.isUnsubscribed()) {
                                        return;
                                    }
                                    page++;
                                    search();
                                }
                            }
                        };
                        recyclerView.addOnScrollListener(onScrollListener);
//                        new Handler().postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                search();
//                            }
//                        }, 500);
                        search();

                    }

                });
            }
        });
        threadInit.start();
        isLoaded = true;
    }

    public void onDestroy() {
        super.onDestroy();
        if (subscriptionSearch != null) {
            subscriptionSearch.unsubscribe();
        }
    }

    public void refresh() {
        page = 1;
        if (items != null) {
            items.clear();
        }
        if (map != null) {
            map.clear();
        }
        adapter.notifyDataSetChanged();
        search();
    }

    public void search() {
        showProgress();
        final int type = getArguments().getInt("type");
        String keyWord = ((HomeSearchFragment) getParentFragment()).edtKeySearch.getText().toString();
        subscriptionSearch = APIService.getInstance().search(type, keyWord, page)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io())
                .subscribe(new ISubscriber() {
                    @Override
                    public void done() {
                        super.done();
                        hideProgress();
                    }

                    @Override
                    public void excute(final JSONObject jsonObject) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    final JSONArray data = jsonObject.getJSONArray("data");
                                    if (page == 1 && data.length() == 0) {
                                        llNoData.setVisibility(View.VISIBLE);
                                    } else {
                                        llNoData.setVisibility(View.GONE);
                                    }
                                    switch (type) {
                                        case HomeSearchFragment.PROGRAM:
                                            for (int i = 0; i < data.length(); i++) {
                                                BeanProgram program = gson.fromJson(data.getString(i), BeanProgram.class);
                                                items.add(program);
                                            }
                                            break;
                                        case HomeSearchFragment.PRODUCT:
                                            for (int i = 0; i < data.length(); i++) {
                                                BeanProduct beanProduct = gson.fromJson(data.getString(i), BeanProduct.class);
                                                map.put(beanProduct.getId(), beanProduct);
                                                for (Object product : map.values().toArray()) {
                                                    ((BeanProduct) product).setInCart(false);
                                                    if (StoreCart.getProducts().contains(((BeanProduct) product).getId())) {
                                                        ((BeanProduct) product).setInCart(true);
                                                    }
                                                }

                                            }

                                            break;
                                        case HomeSearchFragment.NEWS:
                                            for (int i = 0; i < data.length(); i++) {
                                                BeanNews beanNews = new Gson().fromJson(data.getString(i), BeanNews.class);
                                                items.add(beanNews);
                                            }
                                            break;
                                        case HomeSearchFragment.STORE_NAME:
                                            for (int i = 0; i < data.length(); i++) {
                                                BeanStore store = gson.fromJson(data.getString(i), BeanStore.class);
                                                items.add(store);
                                            }
                                            break;
                                    }
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (data.length() < 10) {
                                                recyclerView.removeOnScrollListener(onScrollListener);
                                            }
                                            adapter.notifyDataSetChanged();
                                        }
                                    });

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }
                });
    }

}
