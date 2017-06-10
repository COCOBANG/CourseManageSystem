package Controller;

import DAO.HibernateUtil;
import DTO.Plan.PlnLsnInfo;
import DTO.Plan.PlnMjrInfo;
import DTO.Plan.RsLesson;
import DTO.Response.Error;
import DTO.Response.Response;
import Service.PlanService;
import org.hibernate.Session;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by Jaho on 2017/6/8.
 * 教学计划统计相关
 */

@CrossOrigin
@RestController
@RequestMapping("/plan")
public class PlanController {

    @RequestMapping(value = "/type",method = RequestMethod.GET)
    public Response getLessonsInType(@RequestParam("year") String year,@RequestParam("season") String season){

        Session session = HibernateUtil.getSession();
        PlanService planService = new PlanService(session);

        Response<List<PlnLsnInfo>> response = new Response<>();
        List<PlnLsnInfo> plnLsnInfos = planService.getLsnsInTypeOfPln(year,season);
        response.setData(plnLsnInfos);
        // 关闭会话
        session.close();

        if (plnLsnInfos == null)
            response.setError(new Error(1,"获取信息失败"));
        else
            response.setError(new Error(0,"获取成功"));

        return response;
    }

    @RequestMapping(value = "/major",method = RequestMethod.GET)
    public Response getLessonsInMajor(@RequestParam("year") String year,@RequestParam("season") String season){

        Session session = HibernateUtil.getSession();
        PlanService planService = new PlanService(session);

        Response<List<PlnMjrInfo>> response = new Response<>();
        List<PlnMjrInfo> plnMjrInfos = planService.getLsnsInMjrOfPlan(year,season);
        response.setData(plnMjrInfos);
        // 关闭会话
        session.close();

        if (plnMjrInfos == null)
            response.setError(new Error(1,"获取信息失败"));
        else
            response.setError(new Error(0,"获取成功"));


        return response;
    }

    @RequestMapping(value = "/resuse",method = RequestMethod.GET)
    public Response getResuseLesson(@RequestParam("year") String year,@RequestParam("season") String season){

        Session session = HibernateUtil.getSession();
        PlanService planService = new PlanService(session);

        Response<List<RsLesson>> response = new Response<>();
        List<RsLesson> rsLessons = planService.getReuseLessons(year,season);
        response.setData(rsLessons);
        // 关闭会话
        session.close();

        if (rsLessons == null)
            response.setError(new Error(1,"获取信息失败"));
        else
            response.setError(new Error(0,"获取成功"));

        return response;
    }
}
