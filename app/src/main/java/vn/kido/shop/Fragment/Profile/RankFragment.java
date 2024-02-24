package vn.kido.shop.Fragment.Profile;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import vn.kido.shop.Adapter.ViewPagerAdapter;
import vn.kido.shop.Api.APIService;
import vn.kido.shop.Api.ISubscriber;
import vn.kido.shop.Bean.BeanUserLevel;
import vn.kido.shop.Class.CmmFunc;
import vn.kido.shop.Fragment.BaseFragment;
import vn.kido.shop.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class RankFragment extends BaseFragment {
    TabLayout tab;
    ViewPager pager;
    ViewPagerAdapter adapter;
    TextView txtPurchase, txtAppName, txtNextMoney;
    ImageView ivCurrentLevel, ivNextLevel, ivUser;
    SeekBar seek;

    public RankFragment() {
        // Required empty public constructor
    }

    public static RankFragment newInstance() {
        Bundle args = new Bundle();
        RankFragment fragment = new RankFragment();
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
        rootView = inflater.inflate(R.layout.fragment_rank, container, false);
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
                tab = rootView.findViewById(R.id.tab);
                pager = rootView.findViewById(R.id.pager);
                txtPurchase = rootView.findViewById(R.id.txt_purchase);
                txtAppName = rootView.findViewById(R.id.txt_app_name);
                ivCurrentLevel = rootView.findViewById(R.id.iv_current_level);
                ivNextLevel = rootView.findViewById(R.id.iv_next_level);
                ivUser = rootView.findViewById(R.id.iv_user);
                txtNextMoney = rootView.findViewById(R.id.txt_next_money);
                seek = rootView.findViewById(R.id.seek);
                final TextView title = rootView.findViewById(R.id.title);
                adapter = new ViewPagerAdapter(getChildFragmentManager());
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        title.setText(getString(R.string.rank_customer));
                        getUserLevel();
                    }
                });
            }
        });
        threadInit.start();
        isLoaded = true;
    }

    private void UITab(List<BeanUserLevel> listTitle) {
        try {
            for (int i = 0; i < listTitle.size(); i++) {
                View row = getActivity().getLayoutInflater().inflate(R.layout.tab_rank_customer, null);
                TextView name = row.findViewById(R.id.name);
                ImageView ivRank = row.findViewById(R.id.iv_rank);
                name.setText(listTitle.get(i).getLevelName());
                tab.getTabAt(i).setCustomView(row);

                switch (i) {
                    case 0:
                        ivRank.setImageResource(R.drawable.new_member);
                        break;
                    case 1:
                        ivRank.setImageResource(R.drawable.silver);
                        break;
                    case 2:
                        ivRank.setImageResource(R.drawable.gold);
                        break;
                    case 3:
                        ivRank.setImageResource(R.drawable.titan);
                        break;
                }
            }
            View row = tab.getTabAt(0).getCustomView();
            TextView name = row.findViewById(R.id.name);
            name.setTextColor(getResources().getColor(R.color.main));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getUserLevel() {
        showProgress();
        APIService.getInstance().getUserLevel()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<String, Object>() {
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
                    JSONObject data = jsonObject.getJSONObject("data");
                    txtPurchase.setText(CmmFunc.formatMoney(Math.round(data.getInt("total_money")), false));
                    txtAppName.setText(data.getString("app_name"));

                    seek.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View view, MotionEvent motionEvent) {
                            return true;
                        }
                    });

                    final int currentLevel = data.getInt("current_level_id");

                    switch (currentLevel) {
                        case 0:
                            ivCurrentLevel.setImageResource(R.drawable.new_member);
                            ivNextLevel.setImageResource(R.drawable.silver);
//                            txtNextMoney.setText(getString(R.string.rank_next_1) + " " + CmmFunc.formatMoney(Math.round(data.getInt("money_require")), true) + " " +
//                                    getString(R.string.rank_next_2) + " " + getString(R.string.silver));
                            break;
                        case 1:
                            ivCurrentLevel.setImageResource(R.drawable.silver);
                            ivNextLevel.setImageResource(R.drawable.gold);
//                            txtNextMoney.setText(getString(R.string.rank_next_1) + " " + CmmFunc.formatMoney(Math.round(data.getInt("money_require")), true) + " " +
//                                    getString(R.string.rank_next_2) + " " + getString(R.string.gold));
                            break;
                        case 2:
                            ivCurrentLevel.setImageResource(R.drawable.gold);
                            ivNextLevel.setImageResource(R.drawable.titan);
//                            txtNextMoney.setText(getString(R.string.rank_next_1) + " " + CmmFunc.formatMoney(Math.round(data.getInt("money_require")), true) + " " +
//                                    getString(R.string.rank_next_2) + " " + getString(R.string.titan));
                            break;
                        case 3:
                            ivCurrentLevel.setImageResource(R.drawable.gold);
                            ivNextLevel.setImageResource(R.drawable.titan);
                            //txtNextMoney.setText(getString(R.string.rank_next_1) + " " + CmmFunc.formatMoney(Math.round(data.getInt("money_require")), true) + " " + getString(R.string.rank_next_2));
                            break;
                    }

                    String levels = data.getString("levels");
                    Type listType = new TypeToken<List<BeanUserLevel>>() {
                    }.getType();
                    final List<BeanUserLevel> beanUserLevels = new Gson().fromJson(levels, listType);
                    for (int i = 0; i < beanUserLevels.size(); i++) {
                        BeanUserLevel beanUserLevel = beanUserLevels.get(i);
                        adapter.addFrag(ChildRankFragment.newInstance(beanUserLevel.getDescription()), beanUserLevel.getLevelName());


                        int currentID = data.getInt("current_level_id");
                        if (beanUserLevel.getIdLevel() == currentID) {
                            if(i == beanUserLevels.size() - 1){
                                rootView.findViewById(R.id.ll_seek).setVisibility(View.GONE);
                            }
                             else{
                                rootView.findViewById(R.id.ll_seek).setVisibility(View.VISIBLE);
                                float requireMoney = Float.parseFloat(String.valueOf(data.getInt("money_require")));
                                BeanUserLevel beanUserLevel_2 = beanUserLevels.get(i + 1);
                                float value = Float.parseFloat(String.valueOf(beanUserLevel_2.getValue() - beanUserLevel.getValue()));
                                float percent = requireMoney / value;
                                float progress = percent * 100;
                                float seekProgress = 100 - progress;
                                int seekPercent = Math.round((int) seekProgress);
                                seek.setProgress(seekPercent);
                                float progressX = percent * seek.getWidth();
                                ivUser.setX(seek.getWidth() - progressX);
                                txtNextMoney.setText(getString(R.string.rank_next_1) + " " + CmmFunc.formatMoney(Math.round(data.getInt("money_require")), true) + " " +
                                        getString(R.string.rank_next_2) + " " + beanUserLevel_2.getLevelName());
                            }
                        }

                    }


                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pager.setAdapter(adapter);
                            tab.setupWithViewPager(pager);
                            pager.setCurrentItem(currentLevel);
                            UITab(beanUserLevels);

                            tab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                                @Override
                                public void onTabSelected(TabLayout.Tab tab) {
                                    View row = tab.getCustomView();
                                    TextView name = row.findViewById(R.id.name);
                                    name.setTextColor(getResources().getColor(R.color.main));
                                }

                                @Override
                                public void onTabUnselected(TabLayout.Tab tab) {
                                    View row = tab.getCustomView();
                                    TextView name = row.findViewById(R.id.name);
                                    name.setTextColor(getResources().getColor(R.color.gray_700));
                                }

                                @Override
                                public void onTabReselected(TabLayout.Tab tab) {
                                }
                            });
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
