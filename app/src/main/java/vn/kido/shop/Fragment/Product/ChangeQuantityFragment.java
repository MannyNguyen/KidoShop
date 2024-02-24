package vn.kido.shop.Fragment.Product;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.json.JSONObject;

import vn.kido.shop.Class.CmmFunc;
import vn.kido.shop.Fragment.BaseFragment;
import vn.kido.shop.Helper.FragmentHelper;
import vn.kido.shop.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChangeQuantityFragment extends BaseFragment {
    TextView unitName, unitPrice, txtTotalPayment, txtUnit, txtQuantity;
    EditText numberTotal;
    com.shawnlin.numberpicker.NumberPicker numberPicker;
    Button submit;
    FrameLayout outer;
//    BeanAttribute current;
//    List<BeanAttribute> attributes;

    public ChangeQuantityFragment() {
        // Required empty public constructor
    }

    public static ChangeQuantityFragment newInstance(String name, int unitMoney, String maxUnit, int position) {
        Bundle args = new Bundle();
        args.putString("name", name);
        args.putString("max_unit", maxUnit);
        args.putInt("unit_money", unitMoney);
        args.putInt("position", position);
        ChangeQuantityFragment fragment = new ChangeQuantityFragment();
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
        rootView = inflater.inflate(R.layout.fragment_change_quantity, container, false);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (isLoaded) {
            return;
        }
//        Gson gson = new Gson();
//        current = gson.fromJson(getArguments().getString("current"), BeanAttribute.class);
//        Type listAtts = new TypeToken<List<BeanAttribute>>() {
//        }.getType();
//        attributes = new Gson().fromJson(getArguments().getString("attributes"), listAtts);
        threadInit = new Thread(new Runnable() {
            @Override
            public void run() {
                txtQuantity = rootView.findViewById(R.id.txt_quantity);
                unitName = rootView.findViewById(R.id.unit_name);
                numberPicker = rootView.findViewById(R.id.number_picker);
                unitPrice = rootView.findViewById(R.id.unit_price);
                numberTotal = rootView.findViewById(R.id.number_total);
                numberPicker = rootView.findViewById(R.id.number_picker);
                txtTotalPayment = rootView.findViewById(R.id.txt_total_payment);
                txtUnit = rootView.findViewById(R.id.txt_unit);
                submit = rootView.findViewById(R.id.submit);
                outer = rootView.findViewById(R.id.outer);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final DetailProductFragment detailProductFragment = (DetailProductFragment) FragmentHelper.findFragmentByTag(DetailProductFragment.class);
                        unitName.setText(getArguments().getString("name"));
                        txtUnit.setText(" / " + getArguments().getString("name"));
                        unitPrice.setText(CmmFunc.formatMoney(getArguments().getInt("unit_money"), false));
                        numberPicker.setMinValue(0);
                        numberPicker.setMaxValue(999);
                        numberPicker.setValue(detailProductFragment.product.getAttributes().get(getArguments().getInt("position")).getValue());
                        numberTotal.setText(detailProductFragment.product.getAttributes().get(getArguments().getInt("position")).getValue() + "");
                        txtTotalPayment.setText(CmmFunc.formatMoney((getArguments().getInt("unit_money") * numberPicker.getValue()), false));
                        txtQuantity.setText(getArguments().getString("max_unit"));
                        submit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (detailProductFragment != null) {
//                                    for (BeanAttribute beanAttribute : attributes) {
//                                        if (beanAttribute.getId() == current.getId()) {
//                                            beanAttribute.setValue(numberPicker.getValue());
//                                            continue;
//                                        }
//                                    }
                                    detailProductFragment.product.getAttributes().get(getArguments().getInt("position")).setValue(numberPicker.getValue());
                                    detailProductFragment.detailProductQuantityAdapter.notifyDataSetChanged();
                                    detailProductFragment.isUpdateQuantity = false;
                                //    detailProductFragment.product.getAttributes().get(getArguments().getInt("position")).setQuantity(numberPicker.getValue());
//                                    for (int i = 0; i < detailProductFragment.product.getAttributes().size(); i++) {
//                                        if (detailProductFragment.product.getAttributes().get(i).getId() ==
//                                                (detailProductFragment.product.getAttributes().get(getArguments().getInt("position")).getId())) {
//                                            detailProductFragment.product.getAttributes().get(i).setQuantity();
//                                        }
//                                    }
                                }
                                FragmentHelper.pop(getActivity());
                            }
                        });

//                        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
//                            @Override
//                            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
//                                numberTotal.setText("x" + numberPicker.getValue());
//                                txtTotalPayment.setText(CmmFunc.formatMoney((getArguments().getInt("unit_money") * numberPicker.getValue()), false));
//                            }
//                        });
                        numberPicker.setOnValueChangedListener(new com.shawnlin.numberpicker.NumberPicker.OnValueChangeListener() {
                            @Override
                            public void onValueChange(com.shawnlin.numberpicker.NumberPicker picker, int oldVal, int newVal) {
                                numberTotal.setText(numberPicker.getValue() + "");
                                txtTotalPayment.setText(CmmFunc.formatMoney((getArguments().getInt("unit_money") * numberPicker.getValue()), false));
                            }
                        });

                        numberPicker.setWrapSelectorWheel(false);
                        numberPicker.setDividerThickness(0);
                        numberPicker.setTextColor(ContextCompat.getColor(getContext(), R.color.main_gray));
                        numberPicker.setTextSize(R.dimen.s_18);
                        numberPicker.setSelectedTextSize(R.dimen.s_22);

                        numberTotal.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            }

                            @Override
                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                if (numberTotal.getText().toString() != null && !numberTotal.getText().toString().equals("")) {
                                    numberPicker.setValue(Integer.parseInt(numberTotal.getText().toString()));
                                    txtTotalPayment.setText(CmmFunc.formatMoney((getArguments().getInt("unit_money") * numberPicker.getValue()), false));
                                } else {
                                    numberTotal.setText("0");
                                    numberPicker.setValue(0);
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable editable) {

                            }
                        });

                        outer.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (detailProductFragment != null) {
                                    detailProductFragment.product.getAttributes().get(getArguments().getInt("position")).setValue(numberPicker.getValue());
                                    detailProductFragment.detailProductQuantityAdapter.notifyDataSetChanged();
                                    detailProductFragment.isUpdateQuantity = false;
                                }
                                FragmentHelper.pop(getActivity());
                            }
                        });

                    }
                });
            }
        });
        threadInit.start();
        isLoaded = true;
    }
}
