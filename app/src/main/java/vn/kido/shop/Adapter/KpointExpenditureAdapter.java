package vn.kido.shop.Adapter;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import vn.kido.shop.Bean.BeanKpoint;
import vn.kido.shop.Class.CmmFunc;
import vn.kido.shop.R;

public class KpointExpenditureAdapter extends RecyclerView.Adapter<KpointExpenditureAdapter.MyViewHolder> {
    View itemView;
    Fragment fragment;
    RecyclerView recyclerView;
    List<BeanKpoint> beanKpoints;

    public KpointExpenditureAdapter(Fragment fragment, RecyclerView recyclerView, List<BeanKpoint> beanKpoints) {
        this.fragment = fragment;
        this.recyclerView = recyclerView;
        this.beanKpoints = beanKpoints;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_kpoint, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        try {
            final BeanKpoint beanKpoint = beanKpoints.get(position);
            if (beanKpoint != null) {
//                if (position % 2 == 0) {
//                    //itemView.setBackgroundColor(Color.parseColor("#ffffff"));
//                    itemView.setBackgroundResource(R.drawable.gray_box);
//                } else {
//                    itemView.setBackgroundResource(R.drawable.white_radius_box);
//                    //itemView.setBackgroundColor(Color.parseColor("#ebebeb"));
//                }
                int isReceive = beanKpoint.getIsReceive();
                if (isReceive == 0) {
                    holder.llRequestPrice.setVisibility(View.VISIBLE);
                    holder.llReceive.setVisibility(View.GONE);
                    holder.ivReceiveBg.setVisibility(View.GONE);
                    holder.txtGiftTitle.setText(beanKpoint.getName());
                    holder.txtGiftName.setText(beanKpoint.getAttribute());
                    holder.txtGiftPrice.setText(CmmFunc.formatMoney(Math.round(beanKpoint.getPrice()), false));
                    holder.txtRequestPrice.setText(CmmFunc.formatMoney(Math.round(beanKpoint.getRequire()), false));
                    holder.txtGiftNotice.setText(beanKpoint.getDescription());
                    Picasso.get().load(beanKpoint.getImage()).into(holder.ivGift);
                    holder.btnReceiveGift.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            claimRewardMoney(beanKpoint.getId(), position);
                        }
                    });
                }
                if (isReceive == 1) {
                    holder.llReceive.setVisibility(View.VISIBLE);
                    holder.ivReceiveBg.setVisibility(View.VISIBLE);
                    holder.llRequestPrice.setVisibility(View.GONE);
                    holder.viewDash.setVisibility(View.GONE);
                    holder.txtGiftTitle.setText(beanKpoint.getName());
                    holder.txtGiftName.setText(beanKpoint.getAttribute());
                    holder.txtGiftPrice.setText(CmmFunc.formatMoney(Math.round(beanKpoint.getPrice()), false));
                    holder.txtGiftNotice.setText(beanKpoint.getDescription());
                    Picasso.get().load(beanKpoint.getImage()).into(holder.ivGift);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return beanKpoints.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtGiftTitle, txtGiftName, txtGiftPrice, txtGiftNotice, txtRequestPrice;
        ImageView ivReceived, ivGift, ivReceiveBg;
        LinearLayout llRequestPrice, llReceive;
        Button btnReceiveGift;
        View viewDash;

        public MyViewHolder(View itemView) {
            super(itemView);
            llReceive = itemView.findViewById(R.id.ll_receive);
            txtGiftTitle = itemView.findViewById(R.id.txt_gift_title);
            txtGiftName = itemView.findViewById(R.id.txt_gift_name);
            txtGiftPrice = itemView.findViewById(R.id.txt_gift_price);
            txtGiftNotice = itemView.findViewById(R.id.txt_gift_notice);
            ivReceived = itemView.findViewById(R.id.iv_received);
            llRequestPrice = itemView.findViewById(R.id.ll_request_price);
            txtRequestPrice = itemView.findViewById(R.id.txt_request_price);
            btnReceiveGift = itemView.findViewById(R.id.btn_receive_gift);
            ivReceiveBg = itemView.findViewById(R.id.iv_receive_bg);
            ivGift = itemView.findViewById(R.id.iv_gift);
            viewDash = itemView.findViewById(R.id.view_dash);
        }
    }

    private void claimRewardMoney(int rewardId, final int position) {
        APIService.getInstance().claimRewardMoney(rewardId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ISubscriber() {
                    @Override
                    public void excute(JSONObject jsonObject) {
                        try {
                            beanKpoints.get(position).setIsReceive(1);
                            KpointExpenditureAdapter.this.notifyDataSetChanged();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }
}
