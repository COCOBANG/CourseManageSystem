package DAO;

import Entity.Memo;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;

/**
 * Created by Jaho on 2017/5/29.
 */
public class MemoDao {

    private Session session = null;

    public MemoDao(Session session){
        this.session = session;
    }

    public void insert(Memo memo){
        session.save(memo);
        return;
    }

    public List<Memo> getMemos(){
        String hql = "from Memo";
        Query query = session.createQuery(hql);
        List<Memo> memos = (List<Memo>) query.list();
        return memos;
    }

}
