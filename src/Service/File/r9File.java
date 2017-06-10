package Service.File;

import DAO.CMRelationDao;
import DTO.FileInfo;
import jxl.Cell;
import jxl.Sheet;
import org.hibernate.Session;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Jaho on 2017/5/30.
 * 行数为9的文件格式
 */
public class r9File extends FileType {

    private Map<String,Integer> itemsMap;
    public r9File() {
        this.setItemsMap();
    }

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
            fileInfo.getMajor().setLevel("专升本");
            int begin = nameContents.indexOf("）");
            int end = nameContents.indexOf("教");
            majorName = nameContents.substring(begin+1,end);
        }else if(nameContents.contains("专科")){
            fileInfo.getMajor().setLevel("专科");
            int begin = nameContents.indexOf("科");
            int end = nameContents.indexOf("教");
            majorName = nameContents.substring(begin+1,end);
        }else {
            fileInfo.getMajor().setLevel("");
        }
        fileInfo.getMajor().setName(majorName);

        // 学院名称
        CMRelationDao cmRelationDao = new CMRelationDao(session);
        String collegeName = cmRelationDao.getCollegeOfMajor(majorName);
        if(collegeName != null)
            fileInfo.getMajor().setCollege(collegeName);


        return fileInfo;
    }

    @Override
    public int getColumns() {
        return 9;
    }

    @Override
    public int getItemPos() {
        return 1;
    }

    @Override
    public Map<String, Integer> getItemsMap() {
        return this.itemsMap;
    }

    @Override
    public String getSemester(List<String> rowData) {

        String [] semesterNames = {"第一学期","第二学期","第三学期","第四学期","第五学期"};
        int semesterNum = Integer.parseInt(rowData.get(6));
        return semesterNames[semesterNum - 1];
    }

    private void setItemsMap() {

        this.itemsMap = new HashMap<String, Integer>();

        itemsMap.put("类型",3);
        itemsMap.put("代码",0);
        itemsMap.put("名称",2);
        itemsMap.put("学分",4);
        itemsMap.put("学时",5);
        itemsMap.put("考核",7);
        itemsMap.put("备注",8);


    }
}
