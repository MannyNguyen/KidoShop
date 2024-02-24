package vn.kido.shop.Fragment.Category;


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

import java.util.LinkedHashMap;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
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
public class ChildProductFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    SwipeRefreshLayout refresher;
    public RecyclerView recycler;
    RecyclerView.OnScrollListener onScrollListener;
    LinearLayoutManager layoutManager;
    ProductAdapter adapter;
    public LinkedHashMap map;
    Subscription subscription;
    Gson gson;
    int page = 1;

    public ChildProductFragment() {
        // Required empty public constructor
    }

    public static ChildProductFragment newInstance(int id) {

        Bundle args = new Bundle();
        args.putInt("id", id);
        ChildProductFragment fragment = new ChildProductFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView != null) {
            return rootView;
        }
        rootView = inflater.inflate(R.layout.fragment_child_product, container, false);
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
                layoutManager = new LinearLayoutManager(getContext());
                map = new LinkedHashMap();
                adapter = new ProductAdapter(ChildProductFragment.this, recycler, map);
                gson = new Gson();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        refresher.setOnRefreshListener(ChildProductFragment.this);
                        recycler.setLayoutManager(layoutManager);
                        recycler.setAdapter(adapter);
                        getProducts();
                        onScrollListener = new RecyclerView.OnScrollListener() {
                            @Override
                            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                                if (layoutManager.findLastCompletelyVisibleItemPosition() == map.size() - 1 && map.size() >= 10) {
                                    if (subscription == null) {
                                        return;
                                    }
                                    if (!subscription.isUnsubscribed()) {
                                        return;
                                    }
                                    page++;
                                    getProducts();

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
        if (subscription != null) {
            subscription.unsubscribe();
        }
    }


    private void getProducts() {
        refresher.setRefreshing(true);
        subscription = APIService.getInstance().getProducts(getArguments().getInt("id"), page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
               .subscribe(new ISubscriber() {
                    @Override
                    public void done() {
                        super.done();
                        refresher.setRefreshing(false);
                    }

                    @Override
                    public void excute(JSONObject jsonObject) {
                        try {
                            JSONArray data = jsonObject.getJSONArray("data");
                            for (int i = 0; i < data.length(); i++) {
                                BeanProduct beanProduct = gson.fromJson(data.getString(i), BeanProduct.class);
                                for (int id : StoreCart.getProducts()) {
                                    if (beanProduct.getId() == id) {
                                        beanProduct.setInCart(true);
                                    }
                                }
                                map.put(beanProduct.getId(), beanProduct);
                            }
                            if (data.length() < 10) {
                                recycler.removeOnScrollListener(onScrollListener);
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
            recycler.removeOnScrollListener(onScrollListener);
            recycler.addOnScrollListener(onScrollListener);
            map.clear();
            page = 1;
            adapter.notifyDataSetChanged();
            refresher.setRefreshing(false);
            getProducts();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
