package vn.kido.shop.Fragment.OAuth;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.auth.api.signin.internal.Storage;

import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;

import vn.kido.shop.Constant.AppConfig;
import vn.kido.shop.Fragment.BaseFragment;
import vn.kido.shop.Helper.StorageHelper;
import vn.kido.shop.MainActivity;
import vn.kido.shop.OAuthActivity;
import vn.kido.shop.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChooseServerFragment extends BaseFragment implements View.OnClickListener {
    public static final String DEV = "server_dev";
    public static final String TEST = "server_test";
    public static final String REAL = "server_real";

    Button btnDev, btnTest, btnReal;

    public ChooseServerFragment() {
        // Required empty public constructor
    }

    public static ChooseServerFragment newInstance() {
        Bundle args = new Bundle();
        ChooseServerFragment fragment = new ChooseServerFragment();
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
        rootView = inflater.inflate(R.layout.fragment_choose_server, container, false);
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
                btnDev = rootView.findViewById(R.id.btn_dev);
                btnTest = rootView.findViewById(R.id.btn_test);
                btnReal = rootView.findViewById(R.id.btn_real);

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        btnReal.setOnClickListener(ChooseServerFragment.this);
                        btnDev.setOnClickListener(ChooseServerFragment.this);
                        btnTest.setOnClickListener(ChooseServerFragment.this);
                    }
                });
            }
        });
        threadInit.start();
        isLoaded = true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_dev:
                StorageHelper.set(StorageHelper.SERVER, DEV);
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                getActivity().finishAffinity();
                break;
            case R.id.btn_test:
                StorageHelper.set(StorageHelper.SERVER, TEST);
                Intent intent2 = new Intent(getActivity(), MainActivity.class);
                startActivity(intent2);
                getActivity().finishAffinity();
                break;

            case R.id.btn_real:
                StorageHelper.set(StorageHelper.SERVER, REAL);
                Intent intent3 = new Intent(getActivity(), MainActivity.class);
                startActivity(intent3);
                getActivity().finishAffinity();
                break;
        }
    }
}
