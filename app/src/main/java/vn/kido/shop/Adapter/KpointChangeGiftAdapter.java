package vn.kido.shop.Adapter;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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
import vn.kido.shop.Fragment.Profile.DialogReceiveKpointFragment;
import vn.kido.shop.Helper.FragmentHelper;
import vn.kido.shop.R;

import static vn.kido.shop.Class.GlobalClass.getContext;

public class KpointChangeGiftAdapter extends RecyclerView.Adapter<KpointChangeGiftAdapter.MyViewHolder> {
    View itemView;
    Fragment fragment;
    RecyclerView recyclerView;
    List<BeanKpoint> beanKpoints;
    int size;

    public KpointChangeGiftAdapter(Fragment fragment, RecyclerView recyclerView, List<BeanKpoint> beanKpoints) {
        this.fragment = fragment;
        this.recyclerView = recyclerView;
        this.beanKpoints = beanKpoints;
        size = CmmFunc.convertDpToPx(getContext(), 240);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_kpoint_gift, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        try {
            final BeanKpoint beanKpoint = beanKpoints.get(position);
            if (beanKpoint != null) {
                Picasso.get().load(beanKpoint.getImage()).resize(size, size).centerInside().into(holder.ivGift);
                holder.txtGiftTitle.setText(beanKpoint.getName());
                holder.txtGiftName.setText(beanKpoint.getAttribute());
                holder.txtGiftPrice.setText(CmmFunc.formatMoney(Math.round(beanKpoint.getPrice()), false));
                holder.txtGiftNotice.setText(beanKpoint.getDescription());
                holder.txtKpoint.setText(beanKpoint.getRequire() + "");
                holder.btnReceiveGift.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        claimRewardKpoint(beanKpoint.getId());
                    }
                });
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
        ImageView ivGift;
        TextView txtGiftTitle, txtGiftName, txtGiftPrice, txtGiftNotice, txtKpoint;
        Button btnReceiveGift;

        public MyViewHolder(View itemView) {
            super(itemView);
            ivGift = itemView.findViewById(R.id.iv_gift);
            txtGiftTitle = itemView.findViewById(R.id.txt_gift_title);
            txtGiftName = itemView.findViewById(R.id.txt_gift_name);
            txtGiftPrice = itemView.findViewById(R.id.txt_gift_price);
            txtGiftNotice = itemView.findViewById(R.id.txt_gift_notice);
            txtKpoint = itemView.findViewById(R.id.txt_kpoint);
            btnReceiveGift = itemView.findViewById(R.id.btn_receive_gift);
        }
    }

    private void claimRewardKpoint(int rewardId) {
        APIService.getInstance().claimRewardKpoint(rewardId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ISubscriber() {
                    @Override
                    public void excute(JSONObject jsonObject) {
                        try {
                            FragmentHelper.add(DialogReceiveKpointFragment.newInstance());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }
}
