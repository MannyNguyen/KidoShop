package vn.kido.shop.Fragment.Order.Change;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;

import vn.kido.shop.Adapter.UpdateRetailAdapter;
import vn.kido.shop.Bean.BeanRetail;
import vn.kido.shop.Fragment.BaseFragment;
import vn.kido.shop.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChildConfirmRetailFragment extends BaseFragment {

    RecyclerView recycler;
    public List<BeanRetail> retails;
    public UpdateRetailAdapter adapter;

    public ChildConfirmRetailFragment() {
        // Required empty public constructor
    }

    public static ChildConfirmRetailFragment newInstance(int position, String data) {

        Bundle args = new Bundle();
        args.putInt("position", position);
        args.putString("data", data);
        ChildConfirmRetailFragment fragment = new ChildConfirmRetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView != null) {
            return rootView;
        }
        rootView = inflater.inflate(R.layout.fragment_child_confirm_retail, container, false);
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
                try {
                    JSONObject jsonObject = new JSONObject(getArguments().getString("data"));
                    Type listType = new TypeToken<List<BeanRetail>>() {
                    }.getType();
                    retails = new Gson().fromJson(jsonObject.getString("items"), listType);
                    recycler = rootView.findViewById(R.id.recycler);
                    adapter = new UpdateRetailAdapter(ChildConfirmRetailFragment.this, recycler, retails);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            recycler.setLayoutManager(new LinearLayoutManager(getContext()));
                            recycler.setAdapter(adapter);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        threadInit.start();
        isLoaded = true;
    }

    public void updateQuantity(int position, int value) {
        try {
            if (retails == null) {
                return;
            }
            BeanRetail beanRetail = retails.get(position);
            beanRetail.setQuantity(value);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
