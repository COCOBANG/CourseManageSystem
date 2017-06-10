package Controller;

import DAO.HibernateUtil;
import DTO.Arrange;
import DTO.LessonInfo;
import DTO.LsnCmprInfo;
import DTO.MajorSearch;
import DTO.Response.Error;
import DTO.Response.Response;
import Service.MajorService;
import org.hibernate.Session;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by Jaho on 2017/5/29.
 * 课程相关
 */

@CrossOrigin
@RestController
@RequestMapping("/lesson")
public class LessonController {

    @RequestMapping(value = "/get", method = RequestMethod.POST)
    public Response arrangeLesson(@RequestBody MajorSearch major){

        Session session = HibernateUtil.getSession();
        MajorService majorService = new MajorService(session);

        List<LessonInfo> lessonInfos = majorService.getLsnInfosOfMajor(major);

        Response<List<LessonInfo>> response = new Response<>();
        response.setData(lessonInfos);

        // 关闭会话
        session.close();

        if (lessonInfos == null)
            response.setError(new Error(1,"获取信息失败"));
        else
            response.setError(new Error(0,"获取成功"));

        return response;
    }

    @RequestMapping(value = "/compare", method = RequestMethod.POST)
    public Response compareLesson(@RequestParam("oldMajor") MajorSearch oldMajor, @RequestParam("newMajor") MajorSearch newMajor){

        Session session = HibernateUtil.getSession();
        MajorService majorService = new MajorService(session);

        List<LsnCmprInfo> lsnCmprInfos = majorService.CmprLsnsBy2Major(oldMajor,newMajor);

        Response<List<LsnCmprInfo>> response = new Response<>();
        response.setData(lsnCmprInfos);

        // 关闭会话
        session.close();

        if (lsnCmprInfos == null)
            response.setError(new Error(1,"获取信息失败"));
        else
            response.setError(new Error(0,"对比成功"));

        return response;

    }

    @RequestMapping(value = "/arrange", method = RequestMethod.POST)
    public Response storeLesson(@RequestBody Arrange arrange) {

        Session session = HibernateUtil.getSession();
        MajorService majorService = new MajorService(session);
        majorService.storeMajorLesson(arrange.getMajor(),arrange.getLessons(),arrange.getYear());

        // 关闭会话
        session.close();

        return new Response(new Error(0,"已保存记录"));
    }

}
