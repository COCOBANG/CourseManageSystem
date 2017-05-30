package DAO;

import Entity.Record;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;

/**
 * Created by Jaho on 2017/5/29.
 */
public class RecordDao {
    private Session session = null;

    public RecordDao(Session session){
        this.session = session;
    }

    public void insert(String time,String content){
        Record record = new Record();
        record.setTime(time);
        record.setContent(content);
        session.save(record);
        return;
    }

    public List<Record> getRecords(){
        String hql = "from Record ";
        Query query = session.createQuery(hql);
        List<Record> records = (List<Record>) query.list();
        return records;
    }

}
