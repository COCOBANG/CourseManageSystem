package Dao;

import Entity.Memo;
import org.hibernate.Session;

/**
 * Created by Jaho on 2017/5/29.
 */
public class MemoDao {

    private Session session = null;

    public MemoDao(Session session){
        this.session = session;
    }

    public void insert(String time,String content){
        Memo memo = new Memo();
        memo.setTime(time);
        memo.setContent(content);
        session.save(memo);
        return;
    }

}
