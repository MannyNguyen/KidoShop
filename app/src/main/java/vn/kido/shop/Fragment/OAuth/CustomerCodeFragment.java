package vn.kido.shop.Fragment.OAuth;


import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import vn.kido.shop.Api.APIService;
import vn.kido.shop.Api.ISubscriber;
import vn.kido.shop.Class.GlobalClass;
import vn.kido.shop.Fragment.BaseFragment;
import vn.kido.shop.Helper.FragmentHelper;
import vn.kido.shop.Helper.StorageHelper;
import vn.kido.shop.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class CustomerCodeFragment extends BaseFragment implements View.OnClickListener {
    EditText edtCustomerCode, edtName, edtShopName;
    Button btnStart;

    public CustomerCodeFragment() {
        // Required empty public constructor
    }

    public static CustomerCodeFragment newInstance(String username, String token) {
        Bundle args = new Bundle();
        args.putString("username", username);
        args.putString("token", token);
        CustomerCodeFragment fragment = new CustomerCodeFragment();
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
        rootView = inflater.inflate(R.layout.fragment_customer_code, container, false);
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
                edtCustomerCode = rootView.findViewById(R.id.edt_customer_code);
                edtName = rootView.findViewById(R.id.edt_name);
                edtShopName = rootView.findViewById(R.id.edt_shop_name);
                btnStart = rootView.findViewById(R.id.btn_start);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        btnStart.setOnClickListener(CustomerCodeFragment.this);
                        rootView.findViewById(R.id.back).setOnClickListener(CustomerCodeFragment.this);
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
            case R.id.btn_start:
                if (edtName.getText().toString().equals("")) {
                    edtName.requestFocus();
                    edtName.setError("");
                    return;
                }
                if (edtShopName.getText().toString().equals("")) {
                    edtShopName.requestFocus();
                    edtShopName.setError("");
                    return;
                }
                if (edtCustomerCode.getText().toString().equals("")) {
                    edtCustomerCode.requestFocus();
                    edtCustomerCode.setError("");
                    return;
                }


                verifyDms(edtCustomerCode.getText().toString(), getArguments().getString("username"), getArguments().getString("token"),
                        edtName.getText().toString(), edtShopName.getText().toString());
                break;
            case R.id.back:
                FragmentHelper.pop(getActivity());
                break;
        }
    }

    private void verifyDms(String dms, String username, String token, String fullName, String shopName) {
        showProgress();
        APIService.getInstance().verifyDms(dms, username, token, fullName, shopName)
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
                            String deviceId = Settings.Secure.getString(GlobalClass.getContext().getContentResolver(),
                                    Settings.Secure.ANDROID_ID);
                            StorageHelper.set(StorageHelper.DEVICE_ID, deviceId);
                            StorageHelper.set(StorageHelper.USERNAME, getArguments().getString("username"));
                            StorageHelper.set(StorageHelper.TOKEN, getArguments().getString("token"));
                            FragmentHelper.replace(SuccessOauthFragment.newInstance());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }
}
