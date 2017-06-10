package Entity;

import javax.persistence.*;

/**
 * Created by Jaho on 2017/5/31.
 */
@Entity
@Table(name = "college_major", schema = "CMS", catalog = "")
public class CollegeMajor {
    private String clgName;
    private String mjrName;
    private String mjrLevel;
    private Integer id;

    @Basic
    @Column(name = "clg_name", nullable = false, length = 50)
    public String getClgName() {
        return clgName;
    }

    public void setClgName(String clgName) {
        this.clgName = clgName;
    }

    @Basic
    @Column(name = "mjr_name", nullable = false, length = 50)
    public String getMjrName() {
        return mjrName;
    }

    public void setMjrName(String mjrName) {
        this.mjrName = mjrName;
    }

    @Basic
    @Column(name = "mjr_level", nullable = false, length = 20)
    public String getMjrLevel() {
        return mjrLevel;
    }

    public void setMjrLevel(String mjrLevel) {
        this.mjrLevel = mjrLevel;
    }

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CollegeMajor that = (CollegeMajor) o;

        if (clgName != null ? !clgName.equals(that.clgName) : that.clgName != null) return false;
        if (mjrName != null ? !mjrName.equals(that.mjrName) : that.mjrName != null) return false;
        if (mjrLevel != null ? !mjrLevel.equals(that.mjrLevel) : that.mjrLevel != null) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = clgName != null ? clgName.hashCode() : 0;
        result = 31 * result + (mjrName != null ? mjrName.hashCode() : 0);
        result = 31 * result + (mjrLevel != null ? mjrLevel.hashCode() : 0);
        result = 31 * result + (id != null ? id.hashCode() : 0);
        return result;
    }
}
