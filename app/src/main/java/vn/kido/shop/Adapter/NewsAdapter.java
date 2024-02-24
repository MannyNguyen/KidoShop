package vn.kido.shop.Adapter;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;
import org.json.JSONObject;

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import vn.kido.shop.Api.APIService;
import vn.kido.shop.Api.ISubscriber;
import vn.kido.shop.Class.CmmFunc;
import vn.kido.shop.Fragment.News.HomeNewsFragment;
import vn.kido.shop.R;
import vn.kido.shop.Bean.BeanNews;
import vn.kido.shop.Fragment.News.DetailNewsFragment;
import vn.kido.shop.Helper.FragmentHelper;

import static vn.kido.shop.Class.GlobalClass.getContext;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.MyViewHolder> {
    View itemView;
    Fragment fragment;
    RecyclerView recyclerView;
    List<BeanNews> map;
    int type, size;

    public NewsAdapter(Fragment fragment, RecyclerView recyclerView, List<BeanNews> map, int type) {
        this.fragment = fragment;
        this.recyclerView = recyclerView;
        this.map = map;
        this.type = type;
        size = CmmFunc.convertDpToPx(getContext(), 240);
    }


    @NonNull
    @Override
    public NewsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_news, parent, false);
        return new NewsAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final NewsAdapter.MyViewHolder holder, int position) {
        try {
            final BeanNews beanNews = map.get(position);
            if (beanNews != null) {
                if (!TextUtils.isEmpty(beanNews.getImage())) {
                    Picasso.get().load(beanNews.getImage()).resize(size, size).centerInside().into(holder.imv_news);
                }

                if (beanNews.getIsSaved() == 1) {
                    holder.imv_save_news.setVisibility(View.GONE);
                    holder.txtSaved.setVisibility(View.VISIBLE);
                } else {
                    holder.imv_save_news.setVisibility(View.VISIBLE);
                    holder.txtSaved.setVisibility(View.GONE);
                }

                if (type == 3) {
                    holder.imv_delete_news.setVisibility(View.VISIBLE);
                    holder.imv_save_news.setVisibility(View.GONE);
                    holder.txtSaved.setVisibility(View.GONE);
                }

                holder.txtTitleNews.setText(beanNews.getTitle());
                holder.txtCreateDate.setText(new DateTime(beanNews.getCreate_date()).toString("dd/MM/yyyy"));
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FragmentHelper.add(DetailNewsFragment.newInstance(beanNews.getId()));
                    }
                });
                holder.imv_delete_news.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deleteNews(beanNews.getId(), beanNews);
                    }
                });
                holder.imv_save_news.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        saveNews(beanNews.getId(), beanNews);
                    }
                });
                holder.txtSaved.setOnClickListener(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return map.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imv_news, imv_delete_news, imv_save_news;
        TextView txtTitleNews, txtCreateDate, txtSaved;

        public MyViewHolder(View itemView) {
            super(itemView);
            imv_news = itemView.findViewById(R.id.img_news);
            txtCreateDate = itemView.findViewById(R.id.txv_create_day);
            txtTitleNews = itemView.findViewById(R.id.txv_title_news);
            imv_delete_news = itemView.findViewById(R.id.imv_delete_news);
            imv_save_news = itemView.findViewById(R.id.imv_save_news);
            txtSaved = itemView.findViewById(R.id.txt_saved);
        }
    }

    public void deleteNews(int iddeletesavenews, final BeanNews beanNews) {
        APIService.getInstance().deletesavenews(iddeletesavenews)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ISubscriber() {
                    @Override
                    public void excute(JSONObject jsonObject) {
                        try {
                            if (jsonObject == null) {
                                return;
                            }
                            map.remove(beanNews);
                            NewsAdapter.this.notifyDataSetChanged();
                            ((HomeNewsFragment) fragment.getParentFragment()).listId.remove((Integer) beanNews.getId());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });
    }

    public void saveNews(int idsavenews, final BeanNews beanNews) {
        APIService.getInstance().savenews(idsavenews)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

                .subscribe(new ISubscriber() {
                    @Override
                    public void excute(JSONObject jsonObject) {
                        try {
                            beanNews.setIsSaved(1);
                            NewsAdapter.this.notifyDataSetChanged();
                            ((HomeNewsFragment) fragment.getParentFragment()).listId.add(beanNews.getId());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });
    }
}