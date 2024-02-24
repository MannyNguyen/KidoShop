package vn.kido.shop.Fragment.Notify;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;
import org.json.JSONObject;

import java.util.Locale;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import vn.kido.shop.Api.APIService;
import vn.kido.shop.Api.ISubscriber;
import vn.kido.shop.Bean.BeanNotify;
import vn.kido.shop.Fragment.BaseFragment;
import vn.kido.shop.Helper.FragmentHelper;
import vn.kido.shop.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailNotifyFragment extends BaseFragment implements View.OnClickListener {
    ImageView imv;
    TextView description, name, createDate;
    Gson gson;

    public DetailNotifyFragment() {
        // Required empty public constructor
    }

    public static DetailNotifyFragment newInstance(int id) {
        Bundle args = new Bundle();
        args.putInt("id", id);
        DetailNotifyFragment fragment = new DetailNotifyFragment();
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
        rootView = inflater.inflate(R.layout.fragment_detail_notify, container, false);
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
                TextView title = rootView.findViewById(R.id.title);
                title.setText(getString(R.string.notify));
                gson = new Gson();
                name = (TextView) rootView.findViewById(R.id.txt_notify_type);
                description = (TextView) rootView.findViewById(R.id.txt_notify_description);
                createDate = (TextView) rootView.findViewById(R.id.txt_notify_date);
                imv = (ImageView) rootView.findViewById(R.id.iv_notify_photo);

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getDetailNotify(getArguments().getInt("id"));
                    }
                });

            }
        });
        threadInit.start();
        isLoaded = true;
    }

    public void getDetailNotify(final int Id) {
        APIService.getInstance().getDetailNotify(Id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ISubscriber() {
                    @Override
                    public void excute(JSONObject jsonObject) {
                        try {
                            JSONObject data = jsonObject.getJSONObject("data");
                            Picasso.get().load(data.getString("image")).into(imv);

                            name.setText(data.getString("title") + "");
                            description.setText(data.getString("detail") + "");
                            createDate.setText(new DateTime(data.getLong("create_date")).toString("EEEE, dd/MM/yyyy", new Locale("vi", "VN")));
                            getUnSeen();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    public void deleteNotify(final int id) {
        APIService.getInstance().delete_notify(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ISubscriber() {
                    @Override
                    public void excute(JSONObject jsonObject) {
                        try {
                            HomeNotifyFragment homeNotifyFragment = (HomeNotifyFragment) FragmentHelper.findFragmentByTag(HomeNotifyFragment.class);
                            if (homeNotifyFragment != null) {
                                int index = BeanNotify.getIndexById(id, homeNotifyFragment.notifies);
                                if (index > -1) {
                                    homeNotifyFragment.notifies.remove(index);
                                    homeNotifyFragment.adapter.notifyDataSetChanged();
                                    FragmentHelper.pop(getActivity());
                                }
                            }
                        } catch (Exception e) {

                        }

                    }
                });
    }

    public void getUnSeen() {
        APIService.getInstance().getUnSeen()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ISubscriber() {
                    @Override
                    public void excute(JSONObject jsonObject) {
                        try {
                            JSONObject data = jsonObject.getJSONObject("data");
                            TextView numNoti = rootView.findViewById(R.id.number_noti);
                            if (data.getInt("num") == 0) {
                                numNoti.setVisibility(View.GONE);
                            } else {
                                numNoti.setText(data.getInt("num") + "");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

        }
    }
}
