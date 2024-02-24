package vn.kido.shop.Fragment.Dialog;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import vn.kido.shop.Api.ISubscriber;
import vn.kido.shop.Fragment.BaseDialogFragment;
import vn.kido.shop.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ErrorDialogFragment extends BaseDialogFragment {
    private Runnable runnable;
    private LinearLayout llError;

    public ErrorDialogFragment() {
        // Required empty public constructor
    }

    public static ErrorDialogFragment newInstance(String message) {
        Bundle args = new Bundle();
        args.putString("message", message);
        ErrorDialogFragment fragment = new ErrorDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static ErrorDialogFragment newInstance(String message, String type) {
        Bundle args = new Bundle();
        args.putString("message", message);
        args.putString("type", type);
        ErrorDialogFragment fragment = new ErrorDialogFragment();
        fragment.setArguments(args );
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_error_dialog, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            llError = getView().findViewById(R.id.ll_error);
            TextView message = getView().findViewById(R.id.message);
            Button submit = getView().findViewById(R.id.submit);
            if (getArguments().containsKey("type") && getArguments().getString("type").equals(ISubscriber.SIGNUP)) {
                submit.setText(getString(R.string.signup));
            }
            if (getArguments().containsKey("type") && getArguments().getString("type").equals(ISubscriber.LOGIN)){
                submit.setText(getString(R.string.signin));
            }
            llError.setOnClickListener(null);
            message.setText(getArguments().getString("message"));
            submit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ErrorDialogFragment.this.dismissAllowingStateLoss();
                            if (runnable != null) {
                                runnable.run();
                            }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Runnable getRunnable() {
        return runnable;
    }

    public void setRunnable(Runnable runnable) {
        this.runnable = runnable;
    }
}
