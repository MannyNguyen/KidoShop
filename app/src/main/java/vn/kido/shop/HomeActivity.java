package vn.kido.shop;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import javax.sql.CommonDataSource;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import vn.kido.shop.Adapter.MenuAdapter;
import vn.kido.shop.Api.APIService;
import vn.kido.shop.Api.ISubscriber;
import vn.kido.shop.Bean.BeanUser;
import vn.kido.shop.Class.CmmFunc;
import vn.kido.shop.Class.GlobalClass;
import vn.kido.shop.Fragment.BaseFragment;
import vn.kido.shop.Fragment.Cart.VerifyCartFragment;
import vn.kido.shop.Fragment.Common.HomeFragment;
import vn.kido.shop.Fragment.Dialog.ErrorDialogFragment;
import vn.kido.shop.Fragment.News.DetailNewsFragment;
import vn.kido.shop.Fragment.OAuth.ChooseServerFragment;
import vn.kido.shop.Fragment.OAuth.LoginFragment;
import vn.kido.shop.Fragment.OAuth.RegisterFragment;
import vn.kido.shop.Fragment.Order.Follow.FollowOrderFragment;
import vn.kido.shop.Fragment.Order.History.HistoryOrderFragment;
import vn.kido.shop.Fragment.Order.Receive.VerifyReceiveOrderFragment;
import vn.kido.shop.Fragment.Profile.ProfileFragment;
import vn.kido.shop.Fragment.Profile.UpdateProfileFragment;
import vn.kido.shop.Helper.AccountHelper;
import vn.kido.shop.Helper.FragmentHelper;
import vn.kido.shop.Helper.StorageHelper;

import static vn.kido.shop.Class.GlobalClass.getActivity;

public class HomeActivity extends BaseActivity implements View.OnClickListener {

    public static int WINDOW_HEIGHT;
    public static int WINDOW_WIDTH;
    TextView name;
    BeanUser beanUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        GlobalClass.setActivity(HomeActivity.this);
        CmmFunc.hideKeyboard(this);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        WINDOW_HEIGHT = displayMetrics.heightPixels;
        WINDOW_WIDTH = displayMetrics.widthPixels;
        setMenu();
        getProfile();
        FragmentHelper.add(HomeFragment.newInstance());
        updateDeviceToken();

        TextView txtServer = findViewById(R.id.txt_server);
        TextView logout = findViewById(R.id.btn_logout);
        switch (StorageHelper.get(StorageHelper.SERVER)) {
            case ChooseServerFragment.DEV:
                txtServer.setVisibility(View.VISIBLE);
                logout.setVisibility(View.VISIBLE);
                txtServer.setText("DEV");
                break;
            case ChooseServerFragment.TEST:
                txtServer.setVisibility(View.VISIBLE);
                logout.setVisibility(View.VISIBLE);
                txtServer.setText("TEST");
                break;
            case ChooseServerFragment.REAL:
                logout.setVisibility(View.GONE);
                txtServer.setVisibility(View.GONE);
                break;
        }
        if (logout.getVisibility() == View.VISIBLE) {
            logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CmmFunc.hideKeyboard(getActivity());
                    if (AccountHelper.isLogin()) {
                        StorageHelper.set(StorageHelper.DEVICE_ID, "");
                        StorageHelper.set(StorageHelper.USERNAME, "");
                        StorageHelper.set(StorageHelper.TOKEN, "");
                        getAccountGuest();
                    } else {
                        FragmentHelper.add(LoginFragment.newInstance(true));
                    }
                }
            });
        }
    }

    private void getAccountGuest() {
        APIService.getInstance().getAccountGuest()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ISubscriber() {
                    @Override
                    public void excute(JSONObject jsonObject) {
                        try {
                            JSONObject data = jsonObject.getJSONObject("data");
                            StorageHelper.set(StorageHelper.DEVICE_ID, data.getString("deviceid"));
                            StorageHelper.set(StorageHelper.USERNAME, data.getString("username"));
                            StorageHelper.set(StorageHelper.TOKEN, data.getString("token"));
                            FragmentHelper.add(LoginFragment.newInstance(true));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }


    private void setMenu() {
        try {
            RecyclerView recycler = findViewById(R.id.recycler_menu);
            MenuAdapter menuAdapter = new MenuAdapter(HomeActivity.this, recycler);
            recycler.setLayoutManager(new LinearLayoutManager(HomeActivity.this));
            recycler.setAdapter(menuAdapter);
            findViewById(R.id.menu_profile_container).setOnClickListener(HomeActivity.this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getProfile() {
        APIService.getInstance().getProfile()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ISubscriber() {
                    @Override
                    public void excute(JSONObject jsonObject) {
                        try {
                            beanUser = new Gson().fromJson(jsonObject.getString("data"), BeanUser.class);
                            name = findViewById(R.id.menu_name);
                            TextView txtUpdateProfile = findViewById(R.id.txt_update_profile);

                            if (beanUser.getFullName().equals("")) {
                                name.setVisibility(View.GONE);
                                txtUpdateProfile.setVisibility(View.VISIBLE);
                            } else {
                                name.setVisibility(View.VISIBLE);
                                txtUpdateProfile.setVisibility(View.GONE);
                            }
                            name.setText(beanUser.getFullName() + "");
                            TextView menuCustomerLevel = findViewById(R.id.menu_customer_level);
                            JSONObject userLevel = jsonObject.getJSONObject("data").getJSONObject("user_level");
                            menuCustomerLevel.setText(userLevel.getString("name") + "");

                            int level = userLevel.getInt("vip_id");
                            if (level == 0) {
                                menuCustomerLevel.setBackground(getResources().getDrawable(R.drawable.customer_new));
                                menuCustomerLevel.setText(userLevel.getString("name") + "");
                            } else if (level == 1) {
                                menuCustomerLevel.setBackground(getResources().getDrawable(R.drawable.customer_silver));
                                menuCustomerLevel.setText(getString(R.string.customer) + " " + userLevel.getString("name") + "");
                            } else if (level == 2) {
                                menuCustomerLevel.setBackground(getResources().getDrawable(R.drawable.customer_gold));
                                menuCustomerLevel.setText(getString(R.string.customer) + " " + userLevel.getString("name") + "");
                            } else if (level == 3) {
                                menuCustomerLevel.setBackground(getResources().getDrawable(R.drawable.customer_titan));
                                menuCustomerLevel.setText(getString(R.string.customer) + " " + userLevel.getString("name") + "");
                            } else {
                                menuCustomerLevel.setBackground(getResources().getDrawable(R.drawable.customer_new));
                                menuCustomerLevel.setText(userLevel.getString("name") + "");
                            }

                            if (!TextUtils.isEmpty(beanUser.getAvatar())) {
                                int size = CmmFunc.convertDpToPx(HomeActivity.this, 240);
                                Picasso.get().load(beanUser.getAvatar()).placeholder(R.drawable.left_avatar).resize(size, size).centerInside().into((ImageView) findViewById(R.id.menu_avatar));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            return;
        }

        Fragment fragment = FragmentHelper.getActiveFragment(HomeActivity.this);
        if (fragment instanceof HomeFragment) {
            finishAffinity();
            return;
        }

        if (fragment instanceof VerifyReceiveOrderFragment
                || fragment instanceof VerifyCartFragment) {
            return;
        }

        if (fragment instanceof FollowOrderFragment) {
            FragmentHelper.pop(HomeActivity.this, HomeFragment.class);
            return;
        }

        if (fragment instanceof HistoryOrderFragment) {
            FragmentHelper.pop(HomeActivity.this, HomeFragment.class);
            return;
        }

        if (fragment instanceof DetailNewsFragment) {
            ((DetailNewsFragment) fragment).onBack();
            return;
        }

        if (fragment instanceof LoginFragment) {
            LoginFragment loginFragment = (LoginFragment) fragment;
            if (loginFragment.getArguments().containsKey("reload")) {
                StorageHelper.set(StorageHelper.TOKEN, "");
                StorageHelper.set(StorageHelper.USERNAME, "");
                StorageHelper.set(StorageHelper.DEVICE_ID, "");
                Intent intent = new Intent(getActivity(), MainActivity.class);
                getActivity().startActivity(intent);
                getActivity().finish();
            } else {
                FragmentHelper.pop(getActivity());
            }
            return;
        }
//        if (fragment instanceof ProgramCartFragment) {
//            return;
//        }

        FragmentHelper.pop(HomeActivity.this);
    }

    private void updateDeviceToken() {
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String token = instanceIdResult.getToken();
                if (token != null) {
                    APIService.getInstance().updateDeviceToken(token)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<String>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            String str = "";
                        }

                        @Override
                        public void onNext(String s) {
                            String str = s;
                        }
                    });
                }
            }
        });
    }

    public static void setStatusBarGradiant(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            Drawable background = activity.getResources().getDrawable(R.drawable.gradient_main);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor(activity.getResources().getColor(android.R.color.transparent));
//            window.setNavigationBarColor(activity.getResources().getColor(android.R.color.transparent));
            window.setBackgroundDrawable(background);
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.menu_profile_container:
                if (!AccountHelper.isLogin()) {
                    ErrorDialogFragment errorDialogFragment = ErrorDialogFragment.newInstance(getString(R.string.require_login), ISubscriber.LOGIN);
                    errorDialogFragment.show(GlobalClass.getActivity().getSupportFragmentManager(), ErrorDialogFragment.class.getName());
                    errorDialogFragment.setRunnable(new Runnable() {
                        @Override
                        public void run() {
                            FragmentHelper.add(LoginFragment.newInstance());
                        }
                    });
                    return;
                }
                if (name.getText().toString().equals("")) {
                    findViewById(R.id.menu_profile_container).setOnClickListener(null);
                    ((DrawerLayout) findViewById(R.id.drawer_layout)).closeDrawer(Gravity.START);
                    FragmentHelper.add(UpdateProfileFragment.newInstance(new Gson().toJson(beanUser)));
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(1000);
                                findViewById(R.id.menu_profile_container).setOnClickListener(HomeActivity.this);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                } else {
                    findViewById(R.id.menu_profile_container).setOnClickListener(null);
                    ((DrawerLayout) findViewById(R.id.drawer_layout)).closeDrawer(Gravity.START);
                    FragmentHelper.add(ProfileFragment.newInstance());
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(1000);
                                findViewById(R.id.menu_profile_container).setOnClickListener(HomeActivity.this);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }


                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            BaseFragment baseFragment = (BaseFragment) FragmentHelper.getActiveFragment(HomeActivity.this);
            if (baseFragment != null) {
                baseFragment.getUnSeen();
                View cart = baseFragment.getView().findViewById(R.id.cart);
                if (cart != null) {
                    baseFragment.getNumberCart();
                }
                View notify = baseFragment.getView().findViewById(R.id.notify);
                if (notify != null) {
                    baseFragment.getUnSeen();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
