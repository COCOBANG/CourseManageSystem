package Service;

import DTO.LessonInfo;
import Entity.Lesson;
import Entity.Major;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by Jaho on 2017/2/28.
 */
public class UtilService {

    private Session session = null;

    public UtilService(Session session){
        this.session = session;
    }

    public List<String> getYears(){
        String hql = "select distinct year from Plan";
        Query query = session.createQuery(hql);
        return query.list();
    }

    public List<String> getLevels() {
        String hql = "select distinct level from Major";
        Query query = session.createQuery(hql);
        return query.list();
    }

    public List<String> getColleges() {

        String hql = "select distinct college from Major ";
        Query query = session.createQuery(hql);
        return query.list();

    }

    public List<String> getMjrNames() {

        String hql = "select name from Major";
        Query query = session.createQuery(hql);
        return query.list();

    }

    public List<String> getLsnTypes() {

        String hql = "select lsnType from MajorLesson";
        Query query = session.createQuery(hql);
        return query.list();

    }

    // 复制专业信息
    public static Major copyMajor(Major oldM){

        Major newM = new Major();
        newM.setName(oldM.getName());
        newM.setCode(oldM.getCode());
        newM.setLevel(oldM.getLevel());
        newM.setSbjct(oldM.getSbjct());
        newM.setSbjctCd(oldM.getSbjctCd());
        newM.setMjrTyp(oldM.getMjrTyp());
        newM.setMjrTypCd(oldM.getMjrTypCd());
        newM.setEduSys(oldM.getEduSys());
        newM.setDegree(oldM.getDegree());
        newM.setDgrNm(oldM.getDgrNm());
        newM.setOfclNum(oldM.getOfclNum());
        newM.setEnhncNum(oldM.getEnhncNum());
        newM.setEnhncNum(oldM.getEnhncNum());
        newM.setCollege(oldM.getCollege());

        return newM;
    }

    // 复制课程信息类为课程实体
    public static Lesson copyLsnInfo(LessonInfo lessonInfo){

        Lesson lesson = new Lesson();
        lesson.setSemester(lessonInfo.getSemester());
        lesson.setExamine(lessonInfo.getExamine());
        lesson.setRemark(lessonInfo.getRemark());
        lesson.setName(lessonInfo.getName());
        lesson.setCode(lessonInfo.getCode());
        lesson.setCredit(lessonInfo.getCredit());
        lesson.setCrdtHrs(lessonInfo.getCrdtHours());

        return lesson;
    }



}
