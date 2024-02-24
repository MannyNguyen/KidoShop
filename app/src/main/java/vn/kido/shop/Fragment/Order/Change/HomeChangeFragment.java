package vn.kido.shop.Fragment.Order.Change;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import vn.kido.shop.Fragment.BaseFragment;
import vn.kido.shop.Helper.FragmentHelper;
import vn.kido.shop.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeChangeFragment extends BaseFragment implements View.OnClickListener {
    LinearLayout llChangeProduct, llPayProduct, llChangeFullOrder, llPolicy;

    public HomeChangeFragment() {
        // Required empty public constructor
    }

    public static HomeChangeFragment newInstance() {
        Bundle args = new Bundle();
        HomeChangeFragment fragment = new HomeChangeFragment();
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
        rootView = inflater.inflate(R.layout.fragment_home_change, container, false);
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
                llChangeProduct = rootView.findViewById(R.id.ll_change_product);
                llPayProduct = rootView.findViewById(R.id.ll_pay_product);
                llChangeFullOrder = rootView.findViewById(R.id.ll_change_full_order);
                llPolicy = rootView.findViewById(R.id.ll_policy);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        title.setText(getString(R.string.change_pay_product));

                        SpannableString content = new SpannableString(getString(R.string.policy_change_product));
                        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);

                        SpannableString content_1 = new SpannableString(getString(R.string.policy_pay_product));
                        content_1.setSpan(new UnderlineSpan(), 0, content_1.length(), 0);

                        SpannableString content_2 = new SpannableString(getString(R.string.policy_pay_full_order));
                        content_2.setSpan(new UnderlineSpan(), 0, content_2.length(), 0);

                        llChangeProduct.setOnClickListener(HomeChangeFragment.this);
                        llPayProduct.setOnClickListener(HomeChangeFragment.this);
                        llChangeFullOrder.setOnClickListener(HomeChangeFragment.this);
                        llPolicy.setOnClickListener(HomeChangeFragment.this);
                    }
                });
            }
        });
        threadInit.start();
        isLoaded = true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_change_full_order:
                FragmentHelper.add(ReturnOrderFragment.newInstance());
                break;
            case R.id.ll_change_product:
                FragmentHelper.add(RetailOrderFragment.newInstance(RetailOrderFragment.CHANGE));
                break;
            case R.id.ll_pay_product:
                FragmentHelper.add(RetailOrderFragment.newInstance(RetailOrderFragment.PAY));
                break;
            case R.id.ll_policy:
                FragmentHelper.add(PolicyChangeFragment.newInstance());
                break;
        }
    }
}
