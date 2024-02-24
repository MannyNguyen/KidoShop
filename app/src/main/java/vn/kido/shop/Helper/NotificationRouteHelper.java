package vn.kido.shop.Helper;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import vn.kido.shop.Fragment.Order.DetailOrderFragment;
import vn.kido.shop.Fragment.Program.DetailProgramFragment;
import vn.kido.shop.Fragment.Support.SupportFragment;

public class NotificationRouteHelper {
    public static final String HOME_SCREEN = "";
    public static final String ORDER = "Order";
    public static final String EVENT = "Event";
    public static final String CHAT = "Chat";

    Context context;

    public NotificationRouteHelper() {
    }

    public void route(FragmentActivity activity, Bundle bundle) {
        try {
            String type = bundle.getString("deep_link_type");
            switch (type) {
                case HOME_SCREEN:
                    break;
                case EVENT:
                    int id = Integer.parseInt(bundle.getString("deep_link_id"));
                    FragmentHelper.add(DetailProgramFragment.newInstance(id));
                    break;
                case ORDER:
                    //int id = Integer.parseInt(bundle.getString("deep_link_id"));
                    FragmentHelper.add(DetailOrderFragment.newInstance(bundle.getString("deep_link_id")));
                    break;

                case CHAT:
                    //int id = Integer.parseInt(bundle.getString("deep_link_id"));
                    FragmentHelper.add(SupportFragment.newInstance());
                    break;
            }
            activity.getIntent().removeExtra("deep_link_type");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
