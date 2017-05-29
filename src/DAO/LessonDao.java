package DAO;

import Entity.Lesson;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;

/**
 * Created by Jaho on 2017/5/29.
 */
public class LessonDao {

    private Session session = null;

    public LessonDao(Session session){
        this.session = session;
    }


    // examine the lesson if has exist
    private int hasExist(Lesson lesson) {

        String hql = "from Lesson where code = :code and name = :name and semester = :semester and planId = :planId";
        Query query = session.createQuery(hql);
        query.setParameter("code",lesson.getCode());
        query.setParameter("name",lesson.getName());
        query.setParameter("semester",lesson.getSemester());
        query.setParameter("planId",lesson.getPlanId());
        Lesson queryResult = (Lesson) query.uniqueResult();
        if (queryResult != null) {
            return queryResult.getLsnId();
        }else {
            return 0;
        }
    }

    // insert single LessonTableEntity to Database
    public int insert(Lesson lesson) {

        int lessonId = this.hasExist(lesson);
        if(lessonId == 0){
            session.save(lesson);
            lessonId = this.hasExist(lesson);
            return lessonId;
        }else {
            return lessonId;
        }
    }

    // 一次插入多条课程信息
    public void insertLessons(List<Lesson> lessons) {
        for (Lesson lesson: lessons) {
            this.insert(lesson);
        }
    }

    //更新课程信息
    public void update(int lessonId,Lesson lesson) {
        lesson.setLsnId(lessonId);
        session.update(lesson);
    }

    // 通过ID查询课程
    public Lesson getLessonById(int lessonId) {
        return session.get(Lesson.class,lessonId);
    }

    // 查询某一教学计划下所有课程
    /*
     * @param planId: 教学计划ID
     * @return 查询结果
     */
    public List<Lesson> getLessonsByPlanId(int planId) {

        String hql = "from Lesson where planId = :planId";
        Query query = session.createQuery(hql);
        query.setParameter("planId",planId);

        return query.list();
    }

    /*
     * @param code:课程代码
     * @param planId: 教学计划ID
     * @return 存在：返回课程ID  不存在：-1
     */
    public int getLessonId(String code,int planId) {

        String hql = "from Lesson where code = :code and planId = :planId";
        Query query = session.createQuery(hql);
        query.setParameter("code",code);
        query.setParameter("planId",planId);
        Lesson queryResult = (Lesson) query.uniqueResult();
        if(queryResult != null) {
            return queryResult.getLsnId();
        }else {
            return -1;
        }
    }
}
