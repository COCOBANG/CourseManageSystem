package DTO;


/**
 * Created by Jaho on 2017/3/9.
 */
public class LessonInfo{

    private String type;
    private String code;
    private String name;
    private String credit;
    private String crdtHours;
    private String semester;
    private String examine;
    private String remark;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCredit() {
        return credit;
    }

    public void setCredit(String credit) {
        this.credit = credit;
    }

    public String getCrdtHours() {
        return crdtHours;
    }

    public void setCrdtHours(String crdtHours) {
        this.crdtHours = crdtHours;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getExamine() {
        return examine;
    }

    public void setExamine(String examine) {
        this.examine = examine;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
