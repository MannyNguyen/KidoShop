package vn.kido.shop.Fragment.Profile;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import vn.kido.shop.Adapter.KpointChangeGiftAdapter;
import vn.kido.shop.Api.APIService;
import vn.kido.shop.Api.ISubscriber;
import vn.kido.shop.Bean.BeanKpoint;
import vn.kido.shop.Fragment.BaseFragment;
import vn.kido.shop.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChildKpointChangeGiftFragment extends BaseFragment {
    TextView txtKpoint, txtShopName;
    RecyclerView recycler;

    public ChildKpointChangeGiftFragment() {
        // Required empty public constructor
    }

    public static ChildKpointChangeGiftFragment newInstance(int type) {
        Bundle args = new Bundle();
        args.putInt("type", type);
        ChildKpointChangeGiftFragment fragment = new ChildKpointChangeGiftFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void manuResume() {
        super.manuResume();
        getKpoint(getArguments().getInt("type"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (rootView != null) {
            return rootView;
        }
        rootView = inflater.inflate(R.layout.fragment_child_kpoint_change_gift, container, false);
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
                txtKpoint = rootView.findViewById(R.id.txt_kpoint);
                txtShopName = rootView.findViewById(R.id.txt_shop_name);
                recycler = rootView.findViewById(R.id.recycler);
                final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        recycler.setLayoutManager(layoutManager);
                        getKpoint(getArguments().getInt("type"));
                    }
                });
            }
        });
        threadInit.start();
        isLoaded = true;
    }

    private void getKpoint(int type){
        showProgress();
        APIService.getInstance().getKpoint(type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<String, Object>() {
                    @Override
                    public Object call(String s) {
                        try {
                            return new JSONObject(s);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        return null;
                    }
                }).subscribe(new ISubscriber() {
            @Override
            public void done() {
                super.done();
                hideProgress();
            }

            @Override
            public void excute(JSONObject jsonObject) {
                try {
                    JSONObject data = jsonObject.getJSONObject("data");
                    txtKpoint.setText(data.getInt("total") + "");
                    txtShopName.setText(data.getString("app_name"));

                    JSONArray gift = data.getJSONArray("gift");
                    Type listType = new TypeToken<List<BeanKpoint>>() {
                    }.getType();

                    List<BeanKpoint> beanKpoints= new Gson().fromJson(gift.toString(), listType);
                    KpointChangeGiftAdapter adapter = new KpointChangeGiftAdapter(ChildKpointChangeGiftFragment.this, recycler, beanKpoints);
                    recycler.setAdapter(adapter);


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }
}
