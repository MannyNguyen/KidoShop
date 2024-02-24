package vn.kido.shop.Fragment.Profile;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import vn.kido.shop.Adapter.SpinnerAdapter;
import vn.kido.shop.Api.APIService;
import vn.kido.shop.Api.ISubscriber;
import vn.kido.shop.Bean.BeanUser;
import vn.kido.shop.Class.CmmFunc;
import vn.kido.shop.Fragment.BaseFragment;
import vn.kido.shop.Fragment.Dialog.ErrorDialogFragment;
import vn.kido.shop.Fragment.Dialog.MessageDialogFragment;
import vn.kido.shop.Fragment.OAuth.RegisterFragment;
import vn.kido.shop.Helper.FragmentHelper;
import vn.kido.shop.Helper.ImageHelper;
import vn.kido.shop.Helper.PermissionHelper;
import vn.kido.shop.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class UpdateProfileFragment extends BaseFragment implements AdapterView.OnItemSelectedListener, View.OnClickListener {
    final int EXTERNAL = 1001;
    PermissionHelper permissionHelper;
    Spinner editspinnerCity, editspinnerDistrict, editspinnerWard;
    TextView txvPhone1, txv_update, agreeLicense2, agreeLicense4, agreeLicense6;;
    Button btnUpdate;
    EditText edtLastName, edtFullName, edtStore, edtAddress, edtPhone2, edtEmail;
    CircleImageView iv_avatar;
    Subscription subscriptionCity, subscriptionDistrict, subscriptionWard, subscriptionUpdate;
    ScrollView scrv;
    public static final int IMAGE_REQUEST_CODE = 11;
    BeanUser beanUser;
    ImageView ivCheck;
    boolean isCheck;

    public UpdateProfileFragment() {
        // Required empty public constructor
    }

    public static UpdateProfileFragment newInstance(String data) {
        Bundle args = new Bundle();
        args.putString("data", data);
        UpdateProfileFragment fragment = new UpdateProfileFragment();
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
        rootView = inflater.inflate(R.layout.fragment_update_profile, container, false);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (isLoaded) {
            return;
        }
        permissionHelper = new PermissionHelper(UpdateProfileFragment.this);

        threadInit = new Thread(new Runnable() {
            @Override
            public void run() {
                beanUser = new Gson().fromJson(getArguments().getString("data"), BeanUser.class);
                final TextView title = rootView.findViewById(R.id.title);
                editspinnerCity = rootView.findViewById(R.id.edit_spinner_city);
                editspinnerDistrict = rootView.findViewById(R.id.edit_spinner_district);
                editspinnerWard = rootView.findViewById(R.id.edit_spinner_country);
                edtLastName = rootView.findViewById(R.id.edt_edit_lastname);
                edtFullName = rootView.findViewById(R.id.edt_edit_fullname);
                edtStore = rootView.findViewById(R.id.edt_edit_shop);
                edtAddress = rootView.findViewById(R.id.edt_edit_address_default);
                txvPhone1 = rootView.findViewById(R.id.edt_edit_phone_1);
                edtPhone2 = rootView.findViewById(R.id.edt_edit_phone_2);
                edtEmail = rootView.findViewById(R.id.edt_edit_email);
                btnUpdate = rootView.findViewById(R.id.btn_update);
                iv_avatar = rootView.findViewById(R.id.edit_iv_avatar);
                scrv = rootView.findViewById(R.id.scroll_view_edit_profile);
                ivCheck = rootView.findViewById(R.id.iv_check);
                agreeLicense2 = rootView.findViewById(R.id.agree_license_2);
                agreeLicense4 = rootView.findViewById(R.id.agree_license_4);
                agreeLicense6 = rootView.findViewById(R.id.agree_license_6);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!beanUser.getFullName().equals("")){
                            title.setText(beanUser.getFullName());
                        }
                        title.setText(getString(R.string.update_info));
                        edtFullName.setText(beanUser.getFullName());
                        edtStore.setText(beanUser.getShopName());
                        edtAddress.setText(beanUser.getAddress());
                        txvPhone1.setText(beanUser.getUserName());
                        edtPhone2.setText(beanUser.getPhone());
                        edtEmail.setText(beanUser.getEmail());

                        //edtAddress.setEnabled(false);
                        editspinnerCity.setEnabled(true);
                        editspinnerDistrict.setEnabled(true);
                        editspinnerWard.setEnabled(true);

                        iv_avatar.setOnClickListener(UpdateProfileFragment.this);
                        ivCheck.setOnClickListener(UpdateProfileFragment.this);
                        agreeLicense2.setOnClickListener(UpdateProfileFragment.this);
                        agreeLicense4.setOnClickListener(UpdateProfileFragment.this);
                        agreeLicense6.setOnClickListener(UpdateProfileFragment.this);

                        if (!TextUtils.isEmpty(beanUser.getAvatar())) {
                            Picasso.get().load(beanUser.getAvatar()).placeholder(R.drawable.user_image).into((iv_avatar));
                        }
                        getRegion(1, 0, 0);

                        edtAddress.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                            public boolean onEditorAction(TextView v, int actionId,
                                                          KeyEvent event) {
                                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                                    CmmFunc.hideKeyboard(getActivity());
                                    return true;
                                }
                                return false;
                            }
                        });
                    }
                });
            }
        });
        threadInit.start();

        isLoaded = true;
    }


    @Override
    public void manuResume() {
        super.manuResume();
        requestPermission();
    }

    private void requestPermission() {
        if (permissionHelper.requestPermission(EXTERNAL, Manifest.permission.WRITE_EXTERNAL_STORAGE, getString(R.string.per_write_storage))) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, IMAGE_REQUEST_CODE);
        }
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

    }

    Subscription getRegion(final int type, final int idCity, final int idDistrict) {
        showProgress();
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
                                    final SpinnerAdapter adapter = new SpinnerAdapter(getContext(), R.layout.row_spinner_update_profile, regions);
                                    editspinnerCity.setAdapter(adapter);
                                    // vòng lặp lấy position từng khu vực
                                    for (int i = 0; i < regions.size(); i++) {
                                        if (regions.get(i).getInt("id") == beanUser.getIdCity()) {
                                            editspinnerCity.setSelection(i);

                                            break;
                                        }

                                    }
                                    editspinnerCity.setOnItemSelectedListener(UpdateProfileFragment.this);
                                    return;
                                }

                                if (type == 2) {
                                    if (beanUser.getIdDistrict() == 0) {
                                        JSONObject jO = new JSONObject();
                                        jO.put("id", 0);
                                        jO.put("name", "");
                                        regions.add(0, jO);
                                    }
                                    final SpinnerAdapter adapter = new SpinnerAdapter(getContext(), R.layout.row_spinner_update_profile, regions);
                                    editspinnerDistrict.setAdapter(adapter);
                                    // vòng lặp lấy position từng khu vực
                                    for (int i = 0; i < regions.size(); i++) {
                                        if (regions.get(i).getInt("id") == beanUser.getIdDistrict() && beanUser.getIdDistrict() != 0) {
                                            editspinnerDistrict.setSelection(i);
                                            break;
                                        }
                                    }
                                    editspinnerDistrict.setOnItemSelectedListener(UpdateProfileFragment.this);
                                    return;
                                }

                                if (type == 3) {
                                    hideProgress();
                                    final SpinnerAdapter adapter = new SpinnerAdapter(getContext(), R.layout.row_spinner_update_profile, regions);
                                    editspinnerWard.setAdapter(adapter);
                                    // vòng lặp lấy position từng khu vực
                                    for (int i = 0; i < regions.size(); i++) {
                                        if (regions.get(i).getInt("id") == beanUser.getIdWard()) {
                                            editspinnerWard.setSelection(i);
                                            break;
                                        }
                                    }
                                    editspinnerWard.setOnItemSelectedListener(UpdateProfileFragment.this);
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
            case R.id.edit_spinner_city:

                JSONObject item = (JSONObject) parent.getSelectedItem();
                if (item != null) {
                    try {
                        subscriptionWard = getRegion(2, item.getInt("id"), 0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;

            case R.id.edit_spinner_district:
                JSONObject itemCity = (JSONObject) editspinnerCity.getSelectedItem();
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Nếu nhận <ActivityResult> từ activity CHỌN ẢNH (không phân biệt từ thư viện hay chụp ảnh mới)
        // và kiểm tra có chọn ảnh hay không để xử lý hàm if này
        if (requestCode == IMAGE_REQUEST_CODE
                && resultCode == Activity.RESULT_OK
                && data != null) {
            gallery(data);
        }

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.edit_iv_avatar:
                requestPermission();
                break;
            case R.id.btn_update:
                try {
                    final String fullName = edtFullName.getText().toString().trim();
                    if (TextUtils.isEmpty(fullName)) {
                        edtFullName.requestFocus();
                        edtFullName.setError(getString(R.string.require));
                        scrv.smoothScrollTo(0, 0);
                        return;
                    }

                    final String store = edtStore.getText().toString().trim();
                    if (TextUtils.isEmpty(store)) {
                        edtStore.requestFocus();
                        edtStore.setError(getString(R.string.require));
                        scrv.smoothScrollTo(0, 0);
                        return;
                    }

                    final String address = edtAddress.getText().toString().trim();
                    if (TextUtils.isEmpty(address)) {
                        edtAddress.requestFocus();
                        edtAddress.setError(getString(R.string.require));
                        scrv.smoothScrollTo(0, 0);
                        return;
                    }

                    String phone1 = txvPhone1.getText().toString().trim();
                    if (TextUtils.isEmpty(phone1)) {
                        txvPhone1.requestFocus();
                        txvPhone1.setError(getString(R.string.require));
                        scrv.smoothScrollTo(0, 0);
                        return;
                    }


                    final int idCity = ((JSONObject) editspinnerCity.getSelectedItem()).getInt("id");
                    final int idDistrict = ((JSONObject) editspinnerDistrict.getSelectedItem()).getInt("id");

                    if (idDistrict == 0) {
                        ErrorDialogFragment errorDialogFragment = ErrorDialogFragment.newInstance("Vui lòng chọn Quận/Huyện");
                        errorDialogFragment.show(getActivity().getSupportFragmentManager(), errorDialogFragment.getClass().getName());
                        return;

                    }

                    final String phone2 = edtPhone2.getText().toString().trim();
                    final String email = edtEmail.getText().toString().trim();
                    final int idWard = ((JSONObject) editspinnerWard.getSelectedItem()).getInt("id");

                    if ((idDistrict != beanUser.getIdDistrict() && beanUser.getIdDistrict() != 0)
                            || (idWard != beanUser.getIdWard() && beanUser.getIdWard() != 0)
                            || (!address.equals(beanUser.getAddress()) && !beanUser.getAddress().equals(""))) {
                        MessageDialogFragment messageDialogFragment = MessageDialogFragment.newInstance();
                        messageDialogFragment.setMessage("Thay đổi thông tin địa lý có thể làm mất dữ liệu mua hàng và không được tham gia các chương trình phù hợp");
                        messageDialogFragment.setRunnable(new Runnable() {
                            @Override
                            public void run() {
                                update(fullName, store, address, idCity, idDistrict, idWard, phone2, email);
                            }
                        });
                        messageDialogFragment.show(getActivity().getSupportFragmentManager(), messageDialogFragment.getClass().getName());
                        return;
                    }
                    update(fullName, store, address, idCity, idDistrict, idWard, phone2, email);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.iv_check:
                if (isCheck == false) {
                    ivCheck.setImageResource(R.drawable.ic_tick_checked);
                    btnUpdate.setBackgroundResource(R.drawable.bg_main_square_box);
                    btnUpdate.setEnabled(true);
                    btnUpdate.setOnClickListener(UpdateProfileFragment.this);
                    isCheck = true;
                } else {
                    ivCheck.setImageResource(R.drawable.ic_tick_uncheck);
                    btnUpdate.setBackgroundResource(R.drawable.bg_gray_square_box);
                    btnUpdate.setOnClickListener(null);
                    btnUpdate.setEnabled(false);
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

    //regionupdateProfile
    private void update(final String fullName,
                        String shopName, final String address,
                        int idCity, int idDistrict, int idWard, final String phone, String email) {
        showProgress();
        Map map = new HashMap<>();
        map.put("fullname", fullName);
        map.put("shop_name", shopName);
        try {
            map.put("address", URLEncoder.encode(address, "UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        map.put("cities_id", idCity);
        map.put("district_id", idDistrict);
        map.put("ward_id", idWard);
        map.put("phone", phone);
        map.put("email", email);
        subscriptionUpdate = APIService.getInstance().updateProfile(map)
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
                        TextView menuName = getActivity().findViewById(R.id.menu_name);
                        TextView txtUpdateProfile = getActivity().findViewById(R.id.txt_update_profile);
                        menuName.setText(fullName);
                        menuName.setVisibility(View.VISIBLE);
                        txtUpdateProfile.setVisibility(View.GONE);
                        FragmentHelper.pop(getActivity());
                    }
                });

    }
    //endregion

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case EXTERNAL:
                boolean isPerpermissionForAllGranted = true;
                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        isPerpermissionForAllGranted = false;
                    }
                }
                if (isPerpermissionForAllGranted) {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    startActivityForResult(intent, IMAGE_REQUEST_CODE);
                }
                break;
        }
    }

    //regiongallery
    private void gallery(final Intent data) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Bitmap bmp = null;
                    Uri uri = Uri.parse(data.getDataString());
                    ImageHelper imageHelper = new ImageHelper();
                    bmp = imageHelper.uriToBitmap(uri);

                    bmp = CmmFunc.resizeBitmap(bmp, 256);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] arr = stream.toByteArray();

                    final String base64 = Base64.encodeToString(arr, Base64.DEFAULT);

                    Map map = new HashMap();
                    map.put("avatar", base64);
                    final Bitmap finalBmp = bmp;
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            iv_avatar.setImageBitmap(finalBmp);
                        }
                    });
                    APIService.getInstance().updateProfile(map)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new ISubscriber() {
                                @Override
                                public void excute(JSONObject jsonObject) {
                                    try {
                                        ImageView menuAvatar = getActivity().findViewById(R.id.menu_avatar);
                                        menuAvatar.setImageBitmap(finalBmp);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                }
                            });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }
    //endregion
}
