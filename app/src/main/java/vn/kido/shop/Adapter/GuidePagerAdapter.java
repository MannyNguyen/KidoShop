package vn.kido.shop.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

import vn.kido.shop.Class.GlobalClass;
import vn.kido.shop.Fragment.BaseFragment;
import vn.kido.shop.R;

public class GuidePagerAdapter extends PagerAdapter{
    LayoutInflater mLayoutInflater;
    BaseFragment fragment;
    List<String> imageGuides;

    public GuidePagerAdapter(BaseFragment fragment, List<String> imageGuides) {
        mLayoutInflater = (LayoutInflater) GlobalClass.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.fragment = fragment;
        this.imageGuides = imageGuides;
    }

    @Override
    public int getCount() {
        return imageGuides.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View itemView = mLayoutInflater.inflate(R.layout.row_guild, container, false);
        try {
            ImageView image = itemView.findViewById(R.id.image);
            String url = imageGuides.get(position);
            if (!TextUtils.isEmpty(url)) {
                Picasso.get().load(url).into(image);
            } else {
                image.setImageResource(0);
            }
            container.addView(itemView);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return itemView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
