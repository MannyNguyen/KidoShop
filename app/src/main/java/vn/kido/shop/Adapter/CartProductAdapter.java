package vn.kido.shop.Adapter;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import vn.kido.shop.Api.APIService;
import vn.kido.shop.Api.ISubscriber;
import vn.kido.shop.Bean.BeanProduct;
import vn.kido.shop.Class.CmmFunc;
import vn.kido.shop.Fragment.BaseFragment;
import vn.kido.shop.Fragment.Cart.CartFragment;
import vn.kido.shop.Fragment.Cart.ChildCartFragment;
import vn.kido.shop.Fragment.Common.HomeFragment;
import vn.kido.shop.Fragment.Product.DetailProductFragment;
import vn.kido.shop.Fragment.Dialog.MessageDialogFragment;
import vn.kido.shop.Fragment.Program.ProductProgramFragment;
import vn.kido.shop.Helper.FragmentHelper;
import vn.kido.shop.R;

import static vn.kido.shop.Class.GlobalClass.getContext;

//me
public class CartProductAdapter extends RecyclerView.Adapter<CartProductAdapter.MyViewHolder> {
    BaseFragment fragment;
    RecyclerView recyclerView;
    List<BeanProduct> items;
    int size;

    public CartProductAdapter(Fragment fragment, RecyclerView recyclerView, List<BeanProduct> items) {
        this.fragment = (BaseFragment) fragment;
        this.recyclerView = recyclerView;
        this.items = items;
        size = CmmFunc.convertDpToPx(getContext(), 240);
    }

    @NonNull
    @Override
    public CartProductAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_producted, parent, false);
        if (viewType == BeanProduct.COMBO) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_combo, parent, false);
        }
        return new CartProductAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final CartProductAdapter.MyViewHolder holder, int position) {
        try {
            final BeanProduct beanProduct = items.get(position);
            if (beanProduct != null) {
                holder.name.setText(beanProduct.getName() + "");
                holder.totalPrice.setText(CmmFunc.formatMoney(beanProduct.getTotalProductPrice(), false));
                if (beanProduct.getTotalPercentDiscount() == 0) {
                    holder.llPercent.setVisibility(View.GONE);
                    holder.afterDiscount.setVisibility(View.GONE);
                }
                holder.percent.setText("(" + beanProduct.getTotalPercentDiscount() + "%)");
                holder.afterDiscount.setText(CmmFunc.formatMoney(beanProduct.getTotalPrice(), false));
                //holder.afterDiscount.setText(CmmFunc.formatMoney(beanProduct.getDiscount(), true));
                holder.price.setText(CmmFunc.formatMoney(Math.round(beanProduct.getPrice()), false));
                holder.priceMax.setText(CmmFunc.formatMoney(Math.round(beanProduct.getPriceMaxUnit()), false));
                holder.minUnit.setText("/ " + beanProduct.getMinUnit());
                holder.maxUnit.setText("/ " + beanProduct.getMaxUnit());

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

                AttributeAdapter attributeAdapter = new AttributeAdapter(fragment, holder.recyclerAttribute, beanProduct.getAttributes(), beanProduct.getId());
                holder.recyclerAttribute.setAdapter(attributeAdapter);

                if (!TextUtils.isEmpty(beanProduct.getImage())) {
                    Picasso.get().load(beanProduct.getImage()).resize(size, size).centerInside().into(holder.thumbnail);
                }
//                if (beanProduct.getType() == BeanProduct.COMBO) {
//                    if (holder.childRecycler != null) {
//                        ChildProductComboAdapter adapter = new ChildProductComboAdapter(fragment, holder.childRecycler, beanProduct.getProducts());
//                        holder.childRecycler.setAdapter(adapter);
//                    }
//                    holder.giftContainer.setVisibility(View.GONE);
//                    if (beanProduct.getGifts().size() > 0 && holder.giftContainer != null) {
//                        holder.giftContainer.setVisibility(View.VISIBLE);
//                        ChildGiftComboAdapter adapter = new ChildGiftComboAdapter(fragment, holder.giftRecycler, beanProduct.getGifts());
//                        holder.giftRecycler.setAdapter(adapter);
//                    }
//
//                    holder.viewDetailCombo.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            if (holder.childContainer.getVisibility() == View.VISIBLE) {
//                                return;
//                            }
//                            holder.childContainer.setVisibility(View.VISIBLE);
//                        }
//                    });
//                    holder.viewCollapseCombo.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            if (holder.childContainer.getVisibility() == View.GONE) {
//                                return;
//                            }
//                            holder.childContainer.setVisibility(View.GONE);
//                        }
//                    });
//                }

                holder.clear.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        removeAllQuantity(beanProduct);
                    }
                });

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FragmentHelper.add(DetailProductFragment.newInstance(beanProduct.getId()));
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void removeAllQuantity(final BeanProduct beanProduct) {
        MessageDialogFragment messageDialogFragment = MessageDialogFragment.newInstance();
        messageDialogFragment.setTitle(fragment.getString(R.string.confirm));
        messageDialogFragment.setMessage(fragment.getString(R.string.remove_all_product_in_cart));
        messageDialogFragment.setCancelable(true);
        messageDialogFragment.setRunnable(new Runnable() {
            @Override
            public void run() {
                APIService.getInstance().removeAllQuantity(beanProduct.getId(), beanProduct.getType())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new ISubscriber() {

                            @Override
                            public void excute(JSONObject jsonObject) {
//                                items.remove(beanProduct);
//                                if (items.size() == 0) {
//                                    FragmentHelper.pop(fragment.getActivity());
//                                    return;
//                                }
//                                CartProductAdapter.this.notifyDataSetChanged();
//                                ((BaseFragment) fragment).getNumberCart();
//                                getCart();
//                                if(items.size() == 1){
//                                    FragmentHelper.pop(fragment.getActivity());
//                                    return;
//                                }

                                try {
                                    CartFragment cartFragment = (CartFragment) fragment.getParentFragment();
                                    int countProduct = 0;
                                    for (int i = 0; i < cartFragment.adapter.getCount(); i++) {
                                        ChildCartFragment childCartFragment = (ChildCartFragment) cartFragment.adapter.getItem(i);
                                        countProduct += childCartFragment.products.size();
                                    }
                                    if (countProduct == 1) {
                                        FragmentHelper.pop(fragment.getActivity(), HomeFragment.class);
                                        return;
                                    }
                                    ((BaseFragment) fragment.getParentFragment()).getNumberCart();
                                    ((CartFragment) fragment.getParentFragment()).getCart2();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                return;
                            }
                        });
            }
        });
        messageDialogFragment.show(fragment.getActivity().getSupportFragmentManager(), messageDialogFragment.getClass().getName());

    }


    private void getCart() {
        APIService.getInstance().getCart()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ISubscriber() {
                    @Override
                    public void excute(JSONObject jsonObject) {
                        try {
                            JSONObject data = jsonObject.getJSONObject("data");
                            JSONObject cartInfo = data.getJSONObject("cart_info");
//                            if (fragment instanceof CartFragment) {
//                                CartFragment cartFragment = (CartFragment) fragment;
//                                cartFragment.totalMoney.setText(CmmFunc.formatMoney(cartInfo.getString("total_money"), false));
//                                cartFragment.discount.setText("-" + CmmFunc.formatMoney(cartInfo.getString("discount"), false));
//                                cartFragment.realPay.setText(CmmFunc.formatMoney(cartInfo.getString("total"), false));
//                                cartFragment.totalPoint.setText(CmmFunc.formatKPoint(cartInfo.getString("total_point")));
//
//
//                                Type listGift = new TypeToken<List<BeanProduct>>() {
//                                }.getType();
//                                List gifts = new Gson().fromJson(cartInfo.getString("gifts"), listGift);
//                                if (gifts.size() == 0) {
//                                    cartFragment.frameGifts.setVisibility(View.GONE);
//                                } else {
//                                    cartFragment.frameGifts.setVisibility(View.VISIBLE);
//                                    GiftAdapter giftAdapter = new GiftAdapter(cartFragment, cartFragment.recyclerGift, gifts);
//                                    cartFragment.recyclerGift.setLayoutManager(new LinearLayoutManager(getContext()));
//                                    cartFragment.recyclerGift.setAdapter(giftAdapter);
//                                }
//
//                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
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
        TextView name, price, priceMax, totalPrice, minUnit, maxUnit, percent, afterDiscount;
        View program;
        LinearLayout llPercent;
        RecyclerView recyclerAttribute;
        //RecyclerView childRecycler;
        //View viewDetailCombo, viewCollapseCombo, childContainer, giftContainer, program;
        ImageView clear;

        public MyViewHolder(View itemView) {
            super(itemView);
            thumbnail = itemView.findViewById(R.id.thumbnail);
            name = itemView.findViewById(R.id.name);

            price = itemView.findViewById(R.id.price);
            priceMax = itemView.findViewById(R.id.price_max);
            totalPrice = itemView.findViewById(R.id.total_price);
            llPercent = itemView.findViewById(R.id.ll_percent);
            percent = itemView.findViewById(R.id.percent);
            afterDiscount = itemView.findViewById(R.id.after_discount);

//            childRecycler = itemView.findViewById(R.id.child_recycler);
//            if (childRecycler != null) {
//                childRecycler.setLayoutManager(new LinearLayoutManager(fragment.getContext()));
//            }
//            viewDetailCombo = itemView.findViewById(R.id.view_expand_combo);
//            viewCollapseCombo = itemView.findViewById(R.id.view_collapse_combo);
//            childContainer = itemView.findViewById(R.id.child_container);
//            giftContainer = itemView.findViewById(R.id.gift_container);
            clear = itemView.findViewById(R.id.clear);
            program = itemView.findViewById(R.id.program);
            minUnit = itemView.findViewById(R.id.min_unit);
            maxUnit = itemView.findViewById(R.id.max_unit);
            recyclerAttribute = itemView.findViewById(R.id.recycler_attribute);
            recyclerAttribute.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        }
    }
}