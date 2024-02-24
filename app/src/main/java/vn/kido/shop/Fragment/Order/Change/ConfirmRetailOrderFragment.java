package vn.kido.shop.Fragment.Order.Change;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
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
import vn.kido.shop.Bean.BeanRetail;
import vn.kido.shop.Fragment.BaseFragment;
import vn.kido.shop.Fragment.Dialog.ErrorDialogFragment;
import vn.kido.shop.Fragment.Order.Follow.FollowOrderFragment;
import vn.kido.shop.Helper.FragmentHelper;
import vn.kido.shop.R;

import static vn.kido.shop.Fragment.Order.Change.RetailOrderFragment.CHANGE;
import static vn.kido.shop.Fragment.Order.Change.RetailOrderFragment.PAY;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConfirmRetailOrderFragment extends BaseFragment implements View.OnClickListener {
    public static final String CONFIRM_RETAIL = "CONFIRM_RETAIL";

    public ViewPagerAdapter adapter;
    TabLayout tab;
    ViewPager pager;
    View submit;
    public boolean isError;

    public ConfirmRetailOrderFragment() {
        // Required empty public constructor
    }

    public static ConfirmRetailOrderFragment newInstance(String type, String data) {

        Bundle args = new Bundle();
        args.putString("type", type);
        args.putString("data", data);
        ConfirmRetailOrderFragment fragment = new ConfirmRetailOrderFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView != null) {
            return rootView;
        }
        rootView = inflater.inflate(R.layout.fragment_confirm_retail_order, container, false);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (isLoaded) {
            return;
        }
        TextView title = rootView.findViewById(R.id.title);
        if (getArguments().getString("type").equals(CHANGE)) {
            title.setText(getString(R.string.change_product_single));
        } else if (getArguments().getString("type").equals(PAY)) {
            title.setText(getString(R.string.pay_product_single));
        }
        threadInit = new Thread(new Runnable() {
            @Override
            public void run() {
                tab = rootView.findViewById(R.id.tab);
                pager = rootView.findViewById(R.id.pager);
                submit = rootView.findViewById(R.id.submit);

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        parseData();
                        submit.setOnClickListener(ConfirmRetailOrderFragment.this);
                    }
                });
            }
        });
        threadInit.start();
        isLoaded = true;
    }

    private void parseData() {
        try {
            JSONArray cartInfo = new JSONArray(getArguments().getString("data"));
            List<String> titles = new ArrayList();
            adapter = new ViewPagerAdapter(getChildFragmentManager());

            for (int i = 0; i < cartInfo.length(); i++) {
                JSONObject jObject = cartInfo.getJSONObject(i);
                titles.add(getString(R.string.cart) + " " + jObject.getString("company_name"));
                adapter.addFrag(ChildConfirmRetailFragment.newInstance(i, jObject.toString()), titles.get(i));

            }
            if (titles.size() == 2) {
                tab.setTabMode(TabLayout.MODE_FIXED);
            } else {
                tab.setTabMode(TabLayout.MODE_SCROLLABLE);
            }
            pager.setAdapter(adapter);
            tab.setupWithViewPager(pager);
            pager.setOffscreenPageLimit(10);
            UITab(titles);
            tab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(final TabLayout.Tab tab) {
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

        } catch (Exception e) {
            e.printStackTrace();
        }
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
//            ChildCartFragment childCartFragment = (ChildCartFragment) adapter.getItem(0);
//            childCartFragment.showPopup(rewards.get(0));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submit:
                try {
                    showProgress();
                    submit.setOnClickListener(null);
                    isError = true;
                    if (isSuccess() == null) {
                        ErrorDialogFragment errorDialogFragment = ErrorDialogFragment.newInstance(getString(R.string.enter_full_info), CONFIRM_RETAIL);
                        errorDialogFragment.show(getActivity().getSupportFragmentManager(), errorDialogFragment.getClass().getName());
                        submit.setOnClickListener(ConfirmRetailOrderFragment.this);
                        hideProgress();
                        return;
                    }
                    int type = (getArguments().getString("type").equals(CHANGE) ? 1 : 2);
                    confirmBookReturn(type, isSuccess().toString());

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private JSONArray isSuccess() {
        JSONArray jsonArray = null;
        try {
            for (int i = 0; i < adapter.getCount(); i++) {
                ChildConfirmRetailFragment fragment = (ChildConfirmRetailFragment) adapter.getItem(i);
                fragment.adapter.notifyDataSetChanged();
                for (BeanRetail beanRetail : fragment.retails) {
                    if (TextUtils.isEmpty(beanRetail.getNote()) || beanRetail.getExpireDate() == 0) {
                        jsonArray = null;
                        return jsonArray;
                    }
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("id", beanRetail.getId());
                    jsonObject.put("quantity", beanRetail.getQuantity());
                    jsonObject.put("expire_date", beanRetail.getExpireDate());
                    jsonObject.put("note", beanRetail.getNote());
                    if (jsonArray == null) {
                        jsonArray = new JSONArray();
                    }
                    jsonArray.put(jsonObject);

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonArray;
    }

    private void confirmBookReturn(int type, String data) {
        APIService.getInstance().confirmBookReturn(type, data)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ISubscriber() {
                    @Override
                    public void done() {
                        super.done();
                        hideProgress();
                        submit.setOnClickListener(ConfirmRetailOrderFragment.this);
                    }

                    @Override
                    public void excute(JSONObject jsonObject) {
                        try {
                            FragmentHelper.add(FollowOrderFragment.newInstance());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }
}
