package com.ayyyyyyylmao.snakeservice;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by marko on 3/13/2017.
 */

public class HighScoresActivity extends AppCompatActivity {

    WrapContentViewPager hsViewPager;
    HighScoresPagerAdapter hsAdapter;

    int level;

    Button bClear;

    SharedPreferences sharedPreferences;

    ViewPager.SimpleOnPageChangeListener onPageChangeListener = new ViewPager.SimpleOnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            level = position;
        }
    };

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            for(int i = 0;i<10;i++){
                editor.remove(level + "player" + (i+1));
                editor.remove(level + "score" + (i+1));
            }
            editor.apply();
            hsViewPager.setAdapter(hsAdapter);
            hsViewPager.setCurrentItem(level);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.high_scores);

        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        hsAdapter = new HighScoresPagerAdapter(this);

        hsViewPager = (WrapContentViewPager)findViewById(R.id.hsviewpager);
        hsViewPager.setAdapter(hsAdapter);
        hsViewPager.addOnPageChangeListener(onPageChangeListener);

        level = hsViewPager.getCurrentItem();

        bClear = (Button)findViewById(R.id.clear);
        bClear.setOnClickListener(onClickListener);
    }
}
