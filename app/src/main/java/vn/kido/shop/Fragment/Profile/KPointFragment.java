package vn.kido.shop.Fragment.Profile;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import vn.kido.shop.Adapter.ViewPagerAdapter;
import vn.kido.shop.Fragment.BaseFragment;
import vn.kido.shop.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class KPointFragment extends BaseFragment {
//    TabLayout tab;
    ViewPager pager;
    ViewPagerAdapter adapter;

    public KPointFragment() {
        // Required empty public constructor
    }

    public static KPointFragment newInstance(int tabPosition) {
        Bundle args = new Bundle();
        args.putInt("tab_position", tabPosition);
        KPointFragment fragment = new KPointFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void manuResume() {
        super.manuResume();
        for (Fragment fragment : getChildFragmentManager().getFragments()) {
            if (fragment instanceof ChildKpointChangeGiftFragment) {
                ((ChildKpointChangeGiftFragment) fragment).manuResume();
                continue;
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (rootView != null) {
            return rootView;
        }
        rootView = inflater.inflate(R.layout.fragment_kpoint, container, false);
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
//                tab = rootView.findViewById(R.id.tab);
                pager = rootView.findViewById(R.id.pager);
                adapter = new ViewPagerAdapter(getChildFragmentManager());
                final TextView title = rootView.findViewById(R.id.title);
                final String[] titles = {getString(R.string.total_expenditure), getString(R.string.change_gift)};
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        title.setText(getString(R.string.kpoint));
                        //adapter.addFrag(ChildKpointExpenditureFragment.newInstance(1), titles[0]);
                        adapter.addFrag(ChildKpointChangeGiftFragment.newInstance(2), titles[1]);
                        pager.setAdapter(adapter);
//                        tab.setupWithViewPager(pager);
//                        UITab(titles);
//                        tab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//                            @Override
//                            public void onTabSelected(TabLayout.Tab tab) {
//                                View row = tab.getCustomView();
//                                TextView name = row.findViewById(R.id.name);
//                                name.setTextColor(getResources().getColor(R.color.main));
//                            }
//
//                            @Override
//                            public void onTabUnselected(TabLayout.Tab tab) {
//                                View row = tab.getCustomView();
//                                TextView name = row.findViewById(R.id.name);
//                                name.setTextColor(getResources().getColor(R.color.gray_700));
//                            }
//
//                            @Override
//                            public void onTabReselected(TabLayout.Tab tab) {
//                            }
//                        });
                        pager.setCurrentItem(getArguments().getInt("tab_position"));
                    }
                });
            }
        });
        threadInit.start();
        isLoaded = true;
    }

//    private void UITab(String[] listTitle) {
//        try {
//            for (int i = 0; i < listTitle.length; i++) {
//                View row = getActivity().getLayoutInflater().inflate(R.layout.tab_layout, null);
//                TextView name = row.findViewById(R.id.name);
//                name.setText(listTitle[i]);
//                tab.getTabAt(i).setCustomView(row);
//            }
//            View row = tab.getTabAt(0).getCustomView();
//            TextView name = row.findViewById(R.id.name);
//            name.setTextColor(getResources().getColor(R.color.main));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}
