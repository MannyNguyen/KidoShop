package vn.kido.shop.Fragment.Dialog;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import vn.kido.shop.Fragment.BaseDialogFragment;
import vn.kido.shop.R;

public class UpdateDialogFragment extends BaseDialogFragment {
    private String message = "";
    private Runnable runnable;
    private Runnable backRunnable;
    private String title = "";
    private boolean isTouchOutSide;
    private ICallback callback;
    public Button cancel;

    public UpdateDialogFragment() {
    }

    public static UpdateDialogFragment newInstance() {
        Bundle args = new Bundle();
        UpdateDialogFragment fragment = new UpdateDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_update_dialog, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            TextView message = getView().findViewById(R.id.message);
            TextView title = getView().findViewById(R.id.title);
            View submit = getView().findViewById(R.id.submit);
            Button cancel = getView().findViewById(R.id.cancel);
            message.setText(getMessage());
            if (!TextUtils.isEmpty(getTitle())) {
                title.setText(getTitle());
            } else {
                title.setVisibility(View.GONE);
            }
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UpdateDialogFragment.this.dismissAllowingStateLoss();
                    if (runnable != null) {
                        runnable.run();
                    }
                }
            });
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UpdateDialogFragment.this.dismissAllowingStateLoss();
                    if (backRunnable != null) {
                        backRunnable.run();
                    }
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

    public Runnable getBackRunnable() {
        return backRunnable;
    }

    public void setBackRunnable(Runnable backRunnable) {
        this.backRunnable = backRunnable;
    }
}
