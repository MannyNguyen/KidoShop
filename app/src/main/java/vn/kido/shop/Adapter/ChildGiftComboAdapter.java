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

import java.util.List;

import vn.kido.shop.Bean.BeanProduct;
import vn.kido.shop.Class.CmmFunc;
import vn.kido.shop.R;

import static vn.kido.shop.Class.GlobalClass.getContext;

//me
public class ChildGiftComboAdapter extends RecyclerView.Adapter<ChildGiftComboAdapter.MyViewHolder> {
    Fragment fragment;
    RecyclerView recyclerView;
    List<BeanProduct> items;
    int size;

    public ChildGiftComboAdapter(Fragment fragment, RecyclerView recyclerView, List<BeanProduct> items) {
        this.fragment = fragment;
        this.recyclerView = recyclerView;
        this.items = items;
        size = CmmFunc.convertDpToPx(getContext(), 240);
    }

    @NonNull
    @Override
    public ChildGiftComboAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_gift, parent, false);
        return new ChildGiftComboAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ChildGiftComboAdapter.MyViewHolder holder, int position) {
        try {
            BeanProduct beanProduct = items.get(position);
            if (beanProduct != null) {
                holder.name.setText(beanProduct.getName() + "");
                holder.number.setText("x" + beanProduct.getQuantity());
                if (!TextUtils.isEmpty(beanProduct.getImage())) {
                    Picasso.get().load(beanProduct.getImage()).resize(size, size).centerInside().into(holder.thumbnail);
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
        TextView name, number;

        public MyViewHolder(View itemView) {
            super(itemView);
            thumbnail = itemView.findViewById(R.id.thumbnail);
            name = itemView.findViewById(R.id.name);
            number = itemView.findViewById(R.id.number);
        }
    }
}