package com.jiaziang8.dplib.utils;

/**
 * Created by jiaziang on 15/11/27.
 */
public class Vector {
    private float x;
    private float y;

    public Vector(float x ,float y){
        this.x = x;
        this.y = y;
    }

    public void setData(float x ,float y){
        this.x = x;
        this.y = y;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return "Vector{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

}
