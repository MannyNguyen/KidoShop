package vn.kido.shop.Fragment.Cart;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import vn.kido.shop.Fragment.BaseFragment;
import vn.kido.shop.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class PromotionGiftFragment extends BaseFragment implements View.OnClickListener {

    TextView txtPromotionCompany, txtPromotionName, txtMoneyPromo, txtTotal, txtRemain, txtDifference, txtPromotionStatus;
    RecyclerView recyclerTab, recyclerPromotionProduct;
    Button btnSubmit;

    public PromotionGiftFragment() {
        // Required empty public constructor
    }

    public static PromotionGiftFragment newInstance() {
        Bundle args = new Bundle();
        PromotionGiftFragment fragment = new PromotionGiftFragment();
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
        rootView = inflater.inflate(R.layout.fragment_promotion_gift, container, false);
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
                txtPromotionCompany = rootView.findViewById(R.id.txt_promotion_company);
                recyclerTab = rootView.findViewById(R.id.recycler_tab);
                txtPromotionName = rootView.findViewById(R.id.txt_promotion_name);
                txtMoneyPromo = rootView.findViewById(R.id.txt_money_promo);
                recyclerPromotionProduct = rootView.findViewById(R.id.recycler_promotion_product);
                txtTotal = rootView.findViewById(R.id.txt_total);
                txtRemain = rootView.findViewById(R.id.txt_remain);
                txtDifference = rootView.findViewById(R.id.txt_difference);
                txtPromotionStatus=rootView.findViewById(R.id.txt_promotion_status);
                btnSubmit=rootView.findViewById(R.id.btn_submit);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        btnSubmit.setOnClickListener(PromotionGiftFragment.this);
                    }
                });
            }
        });
        threadInit.start();
        isLoaded = true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_submit:
                break;
        }
    }
}
