package DAO;

import Entity.Plan;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.query.Query;

/**
 * Created by Jaho on 2017/5/29.
 */
public class PlanDao {

    private Session session = null;

    public PlanDao(Session session){
        this.session = session;
    }

    private int hasExist(String year, String season) {


        String hql = "from Plan where year = :year and season = :season";
        Query query = session.createQuery(hql);
        query.setParameter("year",year);
        query.setParameter("season",season);
        Plan queryResult = (Plan) query.uniqueResult();

        if (queryResult != null) {
            return queryResult.getPlnId();
        }else { return 0; }
    }

    // 插入新的教学计划，并返回ID
    public int insert(String year, String season) {

        int planId = this.hasExist(year,season);
        if ( planId == 0){
            Plan plan=  new Plan();
            plan.setYear(year);
            plan.setSeason(season);
            session.save(plan);
            return this.hasExist(year,season);
        } else {
            return planId;
        }
    }

    // 通过ID查询一个教学计划
    public Plan getPlanById (int planId) {
        return session.get(Plan.class,planId);
    }

    // 通过年份、季节获取对应教学计划ID
    public int getPlanId (String year, String season) {

        String hql = "from Plan where year = :year and season = :season";
        Query query = session.createQuery(hql);
        query.setParameter("year",year);
        query.setParameter("season",season);
        Plan queryResult = (Plan) query.uniqueResult();
        if(queryResult != null) {
            return queryResult.getPlnId();
        }else {
            return -1;
        }
    }

    // 返回一个年份的所有教学计划
    public List<Plan> getPlansOfYear (String year) {

        String hql = "from Plan where year = :year";
        Query query = session.createQuery(hql);
        query.setParameter("year",year);
        List<Plan> plans= query.list();
        return plans;
    }

}
