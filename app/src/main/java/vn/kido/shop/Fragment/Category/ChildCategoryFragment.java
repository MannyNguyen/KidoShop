package vn.kido.shop.Fragment.Category;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import vn.kido.shop.Adapter.CategoryAdapter;
import vn.kido.shop.Api.APIService;
import vn.kido.shop.Api.ISubscriber;
import vn.kido.shop.Bean.BeanCategory;
import vn.kido.shop.Fragment.BaseFragment;
import vn.kido.shop.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChildCategoryFragment extends BaseFragment {

    RecyclerView recycler;
    GridLayoutManager gridLayoutManager;
    CategoryAdapter adapter;
    List<BeanCategory> categories;
    Subscription subscription;


    public ChildCategoryFragment() {
        // Required empty public constructor
    }

    public static ChildCategoryFragment newInstance(int id) {

        Bundle args = new Bundle();
        args.putInt("id", id);
        ChildCategoryFragment fragment = new ChildCategoryFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView != null) {
            return rootView;
        }

        rootView = inflater.inflate(R.layout.fragment_child_category, container, false);
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
                categories = new ArrayList<>();
                adapter = new CategoryAdapter(ChildCategoryFragment.this, recycler, categories);
                gridLayoutManager = new GridLayoutManager(getContext(), 3);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        recycler.setLayoutManager(gridLayoutManager);
                        recycler.setAdapter(adapter);
                        getCategory();
                    }
                });

            }
        });
        threadInit.start();
    }

    private void getCategory() {
        subscription = APIService.getInstance().getCatById(getArguments().getInt("id"))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new ISubscriber() {
                    @Override
                    public void excute(JSONObject jsonObject) {
                        try {
                            Gson gson = new Gson();
                            JSONObject data = jsonObject.getJSONObject("data");
                            JSONArray arr = data.getJSONArray("cates");
                            for (int i = 0; i < arr.length(); i++) {
                                BeanCategory beanCategory = gson.fromJson(arr.getString(i), BeanCategory.class);
                                categories.add(beanCategory);
                            }
                            adapter.notifyDataSetChanged();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (subscription != null) {
            subscription.unsubscribe();
        }
    }
}
