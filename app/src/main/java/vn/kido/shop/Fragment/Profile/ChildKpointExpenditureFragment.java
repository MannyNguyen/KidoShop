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

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import vn.kido.shop.Adapter.KpointExpenditureAdapter;
import vn.kido.shop.Api.APIService;
import vn.kido.shop.Api.ISubscriber;
import vn.kido.shop.Bean.BeanKpoint;
import vn.kido.shop.Class.CmmFunc;
import vn.kido.shop.Fragment.BaseFragment;
import vn.kido.shop.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChildKpointExpenditureFragment extends BaseFragment {
    TextView txtPurchase, txtShopName, txtNotice;
    RecyclerView recycler;
    List<BeanKpoint> beanKpoints = new ArrayList<>();

    public ChildKpointExpenditureFragment() {
        // Required empty public constructor
    }

    public static ChildKpointExpenditureFragment newInstance(int type) {
        Bundle args = new Bundle();
        args.putInt("type", type);
        ChildKpointExpenditureFragment fragment = new ChildKpointExpenditureFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void manuResume() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (rootView != null) {
            return rootView;
        }
        rootView = inflater.inflate(R.layout.fragment_child_kpoint_expenditure, container, false);
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
                txtPurchase = rootView.findViewById(R.id.txt_purchase);
                txtShopName = rootView.findViewById(R.id.txt_shop_name);
                txtNotice = rootView.findViewById(R.id.txt_notice);
                recycler = rootView.findViewById(R.id.recycler);
                LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                recycler.setLayoutManager(layoutManager);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getKpoint(getArguments().getInt("type"));
                    }
                });
            }
        });

        threadInit.start();
        isLoaded = true;
    }

    private void getKpoint(int type) {
        showProgress();
        APIService.getInstance().getKpoint(type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<String, Object>() {
                    @Override
                    public Object call(String s) {
                        try {
                            return new JSONObject(s);
                        } catch (Exception e) {
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
                    txtPurchase.setText(CmmFunc.formatMoney(Math.round(data.getInt("total")), false));
                    txtShopName.setText(data.getString("app_name"));
                    txtNotice.setText("(" + getString(R.string.expen_date) + " " + new DateTime(data.getLong("end_date")).toString("dd/MM/yyyy") + ")");

                    JSONArray gift = data.getJSONArray("gift");
                    Type listType = new TypeToken<List<BeanKpoint>>() {
                    }.getType();

                    beanKpoints = new Gson().fromJson(gift.toString(), listType);
                    KpointExpenditureAdapter adapter = new KpointExpenditureAdapter(ChildKpointExpenditureFragment.this, recycler, beanKpoints);
                    recycler.setAdapter(adapter);

                    adapter.notifyDataSetChanged();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
