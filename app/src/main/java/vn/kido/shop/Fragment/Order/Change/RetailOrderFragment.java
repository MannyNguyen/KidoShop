package vn.kido.shop.Fragment.Order.Change;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import vn.kido.shop.Adapter.ProductRetailAdapter;
import vn.kido.shop.Api.APIService;
import vn.kido.shop.Api.ISubscriber;
import vn.kido.shop.Bean.BeanRetail;
import vn.kido.shop.Fragment.BaseFragment;
import vn.kido.shop.Fragment.Dialog.ErrorDialogFragment;
import vn.kido.shop.Helper.FragmentHelper;
import vn.kido.shop.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class RetailOrderFragment extends BaseFragment implements View.OnClickListener {
    public static final String CHANGE = "CHANGE";
    public static final String PAY = "PAY";
    public static final String RETAIL_ORDER= "RETAIL_ORDER";

    RecyclerView recycler;
    View submit;
    ProductRetailAdapter adapter;
    List<BeanRetail> retails;
    Subscription subscription;
    TextView txtNoOrder;

    public RetailOrderFragment() {
        // Required empty public constructor
    }

    public static RetailOrderFragment newInstance(String type) {

        Bundle args = new Bundle();
        args.putString("type", type);
        RetailOrderFragment fragment = new RetailOrderFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView != null) {
            return rootView;
        }
        rootView = inflater.inflate(R.layout.fragment_retail_order, container, false);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (isLoaded) {
            return;
        }
        TextView title = rootView.findViewById(R.id.title);
        if (getArguments().getString("type").equals(CHANGE)) {
            title.setText(getString(R.string.change_product_single));
        } else if (getArguments().getString("type").equals(PAY)) {
            title.setText(getString(R.string.pay_product_single));
        }
        threadInit = new Thread(new Runnable() {
            @Override
            public void run() {
                recycler = rootView.findViewById(R.id.recycler);
                txtNoOrder = rootView.findViewById(R.id.txt_no_order);
                submit = rootView.findViewById(R.id.submit);
                retails = new ArrayList<>();
                adapter = new ProductRetailAdapter(RetailOrderFragment.this, recycler, retails);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
                        recycler.setAdapter(adapter);
                        getRetails();
                    }
                });
            }
        });
        threadInit.start();
        isLoaded = true;
    }

    private void getRetails() {
        retails.clear();
        adapter.notifyDataSetChanged();
        showProgress();
        subscription = APIService.getInstance().getRetails()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ISubscriber() {
                    @Override
                    public void done() {
                        super.done();
                        hideProgress();
                    }

                    @Override
                    public void excute(JSONObject jsonObject) {
                        try {
                            Type listType = new TypeToken<List<BeanRetail>>() {
                            }.getType();
                            final List<BeanRetail> items = new Gson().fromJson(jsonObject.getString("data"), listType);
                            for (BeanRetail obj : items) {
                                retails.add(obj);
                            }
                            if (items.size() == 0) {
                                txtNoOrder.setVisibility(View.VISIBLE);
                                submit.setVisibility(View.GONE);
                            } else {
                                txtNoOrder.setVisibility(View.GONE);
                                submit.setVisibility(View.VISIBLE);
                                submit.setOnClickListener(RetailOrderFragment.this);
                            }
                            adapter.notifyDataSetChanged();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (subscription != null) {
            subscription.unsubscribe();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submit:
                try {
                    showProgress();
                    submit.setOnClickListener(null);
                    JSONArray jsonArray = new JSONArray();
                    for (BeanRetail beanRetail : retails) {
                        if (!beanRetail.isSelected()) {
                            continue;
                        }
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("id", beanRetail.getId());
                        jsonObject.put("quantity", 1);
                        jsonArray.put(jsonObject);
                    }
                    if (jsonArray.length() == 0) {
                        hideProgress();
                        submit.setOnClickListener(RetailOrderFragment.this);
                        ErrorDialogFragment messageDialogFragment = ErrorDialogFragment.newInstance(getString(R.string.choose_product_change), RETAIL_ORDER);
                        messageDialogFragment.show(getActivity().getSupportFragmentManager(), messageDialogFragment.getClass().getName());
                        return;
                    }
                    bookReturn(jsonArray.toString());

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private void bookReturn(String data) {
        APIService.getInstance().bookReturn(data)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ISubscriber() {
                    @Override
                    public void done() {
                        super.done();
                        hideProgress();
                        submit.setOnClickListener(RetailOrderFragment.this);
                    }

                    @Override
                    public void excute(JSONObject jsonObject) {
                        try {
                            JSONObject data = jsonObject.getJSONObject("data");
                            String cartInfo = data.getString("cart_info");
                            FragmentHelper.add(ConfirmRetailOrderFragment.newInstance(getArguments().getString("type"), cartInfo));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    public void updateList(int id) {
        if (retails == null) {
            return;
        }

        for (BeanRetail beanRetail : retails) {
            if (beanRetail.getId() == id) {
                beanRetail.setSelected(false);
            }
        }

        adapter.notifyDataSetChanged();

    }
}
