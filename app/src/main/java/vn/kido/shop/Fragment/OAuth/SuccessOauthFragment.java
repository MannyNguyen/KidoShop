package vn.kido.shop.Fragment.OAuth;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import vn.kido.shop.Fragment.BaseFragment;
import vn.kido.shop.HomeActivity;
import vn.kido.shop.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SuccessOauthFragment extends BaseFragment {

    public SuccessOauthFragment() {
        // Required empty public constructor
    }

    public static SuccessOauthFragment newInstance() {
        Bundle args = new Bundle();
        SuccessOauthFragment fragment = new SuccessOauthFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_success_oauth, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            getView().findViewById(R.id.btn_start).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), HomeActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
