package vn.kido.shop.Fragment.Order.Receive;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;

import rx.Subscription;
import vn.kido.shop.Api.APIService;
import vn.kido.shop.Class.StoreCart;
import vn.kido.shop.Fragment.BaseDialogFragment;
import vn.kido.shop.Fragment.BaseFragment;
import vn.kido.shop.Fragment.Common.HomeFragment;
import vn.kido.shop.Helper.FragmentHelper;
import vn.kido.shop.Helper.LocationHelper;
import vn.kido.shop.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class VerifyReceiveOrderFragment extends BaseDialogFragment implements View.OnClickListener {

    Subscription subscriptionConfirm;

    public VerifyReceiveOrderFragment() {
        // Required empty public constructor
    }

    public static VerifyReceiveOrderFragment newInstance(String orderCode) {
        Bundle args = new Bundle();
        args.putString("order_code", orderCode);
        VerifyReceiveOrderFragment fragment = new VerifyReceiveOrderFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_verify_receive_order, container, false);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        getView().findViewById(R.id.submit).setOnClickListener(VerifyReceiveOrderFragment.this);
        getView().findViewById(R.id.back).setOnClickListener(VerifyReceiveOrderFragment.this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (subscriptionConfirm != null) {
            subscriptionConfirm.unsubscribe();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                this.dismissAllowingStateLoss();
                break;
            case R.id.submit:
                this.dismissAllowingStateLoss();
                FragmentHelper.add(RateOrderFragment.newInstance(getArguments().getString("order_code")));
                break;
        }
    }
}
