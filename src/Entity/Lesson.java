package Entity;

import javax.persistence.*;

/**
 * Created by Jaho on 2017/5/29.
 */
@Entity
public class Lesson {
    private Integer lsnId;
    private String name;
    private String code;
    private String credit;
    private String crdtHrs;
    private String semester;
    private String examine;
    private String remark;
    private Integer planId;

    @Id
    @Column(name = "lsn_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer getLsnId() {
        return lsnId;
    }

    public void setLsnId(Integer lsnId) {
        this.lsnId = lsnId;
    }

    @Basic
    @Column(name = "name", nullable = false, length = 50)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "code", nullable = false, length = 20)
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Basic
    @Column(name = "credit", nullable = false, length = 20)
    public String getCredit() {
        return credit;
    }

    public void setCredit(String credit) {
        this.credit = credit;
    }

    @Basic
    @Column(name = "crdt_hrs", nullable = true, length = 20)
    public String getCrdtHrs() {
        return crdtHrs;
    }

    public void setCrdtHrs(String crdtHrs) {
        this.crdtHrs = crdtHrs;
    }

    @Basic
    @Column(name = "semester", nullable = false, length = 20)
    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    @Basic
    @Column(name = "examine", nullable = true, length = 20)
    public String getExamine() {
        return examine;
    }

    public void setExamine(String examine) {
        this.examine = examine;
    }

    @Basic
    @Column(name = "remark", nullable = true, length = 20)
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Basic
    @Column(name = "plan_id", nullable = false)
    public Integer getPlanId() {
        return planId;
    }

    public void setPlanId(Integer planId) {
        this.planId = planId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Lesson lesson = (Lesson) o;

        if (lsnId != null ? !lsnId.equals(lesson.lsnId) : lesson.lsnId != null) return false;
        if (name != null ? !name.equals(lesson.name) : lesson.name != null) return false;
        if (code != null ? !code.equals(lesson.code) : lesson.code != null) return false;
        if (credit != null ? !credit.equals(lesson.credit) : lesson.credit != null) return false;
        if (crdtHrs != null ? !crdtHrs.equals(lesson.crdtHrs) : lesson.crdtHrs != null) return false;
        if (semester != null ? !semester.equals(lesson.semester) : lesson.semester != null) return false;
        if (examine != null ? !examine.equals(lesson.examine) : lesson.examine != null) return false;
        if (remark != null ? !remark.equals(lesson.remark) : lesson.remark != null) return false;
        if (planId != null ? !planId.equals(lesson.planId) : lesson.planId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = lsnId != null ? lsnId.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (code != null ? code.hashCode() : 0);
        result = 31 * result + (credit != null ? credit.hashCode() : 0);
        result = 31 * result + (crdtHrs != null ? crdtHrs.hashCode() : 0);
        result = 31 * result + (semester != null ? semester.hashCode() : 0);
        result = 31 * result + (examine != null ? examine.hashCode() : 0);
        result = 31 * result + (remark != null ? remark.hashCode() : 0);
        result = 31 * result + (planId != null ? planId.hashCode() : 0);
        return result;
    }
}
