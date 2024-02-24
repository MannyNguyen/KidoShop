package vn.kido.shop.Adapter;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;

import java.util.List;

import vn.kido.shop.Bean.BeanProduct;
import vn.kido.shop.Class.CmmFunc;
import vn.kido.shop.R;

import static vn.kido.shop.Class.GlobalClass.getContext;

public class ReturnRetailOrderAdapter extends RecyclerView.Adapter<ReturnRetailOrderAdapter.MyViewHolder> {
    Fragment fragment;
    RecyclerView recyclerView;
    List<BeanProduct> items;
    int size;

    public ReturnRetailOrderAdapter(Fragment fragment, RecyclerView recyclerView, List<BeanProduct> items) {
        this.fragment = fragment;
        this.recyclerView = recyclerView;
        this.items = items;
        size = CmmFunc.convertDpToPx(getContext(), 240);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_return_retail_order, parent, false);
        return new ReturnRetailOrderAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        try {
            BeanProduct beanProduct = items.get(position);
            if (beanProduct != null) {
                holder.name.setText(beanProduct.getName() + "");
                holder.date.setText(new DateTime(beanProduct.getExpireDate()).toString("dd/MM/yyyy"));
                Picasso.get().load(beanProduct.getImage()).resize(size, size).centerInside().into(holder.thumbnail);
                holder.reasonReturn.setText(beanProduct.getReason() + "");
                holder.number.setText(beanProduct.getQuantity() + "");
                holder.minUnit.setText(beanProduct.getMinUnit() + "");
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
        TextView name, date, reasonReturn, number, minUnit;
        ImageView thumbnail;

        public MyViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            date = itemView.findViewById(R.id.date);
            thumbnail = itemView.findViewById(R.id.thumbnail);
            reasonReturn = itemView.findViewById(R.id.reason_return);
            number = itemView.findViewById(R.id.number);
            minUnit = itemView.findViewById(R.id.min_unit);
        }
    }
}
