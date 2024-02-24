package vn.kido.shop.Adapter;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.joda.time.DateTime;

import java.util.List;

import vn.kido.shop.Bean.BeanOrder;
import vn.kido.shop.Class.CmmFunc;
import vn.kido.shop.Class.GlobalClass;
import vn.kido.shop.Fragment.Order.DetailOrderFragment;
import vn.kido.shop.Helper.FragmentHelper;
import vn.kido.shop.R;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.MyViewHolder> {
    final int SINGLE = 1;
    final int MULTI = 2;
    Fragment fragment;
    RecyclerView recyclerView;
    List<BeanOrder> orders;

    public OrderAdapter(Fragment fragment, RecyclerView recyclerView, List<BeanOrder> orders) {
        this.fragment = fragment;
        this.recyclerView = recyclerView;
        this.orders = orders;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case SINGLE:
                return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_order_single, parent, false));
            case MULTI:
                return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_order_multi, parent, false));

        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        try {
            final BeanOrder beanOrder = orders.get(position);
            if (beanOrder != null) {
                holder.orderCode.setText(beanOrder.getOrderCode());
                holder.createDate.setText(new DateTime(beanOrder.getCreateDate()).toString("dd/MM/yyyy"));
                holder.llOrderCode.setVisibility(View.VISIBLE);
                if (beanOrder.getType() == 1) {
                    holder.typeOrder.setText(GlobalClass.getContext().getString(R.string.new_order));
                    holder.llOrderCode.setBackgroundResource(R.drawable.bg_pink_top_round);
                } else if (beanOrder.getType() == 2) {
                    holder.llOrderCode.setBackgroundResource(R.drawable.bg_gray_top_round);
                    holder.typeOrder.setText(GlobalClass.getContext().getString(R.string.pay_full_order));
                } else if (beanOrder.getType() == 3) {
                    holder.price.setVisibility(View.GONE);
                    holder.bottomLine.setVisibility(View.GONE);
                    holder.llTotalPay.setVisibility(View.GONE);
                    holder.llOrderCode.setBackgroundResource(R.drawable.bg_gray_top_round);
                    holder.typeOrder.setText(GlobalClass.getContext().getString(R.string.change_product_single));
                } else if (beanOrder.getType() == 4) {
                    holder.llOrderCode.setBackgroundResource(R.drawable.bg_gray_top_round);
                    holder.typeOrder.setText(GlobalClass.getContext().getString(R.string.pay_product_single));
                } else {
                    holder.llOrderCode.setVisibility(View.GONE);
                }

                if (TextUtils.isEmpty(beanOrder.getVerifyCode())) {
                    holder.verifyContainer.setVisibility(View.GONE);
                } else {
                    holder.verifyContainer.setVisibility(View.VISIBLE);
                    holder.verifyCode.setText(beanOrder.getVerifyCode());
                }

                if (getItemViewType(position) == SINGLE) {
                    //holder.orderCode.setText(beanOrder.getOrderCode());
                    //holder.price.setText(CmmFunc.formatMoney(String.format("%.0f", beanOrder.getTotalMoney()), true));
                    holder.price.setText(CmmFunc.formatMoney((beanOrder.getTotalMoney()), false));
                    holder.quantity.setText(beanOrder.getProductCount() + " " + fragment.getString(R.string.product_2));
                    if (beanOrder.getGiftCount() == 0) {
                        holder.quantityGift.setVisibility(View.GONE);
                    } else {
                        holder.quantityGift.setVisibility(View.VISIBLE);
                        holder.quantityGift.setText(beanOrder.getGiftCount() + " " + fragment.getString(R.string.gift));
                    }
                    holder.deliveryDate.setText(new DateTime(beanOrder.getSuggestTime()).toString("dd/MM/yyyy"));

                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            FragmentHelper.add(DetailOrderFragment.newInstance(beanOrder.getOrderCode()));
                        }
                    });
                }
                if (getItemViewType(position) == MULTI) {
                    if (holder.recyclerChildOrder != null) {
                        OrderChildAdapter orderChildAdapter = new OrderChildAdapter(fragment, holder.recyclerChildOrder, beanOrder.getOrderChilds());
                        holder.recyclerChildOrder.setAdapter(orderChildAdapter);
                    }
                }
                holder.totalPay.setText(CmmFunc.formatMoney(beanOrder.getTotalPay(), false));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemViewType(int position) {
        BeanOrder beanOrder = orders.get(position);
        if (beanOrder == null) {
            return -1;
        }
        if (beanOrder.getOrderChilds().size() > 1) {
            return MULTI;
        }
        return SINGLE;
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView orderCode, createDate, quantity, price, deliveryDate, totalPay, quantityGift, typeOrder, verifyCode;
        RecyclerView recyclerChildOrder;
        FrameLayout llOrderCode;
        LinearLayout llTotalPay;
        View bottomLine, verifyContainer;

        public MyViewHolder(View itemView) {
            super(itemView);
            llOrderCode = itemView.findViewById(R.id.ll_order_code);
            typeOrder = itemView.findViewById(R.id.type_order);
            orderCode = itemView.findViewById(R.id.order_code);
            createDate = itemView.findViewById(R.id.create_date);
            quantity = itemView.findViewById(R.id.quantity);
            quantityGift = itemView.findViewById(R.id.quantity_gift);
            price = itemView.findViewById(R.id.price);
            bottomLine = itemView.findViewById(R.id.bottom_line);
            verifyContainer = itemView.findViewById(R.id.verify_container);
            verifyCode = itemView.findViewById(R.id.verify_code);
            llTotalPay = itemView.findViewById(R.id.ll_total_pay);
            deliveryDate = itemView.findViewById(R.id.delivery_date);
            totalPay = itemView.findViewById(R.id.total_pay);
            recyclerChildOrder = itemView.findViewById(R.id.recycler_child_order);
            if (recyclerChildOrder != null) {
                recyclerChildOrder.setLayoutManager(new LinearLayoutManager(fragment.getContext()));
            }
        }
    }
}
