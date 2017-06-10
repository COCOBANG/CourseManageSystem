package Controller;

import DAO.HibernateUtil;
import DTO.ClgMajorInfo;
import DTO.Response.Error;
import DTO.Response.Response;
import Service.CollegeService;
import org.hibernate.Session;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by Jaho on 2017/6/10.
 * 学院相关
 */

@CrossOrigin
@RestController
@RequestMapping("/college")
public class CollegeController {

    /*
     * @param name: 学院名称获取学院下所有专业
     */
    @RequestMapping(value = "/get/majors", method = RequestMethod.GET)
    public Response getInCollege(@RequestParam("name") String name) {

        Session session = HibernateUtil.getSession();
        CollegeService clgService = new CollegeService(session);

        List<ClgMajorInfo> clgMajorInfos = clgService.getMajorsOfCollege(name);

        Response<List<ClgMajorInfo>> response = new Response<>();
        response.setData(clgMajorInfos);

        // 关闭会话
        session.close();

        if (clgMajorInfos == null)
            response.setError(new Error(1,"获取信息失败，数据可能为空"));
        else
            response.setError(new Error(0,"获取成功"));

        return response;
    }
}
