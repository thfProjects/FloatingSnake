package com.ayyyyyyylmao.snakeservice;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by marko on 3/16/2017.
 */

public class WallsPagerAdapter extends PagerAdapter {
    private Context mContext;

    public WallsPagerAdapter(Context context) {
        mContext = context;
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
        WallsEnum customPagerEnum = WallsEnum.values()[position];
        LayoutInflater inflater = LayoutInflater.from(mContext);
        ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.level_chooser_image, collection, false);
        ((ImageView)layout.findViewById(R.id.image)).setImageResource(customPagerEnum.getDrawbleResId());
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
