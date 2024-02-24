package vn.kido.shop.Fragment.Order.Change;


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

import vn.kido.shop.Class.CmmFunc;
import vn.kido.shop.Fragment.BaseFragment;
import vn.kido.shop.Helper.FragmentHelper;
import vn.kido.shop.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class UpdateQuantityRetailFragment extends BaseFragment implements View.OnClickListener {

    com.shawnlin.numberpicker.NumberPicker numberPicker;
    public QuantityListener quantityListener;

    public UpdateQuantityRetailFragment() {
        // Required empty public constructor
    }

    public static UpdateQuantityRetailFragment newInstance(int position, int value, String minUnit) {

        Bundle args = new Bundle();
        args.putInt("position", position);
        args.putInt("value", value);
        args.putString("min_unit", minUnit);
        UpdateQuantityRetailFragment fragment = new UpdateQuantityRetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView != null) {
            return rootView;
        }
        rootView = inflater.inflate(R.layout.fragment_update_quantity_retail, container, false);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (isLoaded) {
            return;
        }
        TextView title = rootView.findViewById(R.id.title);
        title.setText(getString(R.string.quantity) + " " + getArguments().getString("min_unit"));
        threadInit = new Thread(new Runnable() {
            @Override
            public void run() {
                numberPicker = rootView.findViewById(R.id.number_picker);
                final EditText number = rootView.findViewById(R.id.number);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        number.setText(getArguments().getInt("value") + "");
                        numberPicker.setMinValue(1);
                        numberPicker.setMaxValue(999);
                        numberPicker.setValue(getArguments().getInt("value"));
                        numberPicker.setWrapSelectorWheel(false);
                        numberPicker.setDividerThickness(0);
                        numberPicker.setTextColor(ContextCompat.getColor(getContext(), R.color.main_gray));
                        numberPicker.setTextSize(R.dimen.s_18);
                        numberPicker.setSelectedTextSize(R.dimen.s_22);
                        numberPicker.setOnValueChangedListener(new com.shawnlin.numberpicker.NumberPicker.OnValueChangeListener() {
                            @Override
                            public void onValueChange(com.shawnlin.numberpicker.NumberPicker picker, int oldVal, int newVal) {
                                number.setText(newVal + "");
                            }
                        });
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
                                    CmmFunc.hideKeyboard(getActivity());
                                    return true;
                                }
                                return false;
                            }
                        });
                        rootView.findViewById(R.id.submit).setOnClickListener(UpdateQuantityRetailFragment.this);
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
                try {
                    int value = numberPicker.getValue();
                    quantityListener.excute(getArguments().getInt("position"), value);
                    FragmentHelper.pop(getActivity());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    public interface QuantityListener {
        void excute(int position, int value);
    }
}

