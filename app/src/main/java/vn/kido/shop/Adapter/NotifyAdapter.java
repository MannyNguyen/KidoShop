package vn.kido.shop.Adapter;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.json.JSONObject;

import java.util.List;
import java.util.Locale;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import vn.kido.shop.Api.APIService;
import vn.kido.shop.Api.ISubscriber;
import vn.kido.shop.Bean.BeanNotify;
import vn.kido.shop.Fragment.Notify.DetailNotifyFragment;
import vn.kido.shop.Fragment.Order.DetailOrderFragment;
import vn.kido.shop.Helper.FragmentHelper;
import vn.kido.shop.R;

public class NotifyAdapter extends RecyclerView.Adapter<NotifyAdapter.MyViewHolder> {
    Fragment fragment;
    RecyclerView recyclerView;
    List<BeanNotify> notifies;

    public NotifyAdapter(Fragment fragment, RecyclerView recyclerView, List<BeanNotify> notifies) {
        this.fragment = fragment;
        this.recyclerView = recyclerView;
        this.notifies = notifies;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_notify, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        try {
            final BeanNotify notify = notifies.get(position);
            if (notify != null) {
                if (notify.isSeen()) {
                    holder.thumbnail.setImageResource(R.drawable.ic_read);
                } else {
                    holder.thumbnail.setImageResource(R.drawable.ic_unread);
                }
                holder.title.setText(notify.getTitle());
                holder.description.setText(notify.getDescription());
                holder.createDate.setText(new DateTime(notify.getCreateDate()).toString("EEEE, dd/MM/yyyy", new Locale("vi", "VN")));
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        notify.setSeen(true);
                        notifyDataSetChanged();
                        seenNoti(notify.getId());
                        if (notify.getType() == BeanNotify.ORDER) {
                            FragmentHelper.add(DetailOrderFragment.newInstance(notify.getOrderCode() + ""));
                            return;
                        }
                        FragmentHelper.add(DetailNotifyFragment.newInstance(notify.getId()));
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return notifies.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnail;
        TextView title, description, createDate;

        public MyViewHolder(View itemView) {
            super(itemView);
            thumbnail = itemView.findViewById(R.id.thumbnail);
            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
            createDate = itemView.findViewById(R.id.create_date);
        }
    }

    private void seenNoti(int notiId) {
        APIService.getInstance().seenNoti(notiId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ISubscriber() {
                    @Override
                    public void excute(JSONObject jsonObject) {
                        try {
                            JSONObject data = jsonObject.getJSONObject("data");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }
}
