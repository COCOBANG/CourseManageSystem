package DTO;
import DAO.HibernateUtil;
import DAO.PlanDao;
import Entity.Major;
import org.hibernate.Session;

import java.util.Comparator;

/**
 * Created by Jaho on 2017/3/9.
 */
public class MajorComparator implements Comparator{
    @Override
    public int compare(Object o1, Object o2) {

        Session session = HibernateUtil.getSession();
        Major major1 = (Major)o1;
        Major major2 = (Major)o2;
        PlanDao planDao = new PlanDao(session);
        String planYear1 = planDao.getPlanById(major1.getPlanId()).getYear();
        String planYear2 = planDao.getPlanById(major2.getPlanId()).getYear();
        session.close();
        int year1 = Integer.parseInt(planYear1);
        int year2 = Integer.parseInt(planYear2);
        if(year1 == year2){
            return 0;
        }else if(year1 > year2){
            return 1;
        }else {
            return -1;
        }
    }
}
