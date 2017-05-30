package Service.File;

import DAO.CMRelationDao;
import DTO.FileInfo;
import jxl.Cell;
import jxl.Sheet;
import org.hibernate.Session;

import java.util.List;

/**
 * Created by Jaho on 2017/5/30.
 * 行数为9的文件格式
 */
public class r9File implements FileType {
    @Override
    public FileInfo setMajorInfo(Sheet sheet,Session session) {

        FileInfo fileInfo = new FileInfo();

        Cell[] nameRow = sheet.getRow(0);
        String nameContents = nameRow[0].getContents();

        // 年份
        int yearIndex = nameContents.indexOf("级");
        String year = nameContents.substring(0,yearIndex);
        fileInfo.setYear(year);

        // 季节
        if(nameContents.contains("春")){
            fileInfo.setSeason("春");
        }else if(nameContents.contains("秋")){
            fileInfo.setSeason("秋");
        }else {
            fileInfo.setSeason("");
        }

        // 专业名称
        String majorName = "";
        if(nameContents.contains("专升本")){
            fileInfo.setMajorLevel("专升本");
            int begin = nameContents.indexOf("）");
            int end = nameContents.indexOf("教");
            majorName = nameContents.substring(begin+1,end);
        }else if(nameContents.contains("专科")){
            fileInfo.setMajorLevel("专科");
            int begin = nameContents.indexOf("科");
            int end = nameContents.indexOf("教");
            majorName = nameContents.substring(begin+1,end);
        }else {
            fileInfo.setMajorLevel("");
        }
        fileInfo.setMajorName(majorName);

        // 学院名称
        CMRelationDao cmRelationDao = new CMRelationDao(session);
        String collegeName = cmRelationDao.getCollegeOfMajor(majorName);
        if(collegeName.length() > 1)
            fileInfo.getMajor().setCollege(collegeName);


        return fileInfo;
    }

    @Override
    public String getSemester(List<String> rowData) {

        String [] semesterNames = {"第一学期","第二学期","第三学期","第四学期","第五学期"};
        int semesterNum = Integer.parseInt(rowData.get(6));
        return semesterNames[semesterNum - 1];
    }
}
