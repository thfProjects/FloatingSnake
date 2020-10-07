package com.ayyyyyyylmao.snakeservice;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        if(sharedPreferences.getBoolean("firstrun", true)){

            LayoutInflater inflater = getLayoutInflater();
            View adview = inflater.inflate(R.layout.alert_dialog, null);

            final AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setView(adview);
            TextView textView = (TextView)adview.findViewById(R.id.text);
            PixelButton button = (PixelButton)adview.findViewById(R.id.buttonOk);
            textView.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/pixelFJ8pt1__.ttf"));
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                    startService(new Intent(MainActivity.this, SnakeService.class));
                    finish();
                }
            });
            alertDialog.show();

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("firstrun", false);
            editor.apply();
        }else {
            startService(new Intent(MainActivity.this, SnakeService.class));
            finish();
        }
    }
}
