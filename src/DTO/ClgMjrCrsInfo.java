package DTO;

/**
 * Created by Jaho on 2017/3/10.
 * 学院-专业对应关系信息
 */
public class ClgMjrCrsInfo {

    private String majorName;
    private String collegeName;

    public String getMajorName() {
        return majorName;
    }

    public void setMajorName(String majorName) {
        this.majorName = majorName;
    }

    public String getCollegeName() {
        return collegeName;
    }

    public void setCollegeName(String collegeName) {
        this.collegeName = collegeName;
    }
}
