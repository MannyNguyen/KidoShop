package vn.kido.shop.Adapter;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import vn.kido.shop.Bean.BeanOrder;
import vn.kido.shop.Bean.BeanProduct;
import vn.kido.shop.Class.CmmFunc;
import vn.kido.shop.R;

import static vn.kido.shop.Class.GlobalClass.getContext;

public class ReceiptOrderAdapter extends RecyclerView.Adapter<ReceiptOrderAdapter.MyViewHolder> {

    Fragment fragment;
    RecyclerView recyclerView;
    List<BeanOrder> orders;

    public ReceiptOrderAdapter(Fragment fragment, RecyclerView recyclerView, List<BeanOrder> orders) {
        this.fragment = fragment;
        this.recyclerView = recyclerView;
        this.orders = orders;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_order_receipt, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        try {
            BeanOrder beanOrder = orders.get(position);
            if (beanOrder != null) {
                ItemOrderAdapter adapter = new ItemOrderAdapter(fragment, holder.recycler, beanOrder.getItems());
                holder.recycler.setAdapter(adapter);
                holder.title.setText(fragment.getString(R.string.order_code) + " " + beanOrder.getCompanyName());
                holder.thung.setText(beanOrder.getThung() + "");
                holder.le.setText(beanOrder.getLe() + "");
                holder.loc.setText(beanOrder.getLoc() + "");
                holder.totalMoney.setText(CmmFunc.formatMoney(beanOrder.getTotalMoney(), false));
                holder.totalSaleType.setText(CmmFunc.formatMoney(beanOrder.getTotalSaleType(), false));
                holder.totalSaleOrder.setText(CmmFunc.formatMoney(beanOrder.getTotalSaleOrder(), false));
                holder.totalPoint.setText(CmmFunc.formatKPoint(beanOrder.getTotalPoint()));
                holder.total.setText(CmmFunc.formatMoney(beanOrder.getTotal(), false));

                if (beanOrder.getGifts().size() == 0) {
                    holder.giftContainer.setVisibility(View.GONE);
                } else {
                    holder.giftContainer.setVisibility(View.VISIBLE);
                    createGiftText(holder.gift, beanOrder.getGifts());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void createGiftText(LinearLayout parent, List<BeanProduct> gifts) {
        try {
            for (int i = 0; i < gifts.size(); i++) {
                View itemView = LayoutInflater.from(getContext()).inflate(R.layout.row_gift_text, null, false);
                TextView position = itemView.findViewById(R.id.position);
                TextView value = itemView.findViewById(R.id.value);
                TextView numValue = itemView.findViewById(R.id.num_value);
                BeanProduct beanProduct = gifts.get(i);
                position.setText((i + 1) + "");
                value.setText(beanProduct.getName()+"");
                numValue.setText("(" + beanProduct.getNumberGiftName() + ")");
                parent.addView(itemView);
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
        TextView title, totalMoney, totalSaleType, totalSaleOrder, totalPoint, total, thung, le, loc;
        LinearLayout gift;
        View giftContainer;
        RecyclerView recycler;

        public MyViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            recycler = itemView.findViewById(R.id.recycler_items);
            recycler.setLayoutManager(new LinearLayoutManager(fragment.getContext()));
            recycler.setItemViewCacheSize(30);
            totalMoney = itemView.findViewById(R.id.total_money);
            totalSaleType = itemView.findViewById(R.id.total_sale_type);
            totalSaleOrder = itemView.findViewById(R.id.total_sale_order);
            totalPoint = itemView.findViewById(R.id.total_point);
            total = itemView.findViewById(R.id.total);
            thung = itemView.findViewById(R.id.thung);
            le = itemView.findViewById(R.id.le);
            loc = itemView.findViewById(R.id.loc);
            gift = itemView.findViewById(R.id.gift);
            giftContainer = itemView.findViewById(R.id.gift_container);
        }
    }
}
