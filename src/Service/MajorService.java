package Service;

import DAO.PlanDao;
import DAO.MajorDao;
import DAO.LessonDao;
import DAO.MjrLsnDao;

import DTO.LessonInfo;
import DTO.MajorSearch;
import DTO.LsnCmprInfo;

import java.util.List;
import java.util.ArrayList;

import Entity.Lesson;
import Entity.Major;
import Entity.MajorLesson;
import org.hibernate.Session;

/**
 * Created by Jaho on 2017/2/28.
 */
public class MajorService {

    private Session session = null;

    public MajorService(Session session){
        this.session = session;
    }

    // 根据专业获取所有课程
    public List<LessonInfo> getLsnInfosOfMajor(MajorSearch ms){

        // 获取专业ID
        MajorDao majorDao = new MajorDao(session);
        int majorId =  majorDao.getMajorId(ms);

        // 根据专业ID获取对应所有专业课程关系
        MjrLsnDao mjrLsnDao = new MjrLsnDao(session);
        List<MajorLesson> majorLessons = mjrLsnDao.getMjrLsnsOfMajor(majorId);

        // 获取所有课程信息
        List<LessonInfo> lessonInfos = new ArrayList<LessonInfo>();
        LessonDao lessonDao = new LessonDao(session);
        for (MajorLesson majorLesson: majorLessons) {
            Lesson lesson = lessonDao.getLessonById(majorLesson.getLsnId());
            LessonInfo lessonInfo = UtilService.copyLsn(lesson);
            lessonInfo.setType(majorLesson.getLsnType());
            lessonInfos.add(lessonInfo);
        }
        return lessonInfos;
    }


    public List<String> compare2Major(MajorSearch oldMs, MajorSearch newMs){

        // 根据信息获取完整专业信息
        MajorDao majorDao = new MajorDao(session);
        Major oldMajor = majorDao.getMajor(oldMs);
        Major newM = majorDao.getMajor(newMs);

        // 将专业信息转化为Vector
        List<String> compareResult = new ArrayList<String>();
        List<String> oldMajors = this.ConvertMajor2List(oldMajor);
        List<String> newMajors = this.ConvertMajor2List(newM);

        // 对比项
        String[] cmprItems = {"专业名称","专业代码","专业层次","学科门类","门类代码","专业类别","类别代码","专业学制",
                "授予学位","学位授予数据","正式注册生数","进修注册生数","毕业生数"};

        // 如果没有此记录，则不进行比对
        if(oldMajor == null){
            for(int i = 0; i < cmprItems.length; ++i)
                compareResult.add("没有往前记录");
            return compareResult;
        }

        // 进行比较
        for(int i = 0; i < cmprItems.length; ++i){

            String oldStr = oldMajors.get(i);
            String newStr = newMajors.get(i);

            // 查无记录的情况
            if(oldStr == null){
                oldStr = "";
            }

            // 值为空的情况
            if(oldStr == ""){
                compareResult.add("往前记录为空");
                continue;
            }

            if(!oldStr.equals(newStr)){
                compareResult.add(cmprItems[i] + "由-> " + oldStr  + "变为了-> " + newStr);
            }else {
                compareResult.add("");
            }

        }

        return compareResult;

    }


    public List<LsnCmprInfo> CmprLsnsBy2Major(MajorSearch oldMs, MajorSearch newMs) {

        // 用来存储比较记录
        List<LsnCmprInfo> cmprLsnInfos = new ArrayList<LsnCmprInfo>();

        // 存储同名课程
        List<LessonInfo> oldSameLessons = new ArrayList<LessonInfo>();
        List<LessonInfo> newSameLessons = new ArrayList<LessonInfo>();

        // 获取专业的课程信息
        List<LessonInfo> oldLessons = this.getLsnInfosOfMajor(oldMs);
        List<LessonInfo> newLessons = this.getLsnInfosOfMajor(newMs);

        List<LessonInfo> oldLessonsCopy = this.getLsnInfosOfMajor(oldMs);
        List<LessonInfo> newLessonsCopy = this.getLsnInfosOfMajor(newMs);

        //比较课程名，查询同名课程
        for (LessonInfo oldLesson:oldLessonsCopy) {
            for (LessonInfo newLesson:newLessonsCopy){
                String oldName = oldLesson.getName();
                String newName = newLesson.getName();
                if(oldName.equals(newName)){
                    oldSameLessons.add(oldLesson);
                    oldLessons.remove(oldLesson);
                    newSameLessons.add(newLesson);
                    newLessons.remove(newLesson);
                }else
                    continue;
            }
        }

        // 2.对同名课程进行比较
        String[] compareItems = {"课程代码","课程类型","学分","学时","学期","考试方式","开/闭卷"};

        for(int i = 0; i < oldSameLessons.size(); ++i){

            LsnCmprInfo lsnCmprInfo = new LsnCmprInfo();

            // 获取比较课程
            LessonInfo oldLesson = oldSameLessons.get(i);
            LessonInfo newLesson = newSameLessons.get(i);

            // 将比较课程存储
            lsnCmprInfo.setOldLesson(oldLesson);
            lsnCmprInfo.setNewLesson(newLesson);

            // 转为List进行比较
            List<String> oldLsn = this.ConvertLesson2List(oldLesson);
            List<String> newLsn = this.ConvertLesson2List(newLesson);

            for (int j = 0; j < oldLsn.size(); ++j){

                String oldStr = oldLsn.get(j);
                String newStr = newLsn.get(j);

//                // MARK:比较学期
//                if(j == 3){
//                    char oc = oldStr.charAt(1);
//                    char nc = newStr.charAt(1);
//                    if(oc == nc){
//                        continue;
//                    }
//                }

                if(!(oldStr.contains(newStr))){
                    lsnCmprInfo.getCompareRecord().add(compareItems[j] + "由-> " + oldStr  + "变为了-> " + newStr);
                }
            }

            cmprLsnInfos.add(lsnCmprInfo);
        }

        // 3.删除的课程信息
        if(oldLessons.size() != 0){
            for (LessonInfo oldLesson:oldLessons) {

                LsnCmprInfo lsnCmprInfo = new LsnCmprInfo();
                lsnCmprInfo.setOldLesson(oldLesson);
                lsnCmprInfo.setNewLesson(this.getNewLesson());
                lsnCmprInfo.getCompareRecord().add("删除了课程-> " + oldLesson.getName());
                cmprLsnInfos.add(lsnCmprInfo);

            }
        }

        // 4.新增的课程信息
        if(newLessons.size() != 0){
            for (LessonInfo newLesson:newLessons) {

                LsnCmprInfo lsnCmprInfo = new LsnCmprInfo();
                lsnCmprInfo.setNewLesson(newLesson);
                lsnCmprInfo.setOldLesson(this.getNewLesson());
                lsnCmprInfo.getCompareRecord().add("新添了课程-> " + newLesson.getName());
                cmprLsnInfos.add(lsnCmprInfo);

            }
        }

        return cmprLsnInfos;
    }

    /*
     *
     * @param ms: 专业信息
     * @param lessonInfos: 课表信息
     * @param year: 新课表年份
     * 存储排课信息
     */
    public void storeMajorLesson(MajorSearch ms,List<LessonInfo> lsnInfos,String year){

        PlanDao planDao = new PlanDao(session);
        MajorDao majorDao = new MajorDao(session);
        LessonDao lessonDao = new LessonDao(session);
        MjrLsnDao mjrLsnDao = new MjrLsnDao(session);

        Major oldMajor = majorDao.getMajor(ms);

        // 设置并存储新的专业信息
        Major newMajor = UtilService.copyMajor(oldMajor);
        int newPlanId = planDao.insert(year,ms.getPlanSeason());
        newMajor.setPlanId(newPlanId);
        int majorId = majorDao.insert(newMajor);

        // 存储一条课程信息，同时存储一条专业/课程对应关系
        for(LessonInfo lessonInfo:lsnInfos){
            MajorLesson majorLesson = new MajorLesson();

            // 存储课程信息
            Lesson lesson = UtilService.copyLsnInfo(lessonInfo);
            lesson.setPlanId(newPlanId);
            int lessonId = lessonDao.insert(lesson);

            // 存储专业/课程对应关系
            majorLesson.setMjrId(majorId);
            majorLesson.setLsnId(lessonId);
            majorLesson.setLsnType(lessonInfo.getType());
            // MARK:目前没有设置是否为主干课
            majorLesson.setPlanId(newPlanId);
            mjrLsnDao.insert(majorLesson);

        }
    }

    public LessonInfo getNewLesson(){

        LessonInfo lessonInfo = new LessonInfo();
        lessonInfo.setSemester("");
        lessonInfo.setExamine("");
        lessonInfo.setRemark("");
        lessonInfo.setName("无记录");
        lessonInfo.setCode("");
        lessonInfo.setCredit("");
        lessonInfo.setCrdtHours("");
        lessonInfo.setType("");
        return  lessonInfo;

    }

    // 将课程转为List,方便进行对比
    private List<String> ConvertLesson2List(LessonInfo lesson) {
        List<String> lessons = new ArrayList<String>();
        lessons.add(lesson.getCode());
        lessons.add(lesson.getType());
        lessons.add(lesson.getCredit());
        lessons.add(lesson.getCrdtHours());
        lessons.add(lesson.getSemester());
        lessons.add(lesson.getExamine());
        lessons.add(lesson.getRemark());

        return lessons;
    }

    // 将专业转为List,方便进行对比
    private List<String> ConvertMajor2List(Major major) {

        List<String> majors = new ArrayList<String>();
        majors.add(major.getName());
        majors.add(major.getCode());
        majors.add(major.getLevel());
        majors.add(major.getSbjct());
        majors.add(major.getSbjctCd());
        majors.add(major.getMjrTyp());
        majors.add(major.getMjrTypCd());
        majors.add(major.getEduSys());
        majors.add(major.getDegree());
        majors.add(major.getDgrNm());
        majors.add(major.getOfclNum());
        majors.add(major.getEnhncNum());
        majors.add(major.getGrdtNum());
        return majors;
    }



}


