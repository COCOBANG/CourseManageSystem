package DTO;

import Entity.Lesson;

import java.util.List;

/**
 * Created by Jaho on 2017/6/10.
 */
public class Arrange {

    private MajorSearch major;
    private String year;
    private List<LessonInfo> lessons;

    public MajorSearch getMajor() {
        return major;
    }

    public void setMajor(MajorSearch major) {
        this.major = major;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public List<LessonInfo> getLessons() {
        return lessons;
    }

    public void setLessons(List<LessonInfo> lessons) {
        this.lessons = lessons;
    }
}
