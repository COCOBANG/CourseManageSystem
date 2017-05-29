package Entity;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * Created by Jaho on 2017/5/29.
 */
@Entity
public class Record {
    private String rTime;
    private String record;

    @Basic
    @Column(name = "r_time", nullable = false, length = 20)
    public String getrTime() {
        return rTime;
    }

    public void setrTime(String rTime) {
        this.rTime = rTime;
    }

    @Basic
    @Column(name = "record", nullable = true, length = -1)
    public String getRecord() {
        return record;
    }

    public void setRecord(String record) {
        this.record = record;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Record record1 = (Record) o;

        if (rTime != null ? !rTime.equals(record1.rTime) : record1.rTime != null) return false;
        if (record != null ? !record.equals(record1.record) : record1.record != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = rTime != null ? rTime.hashCode() : 0;
        result = 31 * result + (record != null ? record.hashCode() : 0);
        return result;
    }
}
