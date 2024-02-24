package vn.kido.shop.Fragment.Cart;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import vn.kido.shop.Adapter.ClaimAdapter;
import vn.kido.shop.Adapter.ProgramProductAdapter;
import vn.kido.shop.Api.APIService;
import vn.kido.shop.Api.ISubscriber;
import vn.kido.shop.Bean.BeanClaim;
import vn.kido.shop.Bean.BeanProduct;
import vn.kido.shop.Class.CmmFunc;
import vn.kido.shop.Fragment.BaseFragment;
import vn.kido.shop.Helper.FragmentHelper;
import vn.kido.shop.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProgramCartFragment extends BaseFragment implements View.OnClickListener {

    RecyclerView recycler;
    RecyclerView recyclerTab;
    List<BeanClaim> claims;
    View submit;
    boolean canClame;
    TextView status, titleChild;
    List products;

    public ProgramCartFragment() {
        // Required empty public constructor
    }

    public static ProgramCartFragment newInstance(int position, int id, String claims) {

        Bundle args = new Bundle();
        args.putInt("position", position);
        args.putInt("id", id);
        args.putString("claims", claims);
        ProgramCartFragment fragment = new ProgramCartFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView != null) {
            return rootView;
        }
        rootView = inflater.inflate(R.layout.fragment_program_cart, container, false);
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
                recycler = rootView.findViewById(R.id.recycler);
                recyclerTab = rootView.findViewById(R.id.recycler_tab);
                status = rootView.findViewById(R.id.status);
                Type listClaim = new TypeToken<List<BeanClaim>>() {
                }.getType();
                claims = new Gson().fromJson(getArguments().getString("claims"), listClaim);

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
                        recyclerTab.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
                        ClaimAdapter adapter = new ClaimAdapter(ProgramCartFragment.this, recyclerTab, claims);
                        recyclerTab.setAdapter(adapter);
                        boolean isClaim = true;
                        for (int i = 0; i < claims.size(); i++) {
                            BeanClaim beanClaim = claims.get(i);
                            if (!beanClaim.isCanClaim()) {
                                beanClaim.setActive(true);
                                getRewards(beanClaim.getId());
                                isClaim = false;
                                break;
                            }
                        }

                        if (isClaim) {
                            claims.get(0).setActive(true);
                            getRewards(claims.get(0).getId());
                        }
                        boolean isDone = true;
                        for (int i = 0; i < claims.size(); i++) {
                            BeanClaim beanClaim = claims.get(i);
                            beanClaim.setActive(false);
                            if (!beanClaim.isCanClaim() && isDone) {
                                beanClaim.setActive(true);
                                getRewards(beanClaim.getId());
                                showAnim(status);
                                recyclerTab.smoothScrollToPosition(i);
                                isDone = false;
                            }
                        }
                        recyclerTab.getAdapter().notifyDataSetChanged();
                        submit = rootView.findViewById(R.id.submit);
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
        for (BeanClaim beanClaim : claims) {
            if (beanClaim.isActive()) {
                getRewards(beanClaim.getId());
                break;
            }
        }

    }

    private void setActive(int id, boolean isActive) {
        for (BeanClaim beanClaim : claims) {
            if (beanClaim.getId() == id) {
                beanClaim.setCanClaim(isActive);
                recyclerTab.getAdapter().notifyDataSetChanged();
                break;
            }
        }
    }

    public void getRewards(final int id) {
        showProgress();
        APIService.getInstance().getRewards(id)
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
                            JSONObject eventReward = data.getJSONObject("event_reward");
                            TextView title = rootView.findViewById(R.id.title);
                            TextView eventName = rootView.findViewById(R.id.event_name);

                            title.setText("Khuyến mãi đổi quà " + eventReward.optString("company_name", ""));
                            eventName.setText(eventReward.optString("event_name", ""));
                            canClame = data.getBoolean("can_claim");
                            //  CartFragment cartFragment = (CartFragment) FragmentHelper.findFragmentByTag(CartFragment.class);

                            if (canClame) {
                                status.setText(getString(R.string.can_claim));
                            } else {
                                status.setText(getString(R.string.not_can_claim));
                            }
                            setActive(id, canClame);
                            submit.setOnClickListener(ProgramCartFragment.this);


                            TextView moneyReward = rootView.findViewById(R.id.total_money_reward);
                            TextView moneyUsed = rootView.findViewById(R.id.total_money_used);
                            TextView moneyRemain = rootView.findViewById(R.id.total_money_remain);
                            TextView moneyOverAllow = rootView.findViewById(R.id.total_money_over_allow);

                            moneyReward.setText(CmmFunc.formatMoney(eventReward.getInt("total_money_reward"), false));
                            moneyUsed.setText(CmmFunc.formatMoney(eventReward.getInt("total_money_used"), false));
                            moneyRemain.setText(CmmFunc.formatMoney(eventReward.getInt("total_money_remain"), false));
                            moneyOverAllow.setText(CmmFunc.formatMoney(eventReward.getInt("total_money_over_allow"), false));
                            //int max = eventReward.getInt("total_money_reward") + eventReward.getInt("total_money_over_allow") - eventReward.getInt("total_money_used");
                            Type listGift = new TypeToken<List<BeanProduct>>() {
                            }.getType();
                            List items = new Gson().fromJson(eventReward.getString("products"), listGift);
                            BeanClaim beanClaim = BeanClaim.getActive(claims);
                            if (products == null) {
                                products = items;
                                ProgramProductAdapter adapter = new ProgramProductAdapter(ProgramCartFragment.this, recycler, products, beanClaim.getId());
                                recycler.setAdapter(adapter);

                            } else {
                                products.clear();
                                for (Object obj : items) {
                                    products.add(obj);
                                }
                                ProgramProductAdapter adapter = (ProgramProductAdapter) recycler.getAdapter();
                                adapter.setId(beanClaim.getId());
                                adapter.notifyDataSetChanged();
                            }

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
                BeanClaim item = BeanClaim.getActive(claims);
                if (!item.isCanClaim()) {
                    showAnim(status);
                    return;
                }

                boolean isDone = true;
                for (int i = 0; i < claims.size(); i++) {
                    BeanClaim beanClaim = claims.get(i);
                    beanClaim.setActive(false);
                    if (!beanClaim.isCanClaim() && isDone) {
                        beanClaim.setActive(true);
                        getRewards(beanClaim.getId());
                        showAnim(status);
                        recyclerTab.smoothScrollToPosition(i);
                        isDone = false;
                    }
                }
                recyclerTab.getAdapter().notifyDataSetChanged();
                if (isDone) {
                    FragmentHelper.pop(getActivity());
                }

                break;
        }
    }

    public void showAnim(View icCart) {
        try {
            final Animation animShake = AnimationUtils.loadAnimation(getContext(), R.anim.anim_shake);
            icCart.startAnimation(animShake);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
