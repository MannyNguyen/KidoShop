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

import vn.kido.shop.Bean.BeanProgram;
import vn.kido.shop.Class.GlobalClass;
import vn.kido.shop.Fragment.BaseFragment;
import vn.kido.shop.Fragment.Program.DetailProgramFragment;
import vn.kido.shop.Helper.FragmentHelper;
import vn.kido.shop.R;

public class ProgramPagerAdapter extends PagerAdapter {
    LayoutInflater mLayoutInflater;
    BaseFragment fragment;
    List<BeanProgram> programs;

    public ProgramPagerAdapter(BaseFragment fragment, List<BeanProgram> programs) {
        mLayoutInflater = (LayoutInflater) GlobalClass.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.fragment = fragment;
        this.programs = programs;
    }

    @Override
    public int getCount() {
        return programs.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View itemView = mLayoutInflater.inflate(R.layout.row_program_pager, container, false);
        try {
            final BeanProgram beanProgram = programs.get(position);
            ImageView image = itemView.findViewById(R.id.thumbnail);
            TextView title = itemView.findViewById(R.id.title);
            if (!TextUtils.isEmpty(beanProgram.getImage())) {
                Picasso.get().load(beanProgram.getImage()).into(image);
            } else {
                image.setImageResource(0);
            }
            title.setText(beanProgram.getTitle());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentHelper.add(DetailProgramFragment.newInstance(beanProgram.getId()));
                }
            });
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
