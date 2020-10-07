package com.ayyyyyyylmao.snakeservice;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.Gravity;

/**
 * Created by 4530 on 31/10/2017.
 */

public class PixelButton extends android.support.v7.widget.AppCompatButton {
    public PixelButton(Context context) {
        super(context);
        init();
    }

    public PixelButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PixelButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        Typeface pixelfont = Typeface.createFromAsset(getContext().getAssets(), "fonts/pixelFJ8pt1__.ttf");
        setTypeface(pixelfont);
        setGravity(Gravity.CENTER);
        setIncludeFontPadding(false);
    }
}
