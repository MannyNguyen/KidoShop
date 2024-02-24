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

import java.util.List;

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
import vn.kido.shop.Fragment.Program.ProductProgramFragment;
import vn.kido.shop.Helper.FragmentHelper;
import vn.kido.shop.R;

import static vn.kido.shop.Class.GlobalClass.getActivity;
import static vn.kido.shop.Class.GlobalClass.getContext;

public class SuggestProductAdapter extends RecyclerView.Adapter<SuggestProductAdapter.MyViewHolder> {
    View itemView;
    Fragment fragment;
    RecyclerView recyclerView;
    List<BeanProduct> productLikes;
    int size;

    public SuggestProductAdapter(Fragment fragment, RecyclerView recyclerView, List<BeanProduct> productLikes) {
        this.fragment = fragment;
        this.recyclerView = recyclerView;
        this.productLikes = productLikes;
        size = CmmFunc.convertDpToPx(getContext(), 240);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_same_product, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        try {
            final BeanProduct productLike = productLikes.get(position);
            if (productLike != null) {
                if (!TextUtils.isEmpty(productLike.getImage())) {
                    Picasso.get().load(productLike.getImage()).resize(size, size).centerInside().into(holder.thumbnail);
                }
                holder.name.setText(productLike.getName());
                holder.price.setText(CmmFunc.formatMoney(Math.round(productLike.getPrice()), false));
                holder.priceMax.setText(CmmFunc.formatMoney(Math.round(productLike.getPriceMaxUnit()), false));
                holder.minUnit.setText("/ " + productLike.getMinUnit());
                holder.maxUnit.setText("/ " + productLike.getMaxUnit());

                if (productLike.getPrice() == 0) {
                    holder.price.setVisibility(View.GONE);
                    holder.priceMax.setVisibility(View.GONE);
                    holder.minUnit.setVisibility(View.GONE);
                    holder.maxUnit.setVisibility(View.GONE);
                    holder.txtRequireLogin.setVisibility(View.VISIBLE);
                }

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FragmentHelper.add(DetailProductFragment.newInstance(productLike.getId()));
                    }
                });

                if (productLike.getEventId() == 0) {
                    holder.program.setVisibility(View.GONE);
                } else {
                    holder.program.setVisibility(View.VISIBLE);
                    holder.program.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            FragmentHelper.add(ProductProgramFragment.newInstance(productLike.getId(), productLike.getName()));
                        }
                    });
                }

                if (!productLike.isInCart()) {
                    holder.add.setImageResource(R.drawable.ic_cart_inactive);
                } else {
                    holder.add.setImageResource(R.drawable.ic_cart_active);
                    //holder.add.setEnabled(false);
                }

                holder.add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (productLike.isInCart()) {
                            //removeAllQuantity(productLike);
//                            addProductToCart(productLike);
                            if (StoreCart.getProducts().contains(productLike.getId())) {
                                final DialogDetailProductFragment messageDialogFragment = DialogDetailProductFragment.newInstance();
                                messageDialogFragment.setMessage(getActivity().getResources().getString(R.string.exist_in_cart));
                                messageDialogFragment.setTouchOutSide(true);
                                messageDialogFragment.setCancelable(true);
                                messageDialogFragment.setRunnable(new Runnable() {
                                    @Override
                                    public void run() {
                                        messageDialogFragment.dismiss();
                                        FragmentHelper.add(CartFragment.newInstance(productLike.getId()));
                                    }
                                });
                                messageDialogFragment.show(getActivity().getSupportFragmentManager(), messageDialogFragment.getClass().getName());
                                //FragmentHelper.add(CartExistFragment.newInstance());
                                return;
                            }
                        } else {
                            addProductToCart(productLike);
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
        return productLikes.size();
    }

    private void addProductToCart(final BeanProduct beanProduct) {
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
                        SuggestProductAdapter.this.notifyDataSetChanged();
                        ((BaseFragment) fragment).getNumberCart();
                        CmmFunc.showAnim(((BaseFragment) fragment).rootView.findViewById(R.id.cart));
                    }
                });
    }

    private void removeAllQuantity(final BeanProduct beanProduct) {
        APIService.getInstance().removeAllQuantity(beanProduct.getId(), BeanProduct.PRODUCT)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ISubscriber() {
                    @Override
                    public void excute(JSONObject jsonObject) {
                        beanProduct.setInCart(false);
                        SuggestProductAdapter.this.notifyDataSetChanged();
                        ((BaseFragment) fragment).getNumberCart();
                    }
                });
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnail, add;
        TextView name, price, priceMax, program, minUnit, maxUnit, txtRequireLogin;

        public MyViewHolder(View itemView) {
            super(itemView);
            thumbnail = itemView.findViewById(R.id.thumbnail);
            program = itemView.findViewById(R.id.program);
            minUnit = itemView.findViewById(R.id.min_unit);
            maxUnit = itemView.findViewById(R.id.max_unit);
            name = itemView.findViewById(R.id.name);
            price = itemView.findViewById(R.id.price);
            priceMax = itemView.findViewById(R.id.price_max);
            add = itemView.findViewById(R.id.add);
            txtRequireLogin = itemView.findViewById(R.id.txt_require_login);
        }
    }
}
