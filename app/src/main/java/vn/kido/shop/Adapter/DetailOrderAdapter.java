package vn.kido.shop.Adapter;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import vn.kido.shop.Bean.BeanAttribute;
import vn.kido.shop.Bean.BeanProduct;
import vn.kido.shop.Class.CmmFunc;
import vn.kido.shop.Fragment.Program.ProductProgramFragment;
import vn.kido.shop.Helper.FragmentHelper;
import vn.kido.shop.R;

import static vn.kido.shop.Class.GlobalClass.getContext;

public class DetailOrderAdapter extends RecyclerView.Adapter<DetailOrderAdapter.MyViewHolder> {
    Fragment fragment;
    RecyclerView recycler;
    List<BeanProduct> items;
    int size;

    public DetailOrderAdapter(Fragment fragment, RecyclerView recycler, List<BeanProduct> items) {
        this.fragment = fragment;
        this.recycler = recycler;
        this.items = items;
        size = CmmFunc.convertDpToPx(getContext(), 240);
    }

    @NonNull
    @Override
    public DetailOrderAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_detail_order, parent, false);
        if (viewType == BeanProduct.COMBO) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_detail_order_combo, parent, false);
        }
        return new DetailOrderAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        try {
            final BeanProduct beanProduct = items.get(position);
            if (beanProduct != null) {
                holder.name.setText(beanProduct.getName() + "");

                holder.totalPrice.setText(CmmFunc.formatMoney(beanProduct.getTotalProductPrice(), false));
                if (beanProduct.getTotalPercentDiscount() == 0) {
                    holder.frameDiscount.setVisibility(View.GONE);
                }
                holder.afterDiscount.setText(CmmFunc.formatMoney(beanProduct.getTotalPrice(), false));
                holder.price.setText(CmmFunc.formatMoney(Math.round(beanProduct.getPrice()), false));
                holder.priceMax.setText(CmmFunc.formatMoney(Math.round(beanProduct.getPriceMaxUnit()), false));
                holder.minUnit.setText("/ " + beanProduct.getMinUnit());
                holder.maxUnit.setText("/ " + beanProduct.getMaxUnit());
                holder.percent.setText("(" + beanProduct.getTotalPercentDiscount() + "%)");
                if (beanProduct.getEventId() == 0) {
                    holder.program.setVisibility(View.GONE);
                } else {
                    holder.program.setVisibility(View.VISIBLE);
                    holder.program.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            FragmentHelper.add(ProductProgramFragment.newInstance(beanProduct.getId(), beanProduct.getName()));
                        }
                    });
                }

                if (beanProduct.getReason().equals("")) {
                    holder.reasonContainer.setVisibility(View.GONE);
                } else {
                    holder.reasonContainer.setVisibility(View.VISIBLE);
                    holder.reason.setText(beanProduct.getReason() + "");
                }

                holder.attribute.addView(createAttribute(beanProduct.getAttributes()));

                if (beanProduct.getAttributes_received().size() == 0) {
                    holder.receiveContainer.setVisibility(View.GONE);
                } else {
                    holder.receiveContainer.setVisibility(View.VISIBLE);
                    holder.attributeReceive.addView(createAttribute(beanProduct.getAttributes_received()));
                }

                if (!TextUtils.isEmpty(beanProduct.getImage())) {
                    Picasso.get().load(beanProduct.getImage()).resize(size, size).centerInside().into(holder.thumbnail);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private View createAttribute(List<BeanAttribute> attributes) {
        LinearLayout view = new LinearLayout(getContext());
        try {
            view.setOrientation(LinearLayout.HORIZONTAL);
            for (BeanAttribute beanAttribute : attributes) {
                View itemView = LayoutInflater.from(getContext()).inflate(R.layout.row_attribute_order, null, false);
                TextView name = itemView.findViewById(R.id.name);
                TextView value = itemView.findViewById(R.id.value);
                name.setText(beanAttribute.getName() + "");
                value.setText(beanAttribute.getValue() + "");
                view.addView(itemView);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return view;
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).getType();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnail;
        TextView name, price, priceMax, minUnit, maxUnit, totalPrice, afterDiscount, percent, reason;
        View program, receiveContainer, reasonContainer;
        HorizontalScrollView attribute, attributeReceive;
        View frameDiscount;

        public MyViewHolder(View itemView) {
            super(itemView);
            thumbnail = itemView.findViewById(R.id.thumbnail);
            name = itemView.findViewById(R.id.name);
            attribute = itemView.findViewById(R.id.attribute);
            attributeReceive = itemView.findViewById(R.id.attribute_received);
            price = itemView.findViewById(R.id.price);
            priceMax = itemView.findViewById(R.id.price_max);
            totalPrice = itemView.findViewById(R.id.total_price);
            frameDiscount = itemView.findViewById(R.id.frame_discount);
            afterDiscount = itemView.findViewById(R.id.after_discount);
            program = itemView.findViewById(R.id.program);
            minUnit = itemView.findViewById(R.id.min_unit);
            maxUnit = itemView.findViewById(R.id.max_unit);
            percent = itemView.findViewById(R.id.percent);
            receiveContainer = itemView.findViewById(R.id.real_receive_container);
            reasonContainer = itemView.findViewById(R.id.reason_container);
            reason = itemView.findViewById(R.id.reason);

            //recyclerAttribute = itemView.findViewById(R.id.recycler_attribute);
            //recyclerAttribute.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        }
    }
}
