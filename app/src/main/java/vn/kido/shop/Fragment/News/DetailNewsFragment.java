package vn.kido.shop.Fragment.News;


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

import com.google.gson.Gson;

import org.json.JSONObject;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import vn.kido.shop.Api.APIService;
import vn.kido.shop.Api.ISubscriber;
import vn.kido.shop.Bean.BeanNews;
import vn.kido.shop.Fragment.BaseFragment;
import vn.kido.shop.Helper.FragmentHelper;
import vn.kido.shop.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailNewsFragment extends BaseFragment {
    WebView webView;
    String url;
    int id;

    public DetailNewsFragment() {
    }

    public static DetailNewsFragment newInstance(int detailnewsid) {
        Bundle args = new Bundle();
        args.putInt("detail_news_id", detailnewsid);
        DetailNewsFragment fragment = new DetailNewsFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView != null) {
            return rootView;
        }
        rootView = inflater.inflate(R.layout.fragment_detail_news, container, false);
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
                webView = rootView.findViewById(R.id.webview);
                final View back = rootView.findViewById(R.id.back);
                final TextView title = rootView.findViewById(R.id.title);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        webView.getSettings().setUseWideViewPort(true);
                        webView.getSettings().setSupportZoom(true);
                        webView.getSettings().setBuiltInZoomControls(true);
                        webView.getSettings().setDisplayZoomControls(false);

                        webView.getSettings().setAllowFileAccess(true);
                        webView.getSettings().setAppCacheEnabled(true);
                        webView.getSettings().setJavaScriptEnabled(true);
                        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
                        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
                        webView.getSettings().setJavaScriptEnabled(true);
                        webView.setWebViewClient(new WebViewClient() {
                            @Override
                            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                                showProgress();
                            }

                            @Override
                            public void onPageFinished(WebView view, String url) {
                                hideProgress();
                            }

                        });
                        webView.setWebChromeClient(new WebChromeClient());
                        title.setText(R.string.news);
                        getDetailNews(getArguments().getInt("detail_news_id"));

                        if (back != null) {
                            back.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    onBack();
                                }
                            });
                        }
                    }
                });

            }
        });
        threadInit.start();
        isLoaded = true;
    }

    public void onBack() {
        try {
            webView.removeAllViews();
            webView.clearHistory();
            webView.clearCache(true);
            webView.destroy();
            webView = null;
            FragmentHelper.pop(getActivity());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getDetailNews(int Id) {
        APIService.getInstance().getDetailNew(Id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<String, Object>() {
                    @Override
                    public Object call(String s) {
                        try {
                            return new JSONObject(s);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                }).subscribe(new ISubscriber() {

            @Override
            public void excute(JSONObject jsonObject) {
                try {
                    if (jsonObject == null) {
                        return;
                    }
                    int code = jsonObject.getInt("code");
                    if (code != 1) {
                        return;
                    }
                    JSONObject newsdetail = jsonObject.getJSONObject("data");

                    BeanNews beanNewss = new Gson().fromJson(newsdetail.getString("news_detail"), BeanNews.class);
                    url = beanNewss.getNews_link();
                    id = beanNewss.getId();

                    webView.loadUrl(url);
                    //webView.loadDataWithBaseURL(url,"","text/html", "UTF-8","");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}

