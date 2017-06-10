package DTO.Plan;

import Entity.Lesson;

import java.util.List;
import java.util.ArrayList;

/**
 * Created by Jaho on 2017/3/9.
 * 查看教学计划内按课程类型划分的所有课程
 */
public class PlnLsnInfo {

    private String type;
    private List<Lesson> lessons;

    public PlnLsnInfo() {
        this.lessons = new ArrayList<Lesson>();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Lesson> getLessons() {
        return lessons;
    }

    public void setLessons(List<Lesson> lessons) {
        this.lessons = lessons;
    }
}
