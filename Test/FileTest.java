import DAO.HibernateUtil;
import DTO.FileInfo;
import Service.File.ExlReader;
import net.sf.json.JSONArray;
import org.hibernate.Session;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * Created by Jaho on 2017/5/31.
 */
public class FileTest {

    @Test
    public void readFile(){
        String filePath = "/Users/Jaho/Desktop/CodeSpace/JavaSpace/专业沿革/教学计划专科总表（20150112）--打印.xls";
        Session session = HibernateUtil.getSession();
        ExlReader exlReader = new ExlReader(session);
        exlReader.openExlFile(filePath);
        List<FileInfo> fileInfoList = exlReader.readExlFile();
        JSONArray fileArray = JSONArray.fromObject(fileInfoList);
        System.out.println(fileArray);
        session.close();

    }

}
