package Dao;

import java.util.List;
import Entity.CollegeMajor;
import org.hibernate.Session;
import org.hibernate.query.Query;

/**
 * Created by Jaho on 2017/5/29.
 */
public class ClgMjrDao {
    private Session session = null;

    public ClgMjrDao(Session session){
        this.session = session;
    }

    // 检查此条学院专业对应关系是否已经存在
    private boolean hasExist(CollegeMajor collegeMajor) {

        String hql = "from CollegeMajor where clgName = :clgName and mjrName = :mjrName and mjrLevel = :mjrLevel";
        Query query = session.createQuery(hql);
        query.setParameter("clgName",collegeMajor.getClgName());
        query.setParameter("mjrName",collegeMajor.getMjrName());
        query.setParameter("mjrLevel",collegeMajor.getMjrLevel());

        if (query.uniqueResult() != null) {
            return true;
        }else {
            return false;
        }
    }
    // 插入学院、专业对应表
    public void insert(CollegeMajor collegeMajor){

        if(hasExist(collegeMajor)){
            return;
        }else {
            session.save(collegeMajor);
        }
    }


    // 更新此条信息
    public void update (CollegeMajor collegeMajor) {
        session.update(collegeMajor);
    }

    public List<CollegeMajor> getMjrsOfCollege(String college){

        String hql = "from CollegeMajor where clgName = :college";
        Query query = session.createQuery(hql);
        query.setParameter("college",college);
        return query.list();
    }

    // 获取某个专业对应的学院名
    public String getCollegeOfMajor(String major){

        String hql = "select clgName from CollegeMajor where mjrName = :major";
        Query query = session.createQuery(hql);
        query.setParameter("major",major);
        String college  = (String) query.uniqueResult();

        return college;

    }
}
