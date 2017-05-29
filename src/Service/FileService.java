package Service;

import DTO.FileInfo;
import cms.dao.entity.*;
import cms.dao.info.*;
import cms.dao.manager.*;
import jxl.Cell;
import jxl.Range;
import jxl.Sheet;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.VerticalAlignment;
import jxl.read.biff.BiffException;
import jxl.write.*;
import org.apache.commons.lang.StringUtils;
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
    private List<FileInfo> fileInfoList = new ArrayList<FileInfo>();

    // write excel format
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

        FileInfo fileInfo = new FileInfo();

        Sheet readSheet = sheets[0];
        //获取Sheet表中所包含的总列数
        int rsColumns = readSheet.getColumns();

        if(rsColumns > 10){
            return readOldExl();
        }else if(rsColumns == 9){
            return readNewExl();
        }else {
            return null;
        }




    }

    public List<FileInfo> readOldExl(){

        for (int sheetNumber = 0; sheetNumber < this.sheets.length; ++sheetNumber){

            FileInfo fileInfo = new FileInfo();

            Sheet readSheet = sheets[sheetNumber];
            //获取Sheet表中所包含的总列数
            int rsColumns = readSheet.getColumns();
            //获取Sheet表中所包含的总行数
            int rsRows = readSheet.getRows();

            // get and set the major name and level
            Cell[] nameRow = readSheet.getRow(1);
            String majorLevel = readSheet.getCell(0,2).getContents();
            fileInfo.setMajorName(StringUtils.substringBeforeLast(nameRow[0].getContents(),"专业") );
            fileInfo.setMajorLevel(majorLevel);
            CollegeMajorTableManager collegeMajorTableManager = new CollegeMajorTableManager(session);
            String majorName = fileInfo.getMajorTableEntity().getMajorName();
            String collegeName = collegeMajorTableManager.getCollegeNameByMajor(majorName);
            if(collegeName.length() > 1){
                fileInfo.getMajorTableEntity().setMajorCollege(collegeName);
            }
            //判断合并单元格区域
            Range[] rangeCell = readSheet.getMergedCells();
            //获取指定单元格的对象引用
            for (int i = 6; i < rsRows; i++) {

                LessonInfo lessonInfo = new LessonInfo();
                List<String> rowData = new ArrayList<String>();

                for (int j = 0; j < rsColumns; j++) {
                    Cell cell = readSheet.getCell(j, i);
                    String str = cell.getContents();
                    for (Range r : rangeCell) {
                        if (i >= r.getTopLeft().getRow()
                                && i <= r.getBottomRight().getRow()
                                && j >= r.getTopLeft().getColumn()
                                && j <= r.getBottomRight().getColumn())
                        {
                            str = readSheet.getCell(r.getTopLeft().getColumn(),r.getTopLeft().getRow()).getContents();
                        }
                    }
                    rowData.add(str);
                }

                // invalid info filter
                if(rowData.get(0).equals("") || rowData.get(0).equals("合计学分") || rowData.get(0).contains("注:")){
                    rowData.clear();
                    continue;
                }

                lessonInfo.setLessonType(rowData.get(0));
                lessonInfo.setLessonCode(rowData.get(1));
                lessonInfo.setLessonName(rowData.get(2));
                lessonInfo.setLessonCredit(rowData.get(3));
                lessonInfo.setLessonCreditHours(rowData.get(4));
                String [] semesterNames = {"第一学期","第二学期","第三学期","第四学期","第五学期"};
                for(int semester = 5; semester < 10; ++semester){
                    if(rowData.get(semester).length() > 0){
                        lessonInfo.setLessonSemester(semesterNames[semester-5]);
                    }
                }
                lessonInfo.setLessonExamine(rowData.get(10));
                lessonInfo.setLessonRemark(rowData.get(11));
                fileInfo.getLessonInfoVector().add(lessonInfo);

            }

            this.fileInfoList.add(fileInfo);

        }
        return this.fileInfoList;
    }

    public List<FileInfo> readNewExl(){

        for (int sheetNumber = 0; sheetNumber < this.sheets.length; ++sheetNumber){

            FileInfo fileInfo = new FileInfo();

            Sheet readSheet = sheets[sheetNumber];
            //获取Sheet表中所包含的总列数
            int rsColumns = readSheet.getColumns();
            //获取Sheet表中所包含的总行数
            int rsRows = readSheet.getRows();

            // get and set the major name and level
            Cell[] nameRow = readSheet.getRow(0);
            String nameContents = nameRow[0].getContents();
            int yearIndex = nameContents.indexOf("级");
            String year = nameContents.substring(0,yearIndex);
            fileInfo.setPlanYear(year);

            if(nameContents.contains("春")){
                fileInfo.setPlanSeason("春");
            }else if(nameContents.contains("秋")){
                fileInfo.setPlanSeason("秋");
            }else {
                fileInfo.setPlanSeason("");
            }

            String majorName = "";

            if(nameContents.contains("专升本")){
                fileInfo.setMajorLevel("专升本");
                int begin = nameContents.indexOf("）");
//                System.out.println("Begin_____" + begin + "________");
                int end = nameContents.indexOf("教");
//                System.out.println("End_____" + end + "________");
                majorName = nameContents.substring(begin+1,end);
//                System.out.println("majorName_____" + majorName + "________");

            }else if(nameContents.contains("专科")){
                fileInfo.setMajorLevel("专科");
                int begin = nameContents.indexOf("科");
//                System.out.println("Begin_____" + begin + "________");
                int end = nameContents.indexOf("教");
//                System.out.println("End_____" + end + "________");
                majorName = nameContents.substring(begin+1,end);
//                System.out.println("majorName_____" + majorName + "________");

            }else {
                fileInfo.setMajorLevel("");
            }

            fileInfo.setMajorName(majorName);
            CollegeMajorTableManager collegeMajorTableManager = new CollegeMajorTableManager(session);
            String collegeName = collegeMajorTableManager.getCollegeNameByMajor(majorName);
            if(collegeName.length() > 1){
                fileInfo.getMajorTableEntity().setMajorCollege(collegeName);
            }
            //判断合并单元格区域
            Range[] rangeCell = readSheet.getMergedCells();
            //获取指定单元格的对象引用
            for (int i = 6; i < rsRows; i++) {

                LessonInfo lessonInfo = new LessonInfo();
                List<String> rowData = new ArrayList<String>();

                for (int j = 0; j < rsColumns; j++) {
                    Cell cell = readSheet.getCell(j, i);
                    String str = cell.getContents();
                    for (Range r : rangeCell) {
                        if (i >= r.getTopLeft().getRow()
                                && i <= r.getBottomRight().getRow()
                                && j >= r.getTopLeft().getColumn()
                                && j <= r.getBottomRight().getColumn())
                        {
                            str = readSheet.getCell(r.getTopLeft().getColumn(),r.getTopLeft().getRow()).getContents();
                        }
                    }
                    rowData.add(str);
                }

                // invalid info filter
                if(rowData.get(0).equals("") || rowData.get(0).equals("合计学分") || rowData.get(0).contains("注:")){
                    rowData.clear();
                    continue;
                }

                lessonInfo.setLessonType(rowData.get(3));
                lessonInfo.setLessonCode(rowData.get(0));
                lessonInfo.setLessonName(rowData.get(2));
                lessonInfo.setLessonCredit(rowData.get(4));
                lessonInfo.setLessonCreditHours(rowData.get(5));

                String [] semesterNames = {"第一学期","第二学期","第三学期","第四学期","第五学期"};
                for(int semester = 5; semester < 10; ++semester){
                    int semesterNum = Integer.parseInt(rowData.get(6));
                    lessonInfo.setLessonSemester(semesterNames[semesterNum - 1]);
                }
                lessonInfo.setLessonExamine(rowData.get(7));
                lessonInfo.setLessonRemark(rowData.get(8));
                fileInfo.getLessonInfoVector().add(lessonInfo);

            }

            this.fileInfoList.add(fileInfo);

        }
        return this.fileInfoList;
    }



    public void InsertFileInfoToDatabase(FileInfo[] fileInfoList) {

        for (FileInfo fileInfo : fileInfoList) {

            // init the table manager
            PlanTableManager planTableManager = new PlanTableManager(session);
            MajorTableManager majorTableManager = new MajorTableManager(session);
            LessonTableManager lessonTableManager = new LessonTableManager(session);
            MajorLessonTableManager majorLessonTableManager = new MajorLessonTableManager(session);
            CollegeMajorTableManager collegeMajorTableManager = new CollegeMajorTableManager(session);

            // store and get the plan_Id
            // need to be delete
//            fileInfo.setPlanSeason("春");
//            fileInfo.setPlanYear("2015");
            int planId = planTableManager.insertPlan(fileInfo.getPlanYear(), fileInfo.getPlanSeason());
//            System.out.print("planId____" + planId + "______");
            // set major and get the major_Id
            fileInfo.getMajorTableEntity().setPlanId(planId);
            String majorName = fileInfo.getMajorTableEntity().getMajorName();
//            System.out.print("majorName____" + majorName + "______");
//            String collegeName = fileInfo.getMajorTableEntity().getMajorCollege();

            String collegeName = collegeMajorTableManager.getCollegeNameByMajor(majorName);
            if(collegeName.length() < 1){
                ModifyInfo modifyInfo = new ModifyInfo();
                modifyInfo.setMajorName(fileInfo.getMajorName());
                modifyInfo.setCollegeName(fileInfo.getMajorTableEntity().getMajorCollege());
                List<ModifyInfo> modifyInfoList= new ArrayList<ModifyInfo>();
                modifyInfoList.add(modifyInfo);
                ModifyService modifyService = new ModifyService(session);
                modifyService.modifyMajorCollege(modifyInfoList);
            }
//
            int majorId = majorTableManager.insertMajor(fileInfo.getMajorTableEntity());
//            System.out.print("majorId____" + majorId + "______");
            String majorLevel = fileInfo.getMajorTableEntity().getMajorLevel();
            System.out.print("majorLevel____" + majorLevel + "______\n");
            System.out.print("lessonInfoNum____" + fileInfo.getLessonInfoVector().size() + "______\n");
            // set and store a lesson and a major_lesson
            int size = fileInfo.getLessonInfoVector().size();
            System.out.print("lessonInfoNum____" + size + "______\n");
            for (int i = 0; i < size; ++i) {
                Vector<LessonInfo> lessonInfoVector = fileInfo.getLessonInfoVector();

                try {
                    LessonInfo lessonInfo = (LessonInfo) lessonInfoVector.get(i);
                    if (lessonInfo == null) {
                        System.out.print("lessonInfo_____NULL_____");
                    }

                    LessonTableEntity lessonTableEntity = new LessonTableEntity();
                    MajorLessonTableEntity majorLessonTableEntity = new MajorLessonTableEntity();
                    // store a lesson
                    lessonTableEntity.setLessonName(lessonInfo.getLessonName());
                    lessonTableEntity.setLessonCode(lessonInfo.getLessonCode());
                    lessonTableEntity.setLessonCredit(lessonInfo.getLessonCredit());
                    lessonTableEntity.setLessonCreditHours(lessonInfo.getLessonCreditHours());
                    lessonTableEntity.setLessonSemester(lessonInfo.getLessonSemester());
                    lessonTableEntity.setLessonExamine(lessonInfo.getLessonExamine());
                    lessonTableEntity.setLessonRemark(lessonInfo.getLessonRemark());
                    lessonTableEntity.setPlanId(planId);

                    int lessonId = lessonTableManager.insertLesson(lessonTableEntity);
                    System.out.print("lessonId____" + lessonId + "______");
                    // store major_lesson
                    majorLessonTableEntity.setMajorLessonMId(majorId);
                    majorLessonTableEntity.setMajorLessonLId(lessonId);
                    majorLessonTableEntity.setMajorLessonType(lessonInfo.getLessonType());
                    if (lessonInfo.getLessonType().contains("必修课")) {
                        majorLessonTableEntity.setMajorLessonIsCore((byte) 1);
                    }else if (lessonInfo.getLessonType().contains("主干课")) {
                        majorLessonTableEntity.setMajorLessonIsCore((byte) 1);
                    } else {
                        majorLessonTableEntity.setMajorLessonIsCore((byte) 0);
                    }
                    majorLessonTableEntity.setPlanId(planId);
                    majorLessonTableManager.insertMajorLesson(majorLessonTableEntity);

                    System.out.println("Insert Success!\n");
                } catch (Exception e) {
                    System.out.println(e);
                }

            }
        }
    }

    public void exportExcelOfLevelMajor (String filePath) {

        //
        UtilService utilService = new UtilService(session);
        List<String> yearList = utilService.getListOfYear();

        Map<String,Map<String,List<String>>> exportYearLevelMap;
        exportYearLevelMap = new YearService(session).getMajorsInLevelOfAllYear();

        // Export data to Excel
        String excelTitle = "层次开设专业数量统计表";
        String[] itemTitle = {"层次","专业名称","数量"};
        this.exportExcelByData(excelTitle,itemTitle,exportYearLevelMap,filePath);

    }

    public void exportExcelOfMajorLesson(String filePath) {

        UtilService utilService = new UtilService(session);
        List<String> yearList = utilService.getListOfYear();

        Map<String,Map<String,List<String>>> exportYearMajorMap;
        exportYearMajorMap = new YearService(session).getLessonsInMajorOfAllYear();

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
                System.out.println("---------MapSize---  "+ i + contentOfSheetMap.size() +"  --------\n");

                int rowFlag = 2;
                String[] keyOfMap = contentOfSheetMap.keySet().toArray(new String[]{});
                for (int j = 0; j < keyOfMap.length; ++j) {

                    String key = keyOfMap[j];
                    List<String> contentOfSheetList = contentOfSheetMap.get(key);
                    int rowOfList = contentOfSheetList.size();
                    System.out.println("---------ListSize---  "+ j + "____" + rowOfList + "  --------\n");

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
                        System.out.println("---------Content---  "+ contentOfSheetList.get(k) + "  --------");
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


    public void exportMajorPlanExcel(String filePath, List<MajorPlanInfo> majorPlanInfoList){
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
            String [] sheetName = new String[majorPlanInfoList.size()];
            for(int sheetFlag = 0; sheetFlag < sheetName.length; ++sheetFlag){
                sheetName[sheetFlag] = majorPlanInfoList.get(sheetFlag).getMajorName();
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
                MajorPlanInfo major = majorPlanInfoList.get(i);
                int rowFlag = 2;
                List<LessonTableEntity> lessons = major.getLessonTableEntityList();
                int rowOfList = lessons.size();
                for (int j = 0; j < rowOfList; ++j) {
                    LessonTableEntity lesson = lessons.get(j);
                    Label majorName = new Label(0, rowFlag + j, major.getMajorName(), titleFormat);
                    Label lessonName = new Label(1, rowFlag + j, lesson.getLessonName(), titleFormat);
                    Label lessonCode = new Label(2, rowFlag + j, lesson.getLessonCode(), titleFormat);
                    Label lessonCredit = new Label(3, rowFlag + j, lesson.getLessonCredit(), titleFormat);
                    Label lessonCreditHours = new Label(4, rowFlag + j, lesson.getLessonCreditHours(), titleFormat);
                    Label lessonSemester = new Label(5, rowFlag + j, lesson.getLessonSemester(), titleFormat);
                    Label lessonExam = new Label(6, rowFlag + j, lesson.getLessonExamine(), titleFormat);
                    Label lessonRemark = new Label(7, rowFlag + j, lesson.getLessonRemark(), titleFormat);

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

    public void exportDegreeLessonExcel(String filePath) {
        Map<String, Map<String, List<MajorInfo>>> contentMap = this.getDegreeLessonMap();
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
                        Label degree = new Label(3,rowLocation, majorInfo.getMajorDegree(),contentCenterFormat);
                        ws.mergeCells(3,rowLocation,3,rowFlag - 1);
                        ws.addCell(degree);

                        // add the major_name to excel
                        Label majorName = new Label(2,rowLocation, majorInfo.getMajorName(),contentCenterFormat);
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





    public Map<String,Map<String,List<MajorInfo>>> getDegreeLessonMap (){
        List<String> yearList = new UtilService(session).getListOfYear();
        Map<String,Map<String,List<MajorInfo>>>  exportMap = new LinkedHashMap<String,Map<String,List<MajorInfo>>>();
        MajorTableManager majorTableManager = new MajorTableManager(session);

        for (String year: yearList) {
            // search all plans by year
            List<PlanTableEntity> planTableEntityList = new PlanTableManager(session).getPlanListOfYear(year);
            // traverse all plans to find all colleges
            for(PlanTableEntity plan:planTableEntityList){
                Map<String,List<MajorInfo>> collegeMajorMap = new HashMap<String,List<MajorInfo>>();
                List<String> collegeList = majorTableManager.getCollegeList(plan.getPlanId());
                if(collegeList.size() == 0){
                    continue;
                }
                // traverse all colleges to find majors
                for(String college:collegeList){
                    List<MajorInfo> majorInfoList = new ArrayList<MajorInfo>();
                    List<MajorTableEntity> majorTableEntityList = majorTableManager.getMajorListByCollegePlan(college,plan.getPlanId());
                    System.out.println("-----Find----" + majorTableEntityList.size() + "---MajorInfo---\n");
                    // traverse all majors to find core lessons
                    for(MajorTableEntity majorTableEntity:majorTableEntityList){
                        MajorInfo majorInfo = new MajorInfo();
                        majorInfo.setMajorName(majorTableEntity.getMajorName() + "-" + majorTableEntity.getMajorLevel());
                        System.out.println("-----MajorInfo----" + majorInfo.getMajorName() + "---MajorInfo---\n");
                        majorInfo.setMajorDegree(majorTableEntity.getMajorDegree());
                        majorInfo.setCoreLessons(this.getCoreLessonsInMajor(majorTableEntity.getMajorId()));
                        majorInfoList.add(majorInfo);
                    }
                    collegeMajorMap.put(college, majorInfoList);
                }
                exportMap.put(plan.getPlanYear() + "-" + plan.getPlanSeason(),collegeMajorMap);
            }

        }

        return  exportMap;

    }

    public List<String> getCoreLessonsInMajor(int majorId){

        String hql = "from MajorLessonTableEntity where majorLessonMId = :majorId and majorLessonIsCore = :isCore";
        Query query = session.createQuery(hql);
        query.setParameter("majorId",majorId);
        query.setParameter("isCore", (byte)1);
        List<MajorLessonTableEntity> majorLessonList = query.list();
        LessonTableManager lessonTableManager = new LessonTableManager(session);

        List<String> resultList = new ArrayList<String>();
        for(MajorLessonTableEntity majorLesson:majorLessonList){
            resultList.add(lessonTableManager.getLessonById(majorLesson.getMajorLessonLId()).getLessonName());
        }
        return resultList;

    }



}
