package DAO;

import Entity.LessonCode;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;

/**
 * Created by Jaho on 2017/5/29.
 */
public class LsnCodeDao {

    private Session session = null;

    public LsnCodeDao(Session session){
        this.session = session;
    }

    public void insert(String code){
        LessonCode lessonCode = new LessonCode();
        lessonCode.setCode(code);
        session.save(lessonCode);
        return;
    }

    public void storeCodes(List<String> lsnCodes){
        for(String lessonCode:lsnCodes)
            this.insert(lessonCode);
    }

    public List<String> getCodes(){
        String hql = "select distinct code from LessonCode";
        Query query = session.createQuery(hql);
        return query.list();
    }


}
