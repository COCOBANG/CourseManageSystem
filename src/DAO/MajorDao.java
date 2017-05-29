package DAO;

import Entity.Plan;
import Entity.Major;
import java.util.List;
import DTO.MajorSearch;
import org.hibernate.Session;
import org.hibernate.query.Query;

/**
 * Created by Jaho on 2017/5/29.
 */
public class MajorDao {

    private Session session = null;

    public MajorDao(Session session){
        this.session = session;
    }

    // 检查该专业信息是否已经存在
    private int hasExist(Major major) {
        String hql = "from Major where name = :name and level = :level and planId = :planId";
        Query query = session.createQuery(hql);
        query.setParameter("name",major.getName());
        query.setParameter("level",major.getLevel());
        query.setParameter("planId",major.getPlanId());
        Major queryResult = (Major) query.uniqueResult();
        if (queryResult != null) {
            return queryResult.getMjrId();
        }else {
            return 0;
        }
    }

    // 插入专业信息
    public int insert(Major major) {

        int majorId = this.hasExist(major);
        if( majorId == 0){
            session.save(major);
            majorId = this.hasExist(major);
            return majorId;
        }else {
            return majorId;
        }

    }

    // 插入多条专业信息
    public void insertMajors (List<Major> majors) {
        for (Major major: majors) {
            this.insert(major);
        }
    }

    // 更新专业信息
    public void update(Major major) {
        session.update(major);
    }

    // query and return  all majors
    // with plan_id
    public List<Major> getMajorListByPlanId (int planId) {
        String hql = "from Major where planId = :planId";
        Query query = session.createQuery(hql);
        query.setParameter("planId",planId);

        return query.list();
    }


    /*
     * @param level:专业层次
     * @param year:教学年份
     * 根据专业层次和教学年份查询所有符合要求的专业
     */
    public List<Major> getMajors(String level , String year) {

        List <Plan> plans = new PlanDao(session).getPlansOfYear(year);

        int planId0 = plans.get(0).getPlnId();
        int planId1 = planId0;

        if(plans.size() > 1){
            planId1 = plans.get(1).getPlnId();
        }

        String hql = "from Major where level = :level and (planId = :planId0 or planId = :planId1)";
        Query query = session.createQuery(hql);
        query.setParameter("level",level);
        query.setParameter("planId0",planId0);
        query.setParameter("planId1",planId1);
        return query.list();


    }

    /*
     * @param year:教学年份
     * 根据教学年份查询所有符合要求的专业
     */
    public List<Major> getMajorsOfYear(String year) {
        List <Plan> plans = new PlanDao(session).getPlansOfYear(year);

        int planId0 = plans.get(0).getPlnId();
        int planId1 = planId0;

        if(plans.size() > 1){
            planId1 = plans.get(1).getPlnId();
        }

        String hql = "from Major where (planId = :planId0 or planId = :planId1)";
        Query query = session.createQuery(hql);
        query.setParameter("planId0",planId0);
        query.setParameter("planId1",planId1);
        return query.list();


    }

    /*
     * @param college:学院
     * 根据学院查询所有符合要求的专业
     */
    public List<Major> getMajorsOfClg (String college) {
        String hql = "from Major where college = :college";
        Query query = session.createQuery(hql);
        query.setParameter("college",college);
        return query.list();
    }

    /*
     * @param college:学院
     * @param planId:教学计划
     * 根据学院和教学计划查询所有符合要求的专业
     */
    public List<Major> getMajors (String college,int planId) {
        String hql = "from Major where college = :college and planId = :planId";
        Query query = session.createQuery(hql);
        query.setParameter("planId",planId);
        query.setParameter("college",college);
        return query.list();
    }

    /*
     * @param ms: MajorSearch类
     * 根据Dto:MajorSearch类查询所有符合要求的专业
     */
    public Major getMajor(MajorSearch ms) {

        String year = ms.getPlanYear();
        String name = ms.getMajorName();
        String level= ms.getMajorLevel();
        String season = ms.getPlanSeason();
        int planId = new PlanDao(session).getPlanId(year,season);
        String hql = "from Major where name = :name  and level = :level and planId = :planId";
        Query query = session.createQuery(hql);
        query.setParameter("name",name);
        query.setParameter("level",level);
        query.setParameter("planId",planId);
        Major queryResult = (Major) query.uniqueResult();

        return queryResult;
    }

    /*
     * @param planId:教学计划
     * 根据教学计划查询所有符合要求的学院名称
     */
    public List<String> getColleges(int planId) {

        String hql = "select distinct college from Major where planId = :planId";
        Query query = session.createQuery(hql);
        query.setParameter("planId",planId);
        List<String> collegeList = query.list();

        return collegeList;
    }



    /*
     * @param ms: MajorSearch类
     * @return: 不存在则返回-1
     * 根据Dto:MajorSearch类查询符合要求的专业ID
     */
    public int getMajorId (MajorSearch ms){

        Major queryResult = this.getMajor(ms);
        if(queryResult != null) {
            return queryResult.getMjrId();
        }else {
            return -1;
        }
    }
}
