package vn.kido.shop.Fragment.Search;


import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import vn.kido.shop.Adapter.ProductAdapter;
import vn.kido.shop.Adapter.SuggestSearchAdapter;
import vn.kido.shop.Api.APIService;
import vn.kido.shop.Api.ISubscriber;
import vn.kido.shop.Bean.BeanProduct;
import vn.kido.shop.Class.CmmFunc;
import vn.kido.shop.Class.StoreCart;
import vn.kido.shop.Fragment.BaseFragment;
import vn.kido.shop.Helper.FragmentHelper;
import vn.kido.shop.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchLiveFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {
    Handler handler;
    EditText content;
    Subscription subSearch, subPopular;
    RecyclerView recycler, recyclerPopular;
    List<String> suggests = new ArrayList<>();
    SuggestSearchAdapter adapter;
    LinearLayout llPopular;
    int page = 1;
    LinkedHashMap map;
    ProductAdapter productAdapter;
    SwipeRefreshLayout refresh;
    RecyclerView.OnScrollListener onScrollListener;
    FrameLayout frameSearch;
    TextView title;

    public SearchLiveFragment() {
        // Required empty public constructor
    }

    public static SearchLiveFragment newInstance() {
        Bundle args = new Bundle();
        SearchLiveFragment fragment = new SearchLiveFragment();
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
        rootView = inflater.inflate(R.layout.fragment_search_live, container, false);
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
                title = rootView.findViewById(R.id.title);
                content = rootView.findViewById(R.id.content);
                recycler = rootView.findViewById(R.id.recycler);
                recyclerPopular = rootView.findViewById(R.id.recycler_popular);
                llPopular = rootView.findViewById(R.id.ll_popular);
                refresh = rootView.findViewById(R.id.refresher);
                frameSearch = rootView.findViewById(R.id.frame_search);
                final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                final LinearLayoutManager layoutManagerPopular = new LinearLayoutManager(getContext());
                map = new LinkedHashMap();
                productAdapter = new ProductAdapter(SearchLiveFragment.this, recyclerPopular, map);
                refresh.setOnRefreshListener(SearchLiveFragment.this);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        title.setText(getString(R.string.search));
                        recycler.setLayoutManager(layoutManager);
                        recyclerPopular.setLayoutManager(layoutManagerPopular);
                        recyclerPopular.setAdapter(productAdapter);
                        getPopularProduct();
                        onScrollListener = new RecyclerView.OnScrollListener() {
                            @Override
                            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                                if (layoutManagerPopular.findLastCompletelyVisibleItemPosition() == map.size() - 1 && map.size() >= 10) {
                                    if (subPopular == null) {
                                        return;
                                    }
                                    if (!subPopular.isUnsubscribed()) {
                                        return;
                                    }
                                    page++;
                                    getPopularProduct();
                                }
                            }
                        };
                        recyclerPopular.addOnScrollListener(onScrollListener);

                        HandlerThread handlerThread = new HandlerThread(SearchLiveFragment.this.getClass().getName(), android.os.Process.THREAD_PRIORITY_BACKGROUND);
                        handlerThread.start();
                        handler = new Handler(handlerThread.getLooper()) {
                            @Override
                            public void handleMessage(Message msg) {
                                if (msg.what == 1) {
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                        }
                                    });

                                }
                            }
                        };

                        adapter = new SuggestSearchAdapter(suggests, recycler, SearchLiveFragment.this);

                        recycler.setAdapter(adapter);

                        content.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            }

                            @Override
                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            }

                            @Override
                            public void afterTextChanged(Editable editable) {
                                try {
                                    if (content.getText().toString().equals("")) {
                                        recycler.setVisibility(View.GONE);
                                        llPopular.setVisibility(View.VISIBLE);
                                        getPopularProduct();
                                        CmmFunc.showKeyboard(getActivity());
                                    } else {
                                        recycler.setVisibility(View.VISIBLE);
                                        llPopular.setVisibility(View.GONE);
                                        if (handler != null) {
                                            handler.removeCallbacksAndMessages(null);
                                        }
                                        final String keyword = content.getText().toString().trim();

                                        if (TextUtils.isEmpty(keyword)) {
                                            suggests.clear();
                                            adapter.notifyDataSetChanged();
                                            return;
                                        }
                                        // Now add a new one
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                getActivity().runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        try {
                                                            searchSuggest(keyword);
                                                            handler.sendEmptyMessage(1);
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                });
                                            }
                                        }, 500);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                        content.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                            @Override
                            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                                boolean handle = false;
                                if (i == EditorInfo.IME_ACTION_SEARCH) {
                                    if (content.getText().toString().equals("")) {
                                        content.setError(getString(R.string.enter_keyword));
                                        content.requestFocus();
                                    } else {
                                        FragmentHelper.add(HomeSearchFragment.newInstance(content.getText().toString()));
                                        //FragmentHelper.replace(HomeSearchFragment.newInstance(content.getText().toString()));
                                        handle = true;
                                    }
                                }
                                return handle;
                            }
                        });

                        frameSearch.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (content.getText().toString().equals("")) {
                                    content.setError(getString(R.string.enter_keyword));
                                    content.requestFocus();
                                } else {
                                    FragmentHelper.add(HomeSearchFragment.newInstance(content.getText().toString()));
                                    //FragmentHelper.replace(HomeSearchFragment.newInstance(content.getText().toString()));
                                }
                            }
                        });
                    }
                });
            }
        });
        threadInit.start();
        isLoaded = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            content.clearFocus();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (subPopular != null) {
            subPopular.unsubscribe();
        }

    }

    private void searchSuggest(String keyWord) {
        suggests.clear();
        adapter.notifyDataSetChanged();
        subSearch = APIService.getInstance().suggestSearch(keyWord)
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new ISubscriber() {
                    @Override
                    public void done() {
                        super.done();
//                        llPopular.setVisibility(View.GONE);
//                        recycler.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void excute(JSONObject jsonObject) {
                        try {
                            JSONArray data = jsonObject.getJSONArray("data");
                            Type listSuggest = new TypeToken<List<String>>() {
                            }.getType();
                            List items = new Gson().fromJson(data.toString(), listSuggest);
                            suggests.addAll(items);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.notifyDataSetChanged();
                                }
                            });

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void getPopularProduct() {
        showProgress();
        subPopular = APIService.getInstance().getHighlightProduct(page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ISubscriber() {
                    @Override
                    public void done() {
                        super.done();
                        hideProgress();
                        recycler.setVisibility(View.GONE);
                        llPopular.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void excute(JSONObject jsonObject) {
                        try {
                            JSONArray data = jsonObject.getJSONArray("data");
                            for (int i = 0; i < data.length(); i++) {
                                BeanProduct beanProduct = new Gson().fromJson(data.getString(i), BeanProduct.class);
                                for (int id : StoreCart.getProducts()) {
                                    if (beanProduct.getId() == id) {
                                        beanProduct.setInCart(true);
                                    }
                                }
                                map.put(beanProduct.getId(), beanProduct);
                            }
                            if (data.length() < 10) {
                                recyclerPopular.removeOnScrollListener(onScrollListener);
                            }
                            productAdapter.notifyDataSetChanged();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    @Override
    public void onRefresh() {
        try {
            if (!subPopular.isUnsubscribed()) {
                subPopular.unsubscribe();
            }
            recyclerPopular.removeOnScrollListener(onScrollListener);
            recyclerPopular.addOnScrollListener(onScrollListener);
            map.clear();
            page = 1;
            productAdapter.notifyDataSetChanged();
            refresh.setRefreshing(false);
            getPopularProduct();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
