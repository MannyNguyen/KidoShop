package vn.kido.shop.Adapter;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import vn.kido.shop.Bean.BeanAttribute;
import vn.kido.shop.Fragment.Cart.ChildCartFragment;
import vn.kido.shop.Fragment.Cart.ProgramCartFragment;
import vn.kido.shop.Fragment.Cart.UpdateQuantityFragment;
import vn.kido.shop.Helper.FragmentHelper;
import vn.kido.shop.R;

public class AttributeAdapter extends RecyclerView.Adapter<AttributeAdapter.MyViewHolder> {

    Fragment fragment;
    RecyclerView recyclerView;
    List<BeanAttribute> attributes;
    int idProduct;
    int idEvent;
    int max;

    public AttributeAdapter(Fragment fragment, RecyclerView recyclerView, List<BeanAttribute> attributes, int idProduct) {
        this.fragment = fragment;
        this.recyclerView = recyclerView;
        this.attributes = attributes;
        this.idProduct = idProduct;

    }

    public AttributeAdapter(Fragment fragment, RecyclerView recyclerView, List<BeanAttribute> attributes, int idProduct, int idEvent, int max) {
        this.fragment = fragment;
        this.recyclerView = recyclerView;
        this.attributes = attributes;
        this.idProduct = idProduct;
        this.idEvent = idEvent;
        this.max = max;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_attribute, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        try {
            final BeanAttribute beanAttribute = attributes.get(position);
            if (beanAttribute != null) {
                //holder.itemView.setLayoutParams(new ViewGroup.LayoutParams(recyclerView.getLayoutManager().w / 4, ViewGroup.LayoutParams.WRAP_CONTENT));
                holder.name.setText(beanAttribute.getName() + "");
                holder.value.setText(beanAttribute.getValue() + "");
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (fragment instanceof ChildCartFragment) {
                            FragmentHelper.add(UpdateQuantityFragment.newInstance(idProduct, beanAttribute, attributes, UpdateQuantityFragment.UPDATE_CART));
                        } else if (fragment instanceof ProgramCartFragment) {
                            FragmentHelper.add(UpdateQuantityFragment.newInstance(idProduct, beanAttribute, attributes, idEvent, UpdateQuantityFragment.UPDATE_PROGRAME_CART, max));
                        }

                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return attributes.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, value;

        public MyViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            value = itemView.findViewById(R.id.value);
        }
    }
}
