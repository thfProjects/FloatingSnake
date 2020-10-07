package com.ayyyyyyylmao.snakeservice;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by 4530 on 24/10/2017.
 */

public class HighScoresPagerAdapter extends PagerAdapter {

    private Context mContext;
    SharedPreferences sharedPreferences;

    public HighScoresPagerAdapter(Context context) {
        mContext = context;

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.highscorelayout, collection, false);
        for(int i = 0;i<10;i++){
            ((TextView)layout.getChildAt(i)).setText((i+1) + ". " + sharedPreferences .getString(position + "player" + (i+1),"player" + (i+1)) + ": " + sharedPreferences .getInt(position + "score" + (i+1),0));
            ((TextView)layout.getChildAt(i)).setTypeface(Typeface.createFromAsset(mContext.getAssets(), "fonts/pixelFJ8pt1__.ttf"));
        }
        collection.addView(layout);
        return layout;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }

    @Override
    public int getCount() {
        return WallsEnum.values().length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        WallsEnum customPagerEnum = WallsEnum.values()[position];
        return mContext.getString(customPagerEnum.getTitleResId());
    }
}
