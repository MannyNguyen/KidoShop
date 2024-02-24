package vn.kido.shop.Adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import vn.kido.shop.Bean.BeanAttribute;
import vn.kido.shop.Class.CmmFunc;
import vn.kido.shop.Class.GlobalClass;
import vn.kido.shop.Class.StoreCart;
import vn.kido.shop.Fragment.Cart.CartFragment;
import vn.kido.shop.Fragment.Product.DetailProductFragment;
import vn.kido.shop.Fragment.Dialog.DialogDetailProductFragment;
import vn.kido.shop.Fragment.Product.ChangeQuantityFragment;
import vn.kido.shop.Helper.FragmentHelper;
import vn.kido.shop.HomeActivity;
import vn.kido.shop.R;

public class DetailProductQuantityAdapter extends RecyclerView.Adapter<DetailProductQuantityAdapter.MyViewHolder> {
    List<BeanAttribute> beanProductAttrs;
    DetailProductFragment fragment;
    RecyclerView recyclerView;

    public DetailProductQuantityAdapter(List<BeanAttribute> beanProductAttrs, DetailProductFragment fragment, RecyclerView recyclerView) {
        this.beanProductAttrs = beanProductAttrs;
        this.fragment = fragment;
        this.recyclerView = recyclerView;


    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_product_attribute, parent, false);
        return new DetailProductQuantityAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        try {
            final BeanAttribute beanProductAttr = beanProductAttrs.get(position);
            if (beanProductAttr != null) {
                int padLeftRight = CmmFunc.convertDpToPx(GlobalClass.getActivity(), 48);
                int itemWidth = ((HomeActivity.WINDOW_WIDTH - padLeftRight) / 3) - (beanProductAttrs.size() * 2);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(itemWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.leftMargin = 1;
                layoutParams.rightMargin = 1;
                holder.llRowQuantity.setLayoutParams(layoutParams);

                holder.txtUnit.setText(beanProductAttr.getName() + "");
                holder.txtQuantity.setText(beanProductAttr.getValue() + "");
                holder.txtMaxUnit.setVisibility(View.INVISIBLE);

                if (position < beanProductAttrs.size() - 1) {
                    holder.txtMaxUnit.setVisibility(View.VISIBLE);
                    holder.txtMaxUnit.setText(beanProductAttr.getQuantityUnit() + "");
                }

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (StoreCart.getProducts().contains(fragment.getArguments().getInt("id"))) {
                            final DialogDetailProductFragment messageDialogFragment = DialogDetailProductFragment.newInstance();
                            messageDialogFragment.setMessage(GlobalClass.getActivity().getResources().getString(R.string.exist_in_cart));
                            messageDialogFragment.setTouchOutSide(true);
                            messageDialogFragment.setCancelable(true);
                            messageDialogFragment.setRunnable(new Runnable() {
                                @Override
                                public void run() {
                                    messageDialogFragment.dismiss();
                                    FragmentHelper.add(CartFragment.newInstance(fragment.getArguments().getInt("id")));
                                }
                            });
                            messageDialogFragment.show(fragment.getActivity().getSupportFragmentManager(), messageDialogFragment.getClass().getName());
                            return;
                        }
                        if (position < beanProductAttrs.size() - 1) {
                            FragmentHelper.add(ChangeQuantityFragment.newInstance(beanProductAttr.getName(), beanProductAttr.getMoney(), beanProductAttr.getNamePerUnit(), position));
                        } else {
                            FragmentHelper.add(ChangeQuantityFragment.newInstance(beanProductAttr.getName(), beanProductAttr.getMoney(), "", position));
                        }

                    }
                });
                addMoney();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return beanProductAttrs.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtUnit, txtQuantity, txtMaxUnit;
        LinearLayout llRowQuantity;

        public MyViewHolder(View itemView) {
            super(itemView);
            llRowQuantity = itemView.findViewById(R.id.ll_row_quantity);
            txtUnit = itemView.findViewById(R.id.txt_unit);
            txtQuantity = itemView.findViewById(R.id.txt_quantity);
            txtMaxUnit = itemView.findViewById(R.id.txt_max_unit);
        }
    }

    void addMoney() {
        try {
            int moneyPayment = 0;
            int numQuantity = 0;
            TextView txtTotalPayment = fragment.getView().findViewById(R.id.txt_total_payment);
            for (int i = 0; i < beanProductAttrs.size(); i++) {
                moneyPayment += beanProductAttrs.get(i).getMoney() * beanProductAttrs.get(i).getValue();
                numQuantity += beanProductAttrs.get(i).getValue() * beanProductAttrs.get(i).getQuantity();
            }
            txtTotalPayment.setText(CmmFunc.formatMoney(moneyPayment, false));
            fragment.numProduct = numQuantity;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
