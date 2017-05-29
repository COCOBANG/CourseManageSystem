package Dao;

import Entity.LessonCode;
import org.hibernate.Session;

/**
 * Created by Jaho on 2017/5/29.
 */
public class LessonCodeDao {

    private Session session = null;

    public LessonCodeDao(Session session){
        this.session = session;
    }

    public void insert(String code){
        LessonCode lessonCode = new LessonCode();
        lessonCode.setCode(code);
        session.save(lessonCode);
        return;
    }
}
