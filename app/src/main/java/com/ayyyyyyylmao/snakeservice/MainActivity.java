package com.ayyyyyyylmao.snakeservice;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    LayoutInflater inflater;
    SharedPreferences sharedPreferences;

    int MANAGE_OVERLAY_PERMISSION_CODE = 5578;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        inflater = getLayoutInflater();

        if(sharedPreferences.getBoolean("firstrun", true)){
            Intent intent = new Intent(MainActivity.this, FirstRunActivity.class);
            startActivity(intent);

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("firstrun", false);
            editor.apply();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            View adview = inflater.inflate(R.layout.overlay_permission_dialog, null);

            final AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setView(adview);
            TextView textView = (TextView)adview.findViewById(R.id.text);
            PixelButton buttonOk = (PixelButton)adview.findViewById(R.id.buttonOk);
            PixelButton buttonCancel = (PixelButton)adview.findViewById(R.id.buttonCancel);
            textView.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/pixelFJ8pt1__.ttf"));
            alertDialog.setCancelable(false);
            alertDialog.setCanceledOnTouchOutside(false);
            buttonOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                    startActivityForResult(intent, MANAGE_OVERLAY_PERMISSION_CODE);
                }
            });
            buttonCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                    finish();
                }
            });
            alertDialog.show();
        }else {
            startService(new Intent(MainActivity.this, SnakeService.class));
            finish();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && requestCode == MANAGE_OVERLAY_PERMISSION_CODE)  {
            if(Settings.canDrawOverlays(this)){
                startService(new Intent(MainActivity.this, SnakeService.class));
                finish();
            }
        }
    }
}
