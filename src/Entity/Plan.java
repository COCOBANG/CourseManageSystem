package Entity;

import javax.persistence.*;

/**
 * Created by Jaho on 2017/5/29.
 */
@Entity
public class Plan {
    private Integer plnId;
    private String year;
    private String season;

    @Id
    @Column(name = "pln_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    public Integer getPlnId() {
        return plnId;
    }

    public void setPlnId(Integer plnId) {
        this.plnId = plnId;
    }

    @Basic
    @Column(name = "year", nullable = false, length = 20)
    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    @Basic
    @Column(name = "season", nullable = false, length = 20)
    public String getSeason() {
        return season;
    }

    public void setSeason(String season) {
        this.season = season;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Plan plan = (Plan) o;

        if (plnId != null ? !plnId.equals(plan.plnId) : plan.plnId != null) return false;
        if (year != null ? !year.equals(plan.year) : plan.year != null) return false;
        if (season != null ? !season.equals(plan.season) : plan.season != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = plnId != null ? plnId.hashCode() : 0;
        result = 31 * result + (year != null ? year.hashCode() : 0);
        result = 31 * result + (season != null ? season.hashCode() : 0);
        return result;
    }
}
