package vn.kido.shop.Adapter;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import vn.kido.shop.Api.APIService;
import vn.kido.shop.Api.ISubscriber;
import vn.kido.shop.Bean.BeanProduct;
import vn.kido.shop.Class.CmmFunc;
import vn.kido.shop.Class.StoreCart;
import vn.kido.shop.Fragment.BaseFragment;
import vn.kido.shop.Fragment.Cart.CartFragment;
import vn.kido.shop.Fragment.Product.DetailProductFragment;
import vn.kido.shop.Fragment.Dialog.DialogDetailProductFragment;
import vn.kido.shop.Helper.FragmentHelper;
import vn.kido.shop.R;

import static vn.kido.shop.Class.GlobalClass.getActivity;

public class ProductPopularAdapter extends RecyclerView.Adapter<ProductPopularAdapter.MyViewHolder> {
    View itemView;
    Fragment fragment;
    RecyclerView recyclerView;
    public LinkedHashMap map;
    int size;

    public ProductPopularAdapter(Fragment fragment, RecyclerView recyclerView, LinkedHashMap map) {
        this.fragment = fragment;
        this.recyclerView = recyclerView;
        this.map = map;
        size = CmmFunc.convertDpToPx(fragment.getActivity(), 240);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_product_popular, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        try {
            final BeanProduct beanProduct = (BeanProduct) map.values().toArray()[position];
            if (beanProduct != null) {

                holder.name.setText(beanProduct.getName());
                holder.kind.setText(beanProduct.getKind());

//                if (beanProduct.getEventId() == 0) {
//                    holder.ivEvent.setVisibility(View.GONE);
//                } else {
//                    holder.ivEvent.setVisibility(View.VISIBLE);
//                }

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FragmentHelper.add(DetailProductFragment.newInstance(beanProduct.getId()));
                    }
                });
                if (!beanProduct.isInCart()) {
                    holder.add.setImageResource(R.drawable.ic_cart_inactive);
                } else {
                    holder.add.setImageResource(R.drawable.ic_order_added);
                }
                holder.add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (StoreCart.getProducts().contains(beanProduct.getId())){
                            final DialogDetailProductFragment messageDialogFragment = DialogDetailProductFragment.newInstance();
                            messageDialogFragment.setMessage(getActivity().getResources().getString(R.string.exist_in_cart));
                            messageDialogFragment.setTouchOutSide(true);
                            messageDialogFragment.setCancelable(true);
                            messageDialogFragment.setRunnable(new Runnable() {
                                @Override
                                public void run() {
                                    messageDialogFragment.dismiss();
                                    FragmentHelper.add(CartFragment.newInstance(beanProduct.getId()));
                                }
                            });
                            messageDialogFragment.show(getActivity().getSupportFragmentManager(), messageDialogFragment.getClass().getName());
                            //FragmentHelper.add(CartExistFragment.newInstance());
                            return;
                        }
                        addProductToCart(position, beanProduct);
                    }
                });

                if (!TextUtils.isEmpty(beanProduct.getImage())) {
                    Picasso.get().load(beanProduct.getImage()).resize(size, size).centerInside().into(holder.thumbnail);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void addProductToCart(final int position, final BeanProduct beanProduct) {
        //In list product, array attribute id and quantity = 1
        JSONArray arrayAtt = new JSONArray();
        JSONObject item = new JSONObject();
        try {
            item.put("id",1);
            item.put("quantity", 1);
            arrayAtt.put(item);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        APIService.getInstance().addProductToCart(beanProduct.getId(), 1,1, arrayAtt)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ISubscriber() {
                    @Override
                    public void excute(JSONObject jsonObject) {
                        beanProduct.setInCart(true);
                        ProductPopularAdapter.this.notifyItemChanged(position);
                        ((BaseFragment) fragment).getNumberCart();
                        CmmFunc.showAnim(((BaseFragment)fragment).rootView.findViewById(R.id.cart));
                    }
                });
    }


    private void removeAllQuantity(final int position, final BeanProduct beanProduct) {
        APIService.getInstance().removeAllQuantity(beanProduct.getId(), BeanProduct.PRODUCT)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ISubscriber() {
                    @Override
                    public void excute(JSONObject jsonObject) {
                        beanProduct.setInCart(false);
                        ProductPopularAdapter.this.notifyItemChanged(position);
                        ((BaseFragment) fragment.getParentFragment()).getNumberCart();
                    }
                });
    }

    @Override
    public int getItemCount() {
        return map.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnail, add, ivEvent;
        TextView name, kind;

        public MyViewHolder(View itemView) {
            super(itemView);
            thumbnail = itemView.findViewById(R.id.thumbnail);
            add = itemView.findViewById(R.id.add);
            name = itemView.findViewById(R.id.name);
            ivEvent = itemView.findViewById(R.id.iv_event);
            kind = itemView.findViewById(R.id.kind);
        }
    }
}
