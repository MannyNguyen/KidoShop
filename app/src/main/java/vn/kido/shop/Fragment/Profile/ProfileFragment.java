package vn.kido.shop.Fragment.Profile;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import vn.kido.shop.Api.APIService;
import vn.kido.shop.Api.ISubscriber;
import vn.kido.shop.Bean.BeanUser;
import vn.kido.shop.Fragment.BaseFragment;
import vn.kido.shop.Helper.FragmentHelper;
import vn.kido.shop.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends BaseFragment implements View.OnClickListener {
    TextView txtUsername, txtAddress, txtCustomerLevel, txtPhone, txtPhone2, txtAddressDefault, txtEmail, txtScore, title;
    LinearLayout llKpoint, llChangeGift;
    CircleImageView ivAvatar;
    BeanUser beanUser;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance() {
        Bundle args = new Bundle();
        ProfileFragment fragment = new ProfileFragment();
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
        rootView = inflater.inflate(R.layout.fragment_profile, container, false);
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
                title = rootView.findViewById(R.id.title);
                txtUsername = rootView.findViewById(R.id.txt_username);
                txtAddress = rootView.findViewById(R.id.txt_address);
                txtPhone = rootView.findViewById(R.id.txt_phone_login);
                txtPhone2 = rootView.findViewById(R.id.txt_phone_login2);
                txtAddressDefault = rootView.findViewById(R.id.txt_address_default);
                txtEmail = rootView.findViewById(R.id.txt_email);
                llKpoint = rootView.findViewById(R.id.ll_kpoint);
                llChangeGift = rootView.findViewById(R.id.ll_change_gift);
                txtCustomerLevel = rootView.findViewById(R.id.txt_customer_level);
                ivAvatar = rootView.findViewById(R.id.iv_avatar);
                txtScore = rootView.findViewById(R.id.txt_score);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        title.setText(getString(R.string.profile_page));
                        txtCustomerLevel.setOnClickListener(ProfileFragment.this);
                        llKpoint.setOnClickListener(ProfileFragment.this);
                        llChangeGift.setOnClickListener(ProfileFragment.this);
                        getProfile();
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
        getProfile();
    }


    private void getProfile() {
        showProgress();
        APIService.getInstance().getProfile().
                subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread()).
                map(new Func1<String, Object>() {
                    @Override
                    public Object call(String s) {
                        try {
                            return new JSONObject(s);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                }).subscribe(new ISubscriber() {
            @Override
            public void done() {
                super.done();
                hideProgress();
            }

            @Override
            public void excute(JSONObject jsonObject) {
                try {
                    beanUser = new Gson().fromJson(jsonObject.getString("data"), BeanUser.class);
                    txtUsername.setText(beanUser.getFullName() + "");
                    txtAddress.setText(beanUser.getShopName() + "");
                    txtPhone.setText(beanUser.getUserName() + "");
                    txtPhone2.setText(beanUser.getPhone() + "");
                    txtEmail.setText(beanUser.getEmail() + "");

                    String address = "";

                    if (!TextUtils.isEmpty(beanUser.getAddress())) {
                        address += beanUser.getAddress() + ", ";
                    }
                    if (!TextUtils.isEmpty(beanUser.getWardName())) {
                        address += beanUser.getWardName() + ", ";
                    }
                    if (!TextUtils.isEmpty(beanUser.getDistrictName())) {
                        address += beanUser.getDistrictName() + ", ";
                    }
                    if (!TextUtils.isEmpty(beanUser.getCityName())) {
                        address += beanUser.getCityName() + ", ";
                    }
                    address = address.substring(0, address.length() - 2);
                    txtAddressDefault.setText(address);

                    txtScore.setText(beanUser.getTotalPoint() + "");

                    //region user level
//                    JSONObject userLevel = jsonObject.getJSONObject("data").getJSONObject("user_level");
//                    txtCustomerLevel.setText(userLevel.getString("name") + "");
//                    if (userLevel.getInt("vip_id") == 0) {
//                        txtCustomerLevel.setBackground(getResources().getDrawable(R.drawable.customer_new));
//                    } else if (userLevel.getInt("vip_id") == 1) {
//                        txtCustomerLevel.setBackground(getResources().getDrawable(R.drawable.customer_silver));
//                    } else if (userLevel.getInt("vip_id") == 2) {
//                        txtCustomerLevel.setBackground(getResources().getDrawable(R.drawable.customer_gold));
//                    } else if (userLevel.getInt("vip_id") == 3) {
//                        txtCustomerLevel.setBackground(getResources().getDrawable(R.drawable.customer_titan));
//                    } else {
//                        txtCustomerLevel.setBackground(getResources().getDrawable(R.drawable.customer_new));
//                    }
                    //endregion

                    if (!TextUtils.isEmpty(beanUser.getAvatar())) {
                        Picasso.get().load(beanUser.getAvatar()).placeholder(R.drawable.user_image).into((ivAvatar));
                    }
                    rootView.findViewById(R.id.edit).setOnClickListener(ProfileFragment.this);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_customer_level:
                FragmentHelper.add(RankFragment.newInstance());
                break;
            case R.id.ll_kpoint:
                FragmentHelper.add(KPointFragment.newInstance(1));
                break;
            case R.id.ll_change_gift:
                FragmentHelper.add(KPointFragment.newInstance(1));
                break;
            case R.id.edit:
                FragmentHelper.add(UpdateProfileFragment.newInstance(new Gson().toJson(beanUser)));
                break;
        }
    }
}
