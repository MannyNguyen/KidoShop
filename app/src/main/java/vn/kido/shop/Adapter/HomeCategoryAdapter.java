package vn.kido.shop.Adapter;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.List;

import vn.kido.shop.Bean.BeanCategory;
import vn.kido.shop.Class.CmmFunc;
import vn.kido.shop.Fragment.Category.CategoryFragment;
import vn.kido.shop.Helper.FragmentHelper;
import vn.kido.shop.HomeActivity;
import vn.kido.shop.R;

import static vn.kido.shop.Class.GlobalClass.getContext;

public class HomeCategoryAdapter extends RecyclerView.Adapter<HomeCategoryAdapter.MyViewHolder> {
    Fragment fragment;
    RecyclerView recyclerView;
    List<BeanCategory> items;
    ViewGroup.MarginLayoutParams layoutParams;
    int size;

    public HomeCategoryAdapter(Fragment fragment, RecyclerView recyclerView, List<BeanCategory> items) {
        this.fragment = fragment;
        this.recyclerView = recyclerView;
        this.items = items;
        int pad = CmmFunc.convertDpToPx(fragment.getActivity(), 20);
        layoutParams = new ViewGroup.MarginLayoutParams(HomeActivity.WINDOW_WIDTH / 2 - pad, HomeActivity.WINDOW_WIDTH / 4);
        layoutParams.setMargins(1, 1, 1, 1);
        size = CmmFunc.convertDpToPx(getContext(), 240);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_home_category, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        try {
            final BeanCategory item = items.get(position);
            if (item != null) {
                holder.itemView.setLayoutParams(layoutParams);
                holder.title.setText(item.getName() + "");
                if (!item.getImage().equals("")) {
                    Picasso.get().load(item.getImage()).resize(size, size).centerInside().into(holder.image);
                }

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FragmentHelper.add(CategoryFragment.newInstance(new Gson().toJson(item), CategoryFragment.TYPE_PRODUCT));
                    }
                });

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
        ImageView image;
        TextView title;

        public MyViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            title = itemView.findViewById(R.id.title);

        }
    }
}
