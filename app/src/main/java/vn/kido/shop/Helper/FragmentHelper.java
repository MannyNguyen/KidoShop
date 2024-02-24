package vn.kido.shop.Helper;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import vn.kido.shop.Class.GlobalClass;
import vn.kido.shop.Fragment.BaseFragment;
import vn.kido.shop.R;

public class FragmentHelper {

    public static Fragment replace(Fragment fragment) {
        if (fragment == null) {
            return null;
        }
        String tag = fragment.getClass().getName();
        FragmentManager manager = GlobalClass.getActivity().getSupportFragmentManager();
        manager.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                //.setCustomAnimations(R.anim.enter_from_right, 0, 0, R.anim.exit_to_right)
                .replace(R.id.home_content, fragment, tag)
                .addToBackStack(tag)
                .commitAllowingStateLoss();
        return fragment;
    }

    public static Fragment add(Fragment fragment) {
        if (fragment == null) {
            return null;
        }
        String tag = fragment.getClass().getName();
        FragmentManager manager = GlobalClass.getActivity().getSupportFragmentManager();
        manager.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                //.setCustomAnimations(R.anim.enter_from_right, 0, 0, R.anim.exit_to_right)
                .add(R.id.home_content, fragment, tag)
                .addToBackStack(tag)
                .commitAllowingStateLoss();
        return fragment;
    }

    public static Fragment add(FragmentActivity activity, Fragment fragment) {
        if (fragment == null) {
            return null;
        }
        String tag = fragment.getClass().getName();
        FragmentManager manager = activity.getSupportFragmentManager();
        manager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .add(R.id.home_content, fragment, tag)
                .addToBackStack(tag)
                .commitAllowingStateLoss();
        return fragment;
    }

    public static Fragment add(FragmentActivity activity, int idContainer, Fragment fragment) {
        if (fragment == null) {
            return null;
        }
        String tag = fragment.getClass().getName();
        FragmentManager manager = activity.getSupportFragmentManager();
        manager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .add(idContainer, fragment, tag)
                .addToBackStack(tag)
                .commitAllowingStateLoss();
        return fragment;
    }

    public static Fragment add(int idContainer, Fragment fragment) {
        if (fragment == null) {
            return null;
        }
        String tag = fragment.getClass().getName();
        FragmentManager manager = GlobalClass.getActivity().getSupportFragmentManager();
        manager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .add(idContainer, fragment, tag)
                .addToBackStack(tag)
                .commitAllowingStateLoss();
        return fragment;
    }

    public static void pop(final FragmentActivity activity) {
        try {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        final FragmentManager manager = activity.getSupportFragmentManager();
                        manager.popBackStack();
                        manager.executePendingTransactions();
                        BaseFragment f = (BaseFragment) manager.findFragmentById(R.id.home_content);
                        if (f != null) {
                            f.manuResume();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void pop(FragmentActivity activity, Class<?> clazz) {
        try {
            FragmentManager manager = activity.getSupportFragmentManager();
            manager.popBackStackImmediate(clazz.getName(), 0);
            BaseFragment f = (BaseFragment) manager.findFragmentById(R.id.home_content);
            if (f != null) {
                f.manuResume();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public static Fragment findFragmentByTag(Class<?> clazz) {
        return GlobalClass.getActivity().getSupportFragmentManager().findFragmentByTag(clazz.getName());
    }


    public static Fragment getActiveFragment(FragmentActivity activity) {
        try {
            activity.getSupportFragmentManager().executePendingTransactions();
            if (activity.getSupportFragmentManager().getBackStackEntryCount() == 0) {
                return null;
            }
            String tag = activity.getSupportFragmentManager().getBackStackEntryAt(activity.getSupportFragmentManager().getBackStackEntryCount() - 1).getName();
            return activity.getSupportFragmentManager().findFragmentByTag(tag);
        } catch (Exception e) {
            return null;
        }
    }
}
