package vn.kido.shop.Adapter;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.JsonReader;
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
import vn.kido.shop.Fragment.Product.ListProductFragment;
import vn.kido.shop.Fragment.Program.ProductProgramFragment;
import vn.kido.shop.Helper.FragmentHelper;
import vn.kido.shop.R;

import static vn.kido.shop.Class.GlobalClass.getActivity;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.MyViewHolder> {

    Fragment fragment;
    RecyclerView recyclerView;
    LinkedHashMap map;
    int size;

    public ProductAdapter(Fragment fragment, RecyclerView recyclerView, LinkedHashMap map) {
        this.fragment = fragment;
        this.recyclerView = recyclerView;
        this.map = map;
        size = CmmFunc.convertDpToPx(fragment.getActivity(), 240);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_product, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        try {
            final BeanProduct beanProduct = (BeanProduct) map.values().toArray()[position];
            if (beanProduct != null) {
                if (!TextUtils.isEmpty(beanProduct.getImage())) {
                    Picasso.get().load(beanProduct.getImage()).resize(size, size).centerInside().into(holder.thumbnail);
                }
                holder.name.setText(beanProduct.getName());
                if (beanProduct.getPrice() != 0) {
                    holder.statusGuest.setVisibility(View.GONE);
                    holder.containerPrice.setVisibility(View.VISIBLE);
                    holder.price.setText(CmmFunc.formatMoney(Math.round(beanProduct.getPrice()), false));
                    holder.priceMax.setText(CmmFunc.formatMoney(Math.round(beanProduct.getPriceMaxUnit()), false));
                    holder.minUnit.setText("/ " + beanProduct.getMinUnit());
                    holder.maxUnit.setText("/ " + beanProduct.getMaxUnit());
                } else {
                    holder.statusGuest.setVisibility(View.VISIBLE);
                    holder.containerPrice.setVisibility(View.INVISIBLE);
                }

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

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FragmentHelper.add(DetailProductFragment.newInstance(beanProduct.getId()));
                    }
                });

                if (!beanProduct.isInCart()) {
                    holder.add.setImageResource(R.drawable.ic_cart_inactive);
                } else {
                    holder.add.setImageResource(R.drawable.ic_cart_active);
                    //holder.add.setEnabled(false);
                }
//                holder.add.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if (beanProduct.isInCart()) {
//                            //removeAllQuantity(beanProduct);
//                            addProductToCart(beanProduct);
//                        } else {
//                            holder.add.setImageResource(R.drawable.ic_cart_active);
//                            addProductToCart(beanProduct);
//                        }
//
//                    }
//                });

                holder.add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (StoreCart.getProducts().contains(beanProduct.getId())) {
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
                        addProductToCart(beanProduct);
                    }
                });


                //Todo Chua chay ListProducFragment
                if (fragment.getArguments().containsKey("type")) {
                    String type = fragment.getArguments().getString("type");
                    if (type == null) {
                        holder.ivNewPopular.setVisibility(View.INVISIBLE);
                        return;
                    }
                    if (type.equals(ListProductFragment.POPULAR)) {

                        if (beanProduct.getEventId() == 0) {
                            holder.ivNewPopular.setVisibility(View.INVISIBLE);
                        } else {
                            holder.ivNewPopular.setVisibility(View.VISIBLE);
                            holder.ivNewPopular.setImageResource(R.drawable.ic_km_tag);

                        }

                    } else if (type.equals(ListProductFragment.NEW)) {
                        holder.ivNewPopular.setVisibility(View.VISIBLE);
                        holder.ivNewPopular.setImageResource(R.drawable.ic_new_product);
                    }
                } else {
                    holder.ivNewPopular.setVisibility(View.INVISIBLE);

                }

//                if( beanProduct.getKind()!= null && beanProduct.getKind().toLowerCase().equals("khuyến mãi")){
//                    holder.ivNewPopular.setImageResource(R.drawable.ic_km_tag);
//                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                        ProductAdapter.this.notifyDataSetChanged();
                        if (fragment.getParentFragment() != null) {
                            ((BaseFragment) fragment.getParentFragment()).getNumberCart();
                            CmmFunc.showAnim(((BaseFragment) fragment.getParentFragment()).rootView.findViewById(R.id.cart));
                        } else {
                            ((BaseFragment) fragment).getNumberCart();
                            CmmFunc.showAnim(((BaseFragment) fragment).rootView.findViewById(R.id.cart));
                        }
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
                        ProductAdapter.this.notifyDataSetChanged();
                        if (fragment.getParentFragment() != null) {
                            ((BaseFragment) fragment.getParentFragment()).getNumberCart();
                        } else {
                            ((BaseFragment) fragment).getNumberCart();
                        }
                    }
                });
    }

    @Override
    public int getItemCount() {
        return map.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnail, add, ivNewPopular;
        TextView name, price, priceMax, minUnit, maxUnit, statusGuest;
        View container, program, containerPrice;

        public MyViewHolder(View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.container);
            containerPrice = itemView.findViewById(R.id.container_price);
            program = itemView.findViewById(R.id.program);
            thumbnail = itemView.findViewById(R.id.thumbnail);
            ivNewPopular = itemView.findViewById(R.id.iv_new_popular);
            add = itemView.findViewById(R.id.add);
            name = itemView.findViewById(R.id.name);
            price = itemView.findViewById(R.id.price);
            priceMax = itemView.findViewById(R.id.price_max);
            minUnit = itemView.findViewById(R.id.min_unit);
            maxUnit = itemView.findViewById(R.id.max_unit);
            statusGuest = itemView.findViewById(R.id.status_guest);
        }
    }
}
