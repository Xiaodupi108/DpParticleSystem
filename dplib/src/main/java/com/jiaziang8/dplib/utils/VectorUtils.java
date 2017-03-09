package com.jiaziang8.dplib.utils;

/**
 * Created by jiaziang on 15/11/27.
 */
public class VectorUtils {

    public static Vector vectorSubtract(Vector left, Vector right){
        return new Vector(left.getX()-right.getX(), left.getY()-right.getY());
    }

    public static Vector vectorAdd(Vector left, Vector right){
        return new Vector(left.getX()+right.getX(), left.getY()+right.getY());
    }

    public static Vector vectorMultiplyScale(Vector first, float value){
        return new Vector(first.getX()*value, first.getY()*value);
    }

    public static Vector vectorNormalize(Vector first){
        float tmp = (float) Math.sqrt(Math.pow(first.getX(),2) + Math.pow(first.getY(), 2));
        return new Vector(first.getX()/tmp, first.getY()/tmp);
    }

}
