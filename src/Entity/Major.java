package Entity;

import javax.persistence.*;

/**
 * Created by Jaho on 2017/5/29.
 */
@Entity
public class Major {
    private Integer mjrId;
    private String name;
    private String level;
    private String code;
    private String sbjct;
    private String sbjctCd;
    private String mjrTyp;
    private String mjrTypCd;
    private String eduSys;
    private String degree;
    private String dgrNm;
    private String ofclNum;
    private String enhncNum;
    private String grdtNum;
    private String college;
    private Integer planId;

    @Id
    @Column(name = "mjr_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    public Integer getMjrId() {
        return mjrId;
    }

    public void setMjrId(Integer mjrId) {
        this.mjrId = mjrId;
    }

    @Basic
    @Column(name = "name", nullable = true, length = 50)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "level", nullable = true, length = 20)
    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    @Basic
    @Column(name = "code", nullable = true, length = 20)
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Basic
    @Column(name = "sbjct", nullable = true, length = 20)
    public String getSbjct() {
        return sbjct;
    }

    public void setSbjct(String sbjct) {
        this.sbjct = sbjct;
    }

    @Basic
    @Column(name = "sbjct_cd", nullable = true, length = 20)
    public String getSbjctCd() {
        return sbjctCd;
    }

    public void setSbjctCd(String sbjctCd) {
        this.sbjctCd = sbjctCd;
    }

    @Basic
    @Column(name = "mjr_typ", nullable = true, length = 20)
    public String getMjrTyp() {
        return mjrTyp;
    }

    public void setMjrTyp(String mjrTyp) {
        this.mjrTyp = mjrTyp;
    }

    @Basic
    @Column(name = "mjr_typ_cd", nullable = true, length = 20)
    public String getMjrTypCd() {
        return mjrTypCd;
    }

    public void setMjrTypCd(String mjrTypCd) {
        this.mjrTypCd = mjrTypCd;
    }

    @Basic
    @Column(name = "edu_sys", nullable = true, length = 20)
    public String getEduSys() {
        return eduSys;
    }

    public void setEduSys(String eduSys) {
        this.eduSys = eduSys;
    }

    @Basic
    @Column(name = "degree", nullable = true, length = 20)
    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    @Basic
    @Column(name = "dgr_nm", nullable = true, length = 20)
    public String getDgrNm() {
        return dgrNm;
    }

    public void setDgrNm(String dgrNm) {
        this.dgrNm = dgrNm;
    }

    @Basic
    @Column(name = "ofcl_num", nullable = true, length = 20)
    public String getOfclNum() {
        return ofclNum;
    }

    public void setOfclNum(String ofclNum) {
        this.ofclNum = ofclNum;
    }

    @Basic
    @Column(name = "enhnc_num", nullable = true, length = 20)
    public String getEnhncNum() {
        return enhncNum;
    }

    public void setEnhncNum(String enhncNum) {
        this.enhncNum = enhncNum;
    }

    @Basic
    @Column(name = "grdt_num", nullable = true, length = 20)
    public String getGrdtNum() {
        return grdtNum;
    }

    public void setGrdtNum(String grdtNum) {
        this.grdtNum = grdtNum;
    }

    @Basic
    @Column(name = "college", nullable = true, length = 20)
    public String getCollege() {
        return college;
    }

    public void setCollege(String college) {
        this.college = college;
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

        Major major = (Major) o;

        if (mjrId != null ? !mjrId.equals(major.mjrId) : major.mjrId != null) return false;
        if (name != null ? !name.equals(major.name) : major.name != null) return false;
        if (level != null ? !level.equals(major.level) : major.level != null) return false;
        if (code != null ? !code.equals(major.code) : major.code != null) return false;
        if (sbjct != null ? !sbjct.equals(major.sbjct) : major.sbjct != null) return false;
        if (sbjctCd != null ? !sbjctCd.equals(major.sbjctCd) : major.sbjctCd != null) return false;
        if (mjrTyp != null ? !mjrTyp.equals(major.mjrTyp) : major.mjrTyp != null) return false;
        if (mjrTypCd != null ? !mjrTypCd.equals(major.mjrTypCd) : major.mjrTypCd != null) return false;
        if (eduSys != null ? !eduSys.equals(major.eduSys) : major.eduSys != null) return false;
        if (degree != null ? !degree.equals(major.degree) : major.degree != null) return false;
        if (dgrNm != null ? !dgrNm.equals(major.dgrNm) : major.dgrNm != null) return false;
        if (ofclNum != null ? !ofclNum.equals(major.ofclNum) : major.ofclNum != null) return false;
        if (enhncNum != null ? !enhncNum.equals(major.enhncNum) : major.enhncNum != null) return false;
        if (grdtNum != null ? !grdtNum.equals(major.grdtNum) : major.grdtNum != null) return false;
        if (college != null ? !college.equals(major.college) : major.college != null) return false;
        if (planId != null ? !planId.equals(major.planId) : major.planId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = mjrId != null ? mjrId.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (level != null ? level.hashCode() : 0);
        result = 31 * result + (code != null ? code.hashCode() : 0);
        result = 31 * result + (sbjct != null ? sbjct.hashCode() : 0);
        result = 31 * result + (sbjctCd != null ? sbjctCd.hashCode() : 0);
        result = 31 * result + (mjrTyp != null ? mjrTyp.hashCode() : 0);
        result = 31 * result + (mjrTypCd != null ? mjrTypCd.hashCode() : 0);
        result = 31 * result + (eduSys != null ? eduSys.hashCode() : 0);
        result = 31 * result + (degree != null ? degree.hashCode() : 0);
        result = 31 * result + (dgrNm != null ? dgrNm.hashCode() : 0);
        result = 31 * result + (ofclNum != null ? ofclNum.hashCode() : 0);
        result = 31 * result + (enhncNum != null ? enhncNum.hashCode() : 0);
        result = 31 * result + (grdtNum != null ? grdtNum.hashCode() : 0);
        result = 31 * result + (college != null ? college.hashCode() : 0);
        result = 31 * result + (planId != null ? planId.hashCode() : 0);
        return result;
    }
}
