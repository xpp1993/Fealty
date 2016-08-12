package com.lxkj.administrator.fealty.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/8/12.
 */
public class SportData implements Serializable {
    private int steps;
    private int calories;
    private float distance;

    public SportData(int steps, int calories, float distance) {
        this.steps = steps;
        this.calories = calories;
        this.distance = distance;
    }

    public SportData() {
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    @Override
    public String toString() {
        return "SportData{" +
                "steps=" + steps +
                ", calories=" + calories +
                ", distance=" + distance +
                '}';
    }
}
