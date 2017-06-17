package Controller;

import DAO.HibernateUtil;
import DAO.LsnCodeDao;
import DTO.CMRelation;
import DTO.Response.Error;
import DTO.Response.Response;
import Service.RelationService;
import Service.UtilService;
import org.hibernate.Session;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Jaho on 2017/6/10.
 * 学院-专业对应关系
 * 禁用课程代码
 * 选择框的信息
 */

@CrossOrigin
@RestController
@RequestMapping("/set")
public class SetController {

    @RequestMapping(value = "/relation", method = RequestMethod.GET)
    public Response setRelation(@RequestParam("relations")List<CMRelation> relations) {

        Session session = HibernateUtil.getSession();
        RelationService relationService = new RelationService(session);
        relationService.insertRelations(relations);

        // 关闭会话
        session.close();

        return new Response(new Error(0,"已保存记录"));

    }

    @RequestMapping(value = "/relation/get", method = RequestMethod.GET)
    public Response getRelation() {

        Session session = HibernateUtil.getSession();
        RelationService relationService = new RelationService(session);

        List<CMRelation> relations = relationService.getRelations();
        Response<List<CMRelation>> response = new Response<>();
        response.setData(relations);


        if(relations == null)
            response.setError(new Error(1,"获取不到对应关系"));
        else
            response.setError(new Error(0,"获取成功"));

        // 关闭会话
        session.close();

        return response;

    }



    @RequestMapping(value = "/code", method = RequestMethod.GET)
    public Response setCode(@RequestParam("codes")List<String> codes) {

        Session session = HibernateUtil.getSession();
        LsnCodeDao lsnCodeDao = new LsnCodeDao(session);
        lsnCodeDao.storeCodes(codes);

        // 关闭会话
        session.close();

        return new Response(new Error(0,"已保存记录"));

    }

    @RequestMapping(value = "/code/get", method = RequestMethod.GET)
    public Response getCode() {

        Session session = HibernateUtil.getSession();
        LsnCodeDao lsnCodeDao = new LsnCodeDao(session);

        List<String> codes = lsnCodeDao.getCodes();
        Response<List<String>> response = new Response<>();
        response.setData(codes);


        if(codes == null)
            response.setError(new Error(1,"获取不到已禁用的课程代码，可能为空"));
        else
            response.setError(new Error(0,"获取成功"));

        // 关闭会话
        session.close();

        return response;

    }

    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public Response getSets() {

        Session session = HibernateUtil.getSession();
        UtilService utilService = new UtilService(session);
        Map<String,List<String>> result = new HashMap<>();
        result.put("year",utilService.getYears());
        result.put("level",utilService.getLevels());
        result.put("type",utilService.getLsnTypes());
        result.put("major",utilService.getMjrNames());
        result.put("college",utilService.getColleges());
        Response<Map<String,List<String>>> response = new Response<>();
        response.setData(result);


        if(result == null)
            response.setError(new Error(1,"获取不到选择数据，数据可能为空"));
        else
            response.setError(new Error(0,"获取成功"));

        // 关闭会话
        session.close();

        return response;

    }
}
