package vn.kido.shop.Adapter;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import vn.kido.shop.Bean.BeanMessage;
import vn.kido.shop.Fragment.Support.SupportFragment;
import vn.kido.shop.Helper.FragmentHelper;
import vn.kido.shop.Helper.SocketIOHelper;
import vn.kido.shop.R;

public class MenuMessageAdapter extends RecyclerView.Adapter<MenuMessageAdapter.MyViewHolder> {

    Fragment fragment;
    RecyclerView recyclerView;
    List<BeanMessage.BeanChildMessage> childMessages;

    public MenuMessageAdapter(Fragment fragment, RecyclerView recyclerView, List<BeanMessage.BeanChildMessage> childMessages) {
        this.fragment = fragment;
        this.recyclerView = recyclerView;
        this.childMessages = childMessages;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_message_menu, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        try {
            final BeanMessage.BeanChildMessage childMessage = childMessages.get(position);
            if (childMessage != null) {
                holder.text.setText(childMessage.getName());
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        BeanMessage beanMessage = new BeanMessage();
                        beanMessage.setMe(BeanMessage.ISME);
                        beanMessage.setType(BeanMessage.TEXT);
                        beanMessage.setMessage(childMessage.getName());
                        beanMessage.setTimestamp(System.currentTimeMillis());
                        if (fragment instanceof SupportFragment) {
                            SupportFragment supportFragment = (SupportFragment) fragment;
                            supportFragment.messages.add(beanMessage);
                            supportFragment.adapter.notifyItemInserted(supportFragment.messages.size() - 1);
                            supportFragment.recycler.smoothScrollToPosition(supportFragment.messages.size() - 1);
                            SocketIOHelper.getInstance().sendMessage(BeanMessage.DEFAULT, childMessage.getName(), childMessage.getId());
                        }
                        FragmentHelper.pop(fragment.getActivity());
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return childMessages.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView text;

        public MyViewHolder(View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.text);
        }
    }
}
