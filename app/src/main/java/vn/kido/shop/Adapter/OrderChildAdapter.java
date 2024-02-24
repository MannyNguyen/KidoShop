package vn.kido.shop.Adapter;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.joda.time.DateTime;

import java.util.List;

import vn.kido.shop.Bean.BeanOrder;
import vn.kido.shop.Class.CmmFunc;
import vn.kido.shop.Fragment.Order.DetailOrderFragment;
import vn.kido.shop.Helper.FragmentHelper;
import vn.kido.shop.R;

public class OrderChildAdapter extends RecyclerView.Adapter<OrderChildAdapter.MyViewHolder> {

    Fragment fragment;
    RecyclerView recyclerView;
    List<BeanOrder> orders;

    public OrderChildAdapter(Fragment fragment, RecyclerView recyclerView, List<BeanOrder> orders) {
        this.fragment = fragment;
        this.recyclerView = recyclerView;
        this.orders = orders;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_order_child, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        try {
            final BeanOrder beanOrder = orders.get(position);
            if (beanOrder != null) {
                holder.orderCode.setText(beanOrder.getOrderCode());
                holder.price.setText(CmmFunc.formatMoney(beanOrder.getTotalMoney(), false));
                holder.quantity.setText(beanOrder.getProductCount() + " " + fragment.getString(R.string.product_2));
                if (beanOrder.getGiftCount() == 0) {
                    holder.quantityGift.setVisibility(View.GONE);
                } else {
                    holder.quantityGift.setVisibility(View.VISIBLE);
                    holder.quantityGift.setText(beanOrder.getGiftCount() + " " + fragment.getString(R.string.gift));
                }

                holder.storeName.setText(beanOrder.getNppName());
                holder.deliveryDate.setText(new DateTime(beanOrder.getSuggestTime()).toString("dd/MM/yyyy"));
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FragmentHelper.add(DetailOrderFragment.newInstance(beanOrder.getOrderCode()));
                    }
                });
                holder.bottomLine.setVisibility(View.VISIBLE);
                if (position == orders.size() - 1) {
                    holder.bottomLine.setVisibility(View.GONE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    @Override
//    public int getItemViewType(int position) {
//        BeanOrder beanOrder = orders.get(position);
//        if (beanOrder == null) {
//            return -1;
//        }
//
//    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView orderCode, quantity, price, deliveryDate, storeName, quantityGift;
        View bottomLine;

        public MyViewHolder(View itemView) {
            super(itemView);
            orderCode = itemView.findViewById(R.id.order_code);
            storeName = itemView.findViewById(R.id.store_name);
            quantity = itemView.findViewById(R.id.quantity);
            quantityGift = itemView.findViewById(R.id.quantity_gift);
            price = itemView.findViewById(R.id.price);
            deliveryDate = itemView.findViewById(R.id.delivery_date);
            bottomLine = itemView.findViewById(R.id.bottom_line);
        }
    }
}
