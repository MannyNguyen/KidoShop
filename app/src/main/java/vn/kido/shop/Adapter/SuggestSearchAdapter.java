package vn.kido.shop.Adapter;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import vn.kido.shop.Fragment.Search.HomeSearchFragment;
import vn.kido.shop.Helper.FragmentHelper;
import vn.kido.shop.R;

public class SuggestSearchAdapter extends RecyclerView.Adapter<SuggestSearchAdapter.MyViewHolder> {
    List<String> suggests;
    RecyclerView recyclerView;
    Fragment fragment;


    public SuggestSearchAdapter(List<String> suggests, RecyclerView recyclerView, Fragment fragment) {
        this.suggests = suggests;
        this.recyclerView = recyclerView;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_suggest_search, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        try {
            final String textSuggest = suggests.get(position);
            if (textSuggest != null) {
                holder.resultSuggest.setText(textSuggest + "");
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FragmentHelper.add(HomeSearchFragment.newInstance(textSuggest));
                        //FragmentHelper.replace(HomeSearchFragment.newInstance(textSuggest));
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return suggests.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView resultSuggest;

        public MyViewHolder(View itemView) {
            super(itemView);
            resultSuggest = itemView.findViewById(R.id.result_suggest);
        }
    }
}
