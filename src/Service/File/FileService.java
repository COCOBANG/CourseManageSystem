package Service.File;

import DAO.*;
import DTO.*;
import Entity.Lesson;

import Entity.Plan;
import Entity.Major;
import Entity.MajorLesson;

import Service.RelationService;
import Service.UtilService;
import Service.YearService;

import jxl.Cell;
import jxl.Range;
import jxl.Sheet;
import jxl.Workbook;
import jxl.format.Border;
import jxl.format.Alignment;
import jxl.format.BorderLineStyle;
import jxl.read.biff.BiffException;
import jxl.format.VerticalAlignment;
import jxl.write.*;

import org.hibernate.Session;
import org.hibernate.query.Query;

import java.io.*;
import java.util.*;

/**
 * Created by Jaho on 2017/2/28.
 */
public class FileService {

    private Sheet[] sheets = null;
    private Session session = null;
    private jxl.Workbook readWb = null;
    private String[] items = {"名称","类别","代码","学分","学时","考核","备注"};
    private Map<String,Integer> itemMap = new HashMap<String,Integer>();

    // 导出Excel的格式
    private WritableCellFormat titleFormat;
    private WritableCellFormat contentRightFormat;
    private WritableCellFormat contentCenterFormat;


    public FileService(Session session){
        this.session = session;
    }


    public jxl.Workbook openExlFile(String filePath){
        InputStream inputStream;
        try {
            //构建Workbook对象, 只读Workbook对象
            //直接从本地文件创建Workbook
            inputStream = new FileInputStream(filePath);
            readWb = Workbook.getWorkbook(inputStream);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (BiffException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        this.sheets = readWb.getSheets();

        return readWb;
    }

    public void closeFile(){
        readWb.close();
    }

    public List<FileInfo> readExlFile(){

        Sheet readSheet = sheets[0];
        //获取Sheet表中所包含的总列数
        int rsColumns = readSheet.getColumns();

        if(rsColumns == 12)
            return readExl(new r12File());
        else if(rsColumns == 9)
            return readExl(new r9File());
        else
            return null;

    }


    public List<FileInfo> readExl(FileType fileType){

        List<FileInfo> fileInfos = new ArrayList<FileInfo>();

        // 根据第一个Sheet设置表项和列数的对应Map:
        Sheet sheet = sheets[0];// Sheet名称,包括专业和层次信息
        int r = sheet.getRows();// 行数
        int itemRowNum = 0;// Item所在行
        int c = sheet.getColumns();// 列数
        List<String> itemRow = new ArrayList<>();// Item数据

        // 1.查找Item所在行
        for(int i = 0; i < r; ++ i){
            List<String> rowData = this.getRowData(r,i,sheet);
            if(rowData.get(c-1).length() > 0){
                itemRow = rowData;
                itemRowNum = i;
                break;
            }
        }

        // 2.设置表项和列数的对应Map
        for(int i = 0; i < c; ++i){
            // 遍历设置每一个Item对应的列数
            String item = itemRow.get(i);
            for(int j = 0; j < items.length; ++j){
                if(item.contains(items[j]))
                    itemMap.put(items[j],i);
                else if (item.contains("备注"))
                    itemMap.put("开闭",i);
                else if (item.contains("考试方式"))
                    itemMap.put("开闭",i);
            }
        }

        // 遍历每一个Sheet
        for(int sheetNum = 0; sheetNum < this.sheets.length; ++ sheetNum){

            // 每一Sheet对应的文件信息
            FileInfo fileInfo = new FileInfo();

            Sheet readSheet = sheets[sheetNum];
            //获取Sheet表中所包含的总列数
            int rsColumns = readSheet.getColumns();
            //获取Sheet表中所包含的总行数
            int rsRows = readSheet.getRows();

            String sheetName = "";
            for(int i = 0; i < itemRowNum; ++ i){
                List<String> rowData = this.getRowData(rsColumns,i,sheet);
                sheetName += rowData.get(0);
            }

            //获取指定单元格的对象引用
            for (int i = itemRowNum; i < rsRows; i++) {
                List<String> rowData = this.getRowData(rsColumns,i,readSheet);

                // 无效数据过滤
                if(rowData.get(0).equals("") || rowData.get(0).equals("合计学分") || rowData.get(0).contains("注:")){
                    rowData.clear();
                    continue;
                }

                LessonInfo lessonInfo = this.setLsnInfo(rowData);
                lessonInfo.setSemester(fileType.getSemester(rowData));
                fileInfo.setName(sheetName);
                fileInfo.getLessonInfos().add(lessonInfo);
            }

            fileInfos.add(fileInfo);
        }

        return fileInfos;
    }




    /*
     * @param c:列数
     * @param r:获取数据的指定行数
     */
    // 获取Sheet某一行的数据
    private List<String> getRowData(int c,int r,Sheet sheet){

        List<String> rowData = new ArrayList<String>();
        //判断合并单元格区域
        Range[] rangeCell = sheet.getMergedCells();

        for (int j = 0; j < c; j++) {
            Cell cell = sheet.getCell(j, r);
            String str = cell.getContents();
            for (Range range : rangeCell) {
                if (r >= range.getTopLeft().getRow()
                        && r <= range.getBottomRight().getRow()
                        && j >= range.getTopLeft().getColumn()
                        && j <= range.getBottomRight().getColumn())
                {
                    str = sheet.getCell(range.getTopLeft().getColumn(),range.getTopLeft().getRow()).getContents();
                }
            }
            rowData.add(str);
        }

        return rowData;

    }


    // 将文件数据存储至数据库
    public int InsertFileInfoToDatabase(FileInfo[] fileInfoList) {

        for (FileInfo fileInfo : fileInfoList) {

            // 获取所有相关DAO接口
            PlanDao planDao = new PlanDao(session);
            MajorDao majorDao = new MajorDao(session);
            LessonDao lessonDao = new LessonDao(session);
            MjrLsnDao mjrLsnDao = new MjrLsnDao(session);
            CMRelationDao cmRelationDao = new CMRelationDao(session);

            // 存储并获得教学计划ID
            int planId = planDao.insert(fileInfo.getYear(), fileInfo.getSeason());

            // 设置专业的教学计划ID
            fileInfo.getMajor().setPlanId(planId);

            // 从数据库查询专业对应学院
            String majorName = fileInfo.getMajor().getName();
            String collegeName = cmRelationDao.getCollegeOfMajor(majorName);

            // 判断用户若没有提前设置此学院/专业对应关系，则从用户补充的信息设置新的关系并存储
            if(collegeName.length() < 1){
                // 设置此学院/专业对应关系
                CMRelation cmRelation = new CMRelation();
                cmRelation.setMjrName(fileInfo.getMajorName());
                cmRelation.setClgName(fileInfo.getMajor().getCollege());

                List<CMRelation> cmRelations= new ArrayList<CMRelation>();
                cmRelations.add(cmRelation);
                RelationService relationService = new RelationService(session);
                relationService.insertRelations(cmRelations);
            }

            // 保存专业信息并获取ID
            int majorId = majorDao.insert(fileInfo.getMajor());

            String level = fileInfo.getMajor().getLevel();

            // 存储课程并存储专业/课程对应关系
            int size = fileInfo.getLessonInfos().size();
            for (int i = 0; i < size; ++i) {

                List<LessonInfo> lessonInfos = fileInfo.getLessonInfos();

                try {
                    LessonInfo lessonInfo = (LessonInfo) lessonInfos.get(i);
                    if (lessonInfo == null) {
                        continue;
                    }

                    // 新的课程以及专业/课程对应关系
                    Lesson lesson = UtilService.copyLsnInfo(lessonInfo);
                    MajorLesson majorLesson = new MajorLesson();
                    // 设置课程的教学计划ID
                    lesson.setPlanId(planId);
                    int lessonId = lessonDao.insert(lesson);

                    // 保存专业/课程对应关系
                    majorLesson.setMjrId(majorId);
                    majorLesson.setLsnId(lessonId);
                    majorLesson.setLsnType(lessonInfo.getType());
                    majorLesson.setPlanId(planId);
                    mjrLsnDao.insert(majorLesson);
                    return 1;
                } catch (Exception e) {
                    return 0;
                }

            }
        }

        return 0;
    }

    public void exportExcelOfLevelMajor (String filePath) {

        UtilService utilService = new UtilService(session);
        List<String> yearList = utilService.getYears();

        Map<String,Map<String,List<String>>> exportYearLevelMap;
        exportYearLevelMap = new YearService(session).getMjosInLevel();

        // Export data to Excel
        String excelTitle = "层次开设专业数量统计表";
        String[] itemTitle = {"层次","专业名称","数量"};
        this.exportExcelByData(excelTitle,itemTitle,exportYearLevelMap,filePath);

    }

    public void exportExcelOfMajorLesson(String filePath) {

        UtilService utilService = new UtilService(session);
        List<String> yearList = utilService.getYears();

        Map<String,Map<String,List<String>>> exportYearMajorMap;
        exportYearMajorMap = new YearService(session).getLsnsInMajor();

        // Export data to Excel
        String excelTitle = "专业开设课程数量统计表";
        String[] itemTitle = {"专业名称","课程名称","数量"};
        this.exportExcelByData(excelTitle,itemTitle,exportYearMajorMap,filePath);

    }

    public int exportExcelByData(String excelTitle,String[] itemTitle,Map<String,Map<String,List<String>>> contentMap,String filePath ){
        // Write book for write excel
        WritableWorkbook workbook;
        try {
            workbook = Workbook.createWorkbook(new File(filePath + excelTitle + ".xls"));

            // Set Format
            WritableFont TitleFont = new WritableFont(WritableFont.ARIAL, 20);
            WritableFont NormalFont = new WritableFont(WritableFont.ARIAL, 12);

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

            // Content-right-Format
            contentRightFormat = new WritableCellFormat(NormalFont);
            contentRightFormat.setBorder(Border.ALL, BorderLineStyle.THIN);
            contentRightFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
            contentRightFormat.setAlignment(Alignment.RIGHT);
            contentRightFormat.setWrap(false);

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


    public void exportMajorPlanExcel(String filePath, List<PlnMjrInfo> plnMjrInfos){
        // Write book for write excel
        WritableWorkbook workbook;
        String excelTitle = "教学计划(按专业)导出表";
        try{
            workbook = Workbook.createWorkbook(new File(filePath + excelTitle + ".xls"));

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
                Label sheetTitle = new Label(0,0,sheetName[i] + excelTitle,titleFormat);
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

    public void exportDegreeLessonExcel(String filePath,List<String> types) {
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
            WritableFont TitleFont = new WritableFont(WritableFont.ARIAL, 20);
            WritableFont NormalFont = new WritableFont(WritableFont.ARIAL, 12);

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

            // Content-right-Format
            contentRightFormat = new WritableCellFormat(NormalFont);
            contentRightFormat.setBorder(Border.ALL, BorderLineStyle.THIN);
            contentRightFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
            contentRightFormat.setAlignment(Alignment.RIGHT);
            contentRightFormat.setWrap(false);
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

        List<String> yearList = new UtilService(session).getYears();
        Map<String,Map<String,List<MajorInfo>>>  exportMap = new LinkedHashMap<String,Map<String,List<MajorInfo>>>();
        MajorDao majorDao = new MajorDao(session);

        for (String year: yearList) {
            // 获取这一年的所有教学计划
            List<Plan> plans = new PlanDao(session).getPlansOfYear(year);

            // 遍历教学计划进行统计
            for(Plan plan:plans){

                // 获取该教学计划所有相关学院
                Map<String,List<MajorInfo>> collegeMajorMap = new HashMap<String,List<MajorInfo>>();
                List<String> colleges = majorDao.getColleges(plan.getPlnId());

                if(colleges.size() == 0)
                    continue;

                // 遍历学院查询所有相关专业
                for(String college:colleges){
                    List<MajorInfo> majorInfos = new ArrayList<MajorInfo>();
                    List<Major> majors = majorDao.getMajors(college,plan.getPlnId());

                    // 遍历专业获取所有相关课程
                    for(Major major:majors){
                        MajorInfo majorInfo = new MajorInfo();
                        majorInfo.setName(major.getName() + "-" + major.getLevel());
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
    public List<String> getCoreLessonsInMajor(int majorId,List<String> types){

        String hql = "from MajorLesson where mjrId = :mjrId  and lsnType in :types";
        Query query = session.createQuery(hql);
        query.setParameter("types", types);
        query.setParameter("mjrId",majorId);
        List<MajorLesson> majorLessons = query.list();
        LessonDao lessonDao = new LessonDao(session);
        List<String> resultList = new ArrayList<String>();

        // 遍历所有专业/课程关系获取课程名称
        for(MajorLesson majorLesson:majorLessons)
            resultList.add(lessonDao.getLessonById(majorLesson.getLsnId()).getName());

        return resultList;

    }

    private LessonInfo setLsnInfo(List<String> rowData){

        LessonInfo lessonInfo = new LessonInfo();

        lessonInfo.setName(rowData.get(itemMap.get("名称")));
        lessonInfo.setType(rowData.get(itemMap.get("类型")));
        lessonInfo.setCode(rowData.get(itemMap.get("代码")));
        lessonInfo.setCredit(rowData.get(itemMap.get("学分")));
        lessonInfo.setCrdtHours(rowData.get(itemMap.get("学时")));
        lessonInfo.setExamine(rowData.get(itemMap.get("考核")));
        lessonInfo.setRemark(rowData.get(itemMap.get("备注")));

        return lessonInfo;
    }
}
