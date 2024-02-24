package vn.kido.shop.Fragment.Order.Follow;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import vn.kido.shop.Adapter.ViewPagerAdapter;
import vn.kido.shop.Fragment.BaseFragment;
import vn.kido.shop.Fragment.Common.HomeFragment;
import vn.kido.shop.Helper.FragmentHelper;
import vn.kido.shop.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class FollowOrderFragment extends BaseFragment {
    ViewPagerAdapter adapter;
    TabLayout tab;
    ViewPager pager;

    public FollowOrderFragment() {
        // Required empty public constructor
    }

    public static FollowOrderFragment newInstance() {
        Bundle args = new Bundle();
        FollowOrderFragment fragment = new FollowOrderFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView != null) {
            return rootView;
        }
        rootView = inflater.inflate(R.layout.fragment_follow_order, container, false);
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



        tab = rootView.findViewById(R.id.tab);
        pager = rootView.findViewById(R.id.pager);
        //ChildFragmentManager manage fragment of viewpager
        adapter = new ViewPagerAdapter(getChildFragmentManager());
        TextView title = rootView.findViewById(R.id.title);
        title.setText(getString(R.string.follow_order));

        final String[] titles = {getString(R.string.waiting_confirm), getString(R.string.sending), getString(R.string.sent)};
        for (int i = 0; i < titles.length; i++) {
            adapter.addFrag(ChildOrderFollowFragment.newInstance(i + 1), titles[i]);
        }

        pager.setAdapter(adapter);
        tab.setupWithViewPager(pager);
        //Update to UI
        UITab(titles);

        tab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                View row = tab.getCustomView();
                TextView name = row.findViewById(R.id.name);
                name.setTextColor(getResources().getColor(R.color.main));
                name.setBackground(getResources().getDrawable(R.drawable.background_tab));
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

        isLoaded = true;
    }

    @Override
    public void manuResume() {
        super.manuResume();
        try {
            rootView.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentHelper.pop(getActivity(), HomeFragment.class);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void UITab(String[] titles) {
        try {
            for (int i = 0; i < titles.length; i++) {
                View row = getActivity().getLayoutInflater().inflate(R.layout.tab_layout, null);
                TextView name = row.findViewById(R.id.name);
                name.setText(titles[i]);
                tab.getTabAt(i).setCustomView(row);
                //name.setBackground(getResources().getDrawable(R.color.main));
                if (i == titles.length - 1) {
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
}
