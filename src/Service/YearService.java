package Service;

import DAO.MajorDao;
import DAO.MjrLsnDao;

import Entity.Major;
import Entity.Lesson;
import Entity.MajorLesson;

import org.hibernate.Session;

import java.util.*;

/**
 * Created by Jaho on 2017/2/28.
 */
public class YearService {

    private Session session = null;

    public YearService(Session session){
        this.session = session;
    }

    // 查看某一年，按专业划分的课程信息
    public Map<String,List<String>> getLsnsInMjrOfYear(String year){

        // 获取该年的所有专业信息
        MajorDao majorDao = new MajorDao(session);
        List<Major> majors = majorDao.getMajorsOfYear(year);

        // 返回结果
        Map<String,List<String>> majorLessonMap = new HashMap<String,List<String>>();

        // 获取每一个专业对应的课程信息:
        MjrLsnDao mjrLsnDao = new MjrLsnDao(session);
        for (Major major:majors) {
            // 1.获取专业对应的专业/课程关系
            List<String> lessonNames = new ArrayList<String>();
            List<MajorLesson> majorLessons = mjrLsnDao.getMjrLsnsOfMajor(major.getMjrId());

            for (MajorLesson ml: majorLessons) {
                // 2.获取课程名称
                Lesson lesson = session.get(Lesson.class,ml.getLsnId());
                lessonNames.add(lesson.getName());
            }

            // 3.保存专业-课程信息
            majorLessonMap.put(major.getLevel() + "--" + major.getName(),lessonNames);
        }
        return majorLessonMap;
    }


    // 查看某一年，按照层次划分的专业信息
    public Map<String,List<String>> getMjrsInLvlOfYear(String majorYear){


        // 获取所有层次
        String[] levels = new UtilService(session).getLevels().toArray(new String[]{});

        // 存放返回结果
        Map<String,List<String>> majorLessonMap = new HashMap<String,List<String>>();

        // 获取每个层次的专业信息:
        MajorDao majorDao = new MajorDao(session);

        for( int i = 0; i < levels.length; ++i){
            List<String> majorNames = new ArrayList<String>();

            // 1.获取专业信息
            List<Major> majors = majorDao.getMajors(levels[i],majorYear);

            // 2.添加专业名称
            for (Major major: majors)
                majorNames.add(major.getName());
            // 3.保存层次-专业信息
            majorLessonMap.put(levels[i],majorNames);
        }
        return majorLessonMap;
    }

    // 获取每一年的，按照专业划分的课程信息
    public Map<String,Map<String,List<String>>> getLsnsInMajor(){

        // 获取所有年份
        List<String> years = new UtilService(session).getYears();
        // 返回结果
        LinkedHashMap<String,Map<String,List<String>>> resultMap = new LinkedHashMap<String,Map<String,List<String>>>();

        for (String year: years) {
            Map<String,List<String>> majorLessonMap = this.getLsnsInMjrOfYear(year);
            resultMap.put(year,majorLessonMap);
        }

        return resultMap;

    }

    // 获取每一年的，按照层次划分的专业信息
    public Map<String,Map<String,List<String>>> getMjosInLevel() {

        // 获取所有年份
        List<String> yearList = new UtilService(session).getYears();
        LinkedHashMap<String,Map<String,List<String>>> resultMap = new LinkedHashMap<String,Map<String,List<String>>>();

        // 返回结果
        for (String year: yearList) {
            Map<String,List<String>> levelMajorMap = this.getMjrsInLvlOfYear(year);
            resultMap.put(year,levelMajorMap);
        }

        return resultMap;
    }

}
