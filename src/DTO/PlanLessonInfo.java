package DTO;

import Entity.Lesson;
import java.util.List;
import java.util.ArrayList;

/**
 * Created by Jaho on 2017/3/9.
 * 根据教学计划、课程类型查看所有课程
 */
public class PlanLessonInfo {

    private String type;
    private List<Lesson> lessons;

    public PlanLessonInfo() {
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
