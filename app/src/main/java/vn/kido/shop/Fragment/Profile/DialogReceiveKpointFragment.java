package vn.kido.shop.Fragment.Profile;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import vn.kido.shop.Fragment.BaseFragment;
import vn.kido.shop.R;

public class DialogReceiveKpointFragment extends BaseFragment implements View.OnClickListener{
    Button btnBack;

    public static DialogReceiveKpointFragment newInstance() {
        Bundle args = new Bundle();
        DialogReceiveKpointFragment fragment = new DialogReceiveKpointFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView != null) {
            return rootView;
        }
        rootView = inflater.inflate(R.layout.dialog_receive_gift, container, false);
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
                btnBack = rootView.findViewById(R.id.back);
            }
        });
        isLoaded = true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
        }
    }
}
