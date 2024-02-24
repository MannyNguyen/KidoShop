package vn.kido.shop.IControl;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.CheckBox;

import vn.kido.shop.R;

public class ICheckbox extends CheckBox {
    public ICheckbox(Context context) {
        super(context);
        applyCustomFont(context);
    }

    public ICheckbox(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyCustomFont(context, attrs);
    }

    public ICheckbox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        applyCustomFont(context, attrs);
    }

    private void applyCustomFont(Context context, AttributeSet attributes) {
        TypedArray a = context.obtainStyledAttributes(attributes,
                R.styleable.CustomFontTextView);
        int cf = a.getInteger(R.styleable.CustomFontTextView_fontName, 0);
        int fontName = 0;

        switch (cf) {
            case 1:
                fontName = R.string.BRYANTLG_NORMAL;
                break;
            case 2:
                fontName = R.string.BRYANTLG_BOLD;
                break;

            default:
                fontName=R.string.BRYANTLG_NORMAL;
                break;
        }

        String customFont = getResources().getString(fontName);
        Typeface tf = Typeface.createFromAsset(context.getAssets(), customFont + ".TTF");
        setTypeface(tf);
        a.recycle();
    }

    private void applyCustomFont(Context context) {
        Typeface customFont = Typeface.createFromAsset(context.getAssets(), "BRYANTLG_NORMAL.TTF");
        setTypeface(customFont);
    }
}
