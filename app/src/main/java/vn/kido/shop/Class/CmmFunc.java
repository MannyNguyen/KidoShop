package vn.kido.shop.Class;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;

import vn.kido.shop.R;

import static vn.kido.shop.Class.GlobalClass.getContext;

public class CmmFunc {
    public static void hideKeyboard(Activity activity) {
        try {
            if (activity == null) {
                return;
            }
            View v = activity.getCurrentFocus();
            if (v == null) {
                return;
            }
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getRootView().getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showKeyboard(Activity activity) {
        try {
            View v = GlobalClass.getActivity().getCurrentFocus();
            if (v != null) {
                InputMethodManager imm = (InputMethodManager) GlobalClass.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(v, 0);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
            }
            activity.getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int convertDpToPx(Context activity, int dp) {
        try {
            return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, activity.getResources().getDisplayMetrics()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dp;
    }

    public static String formatMoney(Object value, boolean isPrefix) {
        String str = value + "";
        try {
            for (int i = str.length() - 3; i > 0; i -= 3) {
                str = new StringBuilder(str).insert(i, ",").toString();
            }
//            if (isPrefix) {
//                str = str + " " + GlobalClass.getActivity().getString(R.string.vnd);
//            }
            return str;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String formatKPoint(Object value) {
        return "+ " + value + " Kpoint";
    }

    public static Bitmap resizeBitmap(Bitmap bitmap, int maxSize) {
        if (bitmap == null)
            return null;
        try {
            int actualWidth = bitmap.getWidth();
            int actualHeight = bitmap.getHeight();
            float rate = 1;
            if (actualHeight > maxSize || actualWidth > maxSize) {
                if (actualWidth > actualHeight) {
                    rate = (float) maxSize / actualWidth;
                } else {
                    rate = (float) maxSize / actualHeight;
                }
                bitmap = Bitmap.createScaledBitmap(bitmap, Math.round(bitmap.getWidth() * rate), Math.round(bitmap.getHeight() * rate), false);
                return bitmap;
            } else {
                return bitmap;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void showAnim(View icCart) {
        try {
            final Animation animShake = AnimationUtils.loadAnimation(getContext(), R.anim.anim_shake);
            icCart.startAnimation(animShake);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
