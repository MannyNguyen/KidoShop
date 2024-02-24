package vn.kido.shop.Fragment.Product;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

import vn.kido.shop.Adapter.ImageProductAdapter;
import vn.kido.shop.Bean.BeanProduct;
import vn.kido.shop.Fragment.BaseFragment;
import vn.kido.shop.Helper.FragmentHelper;
import vn.kido.shop.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ImageProductFragment extends BaseFragment implements View.OnClickListener {
    FrameLayout outer;
    TabLayout tab;
    ViewPager pager;
    List<String> images;
    public BeanProduct product;

    public ImageProductFragment() {
        // Required empty public constructor
    }

    public static ImageProductFragment newInstance(int position, List<String> images) {
        Bundle args = new Bundle();
        args.putInt("position", position);
        args.putStringArrayList("images", (ArrayList<String>) images);
        ImageProductFragment fragment = new ImageProductFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (rootView != null) {
            return rootView;
        }
        rootView = inflater.inflate(R.layout.fragment_image_product, container, false);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (isLoaded) {
            return;
        }
        threadInit = new Thread(new Runnable() {
            @Override
            public void run() {
                outer = rootView.findViewById(R.id.outer);
                tab = rootView.findViewById(R.id.tab);
                pager = rootView.findViewById(R.id.pager);

                images = new ArrayList(getArguments().getStringArrayList("images"));

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ImageProductAdapter adapter = new ImageProductAdapter(ImageProductFragment.this, images);
                        pager.setAdapter(adapter);
                        tab.setupWithViewPager(pager);
                        pager.setCurrentItem(getArguments().getInt("position"));
                        outer.setOnClickListener(ImageProductFragment.this);
                    }
                });
            }
        });
        threadInit.start();
        isLoaded = true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.outer:
                FragmentHelper.pop(getActivity());
                break;
        }
    }
}
