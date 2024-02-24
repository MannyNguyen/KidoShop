package vn.kido.shop.Fragment.Common;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import vn.kido.shop.Adapter.BannerPagerAdapter;
import vn.kido.shop.Adapter.HomeCategoryAdapter;
import vn.kido.shop.Adapter.NewsAdapter;
import vn.kido.shop.Adapter.ProductNewAdapter;
import vn.kido.shop.Adapter.ProductPopularAdapter;
import vn.kido.shop.Api.APIService;
import vn.kido.shop.Api.ISubscriber;
import vn.kido.shop.Bean.BeanBanner;
import vn.kido.shop.Bean.BeanCategory;
import vn.kido.shop.Bean.BeanNews;
import vn.kido.shop.Bean.BeanProduct;
import vn.kido.shop.Class.StoreCart;
import vn.kido.shop.Fragment.BaseFragment;
import vn.kido.shop.Fragment.Dialog.MessageDialogFragment;
import vn.kido.shop.Fragment.News.HomeNewsFragment;
import vn.kido.shop.Fragment.OAuth.LoginFragment;
import vn.kido.shop.Fragment.Product.ListProductFragment;
import vn.kido.shop.Helper.FragmentHelper;
import vn.kido.shop.Helper.LocationHelper;
import vn.kido.shop.Helper.NotificationRouteHelper;
import vn.kido.shop.Helper.PermissionHelper;
import vn.kido.shop.Helper.StorageHelper;
import vn.kido.shop.OAuthActivity;
import vn.kido.shop.R;

import static vn.kido.shop.Class.GlobalClass.getActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends BaseFragment implements View.OnClickListener {
    //region Variables
    private final int LOCATION = 100;
    private final int GPS = 1;
    public static int BANNER = 1;
    ViewPager headerPager;
    TabLayout headerTab;
    RecyclerView recyclerCategory, recyclerProductNew, recyclerProductPopular, recyclerNews;
    List<BeanCategory> categories;
    List banners;
    Subscription subscriptionMain;
    LinearLayout llNewProduct, llHighlightProduct;
    Timer timer;

    ProductNewAdapter newProductAdapter;
    ProductPopularAdapter popularProductAdapter;
    LinkedHashMap mapNew, mapPopular;
    PermissionHelper permissionHelper;
    //endregion

    //region Init
    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance() {
        Bundle args = new Bundle();
        HomeFragment fragment = new HomeFragment();
        fragment.setArguments(args);
        return fragment;
    }
    //endregion

    //region Override

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView != null) {
            return rootView;
        }
        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (isLoaded) {
            return;
        }
        permissionHelper = new PermissionHelper(HomeFragment.this);
        threadInit = new Thread(new Runnable() {
            @Override
            public void run() {
                headerTab = rootView.findViewById(R.id.header_tab);
                headerPager = rootView.findViewById(R.id.header_pager);
                recyclerCategory = rootView.findViewById(R.id.recycler_category);
                recyclerProductNew = rootView.findViewById(R.id.recycler_new_product);
                recyclerProductPopular = rootView.findViewById(R.id.recycler_popular_product);
                recyclerNews = rootView.findViewById(R.id.recycler_popular_news);
                llNewProduct = rootView.findViewById(R.id.ll_new_product);
                llHighlightProduct = rootView.findViewById(R.id.ll_highlight_product);
                final GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2, LinearLayoutManager.HORIZONTAL, false);
                categories = new ArrayList<>();
                banners = new ArrayList();
                final HomeCategoryAdapter homeCategoryAdapter = new HomeCategoryAdapter(HomeFragment.this, recyclerCategory, categories);
                final BannerPagerAdapter bannerPagerAdapter = new BannerPagerAdapter(HomeFragment.this, banners, BANNER);
                mapNew = new LinkedHashMap();
                mapPopular = new LinkedHashMap();
                newProductAdapter = new ProductNewAdapter(HomeFragment.this, recyclerProductNew, mapNew);
                popularProductAdapter = new ProductPopularAdapter(HomeFragment.this, recyclerProductPopular, mapPopular);

                rootView.findViewById(R.id.see_more_new).setOnClickListener(HomeFragment.this);
                llNewProduct.setOnClickListener(HomeFragment.this);
                llHighlightProduct.setOnClickListener(HomeFragment.this);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        recyclerCategory.setLayoutManager(gridLayoutManager);
                        recyclerCategory.setAdapter(homeCategoryAdapter);
                        recyclerProductNew.setAdapter(newProductAdapter);
                        recyclerProductPopular.setAdapter(popularProductAdapter);
                        headerPager.setAdapter(bannerPagerAdapter);
                        headerTab.setupWithViewPager(headerPager);
                        recyclerProductNew.setLayoutManager(new LinearLayoutManager(getContext()));
                        recyclerProductNew.setItemViewCacheSize(10);
                        recyclerProductPopular.setLayoutManager(new GridLayoutManager(getContext(), 2));
                        recyclerProductPopular.setItemViewCacheSize(10);
                        recyclerNews.setLayoutManager(new LinearLayoutManager(getContext()));
                        recyclerNews.setItemViewCacheSize(10);
                        getMainScreen();
                        requestPermission();

                    }
                });
            }
        });

        threadInit.start();
        isLoaded = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            Bundle bundle = getActivity().getIntent().getExtras();
            if (bundle != null) {
                if (bundle.containsKey("deep_link_type")) {
                    new NotificationRouteHelper().route(getActivity(), bundle);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (subscriptionMain != null && subscriptionMain.isUnsubscribed()) {
            subscriptionMain.unsubscribe();
        }
    }

    //endregion

    //region Methods
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.see_more_new:
                FragmentHelper.add(HomeNewsFragment.newInstance());
                break;
            case R.id.ll_new_product:
                FragmentHelper.add(ListProductFragment.newInstance(ListProductFragment.NEW));
                break;
            case R.id.ll_highlight_product:
                FragmentHelper.add(ListProductFragment.newInstance(ListProductFragment.POPULAR));
                break;
        }
    }

    @Override
    public void manuResume() {
        super.manuResume();
        try {
            getMainScreen();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void requestPermission() {

        if (permissionHelper.requestPermission(LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, "Xin cấp quyền Vị Trí cho Kido!")) {
            if (!LocationHelper.isLocationEnabled(getContext()) && StorageHelper.getBool(StorageHelper.POPUP_LOCATION) == false) {
                MessageDialogFragment messageDialogFragment = MessageDialogFragment.newInstance();
                messageDialogFragment.setMessage("Xin vui lòng bật định vị");
                messageDialogFragment.setCancelable(false);
                messageDialogFragment.setRunnable(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                });
                messageDialogFragment.show(getActivity().getSupportFragmentManager(), messageDialogFragment.getClass().getName());
                StorageHelper.set(StorageHelper.POPUP_LOCATION, true);
            } else {
                updateLocation();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case LOCATION:
                if (!LocationHelper.isLocationEnabled(getContext()) && StorageHelper.getBool(StorageHelper.POPUP_LOCATION) == false) {
                    MessageDialogFragment messageDialogFragment = MessageDialogFragment.newInstance();
                    messageDialogFragment.setMessage("Xin vui lòng bật định vị");
                    messageDialogFragment.setCancelable(false);
                    messageDialogFragment.setRunnable(new Runnable() {
                        @Override
                        public void run() {
                            startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), GPS);
                        }
                    });
                    messageDialogFragment.show(getActivity().getSupportFragmentManager(), messageDialogFragment.getClass().getName());
                    StorageHelper.set(StorageHelper.POPUP_LOCATION, true);
                } else {
                    updateLocation();
                }
                break;
            case GPS:
                updateLocation();
                break;
        }
    }

    private void updateLocation() {
        new LocationHelper().getLastLocation(new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                if (locationResult != null) {
                    APIService.getInstance().updateLocation(1, locationResult.getLastLocation().getLatitude(), locationResult.getLastLocation().getLongitude());
                }
            }
        });
    }

    private void getMainScreen() {
        try {
            if (subscriptionMain != null && subscriptionMain.isUnsubscribed()) {
                subscriptionMain.unsubscribe();
            }
//        Realm.getDefaultInstance().executeTransaction(new Realm.Transaction() {
//            @Override
//            public void execute(Realm realm) {
//                RealmResults<RealmProduct> realmProductNews = realm.where(RealmProduct.class).contains("type", RealmProduct.NEW).findAll();
//                for (RealmProduct realmProduct : realmProductNews) {
//                    BeanProduct beanProduct = new BeanProduct();
//                    beanProduct.setId(realmProduct.getId());
//                    beanProduct.setName(realmProduct.getName());
//                    beanProduct.setPrice(realmProduct.getPrice());
//                    beanProduct.setOldPrice(realmProduct.getOldPrice());
//                    //base64
//                    beanProduct.setImage(realmProduct.getImage());
//                    mapNew.put(realmProduct.getId(), beanProduct);
//                }
//
//                RealmResults<RealmProduct> realmProductPopulars = realm.where(RealmProduct.class).contains("type", RealmProduct.POPULAR).findAll();
//                for (RealmProduct realmProduct : realmProductPopulars) {
//                    BeanProduct beanProduct = new BeanProduct();
//                    beanProduct.setId(realmProduct.getId());
//                    beanProduct.setName(realmProduct.getName());
//                    beanProduct.setPrice(realmProduct.getPrice());
//                    beanProduct.setOldPrice(realmProduct.getOldPrice());
//                    beanProduct.setImage(realmProduct.getImage());
//                    mapPopular.put(realmProduct.getId(), beanProduct);
//                }
//                getActivity().runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        newProductAdapter.notifyDataSetChanged();
//                        popularProductAdapter.notifyDataSetChanged();
//                    }
//                });
//            }
//        });
            if (!TextUtils.isEmpty(StorageHelper.get(StorageHelper.HOME_SCREEN))) {
                parseData(new JSONObject(StorageHelper.get(StorageHelper.HOME_SCREEN)));
            }
          
            subscriptionMain = APIService.getInstance().getMainScreen()
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
                            StorageHelper.set(StorageHelper.HOME_SCREEN, jsonObject.toString());
                            parseData(jsonObject);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //endregion

    private Thread autoChangeSlide(final ViewPager pager) {
        if (pager.getChildCount() < 2) {
            return null;
        }
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    int position = 0;
                    if (pager.getCurrentItem() < pager.getChildCount() - 1) {
                        position = pager.getCurrentItem() + 1;
                    }
                    final int finalPosition = position;
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pager.setCurrentItem(finalPosition);
                            //autoChangeSlide(pager);
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        return thread;
    }

    private void parseData(JSONObject jsonObject) {
        try {
            mapNew.clear();
            mapPopular.clear();
            banners.clear();
            categories.clear();
            Gson gson = new Gson();
            JSONObject data = jsonObject.getJSONObject("data");

            final JSONArray topBanners = data.getJSONArray("top_banners");
            for (int i = 0; i < topBanners.length(); i++) {
                final BeanBanner beanBanner = gson.fromJson(topBanners.getString(i), BeanBanner.class);
                banners.add(beanBanner);
            }
            headerPager.getAdapter().notifyDataSetChanged();

            JSONArray productTypes = data.getJSONArray("product_types");
            for (int i = 0; i < productTypes.length(); i++) {
                JSONArray arr = productTypes.getJSONArray(i);
                for (int j = 0; j < arr.length(); j++) {
                    BeanCategory beanCategory = gson.fromJson(arr.getString(j), BeanCategory.class);
                    categories.add(beanCategory);

                }
            }
            recyclerCategory.getAdapter().notifyDataSetChanged();

            JSONArray arrProductNew = data.getJSONArray("product_new");

            for (int i = 0; i < arrProductNew.length(); i++) {
                final BeanProduct beanProduct = gson.fromJson(arrProductNew.getString(i), BeanProduct.class);
                mapNew.put(beanProduct.getId(), beanProduct);
            }
//                            Realm.getDefaultInstance().executeTransactionAsync(new Realm.Transaction() {
//                                @Override
//                                public void execute(Realm realm) {
//                                    for (Object bean : mapNew.values()) {
//                                        RealmProduct realmProduct = RealmProduct.create((BeanProduct) bean, RealmProduct.NEW);
//                                        realm.insertOrUpdate(realmProduct);
//                                    }
//                                }
//                            });
            recyclerProductNew.getAdapter().notifyDataSetChanged();

            JSONArray arrProductPopular = data.getJSONArray("product_hot");

            for (int i = 0; i < arrProductPopular.length(); i++) {
                BeanProduct beanProduct = gson.fromJson(arrProductPopular.getString(i), BeanProduct.class);
                mapPopular.put(beanProduct.getId(), beanProduct);
            }
//                            Realm.getDefaultInstance().executeTransactionAsync(new Realm.Transaction() {
//                                @Override
//                                public void execute(Realm realm) {
//                                    for (Object bean : mapPopular.values()) {
//                                        RealmProduct realmProduct = RealmProduct.create((BeanProduct) bean, RealmProduct.POPULAR);
//                                        realm.insertOrUpdate(realmProduct);
//                                    }
//                                }
//                            });
            recyclerProductPopular.getAdapter().notifyDataSetChanged();

            Type listType = new TypeToken<List<BeanNews>>() {
            }.getType();
            final List<BeanNews> arrNews = new Gson().fromJson(data.getString("news_hot"), listType);
            NewsAdapter newsAdapter = new NewsAdapter(HomeFragment.this, recyclerNews, arrNews, 1);
            recyclerNews.setAdapter(newsAdapter);

            if (timer != null) {
                timer.cancel();
            }
            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    try {
                        autoChangeSlide(headerPager);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, 5000, 5000);

            if (StoreCart.getProducts().size() == 0) {
                APIService.getInstance().getNumberCart()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new ISubscriber() {
                            @Override
                            public void excute(JSONObject jsonObject) {
                                try {
                                    JSONObject data = jsonObject.getJSONObject("data");

                                    Type listType = new TypeToken<List<Integer>>() {
                                    }.getType();
                                    final List<Integer> products = new Gson().fromJson(data.getString("product_ids"), listType);
                                    final List<Integer> combos = new Gson().fromJson(data.getString("combo_ids"), listType);
                                    StoreCart.setProducts(products);
                                    StoreCart.setCombos(combos);
                                    for (Object beanProduct : newProductAdapter.map.values().toArray()) {
                                        if (StoreCart.getProducts().contains(((BeanProduct) beanProduct).getId())) {
                                            ((BeanProduct) beanProduct).setInCart(true);
                                        }
                                    }
                                    newProductAdapter.notifyDataSetChanged();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });

            } else {
                for (Object beanProduct : newProductAdapter.map.values().toArray()) {
                    if (StoreCart.getProducts().contains(((BeanProduct) beanProduct).getId())) {
                        ((BeanProduct) beanProduct).setInCart(true);
                    }
                }
                newProductAdapter.notifyDataSetChanged();
            }
            View loginGuest = rootView.findViewById(R.id.guest);
            if (data.getBoolean("is_guest")) {
                loginGuest.setVisibility(View.VISIBLE);
                loginGuest.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FragmentHelper.add(LoginFragment.newInstance());
                    }
                });
            } else {
                loginGuest.setVisibility(View.GONE);
                loginGuest.setOnClickListener(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
