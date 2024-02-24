package vn.kido.shop.Fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import vn.kido.shop.Api.APIService;
import vn.kido.shop.Api.ISubscriber;
import vn.kido.shop.Bean.BeanProduct;
import vn.kido.shop.Class.CmmFunc;
import vn.kido.shop.Class.StoreCart;
import vn.kido.shop.Fragment.Cart.CartFragment;

import vn.kido.shop.Fragment.Common.HomeFragment;
import vn.kido.shop.Fragment.Notify.DetailNotifyFragment;
import vn.kido.shop.Fragment.Notify.HomeNotifyFragment;

import vn.kido.shop.Fragment.Search.HomeSearchFragment;
import vn.kido.shop.Fragment.Search.SearchLiveFragment;
import vn.kido.shop.Helper.FragmentHelper;
import vn.kido.shop.R;

/**
 * A simple {@link Fragment} subclass.
 */
public abstract class BaseFragment extends Fragment {

    public View rootView;
    public boolean isLoaded;
    public Thread threadInit;
    View layoutProgress;

    public BaseFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    public void manuResume() {
        checkDrawLayout();
        View cart = rootView.findViewById(R.id.cart);
        if (cart != null) {
            getNumberCart();
        }
        View notify = rootView.findViewById(R.id.notify);
        if (notify != null) {
            getUnSeen();
        }
        CmmFunc.hideKeyboard(getActivity());
    }

    private void checkDrawLayout() {
        final DrawerLayout drawerLayout = getActivity().findViewById(R.id.drawer_layout);
        if (drawerLayout == null) {
            return;
        }
        View menu = rootView.findViewById(R.id.menu);
        if (menu != null) {
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            });
        } else {
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }


    }

    @Override
    public void onStart() {
        super.onStart();
        if (rootView != null) {
            rootView.setClickable(true);
            rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CmmFunc.hideKeyboard(getActivity());
                }
            });
            layoutProgress = rootView.findViewById(R.id.layout_progress);
            //Find back button on toolbar
            View back = rootView.findViewById(R.id.back);
            if (back != null) {
                back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FragmentHelper.pop(getActivity());
                    }
                });
            }

            View cart = rootView.findViewById(R.id.cart);
            if (cart != null) {
                getNumberCart();
            }

            View search = rootView.findViewById(R.id.search);
            if (search != null) {
                search.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (FragmentHelper.getActiveFragment(getActivity()) instanceof HomeSearchFragment) {
                            FragmentHelper.pop(getActivity());
                            return;
                        }
                        FragmentHelper.add(SearchLiveFragment.newInstance());
                        //FragmentHelper.replace(SearchLiveFragment.newInstance());
                    }
                });
            }
            View notify = rootView.findViewById(R.id.notify);
            if (notify != null) {
                getUnSeen();
            }

            View llIconHome = rootView.findViewById(R.id.ll_icon_home);
            if (llIconHome != null) {
                llIconHome.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (FragmentHelper.getActiveFragment(getActivity()) instanceof HomeFragment) {
                            return;
                        }
                        FragmentHelper.pop(getActivity(), HomeFragment.class);

                    }
                });
            }

            checkDrawLayout();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
//        CmmFunc.hideKeyboard(getActivity());
//        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);


    }

    public void showProgress() {
        try {
            CmmFunc.hideKeyboard(getActivity());
            if (layoutProgress == null) {
                return;
            }
            if (layoutProgress.getVisibility() == View.VISIBLE) {
                return;
            }
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    layoutProgress.setVisibility(View.VISIBLE);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void hideProgress() {
        try {
            if (layoutProgress == null) {
                return;
            }
            if (layoutProgress.getVisibility() == View.GONE) {
                return;
            }
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    layoutProgress.setVisibility(View.GONE);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getNumberCart() {
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

                            int total = data.getInt("total");
                            View cart = rootView.findViewById(R.id.cart);
                            ImageButton imbCart = rootView.findViewById(R.id.imb_cart);
                            TextView numberCart = rootView.findViewById(R.id.number_cart);
                            if (total > 0) {
                                imbCart.setImageResource(R.drawable.ic_cart_white);
                                numberCart.setVisibility(View.VISIBLE);
                                if (total > 100) {
                                    numberCart.setText("99+");
                                } else {
                                    numberCart.setText(total + "");
                                }

                                cart.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        FragmentHelper.add(CartFragment.newInstance());
                                    }
                                });
                            } else {
                                numberCart.setVisibility(View.GONE);
                                numberCart.setText("");
                                imbCart.setImageResource(R.drawable.ic_cart_gray);
                                cart.setOnClickListener(null);
                            }


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    public void getUnSeen() {
        APIService.getInstance().getUnSeen()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ISubscriber() {
                    @Override
                    public void excute(JSONObject jsonObject) {
                        try {
                            JSONObject data = jsonObject.getJSONObject("data");
                            TextView numNoti = rootView.findViewById(R.id.number_noti);
                            if (data.getInt("num") == 0) {
                                numNoti.setVisibility(View.GONE);
                            } else {
                                numNoti.setVisibility(View.VISIBLE);
                                numNoti.setText(data.getInt("num") + "");
                            }
                            final View notify = rootView.findViewById(R.id.notify);
                            notify.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (FragmentHelper.getActiveFragment(getActivity()) instanceof HomeNotifyFragment || FragmentHelper.getActiveFragment(getActivity()) instanceof DetailNotifyFragment) {
                                        return;
                                    }
                                    FragmentHelper.add(HomeNotifyFragment.newInstance());
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    public void updateStatusProduct(final Map map, final RecyclerView.Adapter adapter) {
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

                            //Reset map
                            for (Object object : map.values().toArray()) {
                                ((BeanProduct) object).setInCart(false);
                            }
                            for (int id : StoreCart.getProducts()) {
                                if (map.containsKey(id)) {
                                    BeanProduct beanProduct = ((BeanProduct) map.get(id));
                                    beanProduct.setInCart(true);
                                }

                            }
                            adapter.notifyDataSetChanged();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

        try {

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
