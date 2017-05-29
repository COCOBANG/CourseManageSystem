package Service;

import DAO.LessonDao;
import DAO.MajorDao;
import DAO.MjrLsnDao;
import DTO.LessonInfo;
import DTO.MajorSearch;
import Entity.Lesson;
import Entity.Major;
import Entity.MajorLesson;
import org.hibernate.Session;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by Jaho on 2017/2/28.
 */
public class MajorService {

    private Session session = null;

    public MajorService(Session session){
        this.session = session;
    }

    // 根据专业获取所有课程
    public List<Lesson> getsOfMajor(MajorSearch ms){

        // 获取专业ID
        MajorDao majorDao = new MajorDao(session);
        int majorId =  majorDao.getMajorId(ms);

        // 根据专业ID获取对应所有专业课程关系
        MjrLsnDao mjrLsnDao = new MjrLsnDao(session);
        List<MajorLesson> majorLessons = mjrLsnDao.getMjrLsnsOfMajor(majorId);

        // 获取所有课程信息
        List<Lesson> lessons = new ArrayList<Lesson>();
        LessonDao lessonDao = new LessonDao(session);
        for (MajorLesson majorLesson: majorLessons) {
            Lesson lesson = lessonDao.getLessonById(majorLesson.getLsnId());
            lessons.add(lesson);
        }
        return lessons;
    }

    public List<LessonInfo> getInfosOfMajor(MajorSearch ms){

        // 获取专业信息ID
        MajorDao majorDao = new MajorDao(session);
        int majorId =  majorDao.getMajorId(ms);

        // 获取所有课程信息
        MjrLsnDao mjrLsnDao = new MjrLsnDao(session);
        List<MajorLesson> majorLessons = mjrLsnDao.getMjrLsnsOfMajor(majorId);

        // 获取所有课程传输信息
        List<LessonInfo> lessonInfos = new ArrayList<LessonInfo>();
        LessonDao lessonDao = new LessonDao(session);
        for (MajorLesson mjrLsn: majorLessons) {
            LessonInfo lessonInfo = new LessonInfo();
            Lesson lesson = lessonDao.getLessonById(mjrLsn.getMjrId());
            lessonInfo.setName(lesson.getName());
            lessonInfo.setCode(lesson.getCode());
            lessonInfo.setCredit(lesson.getCredit());
            lessonInfo.setCrdtHours(lesson.getCrdtHrs());
            lessonInfo.setExamine(lesson.getExamine());
            lessonInfo.setSemester(lesson.getSemester());
            lessonInfo.setRemark(lesson.getRemark());
            lessonInfo.setType(mjrLsn.getLsnType());

            lessonInfos.add(lessonInfo);
        }

        return lessonInfos;
    }

    // 将课程转为Vector,方便进行对比
    private Vector<String> ConvertLesson2Vector(Lesson lesson) {
        Vector<String> lessonVector = new Vector<String>();
        lessonVector.addElement(lesson.getCode());
        lessonVector.addElement(lesson.getCredit());
        lessonVector.addElement(lesson.getCrdtHrs());
        lessonVector.addElement(lesson.getSemester());
        lessonVector.addElement(lesson.getExamine());
        lessonVector.addElement(lesson.getRemark());

        return lessonVector;
    }

    // 将专业转为Vector,方便进行对比
    private Vector<String> ConvertMajor2Vector(Major major) {

        Vector<String> majorVector = new Vector<String>();
        majorVector.addElement(major.getName());
        majorVector.addElement(major.getCode());
        majorVector.addElement(major.getLevel());
        majorVector.addElement(major.getSbjct());
        majorVector.addElement(major.getSbjctCd());
        majorVector.addElement(major.getMjrTyp());
        majorVector.addElement(major.getMjrTypCd());
        majorVector.addElement(major.getEduSys());
        majorVector.addElement(major.getDegree());
        majorVector.addElement(major.getDgrNm());
        majorVector.addElement(major.getOfclNum());
        majorVector.addElement(major.getEnhncNum());
        majorVector.addElement(major.getGrdtNum());
        return majorVector;
    }

    public Vector<String> Compare2Major(MajorSearch oldMs, MajorSearch newMs){
        // get MajorInfo ID
        MajorDao majorDao = new MajorDao(session);
        Major oldMajor = majorDao.getMajor(oldMs);
        Major newMajor = majorDao.getMajor(newMs);
        Vector<String> compareResult = new Vector<String>();
        Vector<String> oldMajors = this.ConvertMajor2Vector(oldMajor);
        Vector<String> newMajors = this.ConvertMajor2Vector(newMajor);

        String[] compareItems = {"专业名称","专业代码","专业层次","学科门类","门类代码","专业类别","类别代码","专业学制",
                "授予学位","学位授予数据","正式注册生数","进修注册生数","毕业生数"};

        // 进行比较
        for(int i = 0; i < compareItems.length; ++i){
            String oldStr = oldMajors.elementAt(i);
            String newStr = newMajors.elementAt(i);

            if(oldStr == null){
                oldStr = "";
            }

            if(oldStr == null){
                compareResult.addElement("没有往前记录");
                continue;
            }
            if(!oldStr.equals(newStr)){
                compareResult.addElement(compareItems[i] + "由-> " + oldStr  + "变为了-> " + newStr);
            }else {
                compareResult.addElement("");
            }
        }

        return compareResult;

    }


    public List<CompareLessonInfo> CompareLessonsBy2Major(MajorSearch oldMajorSearch, MajorSearch newMajorSearch) {

        // store the compare record
        List<CompareLessonInfo> compareLessonInfoList = new ArrayList<CompareLessonInfo>();

        // store the lessons with same name
        Vector<LessonTableEntity> oldSameMajorLessons = new Vector<LessonTableEntity>();
        Vector<LessonTableEntity> newSameMajorLessons = new Vector<LessonTableEntity>();

        // get the lessons of major
        Vector<LessonTableEntity> oldMajorLessons = this.getsOfMajor(oldMajorSearch);
        Vector<LessonTableEntity> newMajorLessons = this.getsOfMajor(newMajorSearch);

        Vector<LessonTableEntity> oldMajorLessonsCopy = this.getsOfMajor(oldMajorSearch);
        Vector<LessonTableEntity> newMajorLessonsCopy = this.getsOfMajor(newMajorSearch);

        //Compare the lessons
        // 1.Find the same name lessons
        for (LessonTableEntity oldLesson:oldMajorLessonsCopy) {
            for (LessonTableEntity newLesson:newMajorLessonsCopy){
                String oldName = oldLesson.getName();
                String newName = newLesson.getName();
                if(oldName.equals(newName)){
                    oldSameMajorLessons.addElement(oldLesson);
                    oldMajorLessons.removeElement(oldLesson);
                    newSameMajorLessons.addElement(newLesson);
                    newMajorLessons.removeElement(newLesson);
                }else {
                    continue;
                }
            }
        }

        // 2.compare the same name lessons
        String[] compareItems = {"课程代码","学分","学时","学期","考试方式","开/闭卷"};

        for(int i = 0; i < oldSameMajorLessons.size(); ++i){

            CompareLessonInfo compareLessonInfo = new CompareLessonInfo();
            LessonTableEntity oldLesson = oldSameMajorLessons.get(i);
            LessonTableEntity newLesson = newSameMajorLessons.get(i);

            compareLessonInfo.setOldLesson(oldLesson);
            compareLessonInfo.setNewLesson(newLesson);

            Vector<String> oldLessonVector = this.ConvertLesson2Vector(oldLesson);
            Vector<String> newLessonVector = this.ConvertLesson2Vector(newLesson);

            for (int j = 0; j < oldLessonVector.size(); ++j){
                String oldStr = oldLessonVector.elementAt(j);
                String newStr = newLessonVector.elementAt(j);
                if(j == 3){
                    char oc = oldStr.charAt(1);
                    char nc = newStr.charAt(1);
                    if(oc == nc){
                        continue;
                    }
                }
                if(!(oldStr.contains(newStr))){
                    compareLessonInfo.getCompareRecord().add(compareItems[j] + "由-> " + oldStr  + "变为了-> " + newStr);
                }
            }

            compareLessonInfoList.add(compareLessonInfo);
        }

        // 3.Delete Record
        if(oldMajorLessons.size() != 0){
            for (LessonTableEntity oldLesson:oldMajorLessons) {
                CompareLessonInfo compareLessonInfo = new CompareLessonInfo();
                compareLessonInfo.setOldLesson(oldLesson);
                compareLessonInfo.setNewLesson(this.getNewLesson());
                compareLessonInfo.getCompareRecord().add("删除了课程-> " + oldLesson.getName());
                compareLessonInfoList.add(compareLessonInfo);
            }
        }

        // 4.new Record
        if(newMajorLessons.size() != 0){
            for (LessonTableEntity newLesson:newMajorLessons) {
                CompareLessonInfo compareLessonInfo = new CompareLessonInfo();
                compareLessonInfo.setNewLesson(newLesson);
                compareLessonInfo.setOldLesson(this.getNewLesson());
                compareLessonInfo.getCompareRecord().add("新添了课程-> " + newLesson.getName());
                System.out.println("新添了课程->" + newLesson.getName());
                compareLessonInfoList.add(compareLessonInfo);
            }
        }

        return compareLessonInfoList;
    }

    public void storeMajorLesson(MajorSearch majorSearch,List<LessonInfo> lessonInfoList){

        Integer oldYear = Integer.parseInt(majorSearch.getPlanYear());
        ++oldYear;
        String newYear = oldYear.toString();
        System.out.print("NewYear--------" + newYear +"----------");
        MajorDao majorDao = new MajorDao(session);
        LessonDao lessonDao = new LessonDao(session);
        PlanDao planDao = new PlanDao(session);
        MajorLessonDao majorLessonDao = new MajorLessonDao(session);
        MajorTableEntity major = majorDao.get(majorSearch);

        // set new major table-------
        MajorTableEntity newMajorTableEntity = new MajorTableEntity();
        newMajorTableEntity.setMajorName(major.getName());
        newMajorTableEntity.setMajorCode(major.getCode());
        newMajorTableEntity.setMajorLevel(major.getLevel());
        newMajorTableEntity.setMajorSubject(major.getSubject());
        newMajorTableEntity.setMajorSubjectCode(major.getSubjectCode());
        newMajorTableEntity.setMajorType(major.getType());
        newMajorTableEntity.setMajorTypeCode(major.getTypeCode());
        newMajorTableEntity.setMajorEduSystem(major.getEduSystem());
        newMajorTableEntity.setMajorDegree(major.getDegree());
        newMajorTableEntity.setMajorDegreeNum(major.getDegreeNum());
        newMajorTableEntity.setMajorRegisterOfficialStuNum(major.getRegisterOfficialStuNum());
        newMajorTableEntity.setMajorRegisterEnhanceStuNum(major.getRegisterEnhanceStuNum());
        newMajorTableEntity.setMajorGraduateNum(major.getGraduateNum());
        newMajorTableEntity.setMajorCollege(major.getCollege());
        int newPlanId = planDao.insertPlan(newYear,majorSearch.getPlanSeason());
        newMajorTableEntity.setPlanId(newPlanId);
        System.out.print("New Plan Id ____" + newPlanId + "______");
        int majorId = majorDao.insertMajor(newMajorTableEntity);
        System.out.print("New majorId ____" + majorId + "______");

        //---------------------------
        // set and store a lesson and a major_lesson
        for(LessonInfo lessonInfo:lessonInfoList){
            LessonTableEntity lessonTableEntity = new LessonTableEntity();
            MajorLessonTableEntity majorLessonTableEntity = new MajorLessonTableEntity();

            // store a lesson
            lessonTableEntity.setLessonSemester(lessonInfo.getSemester());
            lessonTableEntity.setLessonExamine(lessonInfo.getExamine());
            lessonTableEntity.setLessonRemark(lessonInfo.getRemark());
            lessonTableEntity.setLessonName(lessonInfo.getName());
            lessonTableEntity.setLessonCode(lessonInfo.getCode());
            lessonTableEntity.setLessonCredit(lessonInfo.getCredit());
            lessonTableEntity.setLessonCreditHours(lessonInfo.getCreditHours());
            lessonTableEntity.setPlanId(newPlanId);
            int lessonId = lessonDao.insertLesson(lessonTableEntity);

            // store major_lesson
            majorLessonTableEntity.setMajorLessonMId(majorId);
            majorLessonTableEntity.setMajorLessonLId(lessonId);
            majorLessonTableEntity.setMajorLessonType(lessonInfo.getType());
            if (lessonInfo.getType().contains("必修课")){
                majorLessonTableEntity.setMajorLessonIsCore((byte)1);
            }else{
                majorLessonTableEntity.setMajorLessonIsCore((byte)0);
            }
            majorLessonTableEntity.setPlanId(newPlanId);
            majorLessonDao.insertMajorLesson(majorLessonTableEntity);

        }
        System.out.println("Insert Success!\n");
    }

    public LessonTableEntity getNewLesson(){

        LessonTableEntity lessonTableEntity = new LessonTableEntity();
        lessonTableEntity.setLessonSemester("");
        lessonTableEntity.setLessonExamine("");
        lessonTableEntity.setLessonRemark("");
        lessonTableEntity.setLessonName("无记录");
        lessonTableEntity.setLessonCode("");
        lessonTableEntity.setLessonCredit("");
        lessonTableEntity.setLessonCreditHours("");
        lessonTableEntity.setPlanId(0);
        return  lessonTableEntity;

    }

}


