package vn.kido.shop.Adapter;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;

import java.util.List;

import vn.kido.shop.Bean.BeanRetail;
import vn.kido.shop.Class.CmmFunc;
import vn.kido.shop.R;

public class ProductRetailAdapter extends RecyclerView.Adapter<ProductRetailAdapter.MyViewHolder> {

    Fragment fragment;
    RecyclerView recyclerView;
    List<BeanRetail> retails;
    int size;

    public ProductRetailAdapter(Fragment fragment, RecyclerView recyclerView, List<BeanRetail> retails) {
        this.fragment = fragment;
        this.recyclerView = recyclerView;
        this.retails = retails;
        size = CmmFunc.convertDpToPx(fragment.getActivity(), 240);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_product_retail, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        try {
            final BeanRetail beanRetail = retails.get(position);
            if (beanRetail != null) {
                if (!TextUtils.isEmpty(beanRetail.getImage())) {
                    Picasso.get().load(beanRetail.getImage()).resize(size, size).centerInside().into(holder.thumbnail);
                }
                holder.name.setText(beanRetail.getName());
                holder.price.setText(CmmFunc.formatMoney(Math.round(beanRetail.getPrice()), false));
                holder.priceMax.setText(CmmFunc.formatMoney(Math.round(beanRetail.getPriceMaxUnit()), false));
                holder.minUnit.setText("/ " + beanRetail.getMinUnit());
                holder.maxUnit.setText("/ " + beanRetail.getMaxUnit());
                holder.createDate.setText(new DateTime(beanRetail.getCreateDate()).toString("dd/MM/yyyy"));
                if (beanRetail.isSelected()) {
                    holder.selected.setImageResource(R.drawable.ic_tick_active);
                } else {
                    holder.selected.setImageResource(R.drawable.ic_tick_inactive);
                }
                holder.selected.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        beanRetail.setSelected(!beanRetail.isSelected());
                        notifyDataSetChanged();
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return retails.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnail, selected;
        TextView name, price, priceMax, minUnit, maxUnit, createDate;

        public MyViewHolder(View itemView) {
            super(itemView);
            thumbnail = itemView.findViewById(R.id.thumbnail);
            selected = itemView.findViewById(R.id.selected);
            name = itemView.findViewById(R.id.name);
            price = itemView.findViewById(R.id.price);
            priceMax = itemView.findViewById(R.id.price_max);
            minUnit = itemView.findViewById(R.id.min_unit);
            maxUnit = itemView.findViewById(R.id.max_unit);
            createDate = itemView.findViewById(R.id.create_date);
        }
    }
}
