package DTO;

import Entity.Lesson;
import java.util.List;
import java.util.ArrayList;

/**
 * Created by Jaho on 2017/3/10.
 */
public class LsnCmprInfo {

    private Lesson newLesson;
    private Lesson oldLesson;
    private List<String> compareRecord;

    public LsnCmprInfo() {
        this.compareRecord = new ArrayList<String>();
    }

    public Lesson getNewLesson() {
        return newLesson;
    }

    public void setNewLesson(Lesson newLesson) {
        this.newLesson = newLesson;
    }

    public Lesson getOldLesson() {
        return oldLesson;
    }

    public void setOldLesson(Lesson oldLesson) {
        this.oldLesson = oldLesson;
    }

    public List<String> getCompareRecord() {
        return compareRecord;
    }

}
