package vn.kido.shop.Fragment.Order.Change;


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
import vn.kido.shop.Adapter.OrderAdapter;
import vn.kido.shop.Api.APIService;
import vn.kido.shop.Api.ISubscriber;
import vn.kido.shop.Bean.BeanOrder;
import vn.kido.shop.Fragment.BaseFragment;
import vn.kido.shop.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReturnOrderFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {
    RecyclerView recycler;
    SwipeRefreshLayout refresher;
    public List<BeanOrder> orders;
    LinearLayoutManager layoutManager;
    Subscription subscription;
    OrderAdapter orderAdapter;
    TextView txtNoOrder;

    public ReturnOrderFragment() {
        // Required empty public constructor
    }

    public static ReturnOrderFragment newInstance() {
        Bundle args = new Bundle();
        ReturnOrderFragment fragment = new ReturnOrderFragment();
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
        rootView = inflater.inflate(R.layout.fragment_return_order, container, false);
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
                final TextView title = rootView.findViewById(R.id.title);
                recycler = rootView.findViewById(R.id.recycler);
                refresher = rootView.findViewById(R.id.refresher);
                txtNoOrder = rootView.findViewById(R.id.txt_no_order);
                refresher.setOnRefreshListener(ReturnOrderFragment.this);
                layoutManager = new LinearLayoutManager(getContext());
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        title.setText(getString(R.string.pay_full_order));
                        recycler.setLayoutManager(layoutManager);
                        getListOrderReturn();
                    }
                });
            }
        });
        threadInit.start();
        isLoaded = true;
    }

    private void getListOrderReturn() {
        showProgress();
        subscription = APIService.getInstance().getListOrderReturn()
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
                            JSONObject data = jsonObject.getJSONObject("data");
                            JSONArray arr = data.getJSONArray("orders");
                            if (arr.length() == 0) {
                                txtNoOrder.setVisibility(View.VISIBLE);
                            } else {
                                txtNoOrder.setVisibility(View.GONE);
                            }
                            Gson gson = new Gson();
                            orders = new ArrayList<>();
                            for (int i = 0; i < arr.length(); i++) {
                                BeanOrder beanOrder = gson.fromJson(arr.getString(i), BeanOrder.class);
                                orders.add(beanOrder);
                            }
                            orderAdapter = new OrderAdapter(ReturnOrderFragment.this, recycler, orders);
                            recycler.setAdapter(orderAdapter);
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
            orders.clear();
            orderAdapter.notifyDataSetChanged();
            refresher.setRefreshing(false);
            getListOrderReturn();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
