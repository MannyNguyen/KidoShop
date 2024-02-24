package vn.kido.shop.IControl;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.EditText;

import vn.kido.shop.Class.CmmFunc;
import vn.kido.shop.R;

public class IEdittext extends EditText {
    public IEdittext(Context context) {
        super(context);
        applyCustomFont(context);
    }

    public IEdittext(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyCustomFont(context, attrs);
    }

    public IEdittext(Context context, AttributeSet attrs, int defStyleAttr) {
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
                fontName = R.string.BRYANTLG_NORMAL;
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

    @Override
    public void setError(CharSequence error, Drawable icon) {
        setCompoundDrawables(null, null, icon, null);
    }

    @Override
    public void setError(CharSequence error) {
        super.setError(error);
        Drawable dr = getResources().getDrawable(R.drawable.ic_error);
        //add an error icon to yur drawable files
        int size = CmmFunc.convertDpToPx(getContext(), 24);
        dr.setBounds(0, 0, size, size);
        setCompoundDrawables(null, null, dr, null);
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        setCompoundDrawables(null, null, null, null);
    }
}
