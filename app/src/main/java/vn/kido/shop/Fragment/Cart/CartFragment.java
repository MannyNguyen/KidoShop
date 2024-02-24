package vn.kido.shop.Fragment.Cart;


import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import vn.kido.shop.Adapter.ViewPagerAdapter;
import vn.kido.shop.Api.APIService;
import vn.kido.shop.Api.ISubscriber;
import vn.kido.shop.Bean.BeanClaim;
import vn.kido.shop.Fragment.BaseFragment;
import vn.kido.shop.Fragment.Common.HomeFragment;
import vn.kido.shop.Fragment.Dialog.ConfrimCartDialogFragment;
import vn.kido.shop.Helper.FragmentHelper;
import vn.kido.shop.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class CartFragment extends BaseFragment implements View.OnClickListener {
    public ViewPagerAdapter adapter;
    TabLayout tab;
    ViewPager pager;
    List<String> titles;

    private List<BeanClaim> rewards;

    public CartFragment() {
        // Required empty public constructor
    }

    public static CartFragment newInstance(int idProduct) {
        Bundle args = new Bundle();
        args.putInt("id_product", idProduct);
        CartFragment fragment = new CartFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static CartFragment newInstance() {
        Bundle args = new Bundle();
        CartFragment fragment = new CartFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView != null) {
            return rootView;
        }
        rootView = inflater.inflate(R.layout.fragment_cart, container, false);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (isLoaded) {
            return;
        }
        View cart = rootView.findViewById(R.id.cart);
        cart.setEnabled(false);
        threadInit = new Thread(new Runnable() {
            @Override
            public void run() {
                tab = rootView.findViewById(R.id.tab);
                pager = rootView.findViewById(R.id.pager);
                final TextView title = rootView.findViewById(R.id.title);

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        title.setText(getString(R.string.cart));
                        getCart();

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
        getCart2();
    }

    private void UITab(List<String> titles) {
        try {
            for (int i = 0; i < titles.size(); i++) {
                View row = getActivity().getLayoutInflater().inflate(R.layout.tab_layout, null);
                TextView name = row.findViewById(R.id.name);
                name.setText(titles.get(i));
                tab.getTabAt(i).setCustomView(row);
                if (i == titles.size() - 1) {
                    row.findViewById(R.id.ll_line).setVisibility(View.GONE);
                }
            }
            View row = tab.getTabAt(0).getCustomView();
            TextView name = row.findViewById(R.id.name);
            name.setTextColor(getResources().getColor(R.color.main));
            name.setBackground(getResources().getDrawable(R.drawable.background_tab));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getCart() {
        showProgress();
        APIService.getInstance().getCart()
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
                            rewards = new ArrayList<>();
                            JSONObject data = jsonObject.getJSONObject("data");
                            final JSONArray cartInfo = data.getJSONArray("cart_info");
                            titles = new ArrayList();
                            adapter = new ViewPagerAdapter(getChildFragmentManager());
                            if (cartInfo.length() == 0) {
                                rootView.findViewById(R.id.content_container).setVisibility(View.GONE);
                                rootView.findViewById(R.id.empty_cotainer).setVisibility(View.VISIBLE);
                                rootView.findViewById(R.id.back_to_home).setOnClickListener(CartFragment.this);
                                return;
                            }
                            rootView.findViewById(R.id.content_container).setVisibility(View.VISIBLE);
                            rootView.findViewById(R.id.empty_cotainer).setVisibility(View.GONE);
                            for (int i = 0; i < cartInfo.length(); i++) {
                                JSONObject jObject = cartInfo.getJSONObject(i);
                                titles.add(getString(R.string.cart) + " " + jObject.getString("company_name"));
                                adapter.addFrag(ChildCartFragment.newInstance(jObject.toString(), i), titles.get(i));
                                int idReward = jObject.getInt("event_reward_id");
                                String claims = jObject.getString("event_claim_reward_ids");
                                rewards.add(new BeanClaim(idReward, jObject.getBoolean("can_claim"), claims));

                            }
                            if (titles.size() == 2) {
                                tab.setTabMode(TabLayout.MODE_FIXED);
                            } else {
                                tab.setTabMode(TabLayout.MODE_SCROLLABLE);
                            }
                            pager.setAdapter(adapter);
                            tab.setupWithViewPager(pager);
                            UITab(titles);

                            tab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                                @Override
                                public void onTabSelected(final TabLayout.Tab tab) {
                                    try {
                                        View row = tab.getCustomView();
                                        TextView name = row.findViewById(R.id.name);
                                        name.setTextColor(getResources().getColor(R.color.main));
                                        name.setBackground(getResources().getDrawable(R.drawable.background_tab));
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    ChildCartFragment childCartFragment = (ChildCartFragment) adapter.getItem(tab.getPosition());
                                                    childCartFragment.showPopupCondition();
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }, 1000);

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onTabUnselected(TabLayout.Tab tab) {
                                    try {
                                        View row = tab.getCustomView();
                                        TextView name = row.findViewById(R.id.name);
                                        name.setTextColor(getResources().getColor(R.color.white));
                                        name.setBackground(getResources().getDrawable(R.drawable.transparent_tab));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onTabReselected(TabLayout.Tab tab) {

                                }
                            });


                            rootView.findViewById(R.id.submit).setOnClickListener(CartFragment.this);

                            for (int i = 0; i < rewards.size(); i++) {
                                if (!rewards.get(i).isCanClaim()) {
                                    TabLayout.Tab t = tab.getTabAt(i);
                                    ChildCartFragment childCartFragment = (ChildCartFragment) adapter.getItem(i);
                                    t.select();
                                    childCartFragment.showPopup(rewards.get(i).getId(),rewards.get(i).getClaims());
                                    break;
                                }

                            }


                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        if (getArguments().containsKey("id_product")) {
                                            int index = 0;
                                            int currentTab = 0;
                                            for (int i = 0; i < cartInfo.length(); i++) {
                                                JSONObject jObject = cartInfo.getJSONObject(i);
                                                JSONArray items = jObject.getJSONArray("items");
                                                for (int j = 0; j < items.length(); j++) {
                                                    if (getArguments().getInt("id_product") == items.getJSONObject(j).getInt("id")) {
                                                        currentTab = i;
                                                        index = j;
                                                        break;
                                                    }
                                                }
                                            }
                                            if (currentTab > 0) {
                                                TabLayout.Tab t = tab.getTabAt(currentTab);
                                                t.select();
                                            }

                                            if (index > 0) {
                                                ChildCartFragment childCartFragment = (ChildCartFragment) adapter.getItem(currentTab);
                                                float y = childCartFragment.recycler.getY() + childCartFragment.recycler.getChildAt(index).getY();
                                                childCartFragment.scrollView.smoothScrollTo(0, (int) y);
                                            }

                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                }
                            }, 1000);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    public void getCart2() {
        showProgress();
        APIService.getInstance().getCart()
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
                            rewards = new ArrayList<>();
                            JSONObject data = jsonObject.getJSONObject("data");
                            JSONArray cartInfo = data.getJSONArray("cart_info");
                            if (cartInfo.length() == 0) {
                                FragmentHelper.pop(getActivity(), HomeFragment.class);
                                return;
                            }
                            if (adapter.getCount() > cartInfo.length()) {
                                getCart();
                                return;
                            }


                            for (int i = 0; i < cartInfo.length(); i++) {
                                JSONObject jObject = cartInfo.getJSONObject(i);
                                int idReward = jObject.getInt("event_reward_id");
                                String claims = jObject.getString("event_claim_reward_ids");
                                rewards.add(new BeanClaim(idReward, jObject.getBoolean("can_claim"), claims));
                                ChildCartFragment childCartFragment = (ChildCartFragment) adapter.getItem(i);
                                Bundle bundle = new Bundle();
                                bundle.putString("data", jObject.toString());
                                childCartFragment.setArguments(bundle);
                                childCartFragment.reload();
                                if (tab.getSelectedTabPosition() == i) {
                                    childCartFragment.showPopupCondition();
                                }

                            }

                            rootView.findViewById(R.id.submit).setOnClickListener(CartFragment.this);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submit:
                try {
                    for (int i = 0; i < rewards.size(); i++) {
                        Boolean beanReward = rewards.get(i).isCanClaim();
                        if (!beanReward) {
                            TabLayout.Tab t = tab.getTabAt(i);
                            ChildCartFragment childCartFragment = (ChildCartFragment) adapter.getItem(i);
                            t.select();
                            childCartFragment.showPopup(rewards.get(i).getId(),rewards.get(i).getClaims());
                            return;
                        }
                    }

                    ConfrimCartDialogFragment messageDialogFragment = ConfrimCartDialogFragment.newInstance();
                    String message = "";
                    for (int i = 0; i < titles.size(); i++) {
                        if (i == titles.size() - 1) {
                            message += titles.get(i);
                            continue;
                        }
                        message += titles.get(i) + "\n";
                    }
                    messageDialogFragment.setMessage(message);

                    messageDialogFragment.setRunnable(new Runnable() {
                        @Override
                        public void run() {
                            FragmentHelper.add(ConfirmOrderFragment.newInstance());
                        }
                    });
                    messageDialogFragment.show(getActivity().getSupportFragmentManager(), messageDialogFragment.getClass().getName());


                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.back_to_home:
                FragmentHelper.pop(getActivity(), HomeFragment.class);
                break;
        }
    }
}
