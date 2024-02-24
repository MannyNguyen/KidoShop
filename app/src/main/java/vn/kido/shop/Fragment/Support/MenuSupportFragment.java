package vn.kido.shop.Fragment.Support;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import vn.kido.shop.Adapter.MenuMessageAdapter;
import vn.kido.shop.Bean.BeanMessage;
import vn.kido.shop.Fragment.BaseFragment;
import vn.kido.shop.Helper.FragmentHelper;
import vn.kido.shop.Helper.SocketIOHelper;
import vn.kido.shop.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class MenuSupportFragment extends BaseFragment implements View.OnClickListener {

    public RecyclerView recycler;
    public MenuMessageAdapter adapter;
    public List<BeanMessage.BeanChildMessage> items;

    public MenuSupportFragment() {
        // Required empty public constructor
    }

    public static MenuSupportFragment newInstance() {
        Bundle args = new Bundle();
        MenuSupportFragment fragment = new MenuSupportFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView != null) {
            return rootView;
        }
        rootView = inflater.inflate(R.layout.fragment_menu_support, container, false);
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
                items = new ArrayList<>();
                rootView.findViewById(R.id.outer_container).setOnClickListener(MenuSupportFragment.this);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Fragment supportFragment = FragmentHelper.findFragmentByTag(SupportFragment.class);
                        if (supportFragment == null) {
                            return;
                        }
                        adapter = new MenuMessageAdapter(supportFragment, recycler, items);
                        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
                        recycler.setAdapter(adapter);
                        SocketIOHelper.getInstance().sendMessage(BeanMessage.DEFAULT, "", 1);
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
            case R.id.outer_container:
                FragmentHelper.pop(getActivity());
                break;
        }
    }
}
