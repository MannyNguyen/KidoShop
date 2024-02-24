package vn.kido.shop.Adapter;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import vn.kido.shop.Bean.BeanItemOrder;
import vn.kido.shop.Class.CmmFunc;
import vn.kido.shop.R;

public class ItemOrderAdapter extends RecyclerView.Adapter<ItemOrderAdapter.MyViewHolder> {
    Fragment fragment;
    RecyclerView recyclerView;
    List<BeanItemOrder> orders;

    public ItemOrderAdapter(Fragment fragment, RecyclerView recyclerView, List<BeanItemOrder> orders) {
        this.fragment = fragment;
        this.recyclerView = recyclerView;
        this.orders = orders;
    }

    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_order_receipt, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        try {
            final BeanItemOrder beanItemOrder = orders.get(position);
            if (beanItemOrder != null) {
                holder.stt.setText((position + 1) + "");
                holder.title.setText(beanItemOrder.getName());
                holder.thung.setText(beanItemOrder.getThung() + "");
                holder.le.setText(beanItemOrder.getLe() + "");
                holder.loc.setText(beanItemOrder.getLoc() + "");
                holder.price.setText(CmmFunc.formatMoney(beanItemOrder.getUnitPrice(), false));
                holder.moneyDiscount.setText(CmmFunc.formatMoney(beanItemOrder.getTotalMoneyDiscount(), false));
                holder.totalPrice.setText(CmmFunc.formatMoney(beanItemOrder.getTotalPrice(), false));
                if (beanItemOrder.getTotalPercentDiscount() == 0) {
                    holder.percentSale.setVisibility(View.GONE);
                } else {
                    holder.percentSale.setVisibility(View.VISIBLE);
                    holder.percentSale.setText("KM: " + beanItemOrder.getTotalPercentDiscount() + "%");
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView stt, title, thung, le, loc, price, moneyDiscount, totalPrice, percentSale;

        public MyViewHolder(View itemView) {
            super(itemView);
            stt = itemView.findViewById(R.id.stt);
            title = itemView.findViewById(R.id.title);
            thung = itemView.findViewById(R.id.thung);
            le = itemView.findViewById(R.id.le);
            loc = itemView.findViewById(R.id.loc);
            price = itemView.findViewById(R.id.price);
            moneyDiscount = itemView.findViewById(R.id.money_discount);
            totalPrice = itemView.findViewById(R.id.total_price);
            percentSale = itemView.findViewById(R.id.percennt_sale);
        }
    }
}
