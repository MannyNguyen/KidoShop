package vn.kido.shop.Fragment.News;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

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
import vn.kido.shop.Fragment.BaseFragment;
import vn.kido.shop.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeNewsFragment extends BaseFragment {
    public static int BANNER = 1;
    ViewPager headerPager, pagernews;
    TabLayout headerTab;
    Subscription subscriptionBanner;
    TabLayout tabnews;
    ViewPagerAdapter adapternews;
    List banners;
    public List<Integer> listId;

    public HomeNewsFragment() {
        // Required empty public constructor
    }

    public static HomeNewsFragment newInstance() {
        Bundle args = new Bundle();
        HomeNewsFragment fragment = new HomeNewsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView != null) {
            return rootView;
        }
        rootView = inflater.inflate(R.layout.fragment_home_news, container, false);
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
                listId = new ArrayList<>();
                headerTab = rootView.findViewById(R.id.header_tab_news);
                headerPager = rootView.findViewById(R.id.header_pager_news);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getBanners();
                    }
                });
                tabnews = rootView.findViewById(R.id.tabnews);
                pagernews = rootView.findViewById(R.id.pagernews);
                final TextView title = rootView.findViewById(R.id.title);
                adapternews = new ViewPagerAdapter(getChildFragmentManager());
                final String[] titles = {getString(R.string.today), getString(R.string.all), getString(R.string.saved)};
                for (int i = 0; i < titles.length; i++) {
                    adapternews.addFrag(ChildNewsFragment.newInstance(i + 1), titles[i]);
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        title.setText(R.string.news);
                        pagernews.setAdapter(adapternews);
                        pagernews.setOffscreenPageLimit(3);
                        tabnews.setupWithViewPager(pagernews);
                        UITab(titles);

                        tabnews.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                            @Override
                            public void onTabSelected(TabLayout.Tab tab) {
                                View row = tab.getCustomView();
                                TextView name = row.findViewById(R.id.name);
                                name.setTextColor(getResources().getColor(R.color.main));
                                name.setBackground(getResources().getDrawable(R.drawable.background_tab));
                                if (tab.getPosition() == 2) {
                                    Fragment fragment = adapternews.getItem(tab.getPosition());
                                    if (fragment instanceof ChildNewsFragment) {
                                        ChildNewsFragment childNewsFragment = (ChildNewsFragment) fragment;
                                        childNewsFragment.onRefresh();
                                    }
                                } else {
                                    try {
                                        ChildNewsFragment currentFragment = (ChildNewsFragment) adapternews.getItem(tab.getPosition());
                                        for (int i = 0; i < currentFragment.map.size(); i++) {
                                            currentFragment.map.get(i).setIsSaved(0);
                                            for (int j = 0; j < listId.size(); j++) {
                                                if (listId.get(j) == currentFragment.map.get(i).getId()) {
                                                    currentFragment.map.get(i).setIsSaved(1);
                                                }
                                            }
                                        }
                                        currentFragment.newsAdapter.notifyDataSetChanged();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                            @Override
                            public void onTabUnselected(TabLayout.Tab tab) {
                                View row = tab.getCustomView();
                                TextView name = row.findViewById(R.id.name);
                                name.setTextColor(getResources().getColor(R.color.white));
                                name.setBackground(getResources().getDrawable(R.drawable.transparent_tab));
                            }

                            @Override
                            public void onTabReselected(TabLayout.Tab tab) {

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
    public void onDestroy() {
        super.onDestroy();
        if (subscriptionBanner != null && subscriptionBanner.isUnsubscribed()) {
            subscriptionBanner.unsubscribe();
        }
    }

    private void getBanners() {
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
                    public void excute(JSONObject jsonObject) {
                        hideProgress();
                        try {
                            JSONArray topBanner = jsonObject.getJSONArray("data");
                            for (int i = 0; i < topBanner.length(); i++) {
                                BeanBanner beanBanner = new Gson().fromJson(topBanner.getString(i), BeanBanner.class);
                                banners.add(beanBanner);
                            }
                            BannerPagerAdapter adapter = new BannerPagerAdapter(HomeNewsFragment.this, banners, BANNER);
                            headerPager.setAdapter(adapter);
                            headerTab.setupWithViewPager(headerPager);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void UITab(String[] titles) {
        try {
            for (int i = 0; i < titles.length; i++) {
                View row = getActivity().getLayoutInflater().inflate(R.layout.tab_layout, null);
                TextView name = row.findViewById(R.id.name);
                View line = row.findViewById(R.id.ll_line);
                name.setText(titles[i]);
                tabnews.getTabAt(i).setCustomView(row);
                //name.setBackground(getResources().getDrawable(R.color.main));
                if (i == titles.length - 1) {
                    line.setVisibility(View.INVISIBLE);
                }
            }
            View row = tabnews.getTabAt(0).getCustomView();
            TextView name = row.findViewById(R.id.name);
            name.setTextColor(getResources().getColor(R.color.main));
            name.setBackground(getResources().getDrawable(R.drawable.background_tab));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
