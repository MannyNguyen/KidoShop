package vn.kido.shop.Fragment.Program;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import vn.kido.shop.Api.APIService;
import vn.kido.shop.Api.ISubscriber;
import vn.kido.shop.Class.CmmFunc;
import vn.kido.shop.Fragment.BaseFragment;
import vn.kido.shop.Fragment.Dialog.NoticeDialogFragment;
import vn.kido.shop.Fragment.OAuth.LoginFragment;
import vn.kido.shop.Helper.FragmentHelper;
import vn.kido.shop.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailProgramFragment extends BaseFragment implements View.OnClickListener {
    ImageView image;
    TextView title, titleProgram, description;
    Button btnAddEventCart;

    public DetailProgramFragment() {
        // Required empty public constructor
    }

    public static DetailProgramFragment newInstance(int detaileventid) {
        Bundle args = new Bundle();
        args.putInt("detail_event_id", detaileventid);
        DetailProgramFragment fragment = new DetailProgramFragment();
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
        rootView = inflater.inflate(R.layout.fragment_detail_program, container, false);
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
                titleProgram = rootView.findViewById(R.id.title_program);
                description = rootView.findViewById(R.id.description);
                image = rootView.findViewById(R.id.thumbnail);
                btnAddEventCart = rootView.findViewById(R.id.btn_add_event_cart);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        title = rootView.findViewById(R.id.title);
                        title.setText(getString(R.string.detail_event));
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                getDetailEvent(getArguments().getInt("detail_event_id"));
                                btnAddEventCart.setOnClickListener(DetailProgramFragment.this);
                            }
                        });
                    }
                });

            }
        });
        threadInit.start();
        isLoaded = true;
    }

    public void getDetailEvent(int Id) {
        showProgress();
        APIService.getInstance().getDetailEvent(Id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ISubscriber() {
                    @Override
                    public void done() {
                        super.done();
                        hideProgress();
                    }

                    @Override
                    public void excute(JSONObject jsonObject) {
                        try {
                            JSONObject event = jsonObject.getJSONObject("data").getJSONObject("event_detail");
                            title.setText(event.getString("name") + "");
                            titleProgram.setText(event.getString("name") + "");
                            description.setText(event.getString("description") + "");
                            if (event.getBoolean("can_add") == false) {
                                btnAddEventCart.setVisibility(View.GONE);
                            }
                            Picasso.get().load(event.getString("image")).into(image);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    public void addEventToCart(int eventId) {
        showProgress();
        APIService.getInstance().addEventToCart(eventId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ISubscriber() {
                    @Override
                    public void done() {
                        super.done();
                        hideProgress();
                    }

                    @Override
                    public void excute(JSONObject jsonObject) {
                        try {
                            NoticeDialogFragment noticeDialogFragment = NoticeDialogFragment.newInstance();
                            noticeDialogFragment.setTitle(getString(R.string.notify));
                            noticeDialogFragment.setMessage("Đã thêm thành công mặt hàng đang áp dụng chương trình vào giỏ hàng");
                            noticeDialogFragment.setRunnable(new Runnable() {
                                @Override
                                public void run() {
                                    return;
                                }
                            });
                            noticeDialogFragment.show(getActivity().getSupportFragmentManager(), noticeDialogFragment.getClass().getName());
                            getNumberCart();
                            CmmFunc.showAnim(rootView.findViewById(R.id.cart));

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_add_event_cart:
                addEventToCart(getArguments().getInt("detail_event_id"));
                break;
        }
    }
}
