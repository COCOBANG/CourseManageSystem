package Controller;

import DAO.HibernateUtil;
import DAO.RecordDao;
import DTO.Response.Error;
import DTO.Response.Response;

import Entity.Record;
import org.hibernate.Session;

import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by Jaho on 2017/5/29.
 * 获取排课过程中的修改记录
 */

@CrossOrigin
@RestController
@RequestMapping("/record")
public class RecordController {

    @RequestMapping(value = "/get",method = RequestMethod.GET)
    public Response getRecords(){
        Session session = HibernateUtil.getSession();
        RecordDao recordDao = new RecordDao(session);
        List<Record> records = recordDao.getRecords();
        Response<List<Record>> response = new Response<>();
        response.setData(records);

        if(records == null)
            response.setError(new Error(1,"获取不到修改记录"));
        else
            response.setError(new Error(0,"获取成功"));
        
        // 关闭会话
        session.close();

        return response;
    }

    @RequestMapping(value = "insert",method = RequestMethod.POST)
    public Response insertRecords(@RequestParam("records") List<Record> records){
        Session session = HibernateUtil.getSession();
        RecordDao recordDao = new RecordDao(session);

        for(Record record:records)
            recordDao.insert(record);

        // 关闭会话
        session.close();

        return new Response(new Error(0,"已保存记录"));
    }

}
