package vn.kido.shop.Fragment.Dialog;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import vn.kido.shop.Fragment.BaseDialogFragment;
import vn.kido.shop.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class MessageDialogFragment extends BaseDialogFragment {

    private String message = "";
    private Runnable runnable;
    private String title = "";
    private boolean isTouchOutSide;
    private ICallback callback;

    public MessageDialogFragment() {
        // Required empty public constructor
    }

    public static MessageDialogFragment newInstance() {

        Bundle args = new Bundle();
        MessageDialogFragment fragment = new MessageDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_message_dialog, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            TextView message = getView().findViewById(R.id.message);
            TextView title = getView().findViewById(R.id.title);
            View submit = getView().findViewById(R.id.submit);
            message.setText(getMessage());
//            if (!TextUtils.isEmpty(getTitle())) {
//                title.setText(getTitle());
//            } else {
//                title.setVisibility(View.GONE);
//            }
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MessageDialogFragment.this.dismissAllowingStateLoss();
                    if (runnable != null) {
                        runnable.run();
                    }
                }
            });
            getView().findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MessageDialogFragment.this.dismissAllowingStateLoss();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Runnable getRunnable() {
        return runnable;
    }

    public void setRunnable(Runnable runnable) {
        this.runnable = runnable;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isTouchOutSide() {
        return isTouchOutSide;
    }

    public void setTouchOutSide(boolean touchOutSide) {
        isTouchOutSide = touchOutSide;
    }

    public void setCallback(ICallback callback) {
        this.callback = callback;
    }

    public ICallback getCallback() {
        return callback;
    }
}
