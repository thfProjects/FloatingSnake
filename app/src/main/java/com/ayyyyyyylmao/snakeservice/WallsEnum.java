package com.ayyyyyyylmao.snakeservice;

/**
 * Created by marko on 3/16/2017.
 */

public enum WallsEnum {
    NONE(R.string.no_walls_title, R.drawable.walls_no),
    CLASSIC(R.string.classic_walls_title, R.drawable.walls_classic),
    SEPARATOR(R.string.separator_walls_title, R.drawable.walls_separator),
    FOUR_SQUARES(R.string.four_squares_walls_title, R.drawable.walls_four_squares),
    CROSS(R.string.cross_walls_title, R.drawable.walls_cross),
    RECTANGLE(R.string.rectangle_walls_title, R.drawable.walls_rectangle);

    private int mTitleResId;
    private int mDrawableResId;

    WallsEnum(int titleResId, int drawableResId) {
        mTitleResId = titleResId;
        mDrawableResId = drawableResId;
    }

    public int getTitleResId() {
        return mTitleResId;
    }

    public int getDrawbleResId() {
        return mDrawableResId;
    }
}
