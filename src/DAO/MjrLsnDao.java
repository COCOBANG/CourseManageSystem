package DAO;

import java.util.List;

import Entity.MajorLesson;
import org.hibernate.Session;
import org.hibernate.query.Query;

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

    // 根据教学计划查询专业课程关系
    public List<MajorLesson> getMjrLsnsOfPlan (int planId) {

        String hql = "from MajorLesson where planId = :planId";
        Query query = session.createQuery(hql);
        query.setParameter("planId",planId);
        return query.list();

    }

    // 根据专业查询专业课程关系
    public List<MajorLesson> getMjrLsnsOfMajor (int majorId) {

        String hql = "from MajorLesson where mjrId = :mjrId";
        Query query = session.createQuery(hql);
        query.setParameter("mjrId",majorId);
        return query.list();
    }

    // 根据课程查询专业课程关系
    public List<MajorLesson> getMjrLsnsOfLesson (int lessonId) {
        String hql = "from MajorLesson where lsnId = :lsnId";
        Query query = session.createQuery(hql);
        query.setParameter("lsnId",lessonId);
        return query.list();
    }

    // 根据课程类型和教学计划查询专业课程关系
    public List<MajorLesson> getMjrLsnsInType (String lessonType, int planId) {
        String hql = "from MajorLesson where lsnType = :lsnType  and planId = :planId";
        Query query = session.createQuery(hql);
        query.setParameter("lsnType",lessonType);
        query.setParameter("planId",planId);
        return query.list();
    }
}
