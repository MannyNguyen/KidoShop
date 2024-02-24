package vn.kido.shop.Adapter;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import vn.kido.shop.Bean.BeanProgram;
import vn.kido.shop.R;

public class ProductEventAdapter extends RecyclerView.Adapter<ProductEventAdapter.MyViewHolder> {
    List<BeanProgram> events;
    RecyclerView recyclerView;
    Fragment fragment;
    View itemView;

    public ProductEventAdapter(List<BeanProgram> events, RecyclerView recyclerView, Fragment fragment) {
        this.events = events;
        this.recyclerView = recyclerView;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_product_event, parent, false);
        return new ProductEventAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        try {
            final BeanProgram beanProgram = events.get(position);
            holder.eventName.setText("-" + beanProgram.getTitle());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView eventName;

        public MyViewHolder(View itemView) {
            super(itemView);
            eventName = itemView.findViewById(R.id.event_name);
        }
    }
}
