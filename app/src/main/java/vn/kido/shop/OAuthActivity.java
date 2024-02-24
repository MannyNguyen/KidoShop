package vn.kido.shop;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import vn.kido.shop.Class.CmmFunc;
import vn.kido.shop.Class.GlobalClass;
import vn.kido.shop.Fragment.Common.HomeFragment;
import vn.kido.shop.Fragment.OAuth.LoginFragment;
import vn.kido.shop.Fragment.OAuth.RegisterFragment;
import vn.kido.shop.Helper.FragmentHelper;
import vn.kido.shop.Helper.StorageHelper;

import static vn.kido.shop.Class.GlobalClass.getActivity;

public class OAuthActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oauth);
        GlobalClass.setActivity(OAuthActivity.this);
        CmmFunc.hideKeyboard(this);
//        Bundle extras = getIntent().getExtras();
//        if(extras == null) {
//            FragmentHelper.add(LoginFragment.newInstance());
//        } else {
//            FragmentHelper.add(RegisterFragment.newInstance());
//        }
        FragmentHelper.add(LoginFragment.newInstance());
        StorageHelper.set(StorageHelper.HOME_SCREEN, "");
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        Fragment fragment = FragmentHelper.getActiveFragment(OAuthActivity.this);
        if (fragment instanceof LoginFragment) {
            finishAffinity();
            return;
        }

        if (fragment instanceof RegisterFragment) {
            FragmentHelper.pop(OAuthActivity.this, LoginFragment.class);
            return;
        }

        if (fragment instanceof LoginFragment){
            LoginFragment loginFragment = (LoginFragment) fragment;
            if (loginFragment.getArguments().containsKey("reload")) {
                loginFragment.getView().findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        StorageHelper.set(StorageHelper.TOKEN, "");
                        StorageHelper.set(StorageHelper.USERNAME, "");
                        StorageHelper.set(StorageHelper.DEVICE_ID, "");
                        Intent intent = new Intent(OAuthActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
            } else {
                loginFragment.getView().findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FragmentHelper.pop(getActivity());
                    }
                });

            }
        }
    }
}
