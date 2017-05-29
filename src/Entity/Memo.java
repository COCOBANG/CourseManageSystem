package Entity;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * Created by Jaho on 2017/5/29.
 */
@Entity
public class Memo {
    private String mTime;
    private String memo;

    @Basic
    @Column(name = "m_time", nullable = false, length = 20)
    public String getmTime() {
        return mTime;
    }

    public void setmTime(String mTime) {
        this.mTime = mTime;
    }

    @Basic
    @Column(name = "memo", nullable = true, length = -1)
    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Memo memo1 = (Memo) o;

        if (mTime != null ? !mTime.equals(memo1.mTime) : memo1.mTime != null) return false;
        if (memo != null ? !memo.equals(memo1.memo) : memo1.memo != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = mTime != null ? mTime.hashCode() : 0;
        result = 31 * result + (memo != null ? memo.hashCode() : 0);
        return result;
    }
}
