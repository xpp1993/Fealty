package dexin.love.band.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/8/12.
 */
public class SportData implements Serializable {
    private String steps;
    private String calories;
    private String distance;
    private String Parentphone;//用电话号码给运动数据做标识


    public SportData() {
    }

    public SportData(String steps, String calories, String distance) {
        this.steps = steps;
        this.calories = calories;
        this.distance = distance;
    }

    public SportData(String steps, String calories, String parentphone, String distance) {
        this.steps = steps;
        this.calories = calories;
        Parentphone = parentphone;
        this.distance = distance;
    }

    public String getCalories() {
        return calories;
    }

    public void setCalories(String calories) {
        this.calories = calories;
    }

    public String getSteps() {
        return steps;
    }

    public void setSteps(String steps) {
        this.steps = steps;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getParentphone() {
        return Parentphone;
    }

    public void setParentphone(String parentphone) {
        Parentphone = parentphone;
    }

    @Override
    public String toString() {
        return "SportData{" +
                "steps='" + steps + '\'' +
                ", calories='" + calories + '\'' +
                ", distance='" + distance + '\'' +
                ", Parentphone='" + Parentphone + '\'' +
                '}';
    }
}
