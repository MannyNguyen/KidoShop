package vn.kido.shop.Fragment.OAuth;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONObject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import vn.kido.shop.Api.APIService;
import vn.kido.shop.Api.ISubscriber;
import vn.kido.shop.Class.CmmFunc;
import vn.kido.shop.Class.GlobalClass;
import vn.kido.shop.Fragment.BaseFragment;
import vn.kido.shop.Helper.FragmentHelper;
import vn.kido.shop.Helper.StorageHelper;
import vn.kido.shop.HomeActivity;
import vn.kido.shop.R;

public class VerifyOauthFragment extends BaseFragment implements View.OnClickListener {
    public static final String LOGIN = "LOGIN";
    public static final String SIGNUP = "SIGNUP";

    EditText edtOTP;
    View resend;
    Subscription subscriptionLogin, subscriptionVerify;

    public static VerifyOauthFragment newInstance(String username, String type) {
        VerifyOauthFragment fragment = new VerifyOauthFragment();
        Bundle args = new Bundle();
        args.putString("username", username);
        args.putString("type", type);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView != null) {
            return rootView;
        }
        rootView = inflater.inflate(R.layout.fragment_verify_oauth, container, false);
        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (subscriptionLogin != null) {
            subscriptionLogin.unsubscribe();
        }
        if (subscriptionVerify != null) {
            subscriptionVerify.unsubscribe();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (isLoaded) {
            return;
        }
        try {
            resend = getView().findViewById(R.id.btn_resend);
            edtOTP = getView().findViewById(R.id.edt_otp);
            edtOTP.requestFocus();

            edtOTP.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.toString().length() > 3) {
                        String value = s.toString().substring(0, 4);
                        verifyOTP(getArguments().getString("username"), value);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            resend.setOnClickListener(VerifyOauthFragment.this);

        } catch (Exception e) {
            e.printStackTrace();
        }

        isLoaded = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        CmmFunc.showKeyboard(getActivity());
    }

    private void verifyOTP(final String username, String otp) {
        showProgress();
        subscriptionVerify = APIService.getInstance().verifyOTP(username, otp)
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
                            if (jsonObject == null) {
                                return;
                            }
                            int code = jsonObject.getInt("code");
                            if (code == 1) {
                                edtOTP.setText("");
                                JSONObject data = jsonObject.getJSONObject("data");
                                String deviceId = Settings.Secure.getString(GlobalClass.getContext().getContentResolver(),
                                        Settings.Secure.ANDROID_ID);

                                StorageHelper.set(StorageHelper.DEVICE_ID, deviceId);
                                StorageHelper.set(StorageHelper.TOKEN, data.getString("token"));
                                StorageHelper.set(StorageHelper.USERNAME, username);
                                if (getArguments().getString("type").equals(VerifyOauthFragment.LOGIN)){
                                    Intent intent = new Intent(getActivity(), HomeActivity.class);
                                    startActivity(intent);
                                    getActivity().finish();
                                }
                                if (getArguments().getString("type").equals(VerifyOauthFragment.SIGNUP)){
                                    FragmentHelper.replace(SuccessOauthFragment.newInstance());
                                }


//                                if (data.getBoolean("dms_code") == true) {
//                                    FragmentHelper.replace(CustomerCodeFragment.newInstance(username, data.getString("token")));
//                                } else {
//                                    StorageHelper.set(StorageHelper.DEVICE_ID, deviceId);
//                                    StorageHelper.set(StorageHelper.TOKEN, data.getString("token"));
//                                    StorageHelper.set(StorageHelper.USERNAME, username);
//                                    Intent intent = new Intent(getActivity(), HomeActivity.class);
//                                    startActivity(intent);
//                                    getActivity().finish();
//                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });
    }

    private void reSendOTP(String username) {
        showProgress();
        APIService.getInstance().reSendOTP(username)
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
                            resend.setOnClickListener(null);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_resend:
                if (subscriptionLogin != null && subscriptionLogin.isUnsubscribed()) {
                    subscriptionLogin.unsubscribe();
                }
                reSendOTP(getArguments().getString("username"));
                break;
        }
    }
}
