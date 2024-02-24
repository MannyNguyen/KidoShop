package vn.kido.shop.Fragment.OAuth;


import android.content.Intent;
import android.graphics.drawable.Drawable;
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
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import vn.kido.shop.Adapter.SpinnerAdapter;
import vn.kido.shop.Api.APIService;
import vn.kido.shop.Api.ISubscriber;
import vn.kido.shop.Class.CmmFunc;
import vn.kido.shop.Fragment.BaseFragment;
import vn.kido.shop.Helper.FragmentHelper;
import vn.kido.shop.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends BaseFragment implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    Spinner spinnerCity, spinnerDistrict, spinnerWard;
    EditText edtLastName, edtStore, edtAddress, edtPhone;
    TextView agreeLicense2, agreeLicense4, agreeLicense6;
    NestedScrollView scrollView;
    LinearLayout llName, llPhone, llStore, llAddress;
    ImageView ivCheck;
    boolean isCheck;
    Subscription subscriptionCity, subscriptionDistrict, subscriptionWard, subscriptionRegister;
    View submit;

    public RegisterFragment() {
        // Required empty public constructor
    }

    public static RegisterFragment newInstance() {
        Bundle args = new Bundle();
        RegisterFragment fragment = new RegisterFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView != null) {
            return rootView;
        }
        rootView = inflater.inflate(R.layout.fragment_register, container, false);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (isLoaded) {
            return;
        }
        TextView title = rootView.findViewById(R.id.title);
        title.setText(getString(R.string.signup));

        threadInit = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    scrollView = rootView.findViewById(R.id.scroll_view);
                    spinnerCity = rootView.findViewById(R.id.spinner_city);
                    spinnerDistrict = rootView.findViewById(R.id.spinner_district);
                    spinnerWard = rootView.findViewById(R.id.spinner_country);
                    edtLastName = rootView.findViewById(R.id.edt_last_name);

                    edtStore = rootView.findViewById(R.id.edt_store);
                    edtAddress = rootView.findViewById(R.id.edt_address);
                    edtPhone = rootView.findViewById(R.id.phone_number);

                    agreeLicense2 = rootView.findViewById(R.id.agree_license_2);
                    agreeLicense4 = rootView.findViewById(R.id.agree_license_4);
                    agreeLicense6 = rootView.findViewById(R.id.agree_license_6);

                    ivCheck = rootView.findViewById(R.id.iv_check);

                    submit = rootView.findViewById(R.id.submit);

                    llName = rootView.findViewById(R.id.ll_name);
                    llPhone = rootView.findViewById(R.id.ll_phone);
                    llStore = rootView.findViewById(R.id.ll_store);
                    llAddress = rootView.findViewById(R.id.ll_address);

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            subscriptionCity = getRegion(1, 0, 0);
                            submit.setOnClickListener(null);

                            SpannableString content1 = new SpannableString(getString(R.string.agree_license_2));
                            content1.setSpan(new UnderlineSpan(), 0, content1.length(), 0);
                            agreeLicense2.setText(content1);

                            SpannableString content2 = new SpannableString(getString(R.string.agree_license_4));
                            content2.setSpan(new UnderlineSpan(), 0, content2.length(), 0);
                            agreeLicense4.setText(content2);

                            llName.setOnClickListener(RegisterFragment.this);
                            llPhone.setOnClickListener(RegisterFragment.this);
                            llStore.setOnClickListener(RegisterFragment.this);
                            ivCheck.setOnClickListener(RegisterFragment.this);
                            agreeLicense2.setOnClickListener(RegisterFragment.this);
                            agreeLicense4.setOnClickListener(RegisterFragment.this);
                            agreeLicense6.setOnClickListener(RegisterFragment.this);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        threadInit.start();

        isLoaded = true;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (subscriptionCity != null) {
            subscriptionCity.unsubscribe();
        }

        if (subscriptionWard != null) {
            subscriptionWard.unsubscribe();
        }

        if (subscriptionDistrict != null) {
            subscriptionDistrict.unsubscribe();
        }

        if (subscriptionRegister != null) {
            subscriptionRegister.unsubscribe();
        }
    }

    Subscription getRegion(final int type, int idCity, int idDistrict) {
        return APIService.getInstance().getRegion(type, idCity, idDistrict).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<String, JSONObject>() {
                    @Override
                    public JSONObject call(String s) {
                        try {
                            return new JSONObject(s);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                })
                .subscribe(new ISubscriber() {
                    @Override
                    public void done() {
                        super.done();
                        if (type == 3) {
                            hideProgress();
                        }

                    }

                    @Override
                    public void excute(JSONObject jsonObject) {
                        try {
                            if (jsonObject == null) {
                                return;
                            }
                            int code = jsonObject.getInt("code");
                            if (code == 1) {
                                JSONArray data = jsonObject.getJSONArray("data");
                                List<JSONObject> regions = new ArrayList<>();
                                for (int i = 0; i < data.length(); i++) {
                                    regions.add(data.getJSONObject(i));
                                }
                                if (type == 1) {
                                    final SpinnerAdapter adapter = new SpinnerAdapter(getContext(), R.layout.row_spinner_address, regions, true);
                                    spinnerCity.setAdapter(adapter);
                                    spinnerCity.setOnItemSelectedListener(RegisterFragment.this);
                                    return;
                                }

                                if (type == 2) {
                                    final SpinnerAdapter adapter = new SpinnerAdapter(getContext(), R.layout.row_spinner_address, regions);
                                    spinnerDistrict.setAdapter(adapter);
                                    spinnerDistrict.setOnItemSelectedListener(RegisterFragment.this);
                                    return;
                                }

                                if (type == 3) {
                                    final SpinnerAdapter adapter = new SpinnerAdapter(getContext(), R.layout.row_spinner_address, regions);
                                    spinnerWard.setAdapter(adapter);
                                    return;
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.spinner_city:
                if (parent.getSelectedItem() instanceof String) {
                    spinnerDistrict.setAdapter(null);
                    spinnerDistrict.setEnabled(false);
                    spinnerWard.setAdapter(null);
                    spinnerWard.setEnabled(false);
                    return;
                }
                spinnerDistrict.setEnabled(true);
                spinnerWard.setEnabled(true);
                JSONObject item = (JSONObject) parent.getSelectedItem();
                if (item != null) {
                    try {
                        subscriptionWard = getRegion(2, item.getInt("id"), 0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;

            case R.id.spinner_district:
                JSONObject itemCity = (JSONObject) spinnerCity.getSelectedItem();
                JSONObject itemDistrict = (JSONObject) parent.getSelectedItem();
                if (itemDistrict != null) {
                    try {
                        subscriptionWard = getRegion(3, itemCity.getInt("id"), itemDistrict.getInt("id"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submit:
                try {
                    String fullname = edtLastName.getText().toString().trim();
                    if (TextUtils.isEmpty(fullname)) {
                        edtLastName.setError(getString(R.string.require));
                        scrollView.smoothScrollTo(0, 0);
                        return;
                    }
                    String store = edtStore.getText().toString().trim();
                    if (TextUtils.isEmpty(store)) {
                        edtStore.setError(getString(R.string.require));
                        scrollView.smoothScrollTo(0, 0);
                        return;
                    }

                    String address = edtAddress.getText().toString().trim();
                    if (TextUtils.isEmpty(address)) {
                        edtAddress.setError(getString(R.string.require));
                        scrollView.smoothScrollTo(0, 0);
                        return;
                    }

                    String phone1 = edtPhone.getText().toString().trim();
                    if (TextUtils.isEmpty(phone1)) {
                        edtPhone.setError(getString(R.string.require));
                        scrollView.smoothScrollTo(0, 0);
                        return;
                    }

                    if ((spinnerCity.getSelectedItemPosition()) == 0) {
                        Toast.makeText(getContext(), getActivity().getResources().getString(R.string.no_region), Toast.LENGTH_SHORT).show();
                        spinnerCity.requestFocus();
                        return;
                    }

                    if (subscriptionRegister != null && subscriptionRegister.isUnsubscribed()) {
                        subscriptionRegister.unsubscribe();
                    }

                    int idCity = ((JSONObject) spinnerCity.getSelectedItem()).getInt("id");
                    int idDistrict = ((JSONObject) spinnerDistrict.getSelectedItem()).getInt("id");
                    int idWard = ((JSONObject) spinnerWard.getSelectedItem()).getInt("id");
                    register(phone1, fullname, store, address, "", idCity, idDistrict, idWard);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.ll_name:
                edtLastName.requestFocus();
                CmmFunc.showKeyboard(getActivity());
                break;
            case R.id.ll_phone:
                edtPhone.requestFocus();
                CmmFunc.showKeyboard(getActivity());
                break;
            case R.id.ll_store:
                edtStore.requestFocus();
                CmmFunc.showKeyboard(getActivity());
                break;
            case R.id.ll_address:
                edtAddress.requestFocus();
                CmmFunc.showKeyboard(getActivity());
                break;
            case R.id.iv_check:
                if (isCheck == false) {
                    ivCheck.setImageResource(R.drawable.ic_tick_checked);
                    submit.setBackgroundResource(R.drawable.main_box);
                    submit.setEnabled(true);
                    submit.setOnClickListener(RegisterFragment.this);
                    isCheck = true;
                } else {
                    ivCheck.setImageResource(R.drawable.ic_tick_uncheck);
                    submit.setBackgroundResource(R.drawable.gray_disable_box);
                    submit.setOnClickListener(null);
                    submit.setEnabled(false);
                    isCheck = false;
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

    private void register(final String username, String fullname,
                          String shopName, String address, String avatar,
                          int idCity, int idDistrict, int idWard) {
        showProgress();
        subscriptionRegister = APIService.getInstance().register(username, fullname, shopName, address, avatar, idCity, idDistrict, idWard)
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
                        FragmentHelper.replace(VerifyOauthFragment.newInstance(username, VerifyOauthFragment.SIGNUP));
                    }
                });
    }
}
