package Controller;

import DAO.MemoDao;
import DAO.HibernateUtil;

import DTO.Response.Error;
import DTO.Response.Response;

import Entity.Memo;

import org.hibernate.Session;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by Jaho on 2017/5/29.
 * 备忘记录相关
 */

@CrossOrigin
@RestController
@RequestMapping("/memo")
public class MemoController {

    @RequestMapping(value = "/get",method = RequestMethod.GET)
    public Response getRecords(){

        Session session = HibernateUtil.getSession();
        MemoDao memoDao = new MemoDao(session);

        List<Memo> memos = memoDao.getMemos();
        Response<List<Memo>> response = new Response<>();
        response.setData(memos);

        if(memos == null)
            response.setError(new Error(1,"获取不到备忘记录"));
        else
            response.setError(new Error(0,"获取成功"));

        // 关闭会话
        session.close();

        return response;
    }

    @RequestMapping(value = "insert",method = RequestMethod.POST)
    public Response insertRecords(@RequestParam("memos") List<Memo> memos){

        Session session = HibernateUtil.getSession();
        MemoDao memoDao = new MemoDao(session);

        for(Memo memo:memos)
            memoDao.insert(memo);

        // 关闭会话
        session.close();

        return new Response(new Error(0,"已保存记录"));
    }

}
