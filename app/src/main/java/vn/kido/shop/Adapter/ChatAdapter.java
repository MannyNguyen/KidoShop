package vn.kido.shop.Adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;

import java.util.List;
import java.util.Locale;

import vn.kido.shop.Bean.BeanMessage;
import vn.kido.shop.Class.GlobalClass;
import vn.kido.shop.R;

import static vn.kido.shop.Bean.BeanMessage.ISME;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder> {
    private final int TEXT_ME = 1;
    private final int IMAGE_ME = 3;
    private final int TEXT_OTHER = 2;
    private final int IMAGE_OTHER = 4;
    private final int DEFAULT = 5;
    private final int TIME = 6;
    boolean isMyText;
    List<BeanMessage> messages;
    RecyclerView recyclerView;
    Fragment fragment;

    public ChatAdapter(Fragment fragment, List<BeanMessage> messages, RecyclerView recyclerView) {
        this.fragment = fragment;
        this.messages = messages;
        this.recyclerView = recyclerView;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layout = -1;
        switch (viewType) {
            case TEXT_ME:
                layout = R.layout.row_message_text_sender;
                break;
            case IMAGE_ME:
                layout = R.layout.row_message_image_sender;
                break;
            case TEXT_OTHER:
                layout = R.layout.row_message_text_receiver;
                break;
            case IMAGE_OTHER:
                layout = R.layout.row_message_image_receiver;
                break;
            case DEFAULT:
                layout = R.layout.row_message_receiver_service;
                break;
            case TIME:
                layout = R.layout.row_message_date;
                break;
        }
        View v = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        try {
            BeanMessage item = messages.get(position);
            holder.time.setText(new DateTime(item.getTimestamp()).toString("HH:mm"));
            if (item != null) {
                switch (item.getType()) {
                    case BeanMessage.TEXT:
                        holder.message.setText(item.getMessage());
                        break;
                    case BeanMessage.IMAGE:
                        if (item.getMessage().contains("http://") || item.getMessage().contains("https://")) {
                            Picasso.get().load(item.getMessage()).into(holder.image);
                        } else {
                            byte[] decodedString = Base64.decode(item.getMessage(), Base64.DEFAULT);
                            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                            holder.image.setImageBitmap(decodedByte);
                        }
                        break;
                    case BeanMessage.DEFAULT:
                        holder.message.setText(item.getMessage());
                        ChildMessageAdapter adapter = new ChildMessageAdapter(fragment, recyclerView, item.getChild());
                        holder.childRecycler.setAdapter(adapter);
                        break;
                    case BeanMessage.TIME:
                        //new DateTime(item.getTimestamp()).format(DateTimeFormatter.ofPattern("EEEE, dd MMMM, yyyy",Locale.FRENCH))
                        holder.time.setText(new DateTime(item.getTimestamp()).toString("EEEE, HH:mm dd/MM/yyyy", new Locale("vi", "VN")));
                        break;
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemViewType(int position) {
        BeanMessage message = messages.get(position);
        if (message.isMe() == ISME) {
            if (message.getType() == BeanMessage.TEXT) {
                return TEXT_ME;
            }

            if (message.getType() == BeanMessage.IMAGE) {
                return IMAGE_ME;
            }
        } else {
            //OTHER
            if (message.getType() == BeanMessage.TEXT) {
                return TEXT_OTHER;
            }

            if (message.getType() == BeanMessage.IMAGE) {
                return IMAGE_OTHER;
            }

            if (message.getType() == BeanMessage.DEFAULT) {
                return DEFAULT;
            }
            if (message.getType() == BeanMessage.TIME) {
                return TIME;
            }
        }
        return 0;
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView message, time;
        ImageView image;
        RecyclerView childRecycler;

        public MyViewHolder(View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.message);
            time = itemView.findViewById(R.id.time);
            image = itemView.findViewById(R.id.image);
            childRecycler = itemView.findViewById(R.id.child_recycler);
            if (childRecycler != null) {
                childRecycler.setLayoutManager(new LinearLayoutManager(GlobalClass.getContext()));
            }
        }
    }
}
