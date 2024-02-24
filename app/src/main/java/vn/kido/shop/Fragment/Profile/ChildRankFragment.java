package vn.kido.shop.Fragment.Profile;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import vn.kido.shop.Fragment.BaseFragment;
import vn.kido.shop.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChildRankFragment extends BaseFragment {
    public TextView txtDescription;

    public ChildRankFragment() {
        // Required empty public constructor
    }

    public static ChildRankFragment newInstance(String description) {
        Bundle args = new Bundle();
        args.putString("description", description);
        ChildRankFragment fragment = new ChildRankFragment();
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
        rootView = inflater.inflate(R.layout.fragment_child_rank, container, false);
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
                txtDescription = rootView.findViewById(R.id.txt_description);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        txtDescription.setText(getArguments().getString("description"));
                    }
                });
            }
        });
        threadInit.start();
        isLoaded = true;
    }
}
