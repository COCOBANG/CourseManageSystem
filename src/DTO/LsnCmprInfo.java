package DTO;

import Entity.Lesson;

import java.util.List;
import java.util.ArrayList;

/**
 * Created by Jaho on 2017/3/10.
 */
public class LsnCmprInfo {

    private LessonInfo newLesson;
    private LessonInfo oldLesson;
    private List<String> compareRecord;

    public LessonInfo getNewLesson() {
        return newLesson;
    }

    public void setNewLesson(LessonInfo newLesson) {
        this.newLesson = newLesson;
    }

    public LessonInfo getOldLesson() {
        return oldLesson;
    }

    public void setOldLesson(LessonInfo oldLesson) {
        this.oldLesson = oldLesson;
    }

    public List<String> getCompareRecord() {
        return compareRecord;
    }

    public void setCompareRecord(List<String> compareRecord) {
        this.compareRecord = compareRecord;
    }
}
