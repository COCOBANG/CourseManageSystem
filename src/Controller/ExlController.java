package Controller;

import DAO.HibernateUtil;

import DTO.FileInfo;
import DTO.Plan.PlnMjrInfo;
import DTO.Response.Error;
import DTO.Response.Response;
import DTO.Result.FileResult;

import Service.File.ExlReader;
import Service.File.ExlWriter;
import Service.PlanService;

import jxl.write.WriteException;

import org.hibernate.Session;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.*;

import java.util.List;
import java.util.Iterator;

/**
 * Created by Jaho on 2017/5/29.
 * Excel相关的控制
 */

@CrossOrigin
@RestController
@RequestMapping("/exl")
public class ExlController {

    @RequestMapping(value = "/upload",method = RequestMethod.POST)
    public Response upload(HttpServletRequest request){

        String filePath = this.getFilePath(request,false);
        // 获取上下文信息
        ServletContext context = request.getSession().getServletContext();
        // 获取文件解析器
        CommonsMultipartResolver resolver = new CommonsMultipartResolver(context);

        String fullFilePath = "";

        // 判断是否是文件
        if(resolver.isMultipart(request)){
            // 文件转换
            MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
            // 获取文件名称
            Iterator<String> iterator = multiRequest.getFileNames();

            if(iterator == null)
                return FileResult.FILE_UP_ERROR;

            // 处理文件
            while (iterator.hasNext()){
                // 处理文件
                MultipartFile file = multiRequest.getFile(iterator.next());
                String fileName = file.getOriginalFilename();
                fullFilePath = filePath + fileName;
                File newFile = new File(fullFilePath);
                try {
                    file.transferTo(newFile);
                } catch (IOException e) {
                    e.printStackTrace();
                    // 返回文件存储错误
                    return FileResult.FILE_UP_ERROR;
                }
            }
        }else
            return FileResult.FORM_TYPE_ERROR;

        // 开始读取文件信息
        Session session = HibernateUtil.getSession();
        ExlReader exlReader = new ExlReader(session);
        exlReader.openExlFile(fullFilePath);

        List<FileInfo> fileInfos = exlReader.readExlFile();
        Response< List<FileInfo> > response = new Response<List<FileInfo>>();
        response.setData(fileInfos);

        if(fileInfos == null)
            response.setError(new Error(1,"系统未能识别此类型文件"));
        else
            response.setError(new Error(0,"上传成功"));

        // 删除此文件
        File tempFile = new File(fullFilePath);
        if(tempFile.exists())
            tempFile.delete();

        // 关闭回话
        session.close();
        return response;
    }

    @RequestMapping(value = "/download/levelMajor",method = RequestMethod.GET)
    public Response downloadLevelMajorExl(HttpServletRequest request,HttpServletResponse response) {

        String filePath = this.getFilePath(request,true);
        Session session = HibernateUtil.getSession();

        try {
            ExlWriter writer = new ExlWriter(session);
            String fileName = "层次开设专业数量统计表";
            writer.exportExcelOfLevelMajor(filePath,fileName);
            // 关闭会话
            session.close();
            return this.download(response,filePath+fileName);
        } catch (WriteException e) {
            // 关闭会话
            session.close();
            return FileResult.FILE_DOWN_ERROR;
        }

    }


    @RequestMapping(value = "/download/majorLesson",method = RequestMethod.GET)
    public Response downloadMajorLessonExl(HttpServletRequest request,HttpServletResponse response) {

        String filePath = this.getFilePath(request,true);
        Session session = HibernateUtil.getSession();

        try {
            ExlWriter writer = new ExlWriter(session);
            String fileName = "专业开设课程数量统计表";;
            writer.exportExcelOfMajorLesson(filePath,fileName);
            // 关闭会话
            session.close();
            return this.download(response,filePath+fileName);
        } catch (WriteException e) {
            // 关闭会话
            session.close();
            return FileResult.FILE_DOWN_ERROR;
        }

    }

    @RequestMapping(value = "/download/degreeLesson",method = RequestMethod.GET)
    public Response downloadDegreeLessonExl(HttpServletRequest request,HttpServletResponse response,@RequestParam("types") List<String> types) {

        String filePath = this.getFilePath(request,true);
        Session session = HibernateUtil.getSession();

        try {
            ExlWriter writer = new ExlWriter(session);
            String fileName = "专业开设课程数量统计表";
            writer.exportExcelOfDegreeLesson(filePath,types);
            // 关闭会话
            session.close();
            return this.download(response,filePath+fileName);
        } catch (WriteException e) {
            // 关闭会话
            session.close();
            return FileResult.FILE_DOWN_ERROR;
        }

    }

    @RequestMapping(value = "/download/majorPlan",method = RequestMethod.GET)
    public Response downloadMajorPlanExl(HttpServletRequest request,HttpServletResponse response,@RequestParam("year") String year,@RequestParam("season") String season) {

        String filePath = this.getFilePath(request,true);
        Session session = HibernateUtil.getSession();

        try {
            ExlWriter writer = new ExlWriter(session);
            String fileName = "教学计划(按专业)导出表";
            PlanService planService = new PlanService(session);
            List<PlnMjrInfo> mjrInfos = planService.getLsnsInMjrOfPlan(year,season);
            writer.exportExcelOfMajorPlan(filePath,fileName,mjrInfos);
            // 关闭会话
            session.close();
            return this.download(response,filePath+fileName);
        } catch (WriteException e) {
            // 关闭会话
            session.close();
            return FileResult.FILE_DOWN_ERROR;
        }

    }


    public Response download(HttpServletResponse response, String fileUrl) {

        // Set Request and Response
        response.setCharacterEncoding("utf-8");
        response.setContentType("text/JavaScript");
        response.setHeader("Access-Control-Allow-Origin","*");
        response.setContentType("multipart/form-data");
        response.setHeader("Content-Disposition", "attachment;fileName="
                + fileUrl);

        try {

            FileInputStream in = new FileInputStream(fileUrl);
            OutputStream outputStream = response.getOutputStream();
            byte buffer[] = new byte[1024];
            int len;

            while ((len = in.read(buffer)) > 0) {
                outputStream.write(buffer, 0, len);
                in.close();
                outputStream.close();
            }
        }catch(FileNotFoundException e){
            e.printStackTrace();
            return FileResult.NOT_FOUND_ERROR;
        } catch(IOException e){
            e.printStackTrace();
            return FileResult.FILE_DOWN_ERROR;
        }

        return FileResult.FILE_DOWN_SUCCESS;

    }


    private String getFilePath(HttpServletRequest request,boolean Download){

        // 获取上下文信息
        ServletContext context = request.getSession().getServletContext();
        // 用户目录
        String filePath;
        if(Download)
            filePath  = context.getRealPath("/WEB-INF/Download/");
        else
            filePath  = context.getRealPath("/WEB-INF/Upload/");
        // 完整文件路径
        File dir = new File(filePath);

        // 生成目录
        if(!dir.exists())
            dir.mkdirs();

        return filePath;
    }

}
