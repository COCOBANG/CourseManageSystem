package DTO;

import java.util.List;

/**
 * Created by Jaho on 2017/3/3.
 * 查看专业对应主干课
 */
public class MajorInfo {

    private String name;
    private String level;
    private String degree;
    private List<String> coreLessons;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public List<String> getCoreLessons() {
        return coreLessons;
    }

    public void setCoreLessons(List<String> coreLessons) {
        this.coreLessons = coreLessons;
    }

}
