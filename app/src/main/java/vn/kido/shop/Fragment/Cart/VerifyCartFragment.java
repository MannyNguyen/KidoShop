package vn.kido.shop.Fragment.Cart;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import vn.kido.shop.Fragment.BaseFragment;
import vn.kido.shop.Fragment.Common.HomeFragment;
import vn.kido.shop.Helper.FragmentHelper;
import vn.kido.shop.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class VerifyCartFragment extends BaseFragment implements View.OnClickListener {
    TextView orderCode;
    Button btnBackToHome;

    public VerifyCartFragment() {
        // Required empty public constructor
    }

    public static VerifyCartFragment newInstance(String orderCode) {
        Bundle args = new Bundle();
        args.putString("order_code", orderCode);
        VerifyCartFragment fragment = new VerifyCartFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView != null) {
            return rootView;
        }
        rootView = inflater.inflate(R.layout.fragment_verify_cart, container, false);
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
                orderCode = rootView.findViewById(R.id.order_code);
                btnBackToHome = rootView.findViewById(R.id.back_to_home);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        orderCode.setText(getArguments().getString("order_code"));
                        btnBackToHome.setOnClickListener(VerifyCartFragment.this);
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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_to_home:
                FragmentHelper.pop(getActivity(), HomeFragment.class);
                break;
        }
    }
}
