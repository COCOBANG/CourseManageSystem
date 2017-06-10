package Service.File;

import jxl.Sheet;
import DTO.FileInfo;
import org.hibernate.Session;

import java.util.List;
import java.util.Map;

/**
 * Created by Jaho on 2017/5/30.
 * 对不同文件格式进行适配的抽象类
 */
public abstract class FileType {

    abstract public int getColumns();
    abstract public int getItemPos();
    abstract public Map<String, Integer> getItemsMap();
    abstract public String getSemester(List<String> rowData);
    abstract public FileInfo setMajorInfo(Sheet sheet, Session session);

}

