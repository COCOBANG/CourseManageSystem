package Service;

import DAO.PlanDao;
import DAO.MajorDao;

import java.util.List;
import DTO.ClgMajorInfo;
import DTO.MjrComparator;
import java.util.ArrayList;

import Entity.Major;
import Entity.Plan;
import org.hibernate.Session;
import java.util.Collections;

/**
 * Created by Jaho on 2017/2/28.
 * 根据学院查看所有专业信息
 */
public class CollegeService {

    private Session session = null;

    public CollegeService(Session session){
        this.session = session;
    }

    public List<ClgMajorInfo> getMajorsOfCollege(String college){


        List<ClgMajorInfo> clgMajorInfos = new ArrayList<ClgMajorInfo>();
        MajorDao majorDao= new MajorDao(session);

        List<Major> majors = majorDao.getMajorsOfClg(college);

        Collections.sort(majors,new MjrComparator());
        PlanDao planDao = new PlanDao(session);

        for(Major major:majors){
            ClgMajorInfo clgMajorInfo = new ClgMajorInfo();
            Plan plan= planDao.getPlanById(major.getPlanId());
            clgMajorInfo.setMajor(major);
            clgMajorInfo.setYear(plan.getYear());
            clgMajorInfo.setSeason(plan.getSeason());
            clgMajorInfos.add(clgMajorInfo);
        }

        return clgMajorInfos;
    }

}
