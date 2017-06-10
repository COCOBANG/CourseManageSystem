package DTO;

import Entity.Major;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jaho on 2017/3/9.
 * 文件传输信息
 */
public class FileInfo {

    private String year;
    private String season;
    private Major major;
    private List<LessonInfo> lessonInfos;

    public FileInfo() {
        this.lessonInfos = new ArrayList<LessonInfo>();
        this.major = new Major();
    }

    public String getYear() {
        return year;
    }

    public void setSeason(String season) {
        this.season = season;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public Major getMajor() {
        return major;
    }

    public String getSeason() {
        return season;
    }

    public List<LessonInfo> getLessonInfos() {
        return lessonInfos;
    }

}