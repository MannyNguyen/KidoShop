package vn.kido.shop.Fragment.OAuth;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import vn.kido.shop.Fragment.BaseFragment;
import vn.kido.shop.Helper.FragmentHelper;
import vn.kido.shop.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeOAuthFragment extends BaseFragment implements View.OnClickListener {


    public HomeOAuthFragment() {
        // Required empty public constructor
    }

    public static HomeOAuthFragment newInstance() {
        Bundle args = new Bundle();
        HomeOAuthFragment fragment = new HomeOAuthFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView != null) {
            return rootView;
        }
        rootView = inflater.inflate(R.layout.fragment_home_oauth, container, false);
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
                rootView.findViewById(R.id.btn_signin).setOnClickListener(HomeOAuthFragment.this);
                rootView.findViewById(R.id.btn_signup).setOnClickListener(HomeOAuthFragment.this);
                rootView.findViewById(R.id.support).setOnClickListener(HomeOAuthFragment.this);
            }
        });
        threadInit.start();
        isLoaded = true;

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_signin:
                FragmentHelper.replace(LoginFragment.newInstance());
                break;
            case R.id.btn_signup:
                FragmentHelper.replace(RegisterCustomerCodeFragment.newInstance());
                break;
            case R.id.support:
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + getString(R.string.phone_support)));
                startActivity(intent);
                break;
        }
    }
}
