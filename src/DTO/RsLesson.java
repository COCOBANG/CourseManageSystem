package DTO;

import Entity.Lesson;

import java.util.List;

/**
 * Created by Jaho on 2017/5/30.
 */
public class RsLesson {

    private List<String> majorNames;
    private Lesson lesson;

    public List<String> getMajorNames() {
        return majorNames;
    }

    public void setMajorNames(List<String> majorNames) {
        this.majorNames = majorNames;
    }

    public Lesson getLesson() {
        return lesson;
    }

    public void setLesson(Lesson lesson) {
        this.lesson = lesson;
    }
}
