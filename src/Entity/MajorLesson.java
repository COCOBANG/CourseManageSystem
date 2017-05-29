package Entity;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by Jaho on 2017/5/29.
 */
@Entity
@Table(name = "major_lesson", schema = "CMS", catalog = "")
public class MajorLesson {
    private Integer mjrId;
    private Integer lsnId;
    private String lsnType;
    private Integer planId;

    @Basic
    @Column(name = "mjr_id", nullable = false)
    public Integer getMjrId() {
        return mjrId;
    }

    public void setMjrId(Integer mjrId) {
        this.mjrId = mjrId;
    }

    @Basic
    @Column(name = "lsn_id", nullable = false)
    public Integer getLsnId() {
        return lsnId;
    }

    public void setLsnId(Integer lsnId) {
        this.lsnId = lsnId;
    }

    @Basic
    @Column(name = "lsn_type", nullable = false, length = 20)
    public String getLsnType() {
        return lsnType;
    }

    public void setLsnType(String lsnType) {
        this.lsnType = lsnType;
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

        MajorLesson that = (MajorLesson) o;

        if (mjrId != null ? !mjrId.equals(that.mjrId) : that.mjrId != null) return false;
        if (lsnId != null ? !lsnId.equals(that.lsnId) : that.lsnId != null) return false;
        if (lsnType != null ? !lsnType.equals(that.lsnType) : that.lsnType != null) return false;
        if (planId != null ? !planId.equals(that.planId) : that.planId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = mjrId != null ? mjrId.hashCode() : 0;
        result = 31 * result + (lsnId != null ? lsnId.hashCode() : 0);
        result = 31 * result + (lsnType != null ? lsnType.hashCode() : 0);
        result = 31 * result + (planId != null ? planId.hashCode() : 0);
        return result;
    }
}
