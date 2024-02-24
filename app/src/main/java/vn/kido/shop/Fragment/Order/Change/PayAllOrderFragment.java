package vn.kido.shop.Fragment.Order.Change;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import vn.kido.shop.Api.APIService;
import vn.kido.shop.Api.ISubscriber;
import vn.kido.shop.Bean.BeanBase;
import vn.kido.shop.Fragment.BaseFragment;
import vn.kido.shop.Fragment.Order.Follow.FollowOrderFragment;
import vn.kido.shop.Fragment.Order.History.HistoryOrderFragment;
import vn.kido.shop.Helper.FragmentHelper;
import vn.kido.shop.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class PayAllOrderFragment extends BaseFragment implements View.OnClickListener {
    public static final int RETURN = 1;
    public static final int CANCEL = 2;
    TextView orderCode, title, message;
    RecyclerView recycler;
    EditText note;
    Button submit;
    List<BeanReason> reasons = new ArrayList<>();


    RecyclerView.Adapter<IViewHolder> adapter;

    public PayAllOrderFragment() {
        // Required empty public constructor
    }

    public static PayAllOrderFragment newInstance(int type, String orderCode) {
        Bundle args = new Bundle();
        args.putString("order_code", orderCode);
        args.putInt("type", type);
        PayAllOrderFragment fragment = new PayAllOrderFragment();
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
        rootView = inflater.inflate(R.layout.fragment_pay_all_order, container, false);
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
                orderCode = rootView.findViewById(R.id.order_code);
                title = rootView.findViewById(R.id.title);
                recycler = rootView.findViewById(R.id.recycler);
                note = rootView.findViewById(R.id.note);
                submit = rootView.findViewById(R.id.submit);
                message = rootView.findViewById(R.id.message);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        switch (getArguments().getInt("type")) {
                            case RETURN:
                                title.setText(getString(R.string.pay_all_order));
                                break;
                            case CANCEL:
                                title.setText(getString(R.string.cancel_order));
                                break;
                        }
                        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
                        adapter = new RecyclerView.Adapter<IViewHolder>() {
                            @NonNull
                            @Override
                            public IViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_reason, parent, false);
                                return new IViewHolder(itemView);
                            }

                            @Override
                            public void onBindViewHolder(@NonNull IViewHolder holder, int position) {
                                final BeanReason beanReason = reasons.get(position);
                                if (beanReason != null) {
                                    holder.reason.setText(reasons.get(position).getReason());
                                    if (beanReason.isSelected()) {
                                        holder.icon.setImageResource(R.drawable.ic_tick_active);
                                    } else {
                                        holder.icon.setImageResource(R.drawable.ic_tick_inactive);
                                    }
                                }

                                holder.itemView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        for (BeanReason bean : reasons) {
                                            bean.setSelected(false);
                                        }
                                        beanReason.setSelected(true);
                                        notifyDataSetChanged();
                                    }
                                });
                            }

                            @Override
                            public int getItemCount() {
                                return reasons.size();
                            }
                        };
                        recycler.setAdapter(adapter);
                        getReason();

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
            case R.id.submit:
                message.setVisibility(View.GONE);
                String n = note.getText().toString().trim();
                if (n.equals("")) {
                    switch (getArguments().getInt("type")) {
                        case RETURN:
                            message.setText(getString(R.string.require_retail));
                            message.setVisibility(View.VISIBLE);
                            break;
                        case CANCEL:
                            message.setText(getString(R.string.require_cancel));
                            message.setVisibility(View.VISIBLE);
                            break;
                    }
                    return;
                }
                String reason = "";
                for (BeanReason beanReason : reasons) {
                    if (beanReason.isSelected) {
                        reason = beanReason.getReason();
                        break;
                    }
                }
                submit.setOnClickListener(null);
                switch (getArguments().getInt("type")) {
                    case RETURN:
                        submitReturn(n, reason);
                        break;
                    case CANCEL:
                        submitCancel(n, reason);
                        break;
                }

                break;
        }
    }


    private void submitReturn(String note, String reason) {
        APIService.getInstance().confirmReturn(getArguments().getString("order_code"), note, reason)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ISubscriber() {
                    @Override
                    public void done() {
                        super.done();
                        submit.setOnClickListener(PayAllOrderFragment.this);
                    }

                    @Override
                    public void excute(JSONObject jsonObject) {
                        FragmentHelper.add(FollowOrderFragment.newInstance());
                    }
                });
    }


    private void submitCancel(String note, String reason) {
        APIService.getInstance().confirmCancelOrder(getArguments().getString("order_code"), note, reason)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ISubscriber() {
                    @Override
                    public void done() {
                        super.done();
                        submit.setOnClickListener(PayAllOrderFragment.this);
                    }

                    @Override
                    public void excute(JSONObject jsonObject) {
                        switch (getArguments().getInt("type")) {
                            case RETURN:
                                FragmentHelper.add(FollowOrderFragment.newInstance());
                                break;
                            case CANCEL:
                                FragmentHelper.add(HistoryOrderFragment.newInstance(HistoryOrderFragment.TAB_TOTAL));
                                break;
                        }

                    }
                });
    }

    private void getReason() {
        switch (getArguments().getInt("type")) {
            case RETURN:
                getReasonReturn();
                break;
            case CANCEL:
                getReasonCancel();
                break;
        }
    }

    private void getReasonReturn() {
        APIService.getInstance().getReasons(getArguments().getString("order_code"))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ISubscriber() {
                    @Override
                    public void excute(JSONObject jsonObject) {
                        try {
                            JSONObject data = jsonObject.getJSONObject("data");
                            JSONArray strs = data.getJSONArray("reason");
                            for (int i = 0; i < strs.length(); i++) {
                                BeanReason beanReason = new BeanReason();
                                beanReason.setReason(strs.getString(i));
                                beanReason.setSelected(false);
                                if (i == 0) {
                                    beanReason.setSelected(true);
                                }
                                reasons.add(beanReason);
                            }
                            adapter.notifyDataSetChanged();

                            orderCode.setText(data.getString("order_code_return"));
                            submit.setOnClickListener(PayAllOrderFragment.this);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });
    }

    private void getReasonCancel() {
        APIService.getInstance().getReasonCancel(getArguments().getString("order_code"))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ISubscriber() {
                    @Override
                    public void excute(JSONObject jsonObject) {
                        try {
                            JSONObject data = jsonObject.getJSONObject("data");
                            JSONArray strs = data.getJSONArray("reason");
                            for (int i = 0; i < strs.length(); i++) {
                                BeanReason beanReason = new BeanReason();
                                beanReason.setReason(strs.getString(i));
                                beanReason.setSelected(false);
                                if (i == 0) {
                                    beanReason.setSelected(true);
                                }
                                reasons.add(beanReason);
                            }
                            adapter.notifyDataSetChanged();

                            orderCode.setText(data.getString("order_code"));
                            submit.setOnClickListener(PayAllOrderFragment.this);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });
    }

    class BeanReason extends BeanBase {
        private String reason;
        private boolean isSelected;

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }

        public boolean isSelected() {
            return isSelected;
        }

        public void setSelected(boolean selected) {
            isSelected = selected;
        }
    }

    class IViewHolder extends RecyclerView.ViewHolder {
        TextView reason;
        ImageView icon;

        public IViewHolder(View itemView) {
            super(itemView);
            reason = itemView.findViewById(R.id.reason);
            icon = itemView.findViewById(R.id.icon);
        }
    }
}
