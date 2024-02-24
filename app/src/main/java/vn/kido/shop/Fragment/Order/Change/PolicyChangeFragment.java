package vn.kido.shop.Fragment.Order.Change;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import org.json.JSONObject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import vn.kido.shop.Api.APIService;
import vn.kido.shop.Api.ISubscriber;
import vn.kido.shop.Fragment.BaseFragment;
import vn.kido.shop.Helper.FragmentHelper;
import vn.kido.shop.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class PolicyChangeFragment extends BaseFragment {
    WebView wvPolicy;

    public PolicyChangeFragment() {
        // Required empty public constructor
    }

    public static PolicyChangeFragment newInstance() {
        Bundle args = new Bundle();
        PolicyChangeFragment fragment = new PolicyChangeFragment();
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
        rootView = inflater.inflate(R.layout.fragment_policy_change, container, false);
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
                wvPolicy = rootView.findViewById(R.id.wv_policy);
                final View back = rootView.findViewById(R.id.back);
                TextView title = rootView.findViewById(R.id.title);
                title.setText(getString(R.string.change_pay_product));
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        wvPolicy.getSettings().setUseWideViewPort(true);
                        //wvPolicy.setInitialScale(1);
                        wvPolicy.getSettings().setLoadWithOverviewMode(true);
                        wvPolicy.getSettings().setSupportZoom(true);
                        wvPolicy.getSettings().setBuiltInZoomControls(true);
                        wvPolicy.getSettings().setDisplayZoomControls(false);

                        wvPolicy.getSettings().setAllowFileAccess(true);
                        wvPolicy.getSettings().setAppCacheEnabled(true);
                        wvPolicy.getSettings().setJavaScriptEnabled(true);
                        wvPolicy.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);
                        wvPolicy.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
                        wvPolicy.getSettings().setJavaScriptEnabled(true);

                        wvPolicy.setWebViewClient(new WebViewClient() {
                            @Override
                            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                                showProgress();
                            }

                            @Override
                            public void onPageFinished(WebView view, String url) {
                                hideProgress();
                            }

                        });
                        wvPolicy.setWebChromeClient(new WebChromeClient());
                        getReturnPolicy();

//                        if (back != null) {
//                            back.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View view) {
//                                    onBack();
//                                }
//                            });
//                        }
                    }
                });
            }
        });
        threadInit.start();
        isLoaded = true;
    }

//    public void onBack() {
//        try {
//            wvPolicy.removeAllViews();
//            wvPolicy.clearHistory();
//            wvPolicy.clearCache(true);
//            wvPolicy.destroy();
//            wvPolicy = null;
//            FragmentHelper.pop(getActivity());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    public void getReturnPolicy(){
        showProgress();
        APIService.getInstance().getReturnPolicy()
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
                            JSONObject data = jsonObject.getJSONObject("data");
                            wvPolicy.loadUrl(data.getString("policy_return_url"));
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });
    }
}
