package com.ayyyyyyylmao.snakeservice;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.view.PagerTitleStrip;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by 4530 on 31/10/2017.
 */

public class CustomFontPagerTitleStrip extends PagerTitleStrip {
    public CustomFontPagerTitleStrip(Context context) {
        super(context);
    }
    public CustomFontPagerTitleStrip(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/pixelFJ8pt1__.ttf");
        for (int i=0; i<this.getChildCount(); i++) {
            if (this.getChildAt(i) instanceof TextView) {
                ((TextView)this.getChildAt(i)).setTypeface(tf);
            }
        }
    }
}
