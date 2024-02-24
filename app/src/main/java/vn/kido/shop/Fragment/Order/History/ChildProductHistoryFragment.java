package vn.kido.shop.Fragment.Order.History;


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

import java.util.LinkedHashMap;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import vn.kido.shop.Adapter.ProductAdapter;
import vn.kido.shop.Api.APIService;
import vn.kido.shop.Api.ISubscriber;
import vn.kido.shop.Bean.BeanProduct;
import vn.kido.shop.Class.StoreCart;
import vn.kido.shop.Fragment.BaseFragment;
import vn.kido.shop.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChildProductHistoryFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    SwipeRefreshLayout refresher;
    public RecyclerView recycler;
    RecyclerView.OnScrollListener onScrollListener;
    LinearLayoutManager layoutManager;
    ProductAdapter adapter;
    public LinkedHashMap map;
    Subscription subscriptionHistories;
    Gson gson;
    int page = 1;
    TextView txtNoOrder;

    public ChildProductHistoryFragment() {
        // Required empty public constructor
    }

    public static ChildProductHistoryFragment newInstance() {
        Bundle args = new Bundle();
        ChildProductHistoryFragment fragment = new ChildProductHistoryFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView != null) {
            return rootView;
        }
        rootView = inflater.inflate(R.layout.fragment_child_product_history, container, false);
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
                refresher = rootView.findViewById(R.id.refresher);
                recycler = rootView.findViewById(R.id.recycler);
                txtNoOrder = rootView.findViewById(R.id.txt_no_order);
                layoutManager = new LinearLayoutManager(getContext());
                map = new LinkedHashMap();
                adapter = new ProductAdapter(ChildProductHistoryFragment.this, recycler, map);
                gson = new Gson();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        refresher.setOnRefreshListener(ChildProductHistoryFragment.this);
                        recycler.setLayoutManager(layoutManager);
                        recycler.setAdapter(adapter);
                        getHistories();
                        onScrollListener = new RecyclerView.OnScrollListener() {
                            @Override
                            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                                if (layoutManager.findLastCompletelyVisibleItemPosition() == map.size() - 1 && map.size() >= 10) {
                                    if (subscriptionHistories == null) {
                                        return;
                                    }
                                    if (!subscriptionHistories.isUnsubscribed()) {
                                        return;
                                    }
                                    page++;
                                    getHistories();

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
    public void manuResume() {
        super.manuResume();
        updateStatusProduct(map, adapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (subscriptionHistories != null) {
            subscriptionHistories.unsubscribe();
        }
    }


    private void getHistories() {
        showProgress();
        subscriptionHistories = APIService.getInstance().getOrderHistories(1, ((HistoryOrderFragment) getParentFragment()).SORT, page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<String, JSONObject>() {
                    @Override
                    public JSONObject call(String s) {
                        try {
                            return new JSONObject(s);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                }).subscribe(new ISubscriber() {
                    @Override
                    public void done() {
                        super.done();
                        hideProgress();
                    }

                    @Override
                    public void excute(JSONObject jsonObject) {
                        try {
                            JSONObject data = jsonObject.getJSONObject("data");
                            JSONArray arr = data.getJSONArray("orders");
                            for (int i = 0; i < arr.length(); i++) {
                                BeanProduct beanProduct = gson.fromJson(arr.getString(i), BeanProduct.class);
                                for (int id : StoreCart.getProducts()) {
                                    if (beanProduct.getId() == id) {
                                        beanProduct.setInCart(true);
                                    }
                                }
                                map.put(beanProduct.getId(), beanProduct);
                            }
                            if (arr.length() < 10) {
                                recycler.removeOnScrollListener(onScrollListener);
                            }
                            if (map.size() == 0) {
                                txtNoOrder.setVisibility(View.VISIBLE);
                            } else {
                                txtNoOrder.setVisibility(View.GONE);
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
            if (!subscriptionHistories.isUnsubscribed()) {
                subscriptionHistories.unsubscribe();
            }
            recycler.removeOnScrollListener(onScrollListener);
            recycler.addOnScrollListener(onScrollListener);
            map.clear();
            page = 1;
            adapter.notifyDataSetChanged();
            refresher.setRefreshing(false);
            getHistories();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
