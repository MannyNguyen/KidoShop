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
import vn.kido.shop.Fragment.Product.ImageProductFragment;
import vn.kido.shop.Helper.FragmentHelper;
import vn.kido.shop.R;

public class BannerProductAdapter extends PagerAdapter {

    LayoutInflater mLayoutInflater;
    BaseFragment fragment;
    List<String> images;
    int id;

    public BannerProductAdapter(BaseFragment fragment, List<String> images, int id) {
        this.fragment = fragment;
        this.images = images;
        this.id = id;
        mLayoutInflater = (LayoutInflater) GlobalClass.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == (object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        View itemView = mLayoutInflater.inflate(R.layout.row_product_images, container, false);
        try {
            ImageView image = itemView.findViewById(R.id.image);
            final String url = images.get(position);
            if (!TextUtils.isEmpty(url)) {
                Picasso.get().load(url).placeholder(R.drawable.ic_upload_image).into(image);
            } else {
                image.setImageResource(0);
            }
            container.addView(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FragmentHelper.add(ImageProductFragment.newInstance(position, images));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
