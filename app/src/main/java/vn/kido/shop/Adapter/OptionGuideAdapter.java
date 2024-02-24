package vn.kido.shop.Adapter;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import vn.kido.shop.Bean.BeanOptionGuide;
import vn.kido.shop.Fragment.Option.DialogGuideFragment;
import vn.kido.shop.Helper.FragmentHelper;
import vn.kido.shop.R;

public class OptionGuideAdapter extends RecyclerView.Adapter<OptionGuideAdapter.MyViewHolder> {
    View itemView;
    Fragment fragment;
    List<BeanOptionGuide> optionGuides;
    RecyclerView recyclerView;

    public OptionGuideAdapter(Fragment fragment, List<BeanOptionGuide> optionGuides, RecyclerView recyclerView) {
        this.fragment = fragment;
        this.optionGuides = optionGuides;
        this.recyclerView = recyclerView;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_list_guide, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        try {
            final BeanOptionGuide optionGuide = optionGuides.get(position);
            if (optionGuide != null) {
                holder.title.setText(optionGuide.getTitle());
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FragmentHelper.add(DialogGuideFragment.newInstance(optionGuide.getId()));
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return optionGuides.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title;

        public MyViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
        }
    }
}
