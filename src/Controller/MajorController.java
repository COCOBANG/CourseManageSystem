package Controller;

import DAO.HibernateUtil;
import DTO.Arrange;
import DTO.LessonInfo;
import DTO.MajorSearch;
import DTO.Response.Error;
import DTO.Response.Response;
import Service.MajorService;
import org.hibernate.Session;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by Jaho on 2017/5/29.
 * 获取专业相关
 */

@CrossOrigin
@RestController
@RequestMapping("/major")
public class MajorController {

    @RequestMapping(value = "compare", method = RequestMethod.POST)
    public Response compareLesson(@RequestParam("oldMajor") MajorSearch oldMajor, @RequestParam("newMajor") MajorSearch newMajor){

        Session session = HibernateUtil.getSession();
        MajorService majorService = new MajorService(session);

        List<String> cmprInfos = majorService.compare2Major(oldMajor,newMajor);

        Response<List<String> > response = new Response<>();
        response.setData(cmprInfos);

        // 关闭会话
        session.close();

        if (cmprInfos == null)
            response.setError(new Error(1,"获取信息失败"));
        else
            response.setError(new Error(0,"对比成功"));

        return response;

    }

}
