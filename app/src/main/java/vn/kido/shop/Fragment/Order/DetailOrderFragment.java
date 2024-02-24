package vn.kido.shop.Fragment.Order;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import vn.kido.shop.Adapter.DetailOrderAdapter;
import vn.kido.shop.Adapter.GiftAdapter;
import vn.kido.shop.Adapter.ReturnRetailOrderAdapter;
import vn.kido.shop.Api.APIService;
import vn.kido.shop.Api.ISubscriber;
import vn.kido.shop.Bean.BeanProduct;
import vn.kido.shop.Class.CmmFunc;
import vn.kido.shop.Fragment.BaseFragment;
import vn.kido.shop.Fragment.Cart.CartFragment;
import vn.kido.shop.Fragment.Order.Change.PayAllOrderFragment;
import vn.kido.shop.Fragment.Order.Receive.VerifyReceiveOrderFragment;
import vn.kido.shop.Helper.FragmentHelper;
import vn.kido.shop.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailOrderFragment extends BaseFragment implements View.OnClickListener {
    TextView txtOrderCode, txtSender, txtAddress, txtPhone, txtReceiveStatus, txtTotal, txtBonusPoint, txtNotice, confirmCode,
            txtDiscount, txtDiscountOrder, txtRealPay, note, typeName, reason, txtShipperName, txtShipperPhone;
    RecyclerView recycler, recyclerGift;
    LinearLayout llPayInfo, llNoticeConfirm, llTotal, llBonusProduct, llBonusOrder, llPoint, llReason;
    Button btnFullOrder, btnReturnOrder, btnCancelOrder;
    View codeContainer, noteContainer, shipperContainer;
    List<BeanProduct> beanProducts;
    View frameGifts;
    ImageView ivCallShipper, ivSMSShipper;

    public DetailOrderFragment() {
        // Required empty public constructor
    }

    public static DetailOrderFragment newInstance(String orderCode) {
        Bundle args = new Bundle();
        args.putString("order_code", orderCode);
        DetailOrderFragment fragment = new DetailOrderFragment();
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
        rootView = inflater.inflate(R.layout.fragment_detail_order, container, false);
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
                typeName = rootView.findViewById(R.id.type_name);
                txtOrderCode = rootView.findViewById(R.id.order_code);
                txtSender = rootView.findViewById(R.id.txt_sender);
                txtAddress = rootView.findViewById(R.id.txt_address);
                txtPhone = rootView.findViewById(R.id.txt_phone);
                txtReceiveStatus = rootView.findViewById(R.id.txt_receive_status);
                recycler = rootView.findViewById(R.id.recycler);
                frameGifts = rootView.findViewById(R.id.frame_gifts);
                recyclerGift = rootView.findViewById(R.id.recycler_gift);
                txtTotal = rootView.findViewById(R.id.txt_total);
                txtBonusPoint = rootView.findViewById(R.id.txt_bonus_point);
                txtNotice = rootView.findViewById(R.id.txt_notice);
                confirmCode = rootView.findViewById(R.id.confirm_code);
                btnFullOrder = rootView.findViewById(R.id.btn_full_order);
                txtDiscount = rootView.findViewById(R.id.txt_discount);
                txtDiscountOrder = rootView.findViewById(R.id.txt_discount_order);
                txtRealPay = rootView.findViewById(R.id.txt_real_pay);
                codeContainer = rootView.findViewById(R.id.code_container);
                note = rootView.findViewById(R.id.note);
                noteContainer = rootView.findViewById(R.id.note_container);
                btnReturnOrder = rootView.findViewById(R.id.btn_return_order);
                llPayInfo = rootView.findViewById(R.id.ll_pay_info);
                llNoticeConfirm = rootView.findViewById(R.id.ll_notice_confirm);
                llTotal = rootView.findViewById(R.id.ll_total);
                llBonusProduct = rootView.findViewById(R.id.ll_bonus_product);
                llBonusOrder = rootView.findViewById(R.id.ll_bonus_order);
                llPayInfo = rootView.findViewById(R.id.ll_pay_info);
                llReason = rootView.findViewById(R.id.ll_reason);
                llPoint = rootView.findViewById(R.id.ll_point);
                reason = rootView.findViewById(R.id.reason);
                shipperContainer = rootView.findViewById(R.id.shipper_container);
                txtShipperName = rootView.findViewById(R.id.txt_shipper_name);
                txtShipperPhone = rootView.findViewById(R.id.txt_shipper_phone);
                ivCallShipper = rootView.findViewById(R.id.iv_call_shipper);
                ivSMSShipper = rootView.findViewById(R.id.iv_sms_shipper);
                btnCancelOrder = rootView.findViewById(R.id.btn_cancel_order);
                final TextView title = rootView.findViewById(R.id.title);
                rootView.findViewById(R.id.container_reorder).setOnClickListener(DetailOrderFragment.this);

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        title.setText(getString(R.string.detail_order));
                        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
                        getOrderInfo(getArguments().getString("order_code"));
                    }
                });
            }
        });
        threadInit.start();
        isLoaded = true;
    }

    private void getOrderInfo(final String orderCode) {
        showProgress();
        APIService.getInstance().getOrderInfo(orderCode)
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
                            Gson gson = new Gson();
                            beanProducts = new ArrayList<>();
                            JSONObject data = jsonObject.getJSONObject("data");

                            JSONObject orderInfo = data.getJSONObject("order_info");

                            JSONArray items = orderInfo.getJSONArray("items");
                            if (items != null) {
                                for (int i = 0; i < items.length(); i++) {
                                    BeanProduct beanProduct = gson.fromJson(items.getString(i), BeanProduct.class);
                                    beanProducts.add(beanProduct);
                                }
                            }

                            if (orderInfo.getString("verify_code").equals("")) {
                                codeContainer.setVisibility(View.GONE);
                            } else {
                                codeContainer.setVisibility(View.VISIBLE);
                                confirmCode.setText(orderInfo.getString("verify_code"));
                            }

                            if (orderInfo.getBoolean("received")) {
                                btnFullOrder.setVisibility(View.GONE);
                            } else {
                                btnFullOrder.setVisibility(View.VISIBLE);
                                btnFullOrder.setOnClickListener(DetailOrderFragment.this);
                            }

                            txtOrderCode.setText(orderCode + "");
                            txtAddress.setText(getString(R.string.order_address) + ": " + orderInfo.getString("address") + "");
                            txtReceiveStatus.setText("*" + orderInfo.getString("status_name") + "");
                            txtPhone.setText(getString(R.string.order_phone) + ": " + orderInfo.getString("phone") + "");
                            txtSender.setText(getString(R.string.order_receiver) + ": " + orderInfo.getString("fullname") + "");

                            DetailOrderAdapter adapter = new DetailOrderAdapter(DetailOrderFragment.this, recycler, beanProducts);
                            ReturnRetailOrderAdapter returnRetailOrderAdapter = new ReturnRetailOrderAdapter(DetailOrderFragment.this, recycler, beanProducts);

                            Type listGift = new TypeToken<List<BeanProduct>>() {
                            }.getType();
                            List gifts = new Gson().fromJson(orderInfo.getString("gifts"), listGift);
                            if (gifts.size() == 0) {
                                frameGifts.setVisibility(View.GONE);
                            } else {
                                frameGifts.setVisibility(View.VISIBLE);
                                GiftAdapter giftAdapter = new GiftAdapter(DetailOrderFragment.this, recyclerGift, gifts);
                                recyclerGift.setLayoutManager(new LinearLayoutManager(getContext()));
                                recyclerGift.setAdapter(giftAdapter);
                            }

                            txtTotal.setText(CmmFunc.formatMoney(Math.round(orderInfo.getInt("total_money")), false));
                            txtBonusPoint.setText("+" + orderInfo.getInt("total_point") + " Kpoint");
                            txtDiscount.setText(CmmFunc.formatMoney(orderInfo.getInt("tong_km_dong"), false));
                            txtDiscountOrder.setText(CmmFunc.formatMoney(orderInfo.getInt("tong_km_don_hang"), false));
                            txtRealPay.setText(CmmFunc.formatMoney(orderInfo.getInt("tong_thanh_toan"), false));

                            if (orderInfo.getBoolean("is_return")) {
                                btnReturnOrder.setVisibility(View.VISIBLE);
                                btnReturnOrder.setOnClickListener(DetailOrderFragment.this);
                            } else {
                                btnReturnOrder.setVisibility(View.GONE);
                            }

                            //1 - Đơn mới
                            if (orderInfo.getInt("type") == 1) {
                                typeName.setText(getString(R.string.new_order));
                                recycler.setAdapter(adapter);
                                //2 - Trả nguyên đơn
                            } else if (orderInfo.getInt("type") == 2) {
                                typeName.setText(getString(R.string.pay_full_order));
                                recycler.setAdapter(adapter);
                                llReason.setVisibility(View.VISIBLE);
                                reason.setText(orderInfo.getString("reason"));
                                //3 - Đổi hàng lẻ
                            } else if (orderInfo.getInt("type") == 3) {
                                typeName.setText(getString(R.string.change_product_single));
                                llPayInfo.setVisibility(View.GONE);
                                llNoticeConfirm.setVisibility(View.GONE);
                                noteContainer.setVisibility(View.GONE);
                                recycler.setAdapter(returnRetailOrderAdapter);
                                //4 - Trả hàng lẻ
                            } else if (orderInfo.getInt("type") == 4) {
                                typeName.setText(getString(R.string.pay_product_single));
                                llTotal.setVisibility(View.GONE);
                                llBonusOrder.setVisibility(View.GONE);
                                llBonusProduct.setVisibility(View.GONE);
                                llPoint.setVisibility(View.GONE);
                                noteContainer.setVisibility(View.GONE);
                                recycler.setAdapter(adapter);
                            } else {
                                recycler.setAdapter(adapter);
                            }

                            if (orderInfo.getString("note").equals("") || orderInfo.getString("note") == null) {
                                noteContainer.setVisibility(View.GONE);
                            } else {
                                noteContainer.setVisibility(View.VISIBLE);
                                note.setText(orderInfo.getString("note"));
                            }

                            JSONObject shipper = orderInfo.getJSONObject("shipperInfo");
                            final String shipperPhone = shipper.getString("phone");
                            if (TextUtils.isEmpty(shipperPhone)) {
                                shipperContainer.setVisibility(View.GONE);
                                ivCallShipper.setOnClickListener(null);
                                ivSMSShipper.setOnClickListener(null);
                            } else {
                                shipperContainer.setVisibility(View.VISIBLE);
                                txtShipperName.setText(getString(R.string.shipper) + " " + shipper.getString("fullname"));
                                txtShipperPhone.setText(getString(R.string.shipper_phone) + " " + shipperPhone);
                                ivCallShipper.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(Intent.ACTION_DIAL);
                                        intent.setData(Uri.parse("tel:" + shipperPhone));
                                        startActivity(intent);
                                    }
                                });

                                ivSMSShipper.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Uri uri = Uri.parse("smsto:" + shipperPhone);
                                        Intent it = new Intent(Intent.ACTION_SENDTO, uri);
                                        it.putExtra("sms_body", "");
                                        startActivity(it);
                                    }
                                });
                            }
                            if (orderInfo.getBoolean("is_cancel") == true) {
                                btnCancelOrder.setVisibility(View.VISIBLE);
                                btnCancelOrder.setOnClickListener(DetailOrderFragment.this);
                            } else {
                                btnCancelOrder.setVisibility(View.GONE);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_full_order:
                VerifyReceiveOrderFragment verifyReceiveOrderFragment = VerifyReceiveOrderFragment.newInstance(getArguments().getString("order_code"));
                verifyReceiveOrderFragment.show(getActivity().getSupportFragmentManager(), verifyReceiveOrderFragment.getClass().getName());
                break;
            case R.id.container_reorder:
                reOrder(getArguments().getString("order_code"));
                break;
            case R.id.btn_return_order:
                FragmentHelper.add(PayAllOrderFragment.newInstance(PayAllOrderFragment.RETURN, getArguments().getString("order_code")));
                break;
            case R.id.btn_cancel_order:
                FragmentHelper.add(PayAllOrderFragment.newInstance(PayAllOrderFragment.CANCEL, getArguments().getString("order_code")));
                break;
        }
    }

    @Override
    public void manuResume() {
        super.manuResume();
        rootView.findViewById(R.id.container_reorder).setOnClickListener(DetailOrderFragment.this);
    }

    private void reOrder(String orderCode) {
        rootView.findViewById(R.id.container_reorder).setOnClickListener(null);
        showProgress();
        APIService.getInstance().reOrder(orderCode)
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
                        FragmentHelper.add(CartFragment.newInstance());
                    }
                });
    }

}
