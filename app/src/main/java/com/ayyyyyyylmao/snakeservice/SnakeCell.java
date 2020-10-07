package com.ayyyyyyylmao.snakeservice;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import java.util.Random;

/**
 * Created by marko on 3/10/2017.
 */

public class SnakeCell extends ImageView {

    public static final int STATE_NONE = 0;
    public static final int STATE_SNAKE = 1;
    public static final int STATE_POINT = 2;
    public static final int STATE_FOOD = 3;
    public static final int STATE_MUSHROOM = 4;
    public static final int STATE_WALL = 5;

    private int state;

    private int X;

    private int Y;

    private Snake parentSnake;

    private int[] foodDrawables = {R.drawable.food_drumstick, R.drawable.food_apple, R.drawable.food_beetle};

    public SnakeCell(Context context){
        super(context);

        init();

        setState(STATE_NONE);
    }

    public SnakeCell(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.SnakeCell,
                0, 0);

        try {
            setState(a.getInteger(R.styleable.SnakeCell_state, STATE_NONE));
        } finally {
            a.recycle();
        }
    }

    private void init(){
        parentSnake = null;
    }

    public void setState(int newState){
        state = newState;
        drawCell();
    }

    public int getState(){
        return state;
    }

    public void setParentSnake(@Nullable Snake snake){
        parentSnake = snake;
        if(parentSnake != null){
            setState(STATE_SNAKE);
        }else {
            setState(STATE_NONE);
        }
    }

    public void drawCell(){
        setImageDrawable(null);
        switch (state){
            case STATE_NONE:
                setBackgroundColor(Color.TRANSPARENT);
                break;
            case STATE_SNAKE:
                setBackgroundColor(parentSnake.getColor());
                if(parentSnake.getIndexOf(this) == 0){
                    switch (parentSnake.getDirection()){
                        case Snake.UP:
                            setImageResource(R.drawable.snake_head_up);
                            break;
                        case Snake.RIGHT:
                            setImageResource(R.drawable.snake_head_right);
                            break;
                        case Snake.DOWN:
                            setImageResource(R.drawable.snake_head_down);
                            break;
                        case Snake.LEFT:
                            setImageResource(R.drawable.snake_head_left);
                            break;
                    }
                }
                break;
            case STATE_POINT:
                setBackgroundResource(R.drawable.point);
                break;
            case STATE_FOOD:
                setBackgroundResource(foodDrawables[new Random().nextInt(foodDrawables.length)]);
                break;
            case STATE_MUSHROOM:
                setBackgroundResource(R.drawable.mushroom);
                break;
            case STATE_WALL:
                setBackgroundResource(R.drawable.wall);
                break;
        }
    }

    public void setCellX(int x){
        X = x;
    }

    public int getCellX(){
        return X;
    }

    public void setCellY(int y){
        Y = y;
    }

    public int getCellY(){
        return Y;
    }
}
