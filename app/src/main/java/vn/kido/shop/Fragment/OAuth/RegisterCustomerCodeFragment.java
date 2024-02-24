package vn.kido.shop.Fragment.OAuth;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONObject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import vn.kido.shop.Api.APIService;
import vn.kido.shop.Api.ISubscriber;
import vn.kido.shop.Class.CmmFunc;
import vn.kido.shop.Fragment.BaseFragment;
import vn.kido.shop.Helper.FragmentHelper;
import vn.kido.shop.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterCustomerCodeFragment extends BaseFragment implements View.OnClickListener {
    EditText edtLastName, edtStore, edtAddress, edtPhone, edtCustomerCode;
    TextView agreeLicense2, agreeLicense4, agreeLicense6;
    NestedScrollView scrollView;
    LinearLayout llName, llPhone, llStore, llAddress;
    ImageView ivCheck;
    boolean isCheck;
    Subscription subscriptionRegister;
    View submit;

    public RegisterCustomerCodeFragment() {
        // Required empty public constructor
    }

    public static RegisterCustomerCodeFragment newInstance() {
        Bundle args = new Bundle();
        RegisterCustomerCodeFragment fragment = new RegisterCustomerCodeFragment();
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
        rootView = inflater.inflate(R.layout.fragment_register_customer_code, container, false);
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
                scrollView = rootView.findViewById(R.id.scroll_view);
                edtLastName = rootView.findViewById(R.id.edt_last_name);
                edtStore = rootView.findViewById(R.id.edt_store);
                edtAddress = rootView.findViewById(R.id.edt_address);
                edtPhone = rootView.findViewById(R.id.phone_number);
                edtCustomerCode=rootView.findViewById(R.id.edt_customer_code);
                agreeLicense2 = rootView.findViewById(R.id.agree_license_2);
                agreeLicense4 = rootView.findViewById(R.id.agree_license_4);
                agreeLicense6 = rootView.findViewById(R.id.agree_license_6);
                ivCheck = rootView.findViewById(R.id.iv_check);
                submit = rootView.findViewById(R.id.submit);
                llName = rootView.findViewById(R.id.ll_name);
                llPhone = rootView.findViewById(R.id.ll_phone);
                llStore = rootView.findViewById(R.id.ll_store);
                llAddress = rootView.findViewById(R.id.ll_address);
                CmmFunc.hideKeyboard(getActivity());
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        title.setText(getString(R.string.signup));
                        submit.setOnClickListener(null);
                        SpannableString content1 = new SpannableString(getString(R.string.agree_license_2));
                        content1.setSpan(new UnderlineSpan(), 0, content1.length(), 0);
                        agreeLicense2.setText(content1);
                        SpannableString content2 = new SpannableString(getString(R.string.agree_license_4));
                        content2.setSpan(new UnderlineSpan(), 0, content2.length(), 0);
                        agreeLicense4.setText(content2);

                        llName.setOnClickListener(RegisterCustomerCodeFragment.this);
                        llPhone.setOnClickListener(RegisterCustomerCodeFragment.this);
                        llStore.setOnClickListener(RegisterCustomerCodeFragment.this);
                        ivCheck.setOnClickListener(RegisterCustomerCodeFragment.this);
                        agreeLicense2.setOnClickListener(RegisterCustomerCodeFragment.this);
                        agreeLicense4.setOnClickListener(RegisterCustomerCodeFragment.this);
                        agreeLicense6.setOnClickListener(RegisterCustomerCodeFragment.this);
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
            case R.id.iv_check:
                if (isCheck == false) {
                    ivCheck.setImageResource(R.drawable.ic_tick_checked);
                    submit.setBackgroundResource(R.drawable.main_box);
                    submit.setEnabled(true);
                    submit.setOnClickListener(RegisterCustomerCodeFragment.this);
                    isCheck = true;
                } else {
                    ivCheck.setImageResource(R.drawable.ic_tick_uncheck);
                    submit.setBackgroundResource(R.drawable.gray_disable_box);
                    submit.setOnClickListener(null);
                    submit.setEnabled(false);
                    isCheck = false;
                }
                break;
            case R.id.submit:
                try {
                    String fullname = edtLastName.getText().toString().trim();
                    if (TextUtils.isEmpty(fullname)) {
                        edtLastName.setError(getString(R.string.require));
                        scrollView.smoothScrollTo(0, 0);
                        return;
                    }
                    String phone1 = edtPhone.getText().toString().trim();
                    if (TextUtils.isEmpty(phone1)) {
                        edtPhone.setError(getString(R.string.require));
                        scrollView.smoothScrollTo(0, 0);
                        return;
                    }
                    String store = edtStore.getText().toString().trim();
                    if (TextUtils.isEmpty(store)) {
                        edtStore.setError(getString(R.string.require));
                        scrollView.smoothScrollTo(0, 0);
                        return;
                    }
                    String dms = edtCustomerCode.getText().toString().trim();
                    if (TextUtils.isEmpty(dms)){
                        edtCustomerCode.setError(getString(R.string.require));
                        scrollView.smoothScrollTo(0,0);
                        return;
                    }
                    if (subscriptionRegister != null && subscriptionRegister.isUnsubscribed()) {
                        subscriptionRegister.unsubscribe();
                    }
                    registerCustomerCode(phone1, fullname, store, dms);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.agree_license_2:
                String url = getString(R.string.policy_url);
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
                break;
            case R.id.agree_license_4:
                String url_1 = getString(R.string.policy_url);
                Intent i1 = new Intent(Intent.ACTION_VIEW);
                i1.setData(Uri.parse(url_1));
                startActivity(i1);
                break;
            case R.id.agree_license_6:
                String url_3 = getString(R.string.policy_url);
                Intent i3 = new Intent(Intent.ACTION_VIEW);
                i3.setData(Uri.parse(url_3));
                startActivity(i3);
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (subscriptionRegister != null) {
            subscriptionRegister.unsubscribe();
        }
    }

    private void registerCustomerCode(final String phone, String fullname, String shopName, String dms) {
        showProgress();
        subscriptionRegister = APIService.getInstance().registerCustomerCode(phone, fullname, shopName, dms)
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
                        FragmentHelper.replace(VerifyOauthFragment.newInstance(phone, VerifyOauthFragment.SIGNUP));
                    }
                });
    }
}
