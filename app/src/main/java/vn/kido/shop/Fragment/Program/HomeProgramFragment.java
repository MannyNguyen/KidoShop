package vn.kido.shop.Fragment.Program;


import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import vn.kido.shop.Adapter.BannerPagerAdapter;
import vn.kido.shop.Adapter.ViewPagerAdapter;
import vn.kido.shop.Api.APIService;
import vn.kido.shop.Api.ISubscriber;
import vn.kido.shop.Bean.BeanBanner;
import vn.kido.shop.Bean.BeanCompany;
import vn.kido.shop.Fragment.BaseFragment;
import vn.kido.shop.IControl.IPager;
import vn.kido.shop.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeProgramFragment extends BaseFragment {
    public static int BANNER = 1;
    Subscription subscriptionCompany, subscriptionBanner;
    List banners;
    ViewPager pagerCompany;
    IPager pagerBanner;
    TabLayout tabBanner, tabCompany;
    ViewPagerAdapter adapter;

    public HomeProgramFragment() {
        // Required empty public constructor
    }

    public static HomeProgramFragment newInstance() {
        Bundle args = new Bundle();
        HomeProgramFragment fragment = new HomeProgramFragment();
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
        rootView = inflater.inflate(R.layout.fragment_home_program, container, false);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (isLoaded) {
            return;
        }
        TextView title = rootView.findViewById(R.id.title);
        title.setText(getString(R.string.program));
        threadInit = new Thread(new Runnable() {
            @Override
            public void run() {
                pagerBanner = rootView.findViewById(R.id.pager);
                tabBanner = rootView.findViewById(R.id.tab);
                pagerCompany = rootView.findViewById(R.id.pager_company);
                tabCompany = rootView.findViewById(R.id.tab_company);
                adapter = new ViewPagerAdapter(getChildFragmentManager());
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getBanner();
                        pagerCompany.setAdapter(adapter);
                        tabCompany.setupWithViewPager(pagerCompany);
                        tabCompany.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                            @Override
                            public void onTabSelected(TabLayout.Tab tab) {
                                try {
                                    View row = tab.getCustomView();
                                    TextView name = row.findViewById(R.id.name);
                                    name.setTextColor(getResources().getColor(R.color.main));
                                    name.setBackground(getResources().getDrawable(R.drawable.background_tab));
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
                        getCompanyEvent();
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
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void getCompanyEvent() {
        showProgress();
        subscriptionCompany = APIService.getInstance().getCompanyEvent()
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
                            JSONArray data = jsonObject.getJSONArray("data");
                            Type listType = new TypeToken<List<BeanCompany>>() {
                            }.getType();
                            List<BeanCompany> companies = new Gson().fromJson(data.toString(), listType);
                            for (BeanCompany company : companies) {
                                adapter.addFrag(ChildProgramFragment.newInstance(company.getId()), ChildProgramFragment.class.getName());
                            }
                            adapter.notifyDataSetChanged();
                            UITab(companies);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void getBanner() {
        if (subscriptionBanner != null && subscriptionBanner.isUnsubscribed()) {
            subscriptionBanner.unsubscribe();
        }
        banners = new ArrayList();
        showProgress();
        subscriptionBanner = APIService.getInstance().getBanners()
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
                            JSONArray topBanner = jsonObject.getJSONArray("data");
                            for (int i = 0; i < topBanner.length(); i++) {
                                BeanBanner beanBanner = new Gson().fromJson(topBanner.getString(i), BeanBanner.class);
                                banners.add(beanBanner);
                            }
                            BannerPagerAdapter adapter = new BannerPagerAdapter(HomeProgramFragment.this, banners, BANNER);
                            pagerBanner.setAdapter(adapter);
                            tabBanner.setupWithViewPager(pagerBanner);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void UITab(List<BeanCompany> list) {
        try {
            for (int i = 0; i < list.size(); i++) {
                View row = getActivity().getLayoutInflater().inflate(R.layout.tab_layout, null);
                TextView name = row.findViewById(R.id.name);
                View line = row.findViewById(R.id.ll_line);
                name.setText(list.get(i).getName() + "");
                tabCompany.getTabAt(i).setCustomView(row);
                if (i == list.size() - 1) {
                    line.setVisibility(View.INVISIBLE);
                }
            }
            View row = tabCompany.getTabAt(0).getCustomView();
            TextView name = row.findViewById(R.id.name);
            name.setTextColor(getResources().getColor(R.color.main));
            name.setBackground(getResources().getDrawable(R.drawable.background_tab));
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
    }
}
