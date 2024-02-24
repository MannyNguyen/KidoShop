package vn.kido.shop.Fragment.Option;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.json.JSONException;
import org.json.JSONObject;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import vn.kido.shop.Adapter.GuidePagerAdapter;
import vn.kido.shop.Api.APIService;
import vn.kido.shop.Api.ISubscriber;
import vn.kido.shop.Fragment.BaseFragment;
import vn.kido.shop.R;

public class DialogGuideFragment extends BaseFragment {
    ViewPager pager;
    TabLayout tab;
    List<String> images;

    public static DialogGuideFragment newInstance(int id) {
        Bundle args = new Bundle();
        args.putInt("id", id);
        DialogGuideFragment fragment = new DialogGuideFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView != null) {
            return rootView;
        }
        rootView = inflater.inflate(R.layout.dialog_guide, container, false);
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
                pager = rootView.findViewById(R.id.pager);
                tab = rootView.findViewById(R.id.tab);

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getHelpDetail(getArguments().getInt("id"));
                    }
                });
            }
        });
        threadInit.start();
        isLoaded = true;
    }

    private void getHelpDetail(int id) {
        APIService.getInstance().getHelpDetail(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ISubscriber() {
                    @Override
                    public void excute(JSONObject jsonObject) {
                        try {
                            JSONObject data = jsonObject.getJSONObject("data");
                            images = new ArrayList();
                            Gson gson = new Gson();
                            Type typeStrings = new TypeToken<List<String>>() {
                            }.getType();
                            images = gson.fromJson(data.getString("description"), typeStrings);
                            GuidePagerAdapter adapter = new GuidePagerAdapter(DialogGuideFragment.this, images);
                            pager.setAdapter(adapter);
                            tab.setupWithViewPager(pager);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }
}
