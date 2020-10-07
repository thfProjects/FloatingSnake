package com.ayyyyyyylmao.snakeservice;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;

/**
 * Created by marko on 3/11/2017.
 */

public class Snake {

    public interface OnSnakeEventListener {

        void OnEvent(SnakeEvent se);
    }

    public static final int UP = 0;
    public static final int RIGHT = 1;
    public static final int DOWN = 2;
    public static final int LEFT = 3;

    private ArrayList<SnakeCell> cells;

    private int currentDirection;
    private int newDirection;

    private int posX;
    private int posY;

    private int color;

    private int numcolumns;
    private int numrows;

    private OnSnakeEventListener listener;

    public Snake(SnakeCell[][] matrix, int x, int y, int numcolumns, int numrows, int startLength, int color){
        cells = new ArrayList<>();

        this.color = color;

        for(int i = startLength-1;i>=0;i--){
            add(matrix[x][y+i]);
        }

        posX = x;
        posY = y;

        this.numcolumns = numcolumns;
        this.numrows = numrows;

        currentDirection = 0;
        newDirection = 0;

        draw();
    }

    private void add(SnakeCell cell){
        cells.add(0, cell);
        cell.setParentSnake(this);
    }

    private void remove(){
        cells.get(cells.size()-1).setParentSnake(null);
        cells.remove(cells.size()-1);
    }

    public void move(SnakeCell[][] matrix){
        currentDirection = newDirection;
        switch(currentDirection){
            case UP:
                if(posY-1 > -1){
                    handleMove(matrix[posX][posY-1]);
                    posY--;
                }else {
                    handleMove(matrix[posX][numrows-1]);
                    posY = numrows-1;
                }
                break;
            case RIGHT:
                if(posX+1 < numcolumns){
                    handleMove(matrix[posX+1][posY]);
                    posX++;
                }else {
                    handleMove(matrix[0][posY]);
                    posX = 0;
                }
                break;
            case DOWN:
                if(posY+1 < numrows){
                    handleMove(matrix[posX][posY+1]);
                    posY++;
                }else {
                    handleMove(matrix[posX][0]);
                    posY = 0;
                }
                break;
            case LEFT:
                if(posX-1 > -1){
                    handleMove(matrix[posX-1][posY]);
                    posX--;
                }else {
                    handleMove(matrix[numcolumns-1][posY]);
                    posX = numcolumns-1;
                }
                break;
        }

        draw();
    }

    private void handleMove(SnakeCell cell){
        if(cell.getState() == SnakeCell.STATE_NONE){
            add(cell);
            remove();
        }else if(cell.getState() == SnakeCell.STATE_SNAKE){
            gameOver();
        }else if(cell.getState() == SnakeCell.STATE_POINT){
            add(cell);
            if(listener != null){
                listener.OnEvent(new SnakeEvent(SnakeEvent.ACTION_EATEN_POINT));
            }
        }else if(cell.getState() == SnakeCell.STATE_FOOD){
            add(cell);
            remove();
            if(listener != null){
                listener.OnEvent(new SnakeEvent(SnakeEvent.ACTION_EATEN_FOOD));
            }
        }else if(cell.getState() == SnakeCell.STATE_MUSHROOM){
            add(cell);
            remove();
            if(listener != null){
                listener.OnEvent(new SnakeEvent(SnakeEvent.ACTION_EATEN_MUSHROOM));
            }
        }else if(cell.getState() == SnakeCell.STATE_WALL){
            gameOver();
        }
    }

    public void setOnSnakeEventListener(OnSnakeEventListener listener){
        this.listener = listener;
    }

    private void gameOver(){
        if(listener != null){
            listener.OnEvent(new SnakeEvent(SnakeEvent.ACTION_CRASHED));
        }
    }

    public void setDirection(int direction){
        newDirection = direction;
    }

    public int getDirection(){
        return currentDirection;
    }

    public void shorten(int amount){
        for(int i = 0;i < amount;i++){
            remove();
        }
    }

    public int getLength(){
        return cells.size();
    }

    public void setColor(int color){
        this.color = color;
    }

    public int getColor(){
        return color;
    }

    public int getIndexOf(SnakeCell cell){
        return cells.indexOf(cell);
    }

    private void draw(){
        for(int i = 0;i< cells.size(); i++){
            cells.get(i).drawCell();
        }
    }

}
