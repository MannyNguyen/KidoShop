package vn.kido.shop.Fragment.Cart;


import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
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

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import vn.kido.shop.Adapter.CartProductAdapter;
import vn.kido.shop.Adapter.GiftAdapter;
import vn.kido.shop.Api.APIService;
import vn.kido.shop.Api.ISubscriber;
import vn.kido.shop.Bean.BeanProduct;
import vn.kido.shop.Class.CmmFunc;
import vn.kido.shop.Fragment.BaseFragment;
import vn.kido.shop.Helper.FragmentHelper;
import vn.kido.shop.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class CartOldFragment extends BaseFragment implements View.OnClickListener {
    NestedScrollView scrollView;
    public RecyclerView recycler, recyclerGift;
    CartProductAdapter adapter;
    List<BeanProduct> products;
    public TextView totalPoint, totalMoney, discount, realPay;
    View submit;
    public View frameGifts;

    public CartOldFragment() {
        // Required empty public constructor
    }

    public static CartOldFragment newInstance() {
        Bundle args = new Bundle();
        CartOldFragment fragment = new CartOldFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static CartOldFragment newInstance(int idProduct) {
        Bundle args = new Bundle();
        args.putInt("id_product", idProduct);
        CartOldFragment fragment = new CartOldFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView != null) {
            return rootView;
        }
        rootView = inflater.inflate(R.layout.fragment_cart_old, container, false);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        View cart = rootView.findViewById(R.id.cart);
        cart.setEnabled(false);
        if (isLoaded) {
            return;
        }
        TextView title = rootView.findViewById(R.id.title);
        title.setText(getString(R.string.cart));
        threadInit = new Thread(new Runnable() {
            @Override
            public void run() {
                scrollView = rootView.findViewById(R.id.scroll_view);
                products = new ArrayList<>();
                recycler = rootView.findViewById(R.id.recycler);
                recyclerGift = rootView.findViewById(R.id.recycler_gift);
                totalPoint = rootView.findViewById(R.id.total_point);
                totalMoney = rootView.findViewById(R.id.total_money);
                discount = rootView.findViewById(R.id.discount);
                realPay = rootView.findViewById(R.id.real_pay);
                frameGifts = rootView.findViewById(R.id.frame_gifts);
                submit = rootView.findViewById(R.id.submit);

                adapter = new CartProductAdapter(CartOldFragment.this, recycler, products);
                submit.setOnClickListener(CartOldFragment.this);

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
                        recycler.setAdapter(adapter);
                        getCart();
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
        getCart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void getCart() {
        showProgress();
        products.clear();
        APIService.getInstance().getCart()
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
                            Gson gson = new Gson();
                            JSONObject data = jsonObject.getJSONObject("data");
                            JSONObject cartInfo = data.getJSONObject("cart_info");
                            JSONArray items = cartInfo.getJSONArray("items");
                            for (int i = 0; i < items.length(); i++) {
                                BeanProduct beanProduct = gson.fromJson(items.getString(i), BeanProduct.class);
                                products.add(beanProduct);
                            }
                            adapter.notifyDataSetChanged();

                            totalMoney.setText(CmmFunc.formatMoney(cartInfo.getString("total_money"), false));

                            totalPoint.setText(CmmFunc.formatKPoint(cartInfo.getString("total_point")));
                            discount.setText(" - " + CmmFunc.formatMoney(cartInfo.getString("discount"), false));
                            realPay.setText(CmmFunc.formatMoney(cartInfo.getString("total"), false));

                            Type listGift = new TypeToken<List<BeanProduct>>() {
                            }.getType();
                            List gifts = new Gson().fromJson(cartInfo.getString("gifts"), listGift);
                            if (gifts.size() == 0) {
                                frameGifts.setVisibility(View.GONE);
                            } else {
                                frameGifts.setVisibility(View.VISIBLE);
                                GiftAdapter giftAdapter = new GiftAdapter(CartOldFragment.this, recyclerGift, gifts);
                                recyclerGift.setLayoutManager(new LinearLayoutManager(getContext()));
                                recyclerGift.setAdapter(giftAdapter);
                            }

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        if(getArguments().containsKey("id_product")){
                                            int index = 0;
                                            for(BeanProduct beanProduct : products){
                                                if(beanProduct.getId() == getArguments().getInt("id_product")){
                                                    index = products.indexOf(beanProduct);
                                                }
                                            }
                                            if(index > 0){
                                                float y = recycler.getY() + recycler.getChildAt(index).getY();
                                                scrollView.smoothScrollTo(0, (int) y);
                                            }
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                }
                            }, 1000);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submit:
                //showProgress();
                rootView.findViewById(R.id.submit).setOnClickListener(null);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        rootView.findViewById(R.id.submit).setOnClickListener(CartOldFragment.this);
                    }
                }, 500);
                FragmentHelper.add(ConfirmOrderFragment.newInstance());
                break;
        }
    }
}
