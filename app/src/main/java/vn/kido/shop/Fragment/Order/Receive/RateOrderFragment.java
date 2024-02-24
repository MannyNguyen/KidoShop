package vn.kido.shop.Fragment.Order.Receive;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatRatingBar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import vn.kido.shop.Api.APIService;
import vn.kido.shop.Api.ISubscriber;
import vn.kido.shop.Class.CmmFunc;
import vn.kido.shop.Fragment.BaseFragment;
import vn.kido.shop.Fragment.Common.HomeFragment;
import vn.kido.shop.Helper.FragmentHelper;
import vn.kido.shop.Helper.LocationHelper;
import vn.kido.shop.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class RateOrderFragment extends BaseFragment {
    ImageView shipperAvatar;
    TextView shipperName, npp, shopName, address, time, orderCode, quantityProduct, totalPay, comment, productHornor;
    AppCompatRatingBar ratingBar;
    EditText note;
    Button submit;
    int size;

    public RateOrderFragment() {
        // Required empty public constructor
    }

    public static RateOrderFragment newInstance(String orderCode) {
        Bundle args = new Bundle();
        args.putString("order_code", orderCode);
        RateOrderFragment fragment = new RateOrderFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView != null) {
            return rootView;
        }
        rootView = inflater.inflate(R.layout.fragment_rate_order, container, false);
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
                shipperAvatar = rootView.findViewById(R.id.shipper_avatar);
                shipperName = rootView.findViewById(R.id.shipper_name);
                npp = rootView.findViewById(R.id.npp);
                shopName = rootView.findViewById(R.id.shop_name);
                address = rootView.findViewById(R.id.address);
                time = rootView.findViewById(R.id.time);
                orderCode = rootView.findViewById(R.id.order_code);
                quantityProduct = rootView.findViewById(R.id.quantity_product);
                productHornor = rootView.findViewById(R.id.product_hornor);
                totalPay = rootView.findViewById(R.id.total_pay);
                ratingBar = rootView.findViewById(R.id.rating_bar);
                comment = rootView.findViewById(R.id.comment);
                note = rootView.findViewById(R.id.note);
                submit = rootView.findViewById(R.id.submit);
                final TextView title = rootView.findViewById(R.id.title);
                size = CmmFunc.convertDpToPx(getContext(), 240);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        title.setText("Phản hồi");
                        getFeedbackInfo(getArguments().getString("order_code"));

                        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                            @Override
                            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                                if (rating == 1.0) {
                                    comment.setText("Chưa hài lòng");
                                } else if (rating == 2.0) {
                                    comment.setText("Cần cải thiện");
                                } else if (rating == 3.0) {
                                    comment.setText("Bình thường");
                                } else if (rating == 4.0) {
                                    comment.setText("Tốt");
                                } else if (rating == 5.0) {
                                    comment.setText("Tuyệt vời");
                                }
                            }
                        });

                        submit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                confirmReceiveOrder(getArguments().getString("order_code"), (int) ratingBar.getRating(), note.getText().toString());
                            }
                        });

                    }
                });
            }
        });
        threadInit.start();
        isLoaded = true;
    }

    private void getFeedbackInfo(String ordercode) {
        showProgress();
        APIService.getInstance().getFeedbackInfo(ordercode)
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
                            JSONObject data = jsonObject.getJSONObject("data");
                            if (!TextUtils.isEmpty(data.getString("avatar"))) {
                                Picasso.get().load(data.getString("avatar")).resize(size, size).centerInside().into(shipperAvatar);
                            }
                            shipperName.setText(data.getString("shipper_name"));
                            npp.setText(data.getString("supplier_name"));
                            orderCode.setText(data.getString("order_code"));
                            shopName.setText(data.getString("shop_name"));
                            address.setText(data.getString("address_delivery"));
                            time.setText(new DateTime(data.getLong("delivery_time")).toString("HH:mm - dd/MM/yyyy"));
                            totalPay.setText(CmmFunc.formatMoney(data.getInt("total_money"), false));
                            quantityProduct.setText(data.getString("product_count") + " " + getString(R.string.product));
                            productHornor.setText(data.getInt("gift_count")+"");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void confirmReceiveOrder(String ordercode, int rate, String rateOrder) {
        showProgress();
        APIService.getInstance().confirmReceiveOrder(ordercode, rate, rateOrder)
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
                        updateLocation();
//                        rootView.findViewById(R.id.container_verify).setVisibility(View.GONE);
//                        rootView.findViewById(R.id.container_verify_success).setVisibility(View.VISIBLE);
                        FragmentHelper.pop(getActivity(), HomeFragment.class);

                    }
                });
    }

    private void updateLocation() {
        new LocationHelper().getLastLocation(new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                if (locationResult != null) {
                    APIService.getInstance().updateLocation(3, locationResult.getLastLocation().getLatitude(), locationResult.getLastLocation().getLongitude());
                }
            }
        });
    }
}
