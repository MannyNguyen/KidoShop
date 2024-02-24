package vn.kido.shop.Fragment.Cart;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import vn.kido.shop.Api.APIService;
import vn.kido.shop.Api.ISubscriber;
import vn.kido.shop.Bean.BeanAttribute;
import vn.kido.shop.Class.CmmFunc;
import vn.kido.shop.Fragment.BaseFragment;
import vn.kido.shop.Helper.FragmentHelper;
import vn.kido.shop.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class UpdateQuantityFragment extends BaseFragment implements View.OnClickListener {

    public static final String UPDATE_CART = "UPDATE_CART";
    public static final String UPDATE_PROGRAME_CART = "UPDATE_PROGRAME_CART";

    com.shawnlin.numberpicker.NumberPicker numberPicker;
    BeanAttribute current;
    List<BeanAttribute> attributes;
    View submit;


    public UpdateQuantityFragment() {
        // Required empty public constructor
    }

    public static UpdateQuantityFragment newInstance(int idProduct, BeanAttribute current, List<BeanAttribute> attributes, String type) {
        Bundle args = new Bundle();
        args.putInt("id_product", idProduct);
        args.putString("current", new Gson().toJson(current));
        args.putString("attributes", new Gson().toJson(attributes));
        args.putString("type", type);
        UpdateQuantityFragment fragment = new UpdateQuantityFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static UpdateQuantityFragment newInstance(int idProduct, BeanAttribute current, List<BeanAttribute> attributes, int eventRewardId, String type, int max) {
        Bundle args = new Bundle();
        args.putInt("id_product", idProduct);
        args.putInt("event_reward_id", eventRewardId);
        args.putString("current", new Gson().toJson(current));
        args.putString("attributes", new Gson().toJson(attributes));
        args.putString("type", type);
        args.putInt("max", max);
        UpdateQuantityFragment fragment = new UpdateQuantityFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView != null) {
            return rootView;
        }
        rootView = inflater.inflate(R.layout.fragment_update_quantity, container, false);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (isLoaded) {
            return;
        }
        Gson gson = new Gson();
        current = gson.fromJson(getArguments().getString("current"), BeanAttribute.class);
        Type listGift = new TypeToken<List<BeanAttribute>>() {
        }.getType();
        attributes = new Gson().fromJson(getArguments().getString("attributes"), listGift);

        threadInit = new Thread(new Runnable() {
            @Override
            public void run() {
                final TextView title = rootView.findViewById(R.id.title);
                final TextView unit = rootView.findViewById(R.id.unit);
                final TextView maxUnit = rootView.findViewById(R.id.max_unit);
                final EditText number = rootView.findViewById(R.id.number);
                final TextView price = rootView.findViewById(R.id.price);
                final TextView total = rootView.findViewById(R.id.total);

                numberPicker = rootView.findViewById(R.id.number_picker);

                submit = rootView.findViewById(R.id.submit);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        rootView.findViewById(R.id.outer).setOnClickListener(UpdateQuantityFragment.this);
                        submit.setOnClickListener(UpdateQuantityFragment.this);

                        numberPicker.setMinValue(0);
                        if (getArguments().containsKey("max")) {
                            int max = getArguments().getInt("max");
                            //int moneyThisItem = current.getValue() * current.getMoney();
                            numberPicker.setMaxValue(max);
                        } else {
                            numberPicker.setMaxValue(999);
                        }

                        title.setText(getString(R.string.number) + " " + current.getName());
                        unit.setText("/ " + current.getName());
                        if (current.getId() == (attributes.get(attributes.size() - 1)).getId()) {
                            maxUnit.setVisibility(View.GONE);
                        } else {
                            maxUnit.setVisibility(View.VISIBLE);
                            //   maxUnit.setText("(" + current.getName() + " " + current.getQuantity() + " " + attributes.get(attributes.size() - 1).getName() + ")");
                            maxUnit.setText("(" + current.getNamePerUnit() + ")");
                        }

                        price.setText(CmmFunc.formatMoney(current.getMoney(), false));
                        number.setText(current.getValue() + "");
                        numberPicker.setValue(current.getValue());
                        total.setText(CmmFunc.formatMoney(current.getValue() * current.getMoney(), false));
//
//                        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
//                            @Override
//                            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
//                                number.setText("x" + newVal);
//                                total.setText(CmmFunc.formatMoney(newVal * current.getMoney(), false));
//                            }
//                        });

                        numberPicker.setOnValueChangedListener(new com.shawnlin.numberpicker.NumberPicker.OnValueChangeListener() {
                            @Override
                            public void onValueChange(com.shawnlin.numberpicker.NumberPicker picker, int oldVal, int newVal) {
                                number.setText(newVal + "");
                                total.setText(CmmFunc.formatMoney(newVal * current.getMoney(), false));
                            }
                        });
                        numberPicker.setWrapSelectorWheel(false);
                        numberPicker.setDividerThickness(0);
                        numberPicker.setTextColor(ContextCompat.getColor(getContext(), R.color.main_gray));
                        numberPicker.setTextSize(R.dimen.s_18);
                        numberPicker.setSelectedTextSize(R.dimen.s_22);


                        number.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                            @Override
                            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                                if (actionId == EditorInfo.IME_ACTION_DONE) {
                                    if (number.getText().toString().equals("")) {
                                        number.setText(numberPicker.getValue() + "");
                                        number.clearFocus();
                                        CmmFunc.hideKeyboard(getActivity());
                                        return true;
                                    }

                                    int value = Integer.parseInt(number.getText().toString());
                                    if (value > numberPicker.getMaxValue()) {
                                        number.setText(numberPicker.getMaxValue() + "");
                                        numberPicker.setValue(numberPicker.getMaxValue());
                                        number.clearFocus();
                                        CmmFunc.hideKeyboard(getActivity());
                                        return true;
                                    }
                                    numberPicker.setValue(value);
                                    number.clearFocus();
                                    total.setText(CmmFunc.formatMoney(value * current.getMoney(), false));
                                    CmmFunc.hideKeyboard(getActivity());
                                    return true;
                                }
                                return false;
                            }
                        });

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
            case R.id.outer:
                FragmentHelper.pop(getActivity());
                break;
            case R.id.submit:
                showProgress();
                //submit.setOnClickListener(null);
                int count = 0;
//                for (BeanAttribute beanAttribute : attributes) {
//                    if (beanAttribute.getId() == current.getId()) {
//                        beanAttribute.setValue(numberPicker.getValue());
//                        count += beanAttribute.getQuantity() * beanAttribute.getValue();
//                        break;
//                    }
//                    //count += beanAttribute.getQuantity() * beanAttribute.getValue();
//                }
                current.setValue(numberPicker.getValue());
                count = current.getQuantity() * current.getValue();

                if (getArguments().getString("type").equals(UPDATE_CART)) {
                    APIService.getInstance().updateProductToCart(getArguments().getInt("id_product"), count, current.getId())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new ISubscriber() {
                                @Override
                                public void done() {
                                    super.done();
                                    hideProgress();
                                    submit.setOnClickListener(UpdateQuantityFragment.this);
                                }

                                @Override
                                public void excute(JSONObject jsonObject) {
                                    FragmentHelper.pop(getActivity());
                                }
                            });
                } else if (getArguments().getString("type").equals(UPDATE_PROGRAME_CART)) {
                    APIService.getInstance().updateEventReward(getArguments().getInt("event_reward_id"), getArguments().getInt("id_product"), count)
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
                                    FragmentHelper.pop(getActivity());
                                }
                            });
                }


                break;
        }
    }
}
