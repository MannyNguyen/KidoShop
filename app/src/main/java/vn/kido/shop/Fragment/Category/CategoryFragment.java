package vn.kido.shop.Fragment.Category;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import vn.kido.shop.Adapter.ViewPagerAdapter;
import vn.kido.shop.Api.APIService;
import vn.kido.shop.Api.ISubscriber;
import vn.kido.shop.Bean.BeanCategory;
import vn.kido.shop.Fragment.BaseFragment;
import vn.kido.shop.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class CategoryFragment extends BaseFragment {

    public static final int TYPE_PRODUCT = 1;
    public static final int CATEGORY = 2;
    BeanCategory parent;
    TabLayout tab;
    ViewPager pager;
    ViewPagerAdapter adapter;
    Subscription subscription;
    ImageView bannerImage;

    public CategoryFragment() {
        // Required empty public constructor
    }

    public static CategoryFragment newInstance(String data, int typeAPI) {
        Bundle args = new Bundle();
        args.putString("data", data);
        args.putInt("type", typeAPI);
        CategoryFragment fragment = new CategoryFragment();
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

        rootView = inflater.inflate(R.layout.fragment_category, container, false);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (isLoaded) {
            return;
        }
        TextView title = rootView.findViewById(R.id.title);
        parent = new Gson().fromJson(getArguments().getString("data"), BeanCategory.class);
        title.setText(parent.getName() + "");
        threadInit = new Thread(new Runnable() {
            @Override
            public void run() {
                bannerImage = rootView.findViewById(R.id.banner_image);
                tab = rootView.findViewById(R.id.tab);
                pager = rootView.findViewById(R.id.pager);
                adapter = new ViewPagerAdapter(getChildFragmentManager());
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pager.setAdapter(adapter);
                        tab.setupWithViewPager(pager);
                        tab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
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
                        getCategories();
                    }
                });
            }
        });
        threadInit.start();
    }

    @Override
    public void manuResume() {
        super.manuResume();
        try {
            for (Fragment fragment : getChildFragmentManager().getFragments()) {
                if (fragment instanceof ChildProductFragment) {
                    ChildProductFragment childProductFragment = (ChildProductFragment) fragment;
                    childProductFragment.updateStatusProduct(childProductFragment.map, childProductFragment.adapter);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    private void UITab(List<BeanCategory> list) {
//        try {
//            for (int i = 0; i < list.size(); i++) {
//                View row = getActivity().getLayoutInflater().inflate(R.layout.tab_layout, null);
//                TextView name = row.findViewById(R.id.name);
//                name.setText(list.get(i).getName());
//                if (i == 0) {
//                    name.setTextColor(getResources().getColor(R.color.main));
//                }
//                tab.getTabAt(i).setCustomView(row);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    private void UITab(List<BeanCategory> list) {
        try {
            for (int i = 0; i < list.size(); i++) {
                View row = getActivity().getLayoutInflater().inflate(R.layout.tab_layout, null);
                TextView name = row.findViewById(R.id.name);
                View line = row.findViewById(R.id.ll_line);
                name.setText(list.get(i).getName());
                tab.getTabAt(i).setCustomView(row);
                if (i == list.size() - 1) {
                    line.setVisibility(View.INVISIBLE);
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
    public void onDestroy() {
        super.onDestroy();
        if (subscription != null) {
            subscription.unsubscribe();
        }
    }

    private void getCategories() {
        Observable<String> observable = null;
        if (getArguments().getInt("type") == TYPE_PRODUCT) {
            observable = APIService.getInstance().getCategories(parent.getId());
        } else if (getArguments().getInt("type") == CATEGORY) {
            observable = APIService.getInstance().getCatById(parent.getId());
        }
        if (observable == null) {
            return;
        }
        subscription = observable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new ISubscriber() {
                    @Override
                    public void excute(JSONObject jsonObject) {
                        try {
                            Gson gson = new Gson();
                            JSONObject data = jsonObject.getJSONObject("data");
                            String image = data.getString("image");
                            if (!image.equals("")) {
                                Picasso.get().load(image).into(bannerImage);
                            }
                            Type listType = new TypeToken<List<BeanCategory>>() {
                            }.getType();
                            List<BeanCategory> categories = gson.fromJson(data.getString("cates"), listType);
                            for (BeanCategory beanCategory : categories) {
                                if (beanCategory.getParent() == BeanCategory.CATEGORY) {
                                    adapter.addFrag(ChildCategoryFragment.newInstance(beanCategory.getId()), ChildCategoryFragment.class.getName());
                                    continue;
                                }
                                adapter.addFrag(ChildProductFragment.newInstance(beanCategory.getId()), ChildProductFragment.class.getName());
                            }
                            adapter.notifyDataSetChanged();
                            UITab(categories);
                            if(categories.size() == 2){
                                tab.setTabMode(TabLayout.MODE_FIXED);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

}
