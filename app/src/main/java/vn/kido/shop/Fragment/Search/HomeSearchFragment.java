package vn.kido.shop.Fragment.Search;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import vn.kido.shop.Adapter.ViewPagerAdapter;
import vn.kido.shop.Fragment.BaseFragment;
import vn.kido.shop.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeSearchFragment extends BaseFragment implements View.OnClickListener {
    public static final int PROGRAM = 1;
    public static final int PRODUCT = 2;
    public static final int NEWS = 3;
    public static final int STORE_NAME = 4;
    public TextView edtKeySearch;
    ViewPager pager;
    public TabLayout tab;
    ViewPagerAdapter adapter;

    public HomeSearchFragment() {
        // Required empty public constructor
    }

    public static HomeSearchFragment newInstance(String keyWord) {
        Bundle args = new Bundle();
        args.putString("key_word", keyWord);
        HomeSearchFragment fragment = new HomeSearchFragment();
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
        rootView = inflater.inflate(R.layout.fragment_home_search, container, false);
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
                final TextView title = rootView.findViewById(R.id.title);
                edtKeySearch = rootView.findViewById(R.id.edt_key_search);
                pager = rootView.findViewById(R.id.pager);
                tab = rootView.findViewById(R.id.tab);
                adapter = new ViewPagerAdapter(getChildFragmentManager());
                final String[] titles = {getString(R.string.product), getString(R.string.search_program), getString(R.string.search_label), getString(R.string.search_news)};
                adapter.addFrag(ChildSearchFragment.newInstance(PRODUCT), titles[0]);
                adapter.addFrag(ChildSearchFragment.newInstance(PROGRAM), titles[1]);
                adapter.addFrag(ChildSearchFragment.newInstance(STORE_NAME), titles[2]);
                adapter.addFrag(ChildSearchFragment.newInstance(NEWS), titles[3]);

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        title.setText(getString(R.string.search));
                        edtKeySearch.setText(getArguments().getString("key_word"));
                        pager.setAdapter(adapter);
                        tab.setupWithViewPager(pager);
                        //Set number of page has been keep on left and right of current page
                        pager.setOffscreenPageLimit(4);
                        UITab(titles);
                        tab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                            @Override
                            public void onTabSelected(TabLayout.Tab tab) {
                                View row = tab.getCustomView();
                                TextView name = row.findViewById(R.id.name);
                                name.setTextColor(getResources().getColor(R.color.main));
                                name.setBackground(getResources().getDrawable(R.drawable.background_tab));
//                                final Fragment child = getChildFragmentManager().getFragments().get(tab.getPosition());
//                                if (child != null && child instanceof ChildSearchFragment) {
//                                    ChildSearchFragment childSearchFragment = (ChildSearchFragment) child;
//
//                                    if(((ChildSearchFragment) child).map!=null && ((ChildSearchFragment) child).map.size()>0)
//
//                                    if (handlerChild != null && runnableChild != null) {
//                                        handlerChild.removeCallbacks(runnableChild);
//                                    }
//                                    runnableChild = new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            ((ChildSearchFragment) child).search();
//                                        }
//                                    };
//                                    handlerChild = new Handler();
//                                    handlerChild.postDelayed(runnableChild, 500);
//
//                                }
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


                        edtKeySearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                            @Override
                            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                                boolean handle = false;
                                if (i == EditorInfo.IME_ACTION_SEARCH) {
                                    if (edtKeySearch.getText().toString().equals("")) {
                                        edtKeySearch.setError(getString(R.string.enter_keyword));
                                        edtKeySearch.requestFocus();
                                    } else {
                                        for (Fragment fragment : getChildFragmentManager().getFragments()) {
                                            if (fragment instanceof ChildSearchFragment) {
                                                ((ChildSearchFragment) fragment).refresh();
                                            }
                                        }
                                        handle = true;
                                    }
                                }
                                return handle;
                            }
                        });

                        Fragment child = getChildFragmentManager().getFragments().get(0);
                        if (child != null && child instanceof ChildSearchFragment) {
                            ((ChildSearchFragment) child).search();
                        }

                    }
                });
            }
        });
        threadInit.start();
        isLoaded = true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }

    private void UITab(String[] titles) {
        try {
            for (int i = 0; i < titles.length; i++) {
                View row = getActivity().getLayoutInflater().inflate(R.layout.tab_layout, null);
                TextView name = row.findViewById(R.id.name);
                name.setText(titles[i]);
                tab.getTabAt(i).setCustomView(row);
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
