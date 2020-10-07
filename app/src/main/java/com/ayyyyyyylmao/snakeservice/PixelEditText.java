package com.ayyyyyyylmao.snakeservice;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.EditText;

/**
 * Created by 4530 on 12/11/2017.
 */

public class PixelEditText extends EditText {
    public PixelEditText(Context context) {
        super(context);

        init();
    }

    public PixelEditText(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public PixelEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    private void init(){
        Typeface pixelfont = Typeface.createFromAsset(getContext().getAssets(), "fonts/pixelFJ8pt1__.ttf");
        setTypeface(pixelfont);
        setIncludeFontPadding(false);
        setFocusable(true);
    }
}
