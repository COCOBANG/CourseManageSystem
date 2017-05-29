package Dto;

/**
 * Created by Jaho on 2017/2/28.
 */
public class MajorSearch {

    private String planYear;
    private String majorName;
    private String majorLevel;
    private String planSeason;



    public String getPlanYear() {
        return planYear;
    }
    public String getMajorName() {
        return majorName;
    }
    public String getMajorLevel() {
        return majorLevel;
    }
    public String getPlanSeason() {
        return planSeason;
    }

    public void setPlanYear(String planYear) {
        this.planYear = planYear;
    }
    public void setMajorName(String majorName) {
        this.majorName = majorName;
    }
    public void setMajorLevel(String majorLevel) {
        this.majorLevel = majorLevel;
    }
    public void setPlanSeason(String planSeason) {
        this.planSeason = planSeason;
    }
}
