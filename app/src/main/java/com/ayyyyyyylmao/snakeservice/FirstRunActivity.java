package com.ayyyyyyylmao.snakeservice;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by marko on 11/25/2020.
 */

public class FirstRunActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.first_run_dialog);

        TextView textView = (TextView)findViewById(R.id.text);
        PixelButton button = (PixelButton)findViewById(R.id.buttonOk);
        textView.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/pixelFJ8pt1__.ttf"));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setFinishOnTouchOutside(false);
    }

    @Override
    public void onBackPressed(){

    }
}
