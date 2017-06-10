package DTO.Plan;

import Entity.Lesson;

import java.util.List;
import java.util.ArrayList;

/**
 * Created by Jaho on 2017/3/9.
 * 根据教学计划查看所有专业及课程
 */
public class PlnMjrInfo {

    private String majorName;
    private List<Lesson> lessons;

    public PlnMjrInfo() {
        this.lessons = new ArrayList<Lesson>();
    }

    public String getMajorName() {
        return majorName;
    }

    public void setMajorName(String majorName) {
        this.majorName = majorName;
    }

    public List<Lesson> getLessons() {
        return lessons;
    }

    public void setLessons(List<Lesson> lessons) {
        this.lessons = lessons;
    }
}
