package vn.kido.shop.Adapter;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import vn.kido.shop.Class.GlobalClass;
import vn.kido.shop.Fragment.Dialog.ErrorDialogFragment;
import vn.kido.shop.Fragment.News.HomeNewsFragment;
import vn.kido.shop.Fragment.OAuth.LoginFragment;
import vn.kido.shop.Fragment.Option.OptionFragment;
import vn.kido.shop.Fragment.Order.Follow.FollowOrderFragment;
import vn.kido.shop.Fragment.Order.History.HistoryOrderFragment;
import vn.kido.shop.Fragment.Order.Change.HomeChangeFragment;
import vn.kido.shop.Fragment.Program.HomeProgramFragment;
import vn.kido.shop.Fragment.Support.SupportFragment;
import vn.kido.shop.Helper.AccountHelper;
import vn.kido.shop.Helper.FragmentHelper;
import vn.kido.shop.Helper.StorageHelper;
import vn.kido.shop.R;
import vn.kido.shop.Bean.BeanMenu;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuViewHolder> {
    View itemView;
    FragmentActivity activity;
    RecyclerView recyclerView;
    Map map;

    public MenuAdapter(FragmentActivity activity, RecyclerView recycler) {
        this.activity = activity;
        this.recyclerView = recycler;
        loadMenu();
    }

    @Override
    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_menu, parent, false);
        return new MenuViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuViewHolder holder, int position) {
        try {
            final BeanMenu item = (BeanMenu) map.get(position);
            holder.title.setText(item.getTitle());
            holder.thumb.setImageDrawable(activity.getResources().getDrawable(item.getIcon()));
            if (item.isShowNumber()) {
                holder.notice.setVisibility(View.VISIBLE);
            } else {
                holder.notice.setVisibility(View.GONE);
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final DrawerLayout drawerLayout = activity.findViewById(R.id.drawer_layout);
                    drawerLayout.closeDrawer(GravityCompat.START);
                    //todo cheat
                    if (!AccountHelper.isLogin()) {
                        ErrorDialogFragment errorDialogFragment = ErrorDialogFragment.newInstance(GlobalClass.getActivity().getString(R.string.require_login));
                        errorDialogFragment.setRunnable(new Runnable() {
                            @Override
                            public void run() {
                                FragmentHelper.add(LoginFragment.newInstance());
                            }
                        });
                        errorDialogFragment.show(GlobalClass.getActivity().getSupportFragmentManager(), errorDialogFragment.getClass().getName());
                        return;
                    }
                    switch (item.getId()) {
                        case BeanMenu.EVENT_PROGRAM:
                            FragmentHelper.add(R.id.home_content, HomeProgramFragment.newInstance());
                            break;
                        case BeanMenu.HISTORY_ORDER:
                            FragmentHelper.add(R.id.home_content, HistoryOrderFragment.newInstance());
                            break;
                        case BeanMenu.FOLLOW_ORDER:
                            FragmentHelper.add(R.id.home_content, FollowOrderFragment.newInstance());
                            break;
                        case BeanMenu.NEWS:
                            FragmentHelper.add(R.id.home_content, HomeNewsFragment.newInstance());
                            break;
                        case BeanMenu.FEEDBACK:
                            FragmentHelper.add(R.id.home_content, SupportFragment.newInstance());
                            break;
                        case BeanMenu.CHANGE_PAY:
                            FragmentHelper.add(R.id.home_content, HomeChangeFragment.newInstance());
                            break;
                        case BeanMenu.SETTING:
                            FragmentHelper.add(R.id.home_content, OptionFragment.newInstance());
                            break;
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return map.size();
    }


    private void loadMenu() {
        map = new HashMap();
        map.put(0, new BeanMenu(BeanMenu.EVENT_PROGRAM, activity.getResources().getString(R.string.event_program), R.drawable.chuong_trinh));
        map.put(1, new BeanMenu(BeanMenu.HISTORY_ORDER, activity.getResources().getString(R.string.history), R.drawable.lich_su_don_hang));
        map.put(2, new BeanMenu(BeanMenu.FOLLOW_ORDER, activity.getResources().getString(R.string.follow_order), R.drawable.theo_doi_don_hang));
        map.put(3, new BeanMenu(BeanMenu.NEWS, activity.getResources().getString(R.string.order_news), R.drawable.tin_tuc));
        map.put(4, new BeanMenu(BeanMenu.FEEDBACK, activity.getResources().getString(R.string.feedback_247), R.drawable.phan_hoi_cham_soc_menu));
        map.put(5, new BeanMenu(BeanMenu.CHANGE_PAY, activity.getResources().getString(R.string.change_pay_product), R.drawable.leftmenu_change_pay_product));
        map.put(6, new BeanMenu(BeanMenu.SETTING, activity.getResources().getString(R.string.setting), R.drawable.dieu_chinh));
    }


    public class MenuViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private ImageView thumb;
        private View notice;

        public MenuViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            thumb = itemView.findViewById(R.id.thumbnail);
            notice = itemView.findViewById(R.id.notice);
        }
    }
}
