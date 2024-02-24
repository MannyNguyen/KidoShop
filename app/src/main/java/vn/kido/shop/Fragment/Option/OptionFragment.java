package vn.kido.shop.Fragment.Option;


import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import vn.kido.shop.Adapter.OptionGuideAdapter;
import vn.kido.shop.Api.APIService;
import vn.kido.shop.Api.ISubscriber;
import vn.kido.shop.Bean.BeanOptionGuide;
import vn.kido.shop.Fragment.BaseFragment;
import vn.kido.shop.Helper.StorageHelper;
import vn.kido.shop.OAuthActivity;
import vn.kido.shop.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class OptionFragment extends BaseFragment implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {
    SwitchCompat switchActive;
    LinearLayout llVi, llEng;
    TextView txtVi, txtEng;
    ImageView ivViCheck, ivEngCheck;
    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    List<BeanOptionGuide> optionGuides;
    OptionGuideAdapter adapter;
    Button btnLogout;
    Subscription subLogout;

    public OptionFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView != null) {
            return rootView;
        }
        rootView = inflater.inflate(R.layout.fragment_option, container, false);
        return rootView;
    }

    public static OptionFragment newInstance() {
        Bundle args = new Bundle();
        OptionFragment fragment = new OptionFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (isLoaded) {
            return;
        }
        TextView title = rootView.findViewById(R.id.title);
        title.setText(getString(R.string.option));
        threadInit = new Thread(new Runnable() {
            @Override
            public void run() {
                llVi = rootView.findViewById(R.id.ll_vi);
                llEng = rootView.findViewById(R.id.ll_eng);
                txtVi = rootView.findViewById(R.id.txt_vi);
                txtEng = rootView.findViewById(R.id.txt_eng);
                ivViCheck = rootView.findViewById(R.id.iv_vi_check);
                ivEngCheck = rootView.findViewById(R.id.iv_eng_check);
                switchActive = rootView.findViewById(R.id.switch_active);
                recyclerView = rootView.findViewById(R.id.recycler);
                btnLogout = rootView.findViewById(R.id.btn_logout);
                layoutManager = new LinearLayoutManager(getContext());
                recyclerView.setLayoutManager(layoutManager);
                switchActive.setOnCheckedChangeListener(OptionFragment.this);
                llVi.setOnClickListener(OptionFragment.this);
                llEng.setOnClickListener(OptionFragment.this);
                btnLogout.setOnClickListener(OptionFragment.this);


                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (StorageHelper.get(StorageHelper.LANGUAGE).equals("") || StorageHelper.get(StorageHelper.LANGUAGE) == null) {
                            StorageHelper.set(StorageHelper.LANGUAGE, "vi");
                        }
                        if (StorageHelper.get(StorageHelper.LANGUAGE).equals("vi")) {
                            txtVi.setTextColor(getResources().getColor(R.color.main));
                            ivViCheck.setVisibility(View.VISIBLE);
                            txtEng.setTextColor(getResources().getColor(R.color.gray_600));
                            ivEngCheck.setVisibility(View.GONE);

                            Locale locale = new Locale("vi");
                            Locale.setDefault(locale);
                            Configuration config = new Configuration();
                            config.locale = locale;
                        }
                        if (StorageHelper.get(StorageHelper.LANGUAGE).equals("en")) {
                            txtVi.setTextColor(getResources().getColor(R.color.gray_600));
                            ivViCheck.setVisibility(View.GONE);
                            txtEng.setTextColor(getResources().getColor(R.color.main));
                            ivEngCheck.setVisibility(View.VISIBLE);

                            Locale locale = new Locale("en");
                            Locale.setDefault(locale);
                            Configuration config = new Configuration();
                            config.locale = locale;
                        }
                        getHelp();
                    }
                });
            }
        });
        threadInit.start();
        isLoaded = true;
    }

    private void pushActive(int active) {
        APIService.getInstance().activePush(active)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ISubscriber() {
                    @Override
                    public void excute(JSONObject jsonObject) {
                    }
                });
    }

    private void getHelp() {
        APIService.getInstance().getHelp()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ISubscriber() {
                    @Override
                    public void excute(JSONObject jsonObject) {
                        try {
                            JSONObject data = jsonObject.getJSONObject("data");
                            if (data.getInt("is_active") == 1) {
                                switchActive.setChecked(true);
                            }
                            if (data.getInt("is_active") == 0) {
                                switchActive.setChecked(false);
                            }

                            JSONArray help = data.getJSONArray("help");
                            optionGuides = new ArrayList<>();
                            Gson gson = new Gson();
                            for (int i = 0; i < help.length(); i++) {
                                BeanOptionGuide optionGuide = gson.fromJson(help.getString(i), BeanOptionGuide.class);
                                optionGuides.add(optionGuide);
                            }
                            adapter = new OptionGuideAdapter(OptionFragment.this, optionGuides, recyclerView);
                            recyclerView.setAdapter(adapter);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch (compoundButton.getId()) {
            case R.id.switch_active:
                if (!b) {
                    StorageHelper.set(StorageHelper.SWITCH, "0");
                    pushActive(0);
                } else {
                    StorageHelper.set(StorageHelper.SWITCH, "1");
                    pushActive(1);
                }
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_vi:
                txtVi.setTextColor(getResources().getColor(R.color.main));
                ivViCheck.setVisibility(View.VISIBLE);
                txtEng.setTextColor(getResources().getColor(R.color.gray_600));
                ivEngCheck.setVisibility(View.GONE);

//                StorageHelper.set(StorageHelper.LANGUAGE, "vi");
//
//                Locale locale = new Locale(StorageHelper.get(StorageHelper.LANGUAGE));
//                Locale.setDefault(locale);
//                Configuration config = new Configuration();
//                config.locale = locale;
//                getActivity().getResources().updateConfiguration(config, getActivity().getResources().getDisplayMetrics());
//                Intent intent = new Intent(getActivity(), MainActivity.class);
//                startActivity(intent);
//                getActivity().finish();

                break;
            case R.id.ll_eng:
                txtVi.setTextColor(getResources().getColor(R.color.gray_600));
                ivViCheck.setVisibility(View.GONE);
                txtEng.setTextColor(getResources().getColor(R.color.main));
                ivEngCheck.setVisibility(View.VISIBLE);

//                StorageHelper.set(StorageHelper.LANGUAGE, "en");
//
//                Locale localeEn = new Locale(StorageHelper.get(StorageHelper.LANGUAGE));
//                Locale.setDefault(localeEn);
//                Configuration configEn = new Configuration();
//                configEn.locale = localeEn;
//                getActivity().getResources().updateConfiguration(configEn, getActivity().getResources().getDisplayMetrics());
//                Intent intentEn = new Intent(getActivity(), MainActivity.class);
//                startActivity(intentEn);
//                getActivity().finish();
                break;

            case R.id.btn_logout:
                logout();
                break;
        }
    }

    private void logout() {
        subLogout = APIService.getInstance().logout(StorageHelper.get(StorageHelper.USERNAME))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(String s) {

                    }
                });

        StorageHelper.set(StorageHelper.TOKEN, "");
        StorageHelper.set(StorageHelper.USERNAME, "");
        Intent intent = new Intent(getActivity(), OAuthActivity.class);
        startActivity(intent);
        getActivity().finish();
    }
}
