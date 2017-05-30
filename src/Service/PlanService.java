package Service;

import DAO.PlanDao;
import DAO.MajorDao;
import DAO.LessonDao;
import DAO.MjrLsnDao;

import DTO.RsLesson;
import DTO.PlnLsnInfo;
import DTO.PlnMjrInfo;

import Entity.Major;
import Entity.Lesson;
import Entity.MajorLesson;

import java.util.List;
import java.util.ArrayList;
import org.hibernate.Session;

/**
 * Created by Jaho on 2017/2/28.
 */
public class PlanService {

    private Session session = null;

    public PlanService(Session session){
        this.session = session;
    }


    // 查询同一个教学计划内被多个专业使用的课程
    public List<RsLesson> getReuseLessons(String year , String season){

        PlanDao planDao = new PlanDao(session);
        LessonDao lessonDao = new LessonDao(session);
        MjrLsnDao mjrLsnDao = new MjrLsnDao(session);
        MajorDao majorDao = new MajorDao(session);

        // 获取教学计划ID
        int planId = planDao.getPlanId(year,season);
        // 根据教学计划ID获取所有课程

        // 课程信息
        List<Lesson> lessons = lessonDao.getLessonsByPlanId(planId);
        // 重用课程信息
        List<RsLesson> rsLessons = new ArrayList<RsLesson>();


        for (Lesson lesson: lessons) {
            // 判断该课程是否被多个专业使用
            List<MajorLesson> majorLessons = mjrLsnDao.getMjrLsnsOfLesson(lesson.getLsnId());
            if (majorLessons.size() > 1) {

                // 设置该课程信息
                RsLesson rsLesson = new RsLesson();
                rsLesson.setLesson(lesson);

                // 添加使用该课程的专业名称
                for (MajorLesson ml:majorLessons)
                    rsLesson.getMajorNames().add(majorDao.getName(ml.getMjrId()));

            }
        }

        return rsLessons;
    }

    public List<PlnMjrInfo> getLsnsInMjrOfPlan(String year, String season){

        // 获取教学计划ID
        PlanDao planDao = new PlanDao(session);
        int planId = planDao.getPlanId(year,season);

        // 获取该教学计划下所有专业信息
        MajorDao majorDao = new MajorDao(session);
        List<Major> majors = majorDao.getMajorsOfPlan(planId);

        MjrLsnDao mjrLsnDao = new MjrLsnDao(session);
        LessonDao lessonDao = new LessonDao(session);

        // 遍历专业信息，查询并添加课程信息:
        List<PlnMjrInfo> plnMjrInfos = new ArrayList<PlnMjrInfo>();

        // 1.遍历专业
        for(Major major:majors){
            PlnMjrInfo plnMjrInfo = new PlnMjrInfo();
            // 2.获取所有专业/课程对应关系
            List<MajorLesson> majorLessons = mjrLsnDao.getMjrLsnsOfMajor(major.getMjrId());

            // 3.添加课程信息
            for(MajorLesson ml:majorLessons)
                plnMjrInfo.getLessons().add(lessonDao.getLessonById(ml.getLsnId()));


            // 4.设置专业名称，加专业层次以区分同名专业
            plnMjrInfo.setMajorName(major.getName() + "--" + major.getLevel());
            plnMjrInfos.add(plnMjrInfo);
        }

        return plnMjrInfos;

    }



    public List<PlnLsnInfo> getLsnsInTypeOfPln(String year, String season){

        // 获取教学计划ID
        PlanDao planDao = new PlanDao(session);
        int planId = planDao.getPlanId(year,season);

        // 根据教学计划ID获取所有课程信息
        LessonDao lessonDao = new LessonDao(session);
        MjrLsnDao mjrLsnDao = new MjrLsnDao(session);

        List<PlnLsnInfo> plnLsnInfos = new ArrayList<PlnLsnInfo>();

        // 获取所有的课程类型
        List<String> lessonTypeList = new UtilService(session).getLsnTypes();

        // 遍历所有课程类型进行查询
        for(String lessonType :lessonTypeList){
            PlnLsnInfo plnLsnInfo = new PlnLsnInfo();
            // 获取所有专业/课程对应关系
            List<MajorLesson> majorLessons = mjrLsnDao.getMjrLsnsInType(lessonType,planId);

            if(majorLessons.size() != 0){
                // 添加课程信息
                for(MajorLesson ml:majorLessons)
                    plnLsnInfo.getLessons().add(lessonDao.getLessonById(ml.getLsnId()));

                // 设置课程类型
                plnLsnInfo.setType(lessonType);
            }
            plnLsnInfos.add(plnLsnInfo);
        }

        return plnLsnInfos;

    }
}
