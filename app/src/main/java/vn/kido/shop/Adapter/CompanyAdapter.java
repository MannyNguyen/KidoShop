package vn.kido.shop.Adapter;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;
import vn.kido.shop.Bean.BeanCompany;

public class CompanyAdapter extends RecyclerView.Adapter<CompanyAdapter.MyViewHolder>{
    Fragment fragment;
    RecyclerView recyclerView;
    List<BeanCompany> companies;

    public CompanyAdapter(Fragment fragment, RecyclerView recyclerView, List<BeanCompany> companies) {
        this.fragment = fragment;
        this.recyclerView = recyclerView;
        this.companies = companies;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public MyViewHolder(View itemView) {
            super(itemView);
        }
    }
}
