package vn.kido.shop.Adapter;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import vn.kido.shop.Bean.BeanProduct;
import vn.kido.shop.Class.CmmFunc;
import vn.kido.shop.Fragment.BaseFragment;
import vn.kido.shop.R;

import static vn.kido.shop.Class.GlobalClass.getContext;

//me
public class ProgramProductAdapter extends RecyclerView.Adapter<ProgramProductAdapter.MyViewHolder> {
    BaseFragment fragment;
    RecyclerView recyclerView;
    List<BeanProduct> items;
    int size;
    private int id;

    public ProgramProductAdapter(Fragment fragment, RecyclerView recyclerView, List<BeanProduct> items, int id) {
        this.fragment = (BaseFragment) fragment;
        this.recyclerView = recyclerView;
        this.items = items;
        this.setId(id);
        size = CmmFunc.convertDpToPx(getContext(), 240);
    }

    @NonNull
    @Override
    public ProgramProductAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_program_product, parent, false);
        return new ProgramProductAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ProgramProductAdapter.MyViewHolder holder, int position) {
        try {
            final BeanProduct beanProduct = items.get(position);
            if (beanProduct != null) {
                holder.name.setText(beanProduct.getName() + "");
                holder.totalPrice.setText(CmmFunc.formatMoney(beanProduct.getTotalProductPrice(), false));
                holder.price.setText(CmmFunc.formatMoney(Math.round(beanProduct.getPrice()), false));
                holder.minUnit.setText("/ " + beanProduct.getMinUnit());
                holder.min.setText(beanProduct.getMinUnit() + "");
                holder.maxItemUnit.setText(beanProduct.getMaxValue() + "");
                AttributeAdapter attributeAdapter = new AttributeAdapter(fragment, holder.recyclerAttribute, beanProduct.getAttributes(), beanProduct.getId(), getId(), beanProduct.getMaxValue());
                holder.recyclerAttribute.setAdapter(attributeAdapter);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, price, totalPrice, minUnit, maxItemUnit, min;
        RecyclerView recyclerAttribute;

        public MyViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            price = itemView.findViewById(R.id.price);
            totalPrice = itemView.findViewById(R.id.total_product_price);
            minUnit = itemView.findViewById(R.id.min_unit);
            maxItemUnit = itemView.findViewById(R.id.max_item_unit);
            min = itemView.findViewById(R.id.min);
            recyclerAttribute = itemView.findViewById(R.id.recycler_attribute);
            recyclerAttribute.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        }
    }
}