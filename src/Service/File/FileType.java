package Service.File;

import jxl.Sheet;
import DTO.FileInfo;
import org.hibernate.Session;

import java.util.List;

/**
 * Created by Jaho on 2017/5/30.
 */
public interface FileType {
    public FileInfo setMajorInfo(Sheet sheet, Session session);
    public String getSemester(List<String> rowData);
}

