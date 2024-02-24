package vn.kido.shop.Fragment.Order.History;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import vn.kido.shop.Adapter.ViewPagerAdapter;
import vn.kido.shop.Fragment.BaseFragment;
import vn.kido.shop.Fragment.Common.HomeFragment;
import vn.kido.shop.Helper.FragmentHelper;
import vn.kido.shop.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryOrderFragment extends BaseFragment implements View.OnClickListener {
    public static final int TAB_TOTAL = 1;
    public int SORT = 0;
    TabLayout tab;
    ViewPager pager;
    ViewPagerAdapter adapter;
    ImageView icIncrease, icDescrease;

    public HistoryOrderFragment() {
        // Required empty public constructor
    }

    public static HistoryOrderFragment newInstance(int tab) {
        Bundle args = new Bundle();
        args.putInt("tab", tab);
        HistoryOrderFragment fragment = new HistoryOrderFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static HistoryOrderFragment newInstance() {
        Bundle args = new Bundle();
        HistoryOrderFragment fragment = new HistoryOrderFragment();
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
        rootView = inflater.inflate(R.layout.fragment_history_order, container, false);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        rootView.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentHelper.pop(getActivity(), HomeFragment.class);
            }
        });
        if (isLoaded) {
            return;
        }
        final TextView title = rootView.findViewById(R.id.title);
        title.setText(getString(R.string.history));
        threadInit = new Thread(new Runnable() {
            @Override
            public void run() {
                tab = rootView.findViewById(R.id.tab);
                pager = rootView.findViewById(R.id.pager);
                adapter = new ViewPagerAdapter(getChildFragmentManager());
                icIncrease = rootView.findViewById(R.id.ic_increase);
                icDescrease = rootView.findViewById(R.id.ic_descrease);
                final String[] titles = {getString(R.string.most_buy), getString(R.string.total_money), getString(R.string.last_buy)};
                adapter.addFrag(ChildProductHistoryFragment.newInstance(), titles[0]);
                adapter.addFrag(ChildOrderHistoryFragment.newInstance(2), titles[1]);
                adapter.addFrag(ChildOrderHistoryFragment.newInstance(3), titles[2]);

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            pager.setAdapter(adapter);
                            tab.setupWithViewPager(pager);
                            pager.setOffscreenPageLimit(adapter.getCount());
                            UITab(titles);

                            tab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                                @Override
                                public void onTabSelected(TabLayout.Tab tab) {
                                    try {
                                        View row = tab.getCustomView();
                                        TextView name = row.findViewById(R.id.name);
                                        name.setTextColor(getResources().getColor(R.color.main));
                                        name.setBackground(getResources().getDrawable(R.drawable.background_tab));

                                        Fragment fragment = adapter.getItem(tab.getPosition());
                                        if (fragment instanceof ChildProductHistoryFragment) {
                                            ChildProductHistoryFragment childProductHistoryFragment = (ChildProductHistoryFragment) fragment;
                                            childProductHistoryFragment.updateStatusProduct(childProductHistoryFragment.map, childProductHistoryFragment.adapter);
                                        }
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

                            rootView.findViewById(R.id.container_sort).setOnClickListener(HistoryOrderFragment.this);


                            if (getArguments().containsKey("tab")) {
                                TabLayout.Tab tablayout = tab.getTabAt(getArguments().getInt("tab"));
                                tablayout.select();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

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
        for (Fragment fragment : getChildFragmentManager().getFragments()) {
            if (fragment instanceof ChildProductHistoryFragment) {
                ((ChildProductHistoryFragment) fragment).manuResume();
            }
        }
    }

    private void UITab(String[] titles) {
        try {
            for (int i = 0; i < titles.length; i++) {
                View row = getActivity().getLayoutInflater().inflate(R.layout.tab_layout, null);
                TextView name = row.findViewById(R.id.name);
                name.setText(titles[i]);
                tab.getTabAt(i).setCustomView(row);
                if (i == titles.length - 1) {
                    row.findViewById(R.id.ll_line).setVisibility(View.GONE);
                }
                //name.setBackground(getResources().getDrawable(R.color.main));
            }
            View row = tab.getTabAt(0).getCustomView();
            TextView name = row.findViewById(R.id.name);
            name.setTextColor(getResources().getColor(R.color.main));
            name.setBackground(getResources().getDrawable(R.drawable.background_tab));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.container_sort:
                SORT = (SORT + 1) % 2;
                //Increase
                if (SORT > 0) {
                    icIncrease.setImageResource(R.drawable.ic_sort_increase_active);
                    icDescrease.setImageResource(R.drawable.ic_sort_descrease_inactive);
                } else {
                    icIncrease.setImageResource(R.drawable.ic_sort_increase_inactive);
                    icDescrease.setImageResource(R.drawable.ic_sort_descrease_active);
                }
                for (Fragment fragment : getChildFragmentManager().getFragments()) {
                    if (fragment instanceof ChildOrderHistoryFragment) {
                        ((ChildOrderHistoryFragment) fragment).onRefresh();
                        continue;
                    }
                    if (fragment instanceof ChildProductHistoryFragment) {
                        ((ChildProductHistoryFragment) fragment).onRefresh();
                        continue;
                    }
                }
                break;
        }
    }
}
