package vn.kido.shop.Adapter;

import android.graphics.Paint;
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

import java.util.List;

import vn.kido.shop.Bean.BeanProduct;
import vn.kido.shop.Class.CmmFunc;
import vn.kido.shop.R;

//me
public class ChildProductComboAdapter extends RecyclerView.Adapter<ChildProductComboAdapter.MyViewHolder> {
    Fragment fragment;
    RecyclerView recyclerView;
    List<BeanProduct> items;

    public ChildProductComboAdapter(Fragment fragment, RecyclerView recyclerView, List<BeanProduct> items) {
        this.fragment = fragment;
        this.recyclerView = recyclerView;
        this.items = items;
    }

    @NonNull
    @Override
    public ChildProductComboAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_product_combo, parent, false);
        return new ChildProductComboAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ChildProductComboAdapter.MyViewHolder holder, int position) {
        try {
            BeanProduct beanProduct = items.get(position);
            if (beanProduct != null) {
                holder.name.setText(beanProduct.getName() + "");
                holder.oldPrice.setText(CmmFunc.formatMoney(beanProduct.getOldPrice(), false));
                holder.number.setText("x"+beanProduct.getQuantity());
                holder.price.setText(CmmFunc.formatMoney(beanProduct.getPrice(), false));
                if (!TextUtils.isEmpty(beanProduct.getImage())) {
                    Picasso.get().load(beanProduct.getImage()).into(holder.thumbnail);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnail;
        TextView name, oldPrice, price, attribute, number;

        public MyViewHolder(View itemView) {
            super(itemView);
            thumbnail = itemView.findViewById(R.id.thumbnail);
            name = itemView.findViewById(R.id.name);
            oldPrice = itemView.findViewById(R.id.old_price);
            price = itemView.findViewById(R.id.price);
            attribute = itemView.findViewById(R.id.attribute);
            number = itemView.findViewById(R.id.number_in_combo);
            oldPrice.setPaintFlags(oldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
    }
}