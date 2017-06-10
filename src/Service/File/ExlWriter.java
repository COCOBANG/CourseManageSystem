package Service.File;

import DAO.PlanDao;
import DAO.MajorDao;
import DAO.LessonDao;

import DTO.MajorInfo;
import DTO.Plan.PlnMjrInfo;

import Entity.Plan;
import Entity.Major;
import Entity.Lesson;
import Entity.MajorLesson;

import Service.UtilService;
import Service.YearService;

import jxl.write.*;
import jxl.Workbook;
import jxl.format.Border;
import jxl.format.Alignment;
import org.hibernate.Session;
import org.hibernate.query.Query;
import jxl.format.BorderLineStyle;
import jxl.format.VerticalAlignment;

import java.io.File;
import java.util.*;

/**
 * Created by Jaho on 2017/6/8.
 * 生成所需要的Exl
 */

public class ExlWriter {

    private Session session = null;
    // 导出Excel的格式
    private WritableCellFormat titleFormat;
    private WritableCellFormat contentRightFormat;
    private WritableCellFormat contentCenterFormat;

    public ExlWriter(Session session) throws WriteException {
        this.session = session;

        // Set Format
        WritableFont TitleFont = new WritableFont(WritableFont.ARIAL, 20);
        WritableFont NormalFont = new WritableFont(WritableFont.ARIAL, 12);

        // Content-right-Format
        contentRightFormat = new WritableCellFormat(NormalFont);
        contentRightFormat.setBorder(Border.ALL, BorderLineStyle.THIN);
        contentRightFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
        contentRightFormat.setAlignment(Alignment.RIGHT);
        contentRightFormat.setWrap(false);

        // Title-Center-Format
        titleFormat = new WritableCellFormat(TitleFont);
        titleFormat.setBorder(Border.ALL, BorderLineStyle.THIN); // 线条
        titleFormat.setVerticalAlignment(VerticalAlignment.CENTRE); // 文字垂直对齐
        titleFormat.setAlignment(Alignment.CENTRE); // 文字水平对齐
        titleFormat.setWrap(false); // 文字是否换行

        // Content-Center-Format
        contentCenterFormat = new WritableCellFormat(NormalFont);
        contentCenterFormat.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN);
        contentCenterFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
        contentCenterFormat.setAlignment(Alignment.CENTRE);
        contentCenterFormat.setWrap(false);


    }

    public void exportExcelOfLevelMajor (String filePath,String fileName) {

        Map<String,Map<String,List<String>>> exportYearLevelMap;
        exportYearLevelMap = new YearService(session).getMjrsInLevel();

        // Export data to Excel

        String[] itemTitle = {"层次","专业名称","数量"};
        this.exportExcelByData(fileName,itemTitle,exportYearLevelMap,filePath);

    }

    public void exportExcelOfMajorLesson(String filePath,String fileName) {

        Map<String,Map<String,List<String>>> exportYearMajorMap;
        exportYearMajorMap = new YearService(session).getLsnsInMajor();

        // Export data to Excel
        String[] itemTitle = {"专业名称","课程名称","数量"};
        this.exportExcelByData(fileName,itemTitle,exportYearMajorMap,filePath);

    }

    private int exportExcelByData(String excelTitle,String[] itemTitle,Map<String,Map<String,List<String>>> contentMap,String filePath ){
        // Write book for write excel
        WritableWorkbook workbook;
        try {
            workbook = Workbook.createWorkbook(new File(filePath + excelTitle + ".xls"));

            // Create Sheet and fill
            String [] sheetName = contentMap.keySet().toArray(new String[]{});
            int numOfColumn = itemTitle.length;
            // create A sheet each loop
            for (int i = 0; i < sheetName.length; i++) {
                WritableSheet ws = workbook.createSheet(sheetName[i],0);
                // set the title of sheet
                Label sheetTitle = new Label(0,0,sheetName[i] + excelTitle,titleFormat);
                ws.mergeCells(0,0,numOfColumn - 1,0);
                ws.addCell(sheetTitle);

                // set the item title of sheet
                for( int j = 0; j < numOfColumn; ++j){
                    Label item = new Label(j,1,itemTitle[j],titleFormat);
                    ws.addCell(item);
                }

                // get content map of the sheet
                Map<String,List<String>> contentOfSheetMap = contentMap.get(sheetName[i]);

                int rowFlag = 2;
                String[] keyOfMap = contentOfSheetMap.keySet().toArray(new String[]{});
                for (int j = 0; j < keyOfMap.length; ++j) {

                    String key = keyOfMap[j];
                    List<String> contentOfSheetList = contentOfSheetMap.get(key);
                    int rowOfList = contentOfSheetList.size();

                    Label lineTitle = new Label(0,rowFlag,key,titleFormat);
                    Label lineNum = new Label(2,rowFlag,""+rowOfList,titleFormat);
                    ws.addCell(lineNum);
                    ws.addCell(lineTitle);

                    int widthOfList = rowFlag + rowOfList - 1;
                    if (rowOfList == 0){
                        widthOfList = rowFlag;
                    }
                    ws.mergeCells(0,rowFlag,0,widthOfList);
                    ws.mergeCells(2,rowFlag,2,widthOfList);

                    for (int k = 0; k < rowOfList; k++) {
                        Label lineContent = new Label(1,rowFlag + k,contentOfSheetList.get(k),contentCenterFormat);
                        ws.addCell(lineContent);
                    }
                    rowFlag = widthOfList + 1;
                }
            }
            // 写入Excel工作表
            workbook.write();
            // 关闭Excel工作薄对象
            workbook.close();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return 0;
    }

    public void exportExcelOfMajorPlan(String filePath, String fileName,List<PlnMjrInfo> plnMjrInfos){
        // Write book for write excel
        WritableWorkbook workbook;
        try{
            workbook = Workbook.createWorkbook(new File(filePath + fileName + ".xls"));

            // Create Sheet and fill
            String [] sheetName = new String[plnMjrInfos.size()];
            for(int sheetFlag = 0; sheetFlag < sheetName.length; ++sheetFlag){
                sheetName[sheetFlag] = plnMjrInfos.get(sheetFlag).getMajorName();
            }
            String[] itemTitle = {"专业名称","课程名称","课程代码","学分","学时","学期","测验方式","开闭卷"};
            int numOfColumn = itemTitle.length;
            // create A sheet each loop
            for (int i = 0; i < sheetName.length; i++) {
                WritableSheet ws = workbook.createSheet(sheetName[i],0);
                // set the title of sheet
                Label sheetTitle = new Label(0,0,sheetName[i] + fileName,titleFormat);
                ws.mergeCells(0,0,numOfColumn - 1,0);
                ws.addCell(sheetTitle);

                // set the item title of sheet
                for( int j = 0; j < numOfColumn; ++j){
                    Label item = new Label(j,1,itemTitle[j],titleFormat);
                    ws.addCell(item);
                }

                // get content map of the sheet
                PlnMjrInfo plnMjrInfo = plnMjrInfos.get(i);
                int rowFlag = 2;
                List<Lesson> lessons = plnMjrInfo.getLessons();

                int rowOfList = lessons.size();
                for (int j = 0; j < rowOfList; ++j) {

                    Lesson lesson = lessons.get(j);
                    Label majorName = new Label(0, rowFlag + j, plnMjrInfo.getMajorName(), titleFormat);
                    Label lessonName = new Label(1, rowFlag + j, lesson.getName(), titleFormat);
                    Label lessonCode = new Label(2, rowFlag + j, lesson.getCode(), titleFormat);
                    Label lessonCredit = new Label(3, rowFlag + j, lesson.getCredit(), titleFormat);
                    Label lessonCreditHours = new Label(4, rowFlag + j, lesson.getCrdtHrs(), titleFormat);
                    Label lessonSemester = new Label(5, rowFlag + j, lesson.getSemester(), titleFormat);
                    Label lessonExam = new Label(6, rowFlag + j, lesson.getExamine(), titleFormat);
                    Label lessonRemark = new Label(7, rowFlag + j, lesson.getRemark(), titleFormat);

                    ws.addCell(majorName);
                    ws.addCell(lessonName);
                    ws.addCell(lessonCode);
                    ws.addCell(lessonCredit);
                    ws.addCell(lessonCreditHours);
                    ws.addCell(lessonSemester);
                    ws.addCell(lessonExam);
                    ws.addCell(lessonRemark);
                }

                ws.mergeCells(0,rowFlag,0,rowFlag + rowOfList -1);
                rowFlag += rowOfList;

            }
            // 写入Excel工作表
            workbook.write();
            // 关闭Excel工作薄对象
            workbook.close();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }


    public void exportExcelOfDegreeLesson(String filePath,List<String> types) {
        Map<String, Map<String, List<MajorInfo>>> contentMap = this.getDegreeLessonMap(types);
        // Write book for write excel
        WritableWorkbook workbook;
        int rowFlag = 2;
        int rowLocation = 2;
        int planLocation = 2;
        int collegeLocation = 2;
        String[] planNameArray = contentMap.keySet().toArray(new String[]{});
        String[] itemTitle = {"教学计划","学院名称","专业名称","专业学位","主干课名称"};
        int numOfColumn = itemTitle.length;

        try {
            workbook = Workbook.createWorkbook(new File(filePath + "学位课程一览表.xls"));
            // Set Format
            WritableSheet ws = workbook.createSheet("一览表",0);

            // set the title of sheet
            Label sheetTitle = new Label(0,0,"学位课程一览表",titleFormat);
            ws.mergeCells(0,0,numOfColumn - 1,0);
            ws.addCell(sheetTitle);

            // set the item title of sheet
            for( int i = 0; i < numOfColumn; ++i){
                Label item = new Label(i,1,itemTitle[i],titleFormat);
                ws.addCell(item);
            }

            for(int i = 0; i < planNameArray.length; ++i){

                Map<String,List<MajorInfo>> collegeMajorMap = contentMap.get(planNameArray[i]);
                String[] collegeNameArry = collegeMajorMap.keySet().toArray(new String[]{});
                for(int j = 0; j < collegeNameArry.length; ++j){
                    List<MajorInfo> majorInfoList = collegeMajorMap.get(collegeNameArry[j]);

                    for(MajorInfo majorInfo : majorInfoList){

                        for(String lessonName: majorInfo.getCoreLessons()){
                            // add the coreLesson to excel
                            Label coreLesson = new Label(4,rowFlag++,lessonName,contentCenterFormat);
                            ws.addCell(coreLesson);
                        }

                        // add the major_degree to excel
                        Label degree = new Label(3,rowLocation, majorInfo.getDegree(),contentCenterFormat);
                        ws.mergeCells(3,rowLocation,3,rowFlag - 1);
                        ws.addCell(degree);

                        // add the major_name to excel
                        Label majorName = new Label(2,rowLocation, majorInfo.getName(),contentCenterFormat);
                        ws.mergeCells(2,rowLocation,2,rowFlag - 1);
                        ws.addCell(majorName );

                        rowLocation = rowFlag;

                    }

                    // add the college_name to excel
                    Label college = new Label(1,collegeLocation,collegeNameArry[j],contentCenterFormat);
                    ws.mergeCells(1,collegeLocation,1,rowFlag - 1);
                    ws.addCell(college);

                    collegeLocation = rowFlag;

                }

                // add the plan_name to excel
                Label plan = new Label(0,planLocation,planNameArray[i],contentCenterFormat);
                ws.mergeCells(0,planLocation,0,rowFlag - 1);
                ws.addCell(plan);

                planLocation = rowFlag;

            }

            // 写入Excel工作表
            workbook.write();
            // 关闭Excel工作薄对象
            workbook.close();

        }catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }


    public Map<String,Map<String,List<MajorInfo>>> getDegreeLessonMap (List<String> types){

        MajorDao majorDao = new MajorDao(session);
        List<String> yearList = new UtilService(session).getYears();
        Map<String,Map<String,List<MajorInfo>>>  exportMap = new LinkedHashMap<>();

        for (String year: yearList) {
            // 获取这一年的所有教学计划
            List<Plan> plans = new PlanDao(session).getPlansOfYear(year);

            // 遍历教学计划进行统计
            for(Plan plan:plans){

                // 获取该教学计划所有相关学院
                Map<String,List<MajorInfo>> collegeMajorMap = new HashMap<>();
                List<String> colleges = majorDao.getColleges(plan.getPlnId());

                if(colleges.size() == 0)
                    continue;

                // 遍历学院查询所有相关专业
                for(String college:colleges){
                    List<MajorInfo> majorInfos = new ArrayList<>();
                    List<Major> majors = majorDao.getMajors(college,plan.getPlnId());

                    // 遍历专业获取所有相关课程
                    for(Major major:majors){
                        MajorInfo majorInfo = new MajorInfo();
                        majorInfo.setName(major.getName());
                        majorInfo.setLevel(major.getLevel());
                        majorInfo.setDegree(major.getDegree());
                        majorInfo.setCoreLessons(this.getCoreLessonsInMajor(major.getMjrId(),types));
                        majorInfos.add(majorInfo);
                    }
                    // 存储此学院-专业信息
                    collegeMajorMap.put(college, majorInfos);
                }

                exportMap.put(plan.getYear() + "-" + plan.getSeason(),collegeMajorMap);
            }

        }

        return  exportMap;

    }

    // MARK:待测试
    // 获取核心课程
    public List<String> getCoreLessonsInMajor(int majorId,List<String> types){

        String hql = "from MajorLesson where mjrId = :mjrId  and lsnType in :types";
        Query query = session.createQuery(hql);
        query.setParameter("types", types);
        query.setParameter("mjrId",majorId);
        List<MajorLesson> majorLessons = query.list();
        LessonDao lessonDao = new LessonDao(session);
        List<String> resultList = new ArrayList<>();

        // 遍历所有专业/课程关系获取课程名称
        for(MajorLesson majorLesson:majorLessons)
            resultList.add(lessonDao.getLessonById(majorLesson.getLsnId()).getName());

        return resultList;

    }


}
