package vn.kido.shop.Fragment.Cart;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import vn.kido.shop.Adapter.ReceiptOrderAdapter;
import vn.kido.shop.Adapter.SpinnerAdapter;
import vn.kido.shop.Api.APIService;
import vn.kido.shop.Api.ISubscriber;
import vn.kido.shop.Bean.BeanOrder;
import vn.kido.shop.Bean.BeanUser;
import vn.kido.shop.Class.CmmFunc;
import vn.kido.shop.Fragment.BaseFragment;
import vn.kido.shop.Fragment.Profile.ProfileFragment;
import vn.kido.shop.Fragment.Profile.UpdateProfileFragment;
import vn.kido.shop.Helper.FragmentHelper;
import vn.kido.shop.Helper.LocationHelper;
import vn.kido.shop.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConfirmOrderFragment extends BaseFragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    TextView totalMoney, totalSaleType, totalSaleOrder, totalPoint, total, shopName, date, txtCity, txtDistrict, txtWard;
    EditText edtTaxCode, edtNote, edtReceiver, edtAddress, edtPhone;
    Button btnOrder;
    Subscription getBookOrder, confirmBookOrder;
    RecyclerView recycler;
    ReceiptOrderAdapter adapter;
    List<BeanOrder> orders;
    Spinner editSpinnerCity, editSpinnerDistrict, editSpinnerCountry;
    Subscription subscriptionCity, subscriptionDistrict, subscriptionWard;
    NestedScrollView scroll;
    int idCity, idDistrict, idWard;

    public ConfirmOrderFragment() {
        // Required empty public constructor
    }

    public static ConfirmOrderFragment newInstance() {
        Bundle args = new Bundle();
        ConfirmOrderFragment fragment = new ConfirmOrderFragment();
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
        rootView = inflater.inflate(R.layout.fragment_confirm_order, container, false);
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
                orders = new ArrayList<>();
                recycler = rootView.findViewById(R.id.recycler);
                adapter = new ReceiptOrderAdapter(ConfirmOrderFragment.this, recycler, orders);
                totalMoney = rootView.findViewById(R.id.total_money);
                totalSaleType = rootView.findViewById(R.id.total_sale_type);
                totalSaleOrder = rootView.findViewById(R.id.total_sale_order);
                totalPoint = rootView.findViewById(R.id.total_point);
                total = rootView.findViewById(R.id.total);
                edtTaxCode = rootView.findViewById(R.id.edt_tax_code);
                edtNote = rootView.findViewById(R.id.edt_note);
                btnOrder = rootView.findViewById(R.id.btn_order);
                edtReceiver = rootView.findViewById(R.id.edt_receiver);
                edtAddress = rootView.findViewById(R.id.edt_address);
                edtPhone = rootView.findViewById(R.id.edt_phone);
                shopName = rootView.findViewById(R.id.shop_name);
                final TextView title = rootView.findViewById(R.id.title);
                date = rootView.findViewById(R.id.date);
                editSpinnerCity = rootView.findViewById(R.id.edit_spinner_city);
                editSpinnerDistrict = rootView.findViewById(R.id.edit_spinner_district);
                editSpinnerCountry = rootView.findViewById(R.id.edit_spinner_country);
                scroll = rootView.findViewById(R.id.scroll);
                txtCity = rootView.findViewById(R.id.txt_city);
                txtDistrict = rootView.findViewById(R.id.txt_district);
                txtWard = rootView.findViewById(R.id.txt_ward);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        title.setText(getString(R.string.confirm));
                        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
                        recycler.setAdapter(adapter);
                        getBookOrder();

                        editSpinnerCity.setEnabled(true);
                        editSpinnerDistrict.setEnabled(true);
                        editSpinnerCountry.setEnabled(true);

                        btnOrder.setOnClickListener(ConfirmOrderFragment.this);
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
            case R.id.btn_order:
                try {
                    confirmBookOrder();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private void getBookOrder() {
        showProgress();
        getBookOrder = APIService.getInstance().bookOrder()
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
                            JSONObject data = jsonObject.getJSONObject("data");
                            Type listType = new TypeToken<List<BeanOrder>>() {
                            }.getType();
                            final List items = new Gson().fromJson(data.getString("order_info"), listType);
                            orders.addAll(items);
                            adapter.notifyDataSetChanged();
                            date.setText(new DateTime(data.getLong("book_order_date")).toString("dd/MM/yyyy"));
                            totalMoney.setText(CmmFunc.formatMoney(data.getInt("total_order_buy"), false));
                            totalSaleType.setText(CmmFunc.formatMoney(data.getInt("total_order_percent_discount"), false));
                            totalSaleOrder.setText(CmmFunc.formatMoney(data.getInt("total_order_money_discount"), false));
                            totalPoint.setText(CmmFunc.formatKPoint(data.getInt("total_order_point")));
                            total.setText(CmmFunc.formatMoney(data.getInt("total_order_money"), false));
                            edtReceiver.setText(data.getString("fullname"));
                            edtAddress.setText(data.getString("address"));
                            edtPhone.setText(data.getString("phone"));
                            shopName.setText(data.getString("shop_name"));
                            idCity = data.getInt("city_id");
                            idDistrict = data.getInt("district_id");
                            idWard = data.getInt("ward_id");
                            getRegion(1, idCity, idDistrict);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void confirmBookOrder() {
        try {
            if (edtReceiver.getText().toString().equals("")) {
                edtReceiver.setError(getString(R.string.require));
                return;
            }
            if (edtAddress.getText().toString().equals("")) {
                edtAddress.setError(getString(R.string.require));
                return;
            }
            if (edtPhone.getText().toString().equals("")) {
                edtPhone.setError(getString(R.string.require));
                return;
            }

            int idCity = ((JSONObject) editSpinnerCity.getSelectedItem()).getInt("id");
            if (idCity == 0) {
                Toast.makeText(getContext(), getActivity().getResources().getString(R.string.no_region), Toast.LENGTH_SHORT).show();
                txtCity.requestFocus();
                scroll.smoothScrollTo(0, 0);
                return;
            }

            int idDistrict = ((JSONObject) editSpinnerDistrict.getSelectedItem()).getInt("id");
            if (idDistrict == 0) {
                Toast.makeText(getContext(), getActivity().getResources().getString(R.string.no_region), Toast.LENGTH_SHORT).show();
                txtDistrict.requestFocus();
                scroll.smoothScrollTo(0, 0);
                return;
            }

            int idWard = ((JSONObject) editSpinnerCountry.getSelectedItem()).getInt("id");
            if (idWard == 0) {
                Toast.makeText(getContext(), getActivity().getResources().getString(R.string.no_region), Toast.LENGTH_SHORT).show();
                txtWard.requestFocus();
                scroll.smoothScrollTo(0, 0);
                return;
            }

            if (TextUtils.isEmpty(edtAddress.getText().toString())) {
                Toast.makeText(getContext(), getActivity().getResources().getString(R.string.require_address), Toast.LENGTH_SHORT).show();
                edtAddress.requestFocus();
                return;
            }

            confirmBookOrder = APIService.getInstance().confirmBookOrder(edtNote.getText().toString(), edtReceiver.getText().toString(), edtAddress.getText().toString(),
                    edtPhone.getText().toString(), edtTaxCode.getText().toString(), idCity, idDistrict, idWard)
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
                                updateLocation();
                                FragmentHelper.add(VerifyCartFragment.newInstance(jsonObject.getJSONObject("data").getString("order_code") + ""));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateLocation() {
        new LocationHelper().getLastLocation(new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                if (locationResult != null) {
                    APIService.getInstance().updateLocation(2, locationResult.getLastLocation().getLatitude(), locationResult.getLastLocation().getLongitude());
                }
            }
        });
    }

    Subscription getRegion(final int type, final int iC, final int iD) {
        showProgress();
        return APIService.getInstance().getRegion(type, iC, iD).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
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
                                    if (idCity == 0) {
                                        JSONObject jO = new JSONObject();
                                        jO.put("id", 0);
                                        jO.put("name", "");
                                        regions.add(0, jO);
                                    }
                                    final SpinnerAdapter adapter = new SpinnerAdapter(getContext(), R.layout.row_spinner_dropdown_20, regions);
                                    editSpinnerCity.setAdapter(adapter);
                                    // vòng lặp lấy position từng khu vực
                                    for (int i = 0; i < regions.size(); i++) {
                                        if (regions.get(i).getInt("id") == idCity) {
                                            editSpinnerCity.setSelection(i);
                                            break;
                                        }

                                    }
                                    editSpinnerCity.setOnItemSelectedListener(ConfirmOrderFragment.this);
                                    return;
                                }

                                if (type == 2) {
                                    if (idDistrict == 0) {
                                        JSONObject jO = new JSONObject();
                                        jO.put("id", 0);
                                        jO.put("name", "");
                                        regions.add(0, jO);
                                    }
                                    final SpinnerAdapter adapter = new SpinnerAdapter(getContext(), R.layout.row_spinner_dropdown_20, regions);
                                    editSpinnerDistrict.setAdapter(adapter);
                                    // vòng lặp lấy position từng khu vực
                                    for (int i = 0; i < regions.size(); i++) {
                                        if (regions.get(i).getInt("id") == idDistrict && idDistrict != 0) {
                                            editSpinnerDistrict.setSelection(i);
                                            break;
                                        }
                                    }
                                    editSpinnerDistrict.setOnItemSelectedListener(ConfirmOrderFragment.this);
                                    return;
                                }

                                if (type == 3) {
                                    hideProgress();
                                    if (idWard == 0) {
                                        JSONObject jO = new JSONObject();
                                        jO.put("id", 0);
                                        jO.put("name", "");
                                        regions.add(0, jO);
                                    }
                                    final SpinnerAdapter adapter = new SpinnerAdapter(getContext(), R.layout.row_spinner_dropdown_20, regions);
                                    editSpinnerCountry.setAdapter(adapter);
                                    // vòng lặp lấy position từng khu vực
                                    for (int i = 0; i < regions.size(); i++) {
                                        if (regions.get(i).getInt("id") == idWard) {
                                            editSpinnerCountry.setSelection(i);
                                            break;
                                        }
                                    }
                                    editSpinnerCountry.setOnItemSelectedListener(ConfirmOrderFragment.this);
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
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        switch (adapterView.getId()) {
            case R.id.edit_spinner_city:

                JSONObject item = (JSONObject) adapterView.getSelectedItem();
                if (item != null) {
                    try {
                        subscriptionWard = getRegion(2, item.getInt("id"), idDistrict);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;

            case R.id.edit_spinner_district:
                JSONObject itemCity = (JSONObject) editSpinnerCity.getSelectedItem();
                JSONObject itemDistrict = (JSONObject) adapterView.getSelectedItem();
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
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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
    }
}
