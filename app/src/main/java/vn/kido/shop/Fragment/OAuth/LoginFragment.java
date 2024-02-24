package vn.kido.shop.Fragment.OAuth;


import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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
import vn.kido.shop.MainActivity;
import vn.kido.shop.OAuthActivity;
import vn.kido.shop.R;

import static vn.kido.shop.Class.GlobalClass.getActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends BaseFragment implements View.OnClickListener {
    Subscription login;
    EditText edtPhone;
    TextView txtSignup;
    Button btnLogin;

    public LoginFragment() {
        // Required empty public constructor
    }

    public static LoginFragment newInstance() {
        Bundle args = new Bundle();
        LoginFragment fragment = new LoginFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static LoginFragment newInstance(boolean isReload) {
        Bundle args = new Bundle();
        args.putBoolean("reload", isReload);
        LoginFragment fragment = new LoginFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView != null) {
            return rootView;
        }
        rootView = inflater.inflate(R.layout.fragment_login, container, false);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (isLoaded) {
            return;
        }

        if (getActivity() instanceof OAuthActivity) {
            rootView.findViewById(R.id.back).setOnClickListener(null);
            rootView.findViewById(R.id.back).setVisibility(View.GONE);
        } else {
            if (getArguments().containsKey("reload")) {
                rootView.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        StorageHelper.set(StorageHelper.TOKEN, "");
                        StorageHelper.set(StorageHelper.USERNAME, "");
                        StorageHelper.set(StorageHelper.DEVICE_ID, "");
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        getActivity().startActivity(intent);
                        getActivity().finish();
                    }
                });
            } else {
                rootView.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FragmentHelper.pop(getActivity());
                    }
                });

            }

        }
        threadInit = new Thread(new Runnable() {
            @Override
            public void run() {
                edtPhone = rootView.findViewById(R.id.edt_phone);
                btnLogin = rootView.findViewById(R.id.btn_signin);
                txtSignup = rootView.findViewById(R.id.txt_signup);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        SpannableString content = new SpannableString(getString(R.string.signup));
//                        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                        //txtSignup.setText(content);
                        CmmFunc.hideKeyboard(getActivity());
                        edtPhone.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                            @Override
                            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                                if (actionId == EditorInfo.IME_ACTION_DONE) {
                                    if (edtPhone.getText().toString().equals("06321123987789")) {
                                        //choose server
                                        FragmentHelper.add(ChooseServerFragment.newInstance());
                                    } else {
                                        btnLogin.setOnClickListener(null);
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    Thread.sleep(1000);
                                                    btnLogin.setOnClickListener(LoginFragment.this);
                                                } catch (InterruptedException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }).start();
                                        String phone = edtPhone.getText().toString().trim();
                                        if (TextUtils.isEmpty(phone) || phone.length() < 10) {
                                            Drawable icError = getResources().getDrawable(R.drawable.ic_error);
                                            icError.setBounds(0, 0, CmmFunc.convertDpToPx(getActivity(), 16), CmmFunc.convertDpToPx(getActivity(), 16));
                                            edtPhone.setCompoundDrawables(null, null, icError, null);
                                        }
                                        login(phone);
                                    }
                                }
                                return false;
                            }
                        });
                        txtSignup.setOnClickListener(LoginFragment.this);
                        btnLogin.setOnClickListener(LoginFragment.this);
                        rootView.findViewById(R.id.support).setOnClickListener(LoginFragment.this);
                    }
                });

            }
        });
        threadInit.start();
        isLoaded = true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_signin:
                if (edtPhone.getText().toString().equals("06321123987789")) {
                    //choose server
                    FragmentHelper.add(ChooseServerFragment.newInstance());
                } else {
                    btnLogin.setOnClickListener(null);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(1000);
                                btnLogin.setOnClickListener(LoginFragment.this);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                    String phone = edtPhone.getText().toString().trim();
                    if (TextUtils.isEmpty(phone) || phone.length() < 10) {
                        Drawable icError = getResources().getDrawable(R.drawable.ic_error);
                        icError.setBounds(0, 0, CmmFunc.convertDpToPx(getActivity(), 16), CmmFunc.convertDpToPx(getActivity(), 16));
                        edtPhone.setCompoundDrawables(null, null, icError, null);
                        return;
                    }

                    login(phone);
                }
                break;
            case R.id.support:
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + getString(R.string.phone_support)));
                startActivity(intent);
                break;
            case R.id.txt_signup:
                CmmFunc.hideKeyboard(getActivity());
                FragmentHelper.add(RegisterCustomerCodeFragment.newInstance());
                break;
        }
    }

    private void showError(View error) {
        final Animation animShake = AnimationUtils.loadAnimation(getContext(), R.anim.anim_shake);
        error.startAnimation(animShake);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (login != null) {
            login.unsubscribe();
        }
    }

    private void login(final String phone) {
        try {
            showProgress();
            login = APIService.getInstance().login(phone)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new ISubscriber() {
                        @Override
                        public void done() {
                            super.done();
                            hideProgress();
                            CmmFunc.hideKeyboard(getActivity());
                        }

                        @Override
                        public void excute(JSONObject jsonObject) {
                            try {
                                if (jsonObject == null) {
                                    return;
                                }
                                int code = jsonObject.getInt("code");
                                if (code == 1) {
                                    FragmentHelper.replace(VerifyOauthFragment.newInstance(phone, VerifyOauthFragment.LOGIN));
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
