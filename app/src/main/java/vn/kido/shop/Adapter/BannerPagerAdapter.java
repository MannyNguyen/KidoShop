package vn.kido.shop.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import vn.kido.shop.Bean.BeanBanner;
import vn.kido.shop.Class.GlobalClass;
import vn.kido.shop.Fragment.BaseFragment;
import vn.kido.shop.Fragment.Product.DetailProductFragment;
import vn.kido.shop.Fragment.Common.HomeFragment;
import vn.kido.shop.Fragment.Program.DetailProgramFragment;
import vn.kido.shop.Fragment.Program.HomeProgramFragment;
import vn.kido.shop.Helper.FragmentHelper;
import vn.kido.shop.R;

public class BannerPagerAdapter extends PagerAdapter {

    LayoutInflater mLayoutInflater;
    BaseFragment fragment;
    List<BeanBanner> banners;
    int typeBanner;

    public BannerPagerAdapter(BaseFragment fragment, List<BeanBanner> banners, int typeBanner) {
        this.fragment = fragment;
        this.banners = banners;
        this.typeBanner = typeBanner;
        mLayoutInflater = (LayoutInflater) GlobalClass.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return banners.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == (object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View itemView = mLayoutInflater.inflate(R.layout.row_home_banner, container, false);
        ImageView image = itemView.findViewById(R.id.image);
        TextView txtTitle = itemView.findViewById(R.id.txt_title);
        final BeanBanner beanBanner = banners.get(position);
        if (beanBanner != null) {
            if (!TextUtils.isEmpty(beanBanner.getImage())) {
                Picasso.get().load(beanBanner.getImage()).into(image);
            }

            if (typeBanner == HomeFragment.BANNER || typeBanner == HomeProgramFragment.BANNER) {
                txtTitle.setVisibility(View.GONE);
            }
            if (typeBanner == DetailProductFragment.BANNER_TITLE) {
                txtTitle.setVisibility(View.VISIBLE);
                txtTitle.setText(beanBanner.getTitle() + "");
            }
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (beanBanner.getType() == 2) {
                        FragmentHelper.add(DetailProductFragment.newInstance(beanBanner.getId()));
                    }else if (beanBanner.getType()==1){
                        FragmentHelper.add(DetailProgramFragment.newInstance(beanBanner.getId()));
                    }

                }
            });
        } else {
            image.setImageResource(0);
        }
        container.addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
