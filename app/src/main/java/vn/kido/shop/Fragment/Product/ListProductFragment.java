package vn.kido.shop.Fragment.Product;


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

import rx.Observable;
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
public class ListProductFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {
    public static final String POPULAR = "POPULAR";
    public static final String NEW = "NEW";
    RecyclerView recycler;
    int page = 1;
    LinkedHashMap map;
    ProductAdapter adapter;
    SwipeRefreshLayout refresh;
    LinearLayoutManager layoutManager;
    RecyclerView.OnScrollListener onScrollListener;
    Subscription subscription;

    public ListProductFragment() {
        // Required empty public constructor
    }

    public static ListProductFragment newInstance(String type) {
        Bundle args = new Bundle();
        args.putString("type", type);
        ListProductFragment fragment = new ListProductFragment();
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
        rootView = inflater.inflate(R.layout.fragment_list_product, container, false);
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
                recycler = rootView.findViewById(R.id.recycler);
                refresh = rootView.findViewById(R.id.refresher);
                final TextView title = rootView.findViewById(R.id.title);
                layoutManager = new LinearLayoutManager(getContext());
                map = new LinkedHashMap();
                adapter = new ProductAdapter(ListProductFragment.this, recycler, map);
                refresh.setOnRefreshListener(ListProductFragment.this);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        recycler.setLayoutManager(layoutManager);
                        recycler.setAdapter(adapter);
                        if (getArguments().getString("type").equals(POPULAR)) {
                            title.setText(getString(R.string.highlight_product));
                        } else if (getArguments().getString("type").equals(NEW)) {
                            title.setText(getString(R.string.new_product));
                        }
                        getProduct();
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
                                    getProduct();
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


    private void getProduct() {
        showProgress();
        Observable<String> request = null;
        if (getArguments().getString("type").equals(POPULAR)) {
            request = APIService.getInstance().getHighlightProduct(page);
        } else if (getArguments().getString("type").equals(NEW)) {
            request = APIService.getInstance().getNewProduct(page);
        }

        if (request == null) {
            return;
        }

        subscription = request
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
                                BeanProduct beanProduct = new Gson().fromJson(data.getString(i), BeanProduct.class);
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
            refresh.setRefreshing(false);
            getProduct();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
