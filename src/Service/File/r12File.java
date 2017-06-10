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
 * 行数为12的文件格式
 */
public class r12File extends FileType {

    private Map<String,Integer> itemsMap;

    public r12File() {
        this.setItemsMap();
    }

    @Override
    public FileInfo setMajorInfo(Sheet sheet,Session session) {

        FileInfo fileInfo = new FileInfo();
        Cell[] nameRow = sheet.getRow(1);
        String nameContents = nameRow[0].getContents();


        // 专业名称
        int end = nameContents.indexOf("专");
        String majorName = nameContents.substring(0,end);
        fileInfo.getMajor().setName(majorName);

        // 专业层次
        String majorLevel = sheet.getCell(0,2).getContents();
        fileInfo.getMajor().setLevel(majorLevel);
        // 学院名称
        CMRelationDao cmRelationDao = new CMRelationDao(session);
        String collegeName = cmRelationDao.getCollegeOfMajor(majorName);
        if(collegeName != null)
            fileInfo.getMajor().setCollege(collegeName);

        return fileInfo;
    }

    @Override
    public int getColumns() {
        return 12;
    }

    @Override
    public int getItemPos() {
        return 5;
    }

    @Override
    public Map<String, Integer> getItemsMap() {
        return this.itemsMap;
    }

    @Override
    public String getSemester(List<String> rowData) {
        String[] semesterNames = {"第一学期", "第二学期", "第三学期", "第四学期", "第五学期"};
        for (int semester = 5; semester < 10; ++semester) {
            if (rowData.get(semester).length() > 0)
                return semesterNames[semester - 5];
        }
        return "";
    }

    private void setItemsMap() {

        this.itemsMap = new HashMap<String, Integer>();

        itemsMap.put("类型",0);
        itemsMap.put("代码",1);
        itemsMap.put("名称",2);
        itemsMap.put("学分",3);
        itemsMap.put("学时",4);
        itemsMap.put("考核",10);
        itemsMap.put("备注",11);


    }
}
