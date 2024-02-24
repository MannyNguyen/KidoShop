package vn.kido.shop.Adapter;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.List;

import vn.kido.shop.Bean.BeanStore;
import vn.kido.shop.Class.CmmFunc;
import vn.kido.shop.Fragment.Category.CategoryFragment;
import vn.kido.shop.Helper.FragmentHelper;
import vn.kido.shop.R;

import static vn.kido.shop.Class.GlobalClass.getContext;

public class StoreAdapter extends RecyclerView.Adapter<StoreAdapter.MyViewHolder> {

    Fragment fragment;
    RecyclerView recyclerView;
    List<BeanStore> items;
    int size;

    public StoreAdapter(Fragment fragment, RecyclerView recyclerView, List<BeanStore> items) {
        this.fragment = fragment;
        this.recyclerView = recyclerView;
        this.items = items;
        size = CmmFunc.convertDpToPx(getContext(), 240);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_store, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        try {
            final BeanStore item = items.get(position);
            if (item != null) {
                holder.title.setText(item.getName() + "");

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FragmentHelper.add(CategoryFragment.newInstance(new Gson().toJson(item),  CategoryFragment.CATEGORY));
                    }
                });
                if (!item.getImage().equals("")) {
                    Picasso.get().load(item.getImage()).resize(size, size).centerInside().into(holder.image);
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
        TextView title;
        ImageView image;
        CardView container;

        public MyViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            image = itemView.findViewById(R.id.image);
            container = itemView.findViewById(R.id.container);
        }
    }
}
