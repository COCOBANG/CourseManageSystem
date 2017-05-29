package Dao;

import Entity.MajorLesson;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Vector;

/**
 * Created by Jaho on 2017/5/29.
 */
public class MjrLsnDao {

    private Session session = null;
    public MjrLsnDao (Session session){
        this.session = session;
    }

    // 检查此专业课程关系是否已经存在
    private boolean hasExist(MajorLesson mjrLsn) {
        String hql = "from MajorLesson where lsnId = :lsnId and mjrId = :mjrId";
        Query query = session.createQuery(hql);
        query.setParameter("lsnId",mjrLsn.getLsnId());
        query.setParameter("mjrId",mjrLsn.getMjrId());
        if (query.uniqueResult() != null) {
            return true;
        }else {
            return false;
        }
    }

    // 插入一条专业课程关系
    public void insert(MajorLesson mjrLsn) {
        if(hasExist(mjrLsn)){
            return;
        }else {
            session.save(mjrLsn);
        }
    }

    // 插入多条专业课程关系
    public void inserts(List<MajorLesson> mjrLsns) {
        for (MajorLesson mjrLsn: mjrLsns) {
            this.insert(mjrLsn);
        }
    }

    // query and return all major_Lessons
    // with plan_id
    public List<MajorLesson> getMajorLessonListByPlanId (int planId) {
        String hql = "from MajorLesson where planId = :planId";
        Query query = session.createQuery(hql);
        query.setParameter("planId",planId);
        return query.list();
    }

    // query and return all major_Lessons
    // with major_id
    public List<MajorLesson> getMajorLessonListByMajorId (int majorId) {
        String hql = "from MajorLesson where majorLessonMId = :majorLessonMId";
        Query query = session.createQuery(hql);
        query.setParameter("majorLessonMId",majorId);
        return query.list();
    }

    // query and return all major_Lessons
    // with lesson_id
    public List<MajorLesson> getMajorLessonListByLessonId (int lessonId) {
        String hql = "from MajorLesson where majorLessonLId = :majorLessonLId";
        Query query = session.createQuery(hql);
        query.setParameter("majorLessonLId",lessonId);
        return query.list();
    }

    // query and return all major_Lessons
    // with lesson_type and plan_id
    public List<MajorLesson> getMajorLessonListByTypePlanId (String lessonType, int planId) {
        String hql = "from MajorLesson where majorLessonType = :lessonType and planId = :planId";
        Query query = session.createQuery(hql);
        query.setParameter("lessonType",lessonType);
        query.setParameter("planId",planId);
        return query.list();
    }
}
