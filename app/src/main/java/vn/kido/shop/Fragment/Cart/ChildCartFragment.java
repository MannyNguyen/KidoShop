package vn.kido.shop.Fragment.Cart;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import vn.kido.shop.Adapter.CartProductAdapter;
import vn.kido.shop.Adapter.GiftAdapter;
import vn.kido.shop.Adapter.ProgramPagerAdapter;
import vn.kido.shop.Bean.BeanProduct;
import vn.kido.shop.Bean.BeanProgram;
import vn.kido.shop.Class.CmmFunc;
import vn.kido.shop.Fragment.BaseFragment;
import vn.kido.shop.Helper.FragmentHelper;
import vn.kido.shop.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChildCartFragment extends BaseFragment implements View.OnClickListener {
    public NestedScrollView scrollView;
    ViewTreeObserver.OnScrollChangedListener onScrollChangedListener;
    public List<BeanProduct> products = new ArrayList();
    public RecyclerView recycler;
    //boolean isShowPopup;
    int idReward;
    Button saleToChange;
    boolean canClaim;
    String claims;

    boolean isFirst = true;

    public ChildCartFragment() {
        // Required empty public constructor
    }

    public static ChildCartFragment newInstance(String data, int position) {
        Bundle args = new Bundle();
        args.putString("data", data);
        args.putInt("position", position);
        ChildCartFragment fragment = new ChildCartFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView != null) {
            return rootView;
        }
        rootView = inflater.inflate(R.layout.fragment_child_cart, container, false);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        products.clear();
        if (isLoaded) {
            return;
        }
        excute();

        isLoaded = true;
    }

    public void reload() {
        excute();
    }

    private void excute() {
        try {
            Gson gson = new Gson();
            final TextView totalMoney = rootView.findViewById(R.id.total_money);
            final TextView totalSaleType = rootView.findViewById(R.id.total_sale_type);
            final TextView totalSaleOrder = rootView.findViewById(R.id.total_sale_order);
            final TextView totalPoint = rootView.findViewById(R.id.total_point);
            final TextView total = rootView.findViewById(R.id.total);
            final ViewPager pagerProgram = rootView.findViewById(R.id.pager_program);
            final View frameGifts = rootView.findViewById(R.id.frame_gifts);

            saleToChange = rootView.findViewById(R.id.sales_to_change);
            final JSONObject data = new JSONObject(getArguments().getString("data"));
            idReward = data.getInt("event_reward_id");
            canClaim = data.getBoolean("can_claim");
            claims = data.getString("event_claim_reward_ids");
            JSONArray items = data.getJSONArray("items");
            products.clear();
            for (int i = 0; i < items.length(); i++) {
                BeanProduct beanProduct = gson.fromJson(items.getString(i), BeanProduct.class);
                products.add(beanProduct);
            }

            scrollView = rootView.findViewById(R.id.scroll_view);
            recycler = rootView.findViewById(R.id.recycler);
            final RecyclerView recyclerGift = rootView.findViewById(R.id.recycler_gift);
            final CartProductAdapter adapter = new CartProductAdapter(ChildCartFragment.this, recycler, products);

            Type listGift = new TypeToken<List<BeanProduct>>() {
            }.getType();
            final List gifts = gson.fromJson(data.getString("gifts"), listGift);

            Type listEvent = new TypeToken<List<BeanProgram>>() {
            }.getType();
            final List events = gson.fromJson(data.getString("events"), listEvent);
            recycler.setLayoutManager(new LinearLayoutManager(getContext()));
            recycler.setAdapter(adapter);
            frameGifts.setVisibility(View.VISIBLE);
            if (gifts.size() > 0) {
                GiftAdapter giftAdapter = new GiftAdapter(ChildCartFragment.this, recyclerGift, gifts);
                recyclerGift.setLayoutManager(new LinearLayoutManager(getContext()));
                recyclerGift.setAdapter(giftAdapter);
            }

            if (events.size() == 0) {
                rootView.findViewById(R.id.program_container).setVisibility(View.GONE);
            } else {
                rootView.findViewById(R.id.program_container).setVisibility(View.VISIBLE);
                ProgramPagerAdapter programPagerAdapter = new ProgramPagerAdapter(ChildCartFragment.this, events);
                pagerProgram.setAdapter(programPagerAdapter);
            }

            totalMoney.setText(CmmFunc.formatMoney(data.getString("total_money"), false));
            totalSaleType.setText(CmmFunc.formatMoney(data.getString("tong_km_dong"), false));
            totalSaleOrder.setText(CmmFunc.formatMoney(data.getString("tong_km_don_hang"), false));
            total.setText(CmmFunc.formatMoney(data.getString("total"), false));
            totalPoint.setText(CmmFunc.formatKPoint(data.getString("total_point")));

            if (idReward == 0) {
                saleToChange.setVisibility(View.GONE);
            } else {
                saleToChange.setVisibility(View.VISIBLE);
                saleToChange.setOnClickListener(ChildCartFragment.this);
            }

            if (saleToChange.getVisibility() == View.GONE && gifts.size() == 0) {
                frameGifts.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void showPopup(int idReward, String claims) {
        if (!canClaim) {
            if (this.claims == null) {
                this.claims = claims;
            }
            if (idReward != 0 && claims != null) {
                addProgramCartFragment(idReward);
            }
            isFirst = false;
        }
    }

    public void showPopupCondition() {
        if (isFirst == true) {
            if (!canClaim) {
                if (idReward != 0 && claims != null) {
                    addProgramCartFragment(idReward);
                }

            }
            isFirst = false;
        }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sales_to_change:
                addProgramCartFragment(idReward);
                break;
        }
    }

    private void addProgramCartFragment(int idReward) {
        FragmentHelper.add(ProgramCartFragment.newInstance(getArguments().getInt("position"), idReward, claims));
    }
}
