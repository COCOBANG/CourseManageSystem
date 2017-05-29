package DTO;

import Entity.Lesson;
import java.util.List;
import java.util.ArrayList;

/**
 * Created by Jaho on 2017/3/9.
 * 根据教学计划查看所有专业
 */
public class PlanMajorInfo {

    private String majorName;
    private List<Lesson> lessons;

    public PlanMajorInfo() {
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
