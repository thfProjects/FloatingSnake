package com.ayyyyyyylmao.snakeservice;

/**
 * Created by marko on 3/20/2017.
 */

public class SnakeEvent {

    public static final int ACTION_CRASHED = 1;

    public static final int ACTION_EATEN_POINT = 2;

    public static final int ACTION_EATEN_FOOD = 3;

    public static final int ACTION_EATEN_MUSHROOM = 4;

    private int action;

    public SnakeEvent(int action){
        this.action = action;
    }

    public int getAction(){
        return action;
    }
}
