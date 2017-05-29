package DTO;

import Entity.Major;

/**
 * Created by Jaho on 2017/5/29.
 * 根据学院查看所有专业
 */

public class ClgMajorInfo {

    private String year;
    private String season;
    private Major major;

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getSeason() {
        return season;
    }

    public void setSeason(String season) {
        this.season = season;
    }

    public Major getMajor() {
        return major;
    }

    public void setMajor(Major major) {
        this.major = major;
    }
}
