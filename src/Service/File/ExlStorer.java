package Service.File;

import DAO.*;

import DTO.FileInfo;
import DTO.CMRelation;
import DTO.LessonInfo;

import Entity.Lesson;
import Entity.MajorLesson;

import Service.UtilService;
import Service.RelationService;

import org.hibernate.Session;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jaho on 2017/6/8.
 * 将前端传回的数据进行存储
 */
public class ExlStorer {

    private Session session;

    public ExlStorer(Session session) {
        this.session = session;
    }

    // 将文件数据存储至数据库
    public int InsertFileInfoToDatabase(FileInfo[] fileInfoList) {

        for (FileInfo fileInfo : fileInfoList) {

            // 获取所有相关DAO接口
            PlanDao planDao = new PlanDao(session);
            MajorDao majorDao = new MajorDao(session);
            LessonDao lessonDao = new LessonDao(session);
            MjrLsnDao mjrLsnDao = new MjrLsnDao(session);
            CMRelationDao cmRelationDao = new CMRelationDao(session);

            // 存储并获得教学计划ID
            int planId = planDao.insert(fileInfo.getYear(), fileInfo.getSeason());

            // 设置专业的教学计划ID
            fileInfo.getMajor().setPlanId(planId);

            // 从数据库查询专业对应学院
            String majorName = fileInfo.getMajor().getName();
            String collegeName = cmRelationDao.getCollegeOfMajor(majorName);

            // 判断用户若没有提前设置此学院/专业对应关系，则从用户补充的信息设置新的关系并存储
            if(collegeName != null){
                // 设置此学院/专业对应关系
                CMRelation cmRelation = new CMRelation();
                cmRelation.setMjrName(fileInfo.getMajor().getName());
                cmRelation.setClgName(fileInfo.getMajor().getCollege());

                List<CMRelation> cmRelations= new ArrayList<CMRelation>();
                cmRelations.add(cmRelation);
                RelationService relationService = new RelationService(session);
                relationService.insertRelations(cmRelations);
            }

            // 保存专业信息并获取ID
            int majorId = majorDao.insert(fileInfo.getMajor());

            // 存储课程并存储专业/课程对应关系
            int size = fileInfo.getLessonInfos().size();
            for (int i = 0; i < size; ++i) {

                List<LessonInfo> lessonInfos = fileInfo.getLessonInfos();

                try {
                    LessonInfo lessonInfo = (LessonInfo) lessonInfos.get(i);
                    if (lessonInfo == null) {
                        continue;
                    }

                    // 新的课程以及专业/课程对应关系
                    Lesson lesson = UtilService.copyLsnInfo(lessonInfo);
                    MajorLesson majorLesson = new MajorLesson();

                    // 设置课程的教学计划ID
                    lesson.setPlanId(planId);
                    int lessonId = lessonDao.insert(lesson);

                    // 保存专业/课程对应关系
                    majorLesson.setMjrId(majorId);
                    majorLesson.setLsnId(lessonId);
                    majorLesson.setLsnType(lessonInfo.getType());
                    majorLesson.setPlanId(planId);
                    mjrLsnDao.insert(majorLesson);

                    return 1;
                } catch (Exception e) {
                    return 0;
                }

            }
        }

        return 0;
    }

}
