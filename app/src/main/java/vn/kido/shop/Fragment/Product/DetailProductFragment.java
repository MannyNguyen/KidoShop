package vn.kido.shop.Fragment.Product;


import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import vn.kido.shop.Adapter.BannerProductAdapter;
import vn.kido.shop.Adapter.DetailProductQuantityAdapter;
import vn.kido.shop.Adapter.ProductEventAdapter;
import vn.kido.shop.Adapter.SuggestProductAdapter;
import vn.kido.shop.Api.APIService;
import vn.kido.shop.Api.ISubscriber;
import vn.kido.shop.Bean.BeanAttribute;
import vn.kido.shop.Bean.BeanBanner;
import vn.kido.shop.Bean.BeanProduct;
import vn.kido.shop.Bean.BeanProgram;
import vn.kido.shop.Class.CmmFunc;
import vn.kido.shop.Class.StoreCart;
import vn.kido.shop.Fragment.BaseFragment;
import vn.kido.shop.Fragment.Cart.CartFragment;
import vn.kido.shop.Fragment.Dialog.DialogDetailProductFragment;
import vn.kido.shop.Fragment.Dialog.ErrorDialogFragment;
import vn.kido.shop.Fragment.OAuth.LoginFragment;
import vn.kido.shop.Fragment.Program.ProductProgramFragment;
import vn.kido.shop.Helper.AccountHelper;
import vn.kido.shop.Helper.FragmentHelper;
import vn.kido.shop.R;

import static vn.kido.shop.Class.GlobalClass.getActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailProductFragment extends BaseFragment implements View.OnClickListener {
    public boolean isUpdateQuantity = true;
    public static int BANNER_TITLE = 2;
    TabLayout tab;
    ViewPager pager;
    TextView txtProductName, txtInfo, txtElement, txtNutritionInfo,
            txtManual, txtStorageCondition, seeMore, txtRequireLogin;
    public TextView txtTotalPayment, txtPrice, txtMiddlePrice, txtMinPrice, txtMaxUnit, txtMinUnit, txtMiddleUnit;
    public int numProduct;
    LinearLayout llSameProduct, llMoreInfo, llEvent, llMaxUnit, llMiddleUnit, llMinUnit;
    Button btnAddToCart;
    RecyclerView recyclerSame, recyclerEvent, recyclerQuantity;
    List<BeanBanner> eventBanners;
    FrameLayout frameEvent;
    View cart;
    public BeanProduct product;
    BeanAttribute attribute;
    public DetailProductQuantityAdapter detailProductQuantityAdapter;
    public List<BeanAttribute> attributes;
    LinearLayout nutrition2Container;
    boolean isShow;

    public DetailProductFragment() {
        // Required empty public constructor
    }

    public static DetailProductFragment newInstance(int id) {
        Bundle args = new Bundle();
        args.putInt("id", id);
        DetailProductFragment fragment = new DetailProductFragment();
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
        rootView = inflater.inflate(R.layout.fragment_detail_product, container, false);
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
                cart = rootView.findViewById(R.id.cart);
                tab = rootView.findViewById(R.id.tab);
                pager = rootView.findViewById(R.id.pager);
                txtProductName = rootView.findViewById(R.id.txt_product_name);
                txtPrice = rootView.findViewById(R.id.txt_price);
                txtMiddlePrice = rootView.findViewById(R.id.txt_middle_price);
                txtMinPrice = rootView.findViewById(R.id.txt_min_price);
                txtMinUnit = rootView.findViewById(R.id.txt_min_unit);
                txtMaxUnit = rootView.findViewById(R.id.txt_max_unit);
                txtMiddleUnit = rootView.findViewById(R.id.txt_middle_unit);
                txtInfo = rootView.findViewById(R.id.txt_info);
                recyclerSame = rootView.findViewById(R.id.recycler_same);
                btnAddToCart = rootView.findViewById(R.id.btn_add_to_cart);
                txtElement = rootView.findViewById(R.id.txt_element);
                txtNutritionInfo = rootView.findViewById(R.id.txt_nutrition_info);
                nutrition2Container = rootView.findViewById(R.id.nutrition_2_container);
                txtManual = rootView.findViewById(R.id.txt_manual);
                txtStorageCondition = rootView.findViewById(R.id.txt_storage_condition);
                llSameProduct = rootView.findViewById(R.id.ll_same_product);
                seeMore = rootView.findViewById(R.id.see_more);
                llMoreInfo = rootView.findViewById(R.id.ll_more_info);
                recyclerEvent = rootView.findViewById(R.id.recycler_event);
                recyclerQuantity = rootView.findViewById(R.id.recycler_quantity);
                frameEvent = rootView.findViewById(R.id.frame_event);
                txtTotalPayment = rootView.findViewById(R.id.txt_total_payment);
                llEvent = rootView.findViewById(R.id.ll_event);
                llMaxUnit = rootView.findViewById(R.id.ll_max_unit);
                llMiddleUnit = rootView.findViewById(R.id.ll_middle_unit);
                llMinUnit = rootView.findViewById(R.id.ll_min_unit);
                txtRequireLogin = rootView.findViewById(R.id.txt_require_login);
                eventBanners = new ArrayList<>();

                final TextView title = rootView.findViewById(R.id.title);
                final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                final LinearLayoutManager manager = new LinearLayoutManager(getContext());
                final LinearLayoutManager layoutQuantity = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        title.setText(getString(R.string.detail_product));
                        recyclerSame.setLayoutManager(layoutManager);
                        recyclerEvent.setLayoutManager(manager);
                        recyclerQuantity.setLayoutManager(layoutQuantity);
                        //getDetail(getArguments().getInt("id"), getArguments().getInt("variation_id"));
                        getDetail(getArguments().getInt("id"));
                        btnAddToCart.setOnClickListener(DetailProductFragment.this);
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
        if (isUpdateQuantity) {
            //getDetail2(getArguments().getInt("id"));
            //   detailProductQuantityAdapter.notifyDataSetChanged();
        }

        isUpdateQuantity = true;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void getDetail(int id) {
        showProgress();
        APIService.getInstance().getDetailProduct(id)
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
                            product = new Gson().fromJson(data.getString("product_info"), BeanProduct.class);

                            BannerProductAdapter bannerProductAdapter = new BannerProductAdapter(DetailProductFragment.this, product.getImages(), getArguments().getInt("id"));
                            pager.setAdapter(bannerProductAdapter);
                            tab.setupWithViewPager(pager);

                            txtProductName.setText(product.getName() + "");

                            txtInfo.setText(product.getDescription() + "");

                            if (product.getElement().equals("")) {
                                rootView.findViewById(R.id.element_container).setVisibility(View.GONE);
                            } else {
                                rootView.findViewById(R.id.element_container).setVisibility(View.VISIBLE);
                                txtElement.setText(product.getElement() + "");
                            }

                            if (product.getNutrition().equals("")) {
                                rootView.findViewById(R.id.nutrition_container).setVisibility(View.GONE);
                            } else {
                                rootView.findViewById(R.id.nutrition_container).setVisibility(View.VISIBLE);
                                txtNutritionInfo.setText(product.getNutrition() + "");
                            }

                            if (product.getNutritions().size() == 0) {
                                nutrition2Container.setVisibility(View.GONE);
                            } else {
                                nutrition2Container.setVisibility(View.VISIBLE);
                                for (BeanProduct.BeanNutritions beanAttribute : product.getNutritions()) {
                                    View itemView = LayoutInflater.from(getContext()).inflate(R.layout.row_nutri_product, null, false);
                                    TextView left = itemView.findViewById(R.id.left);
                                    TextView right = itemView.findViewById(R.id.right);
                                    left.setText(beanAttribute.getLeft() + "");
                                    right.setText(beanAttribute.getRight() + "");
                                    nutrition2Container.addView(itemView);
                                }
                            }

                            if (product.getUses().equals("")) {
                                rootView.findViewById(R.id.manual_container).setVisibility(View.GONE);
                            } else {
                                rootView.findViewById(R.id.manual_container).setVisibility(View.VISIBLE);
                                txtManual.setText(product.getUses() + "");
                            }

                            if (product.getStorageCondition().equals("")) {
                                rootView.findViewById(R.id.storage_container).setVisibility(View.GONE);
                            } else {
                                rootView.findViewById(R.id.storage_container).setVisibility(View.VISIBLE);
                                txtStorageCondition.setText(product.getStorageCondition() + "");
                            }


                            //region events
                            Type typeEvents = new TypeToken<List<BeanProgram>>() {
                            }.getType();
                            if (data.getJSONArray("event").length() == 0) {
                                frameEvent.setVisibility(View.GONE);
                            }
                            String events = data.getString("event");
                            List<BeanProgram> listEvent = new Gson().fromJson(events, typeEvents);
                            ProductEventAdapter productEventAdapter = new ProductEventAdapter(listEvent,
                                    recyclerEvent, DetailProductFragment.this);
                            recyclerEvent.setAdapter(productEventAdapter);

                            frameEvent.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    FragmentHelper.add(ProductProgramFragment.newInstance(getArguments().getInt("id"), product.getName() + ""));
                                }
                            });

                            llEvent.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (!AccountHelper.isLogin()){
                                        ErrorDialogFragment errorDialogFragment = ErrorDialogFragment.newInstance(getString(R.string.request_login), LOGIN);
                                        errorDialogFragment.show(getActivity().getSupportFragmentManager(), ErrorDialogFragment.class.getName());
                                        errorDialogFragment.setRunnable(new Runnable() {
                                            @Override
                                            public void run() {
                                                FragmentHelper.add(LoginFragment.newInstance());
                                            }
                                        });
                                        return;
                                    }
                                    FragmentHelper.add(ProductProgramFragment.newInstance(getArguments().getInt("id"), product.getName() + ""));
                                }
                            });
                            //endregion

                            //region suggest product
                            Type typeProductLikes = new TypeToken<List<BeanProduct>>() {
                            }.getType();
                            if (data.getJSONArray("product_likes").length() == 0) {
                                llSameProduct.setVisibility(View.GONE);
                            }
                            String productLikes = data.getString("product_likes");
                            List<BeanProduct> listProductLike = new Gson().fromJson(productLikes, typeProductLikes);
                            SuggestProductAdapter adapter = new SuggestProductAdapter(DetailProductFragment.this, recyclerSame, listProductLike);
                            recyclerSame.setAdapter(adapter);
                            //endregion

                            //region quantity

                            for (int i = 0; i < product.getAttributes().size(); i++) {
                                attribute = product.getAttributes().get(i);
                                attribute.setMaxUnit(quantityAtt(attribute, product.getMinUnit()));
                                switch (i) {
                                    case 0:
                                        txtPrice.setVisibility(View.VISIBLE);
                                        txtMaxUnit.setVisibility(View.VISIBLE);
                                        txtPrice.setText(CmmFunc.formatMoney(attribute.getMoney(), false));
                                        txtMaxUnit.setText("/ " + attribute.getNamePerUnit());
                                        if (attribute.getMoney() == 0) {
                                            txtPrice.setVisibility(View.GONE);
                                            txtMaxUnit.setVisibility(View.GONE);
                                            txtRequireLogin.setVisibility(View.VISIBLE);
                                        }
                                        break;
                                    case 1:
                                        txtMiddlePrice.setVisibility(View.VISIBLE);
                                        txtMiddleUnit.setVisibility(View.VISIBLE);
                                        txtMiddlePrice.setText(CmmFunc.formatMoney(attribute.getMoney(), false));
                                        txtMiddleUnit.setText("/ " + attribute.getNamePerUnit());
                                        if (attribute.getMoney() == 0) {
                                            txtMiddlePrice.setVisibility(View.GONE);
                                            txtMiddleUnit.setVisibility(View.GONE);
                                            txtRequireLogin.setVisibility(View.VISIBLE);
                                        }
                                        break;
                                    case 2:
                                        txtMinPrice.setVisibility(View.VISIBLE);
                                        txtMinUnit.setVisibility(View.VISIBLE);
                                        txtMinPrice.setText(CmmFunc.formatMoney(attribute.getMoney(), false));
                                        txtMinUnit.setText("/ " + attribute.getNamePerUnit());
                                        if (attribute.getMoney() == 0) {
                                            txtMinPrice.setVisibility(View.GONE);
                                            txtMinUnit.setVisibility(View.GONE);
                                            txtRequireLogin.setVisibility(View.VISIBLE);
                                        }
                                        break;
                                }
                            }

                            detailProductQuantityAdapter = new DetailProductQuantityAdapter(product.getAttributes(), DetailProductFragment.this, recyclerQuantity);
                            recyclerQuantity.setAdapter(detailProductQuantityAdapter);

                            seeMore.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (isShow == false) {
                                        llMoreInfo.setVisibility(View.VISIBLE);
                                        seeMore.setText(getString(R.string.collipped));
                                        isShow = true;
                                    } else {
                                        llMoreInfo.setVisibility(View.GONE);
                                        seeMore.setText(getString(R.string.see_more));
                                        isShow = false;
                                    }

                                }
                            });
                            //endregion

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    //reload number quantity when user change quantity from another screen
    private void getDetail2(int id) {
        showProgress();
        APIService.getInstance().getDetailProduct(id)
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
                            product = new Gson().fromJson(data.getString("product_info"), BeanProduct.class);
                            detailProductQuantityAdapter = new DetailProductQuantityAdapter(product.getAttributes(), DetailProductFragment.this, recyclerQuantity);
                            recyclerQuantity.setAdapter(detailProductQuantityAdapter);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void addProductToCart(int id, int numProduct) {
        //add product to cart, typeUnit = 0 if in this screen, user can choose number quantity.
        JSONArray arrayAtt = new JSONArray();
        try {
            for (int i = 0; i < product.getAttributes().size(); i++) {
                JSONObject items = new JSONObject();
                items.put("id", product.getAttributes().get(i).getId());
                items.put("quantity", product.getAttributes().get(i).getValue());
                arrayAtt.put(items);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        APIService.getInstance().addProductToCart(id, numProduct, 0, arrayAtt)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ISubscriber() {
                    @Override
                    public void done() {
                        super.done();
                    }

                    @Override
                    public void excute(JSONObject jsonObject) {
                        CmmFunc.showAnim(cart);
                        ((BaseFragment) DetailProductFragment.this).getNumberCart();
                    }
                });
    }

    private String quantityAtt(BeanAttribute attribute, String minUnit) {
        if (attribute.getQuantity() == 1) {
            return attribute.getName();
        }
        return attribute.getName() + " ( " + attribute.getQuantity() + " " + minUnit + " )";
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_add_to_cart:
                try {
                    btnAddToCart.setOnClickListener(null);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            btnAddToCart.setOnClickListener(DetailProductFragment.this);
                        }
                    }).start();
                    if (StoreCart.getProducts().contains(getArguments().getInt("id"))) {
                        final DialogDetailProductFragment messageDialogFragment = DialogDetailProductFragment.newInstance();
                        messageDialogFragment.setMessage(getResources().getString(R.string.exist_in_cart));
                        messageDialogFragment.setTouchOutSide(true);
                        messageDialogFragment.setCancelable(true);
                        messageDialogFragment.setRunnable(new Runnable() {
                            @Override
                            public void run() {
                                messageDialogFragment.dismiss();
                                FragmentHelper.add(CartFragment.newInstance(getArguments().getInt("id")));
                            }
                        });
                        messageDialogFragment.show(getActivity().getSupportFragmentManager(), messageDialogFragment.getClass().getName());
                        return;
                    }
                    addProductToCart(getArguments().getInt("id"), numProduct);
                } catch (Resources.NotFoundException e) {
                    e.printStackTrace();
                }
                break;
        }
    }
}
