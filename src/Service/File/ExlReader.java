package Service.File;

import DTO.FileInfo;
import DTO.LessonInfo;

import jxl.Cell;
import jxl.Range;
import jxl.Sheet;
import jxl.Workbook;
import org.hibernate.Session;
import jxl.read.biff.BiffException;

import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;

/**
 * Created by Jaho on 2017/6/7.
 * 读取Excel数据
 */


public class ExlReader {

    private Session session;
    private Sheet[] sheets = null;
    private Workbook readWb = null;

    public ExlReader(Session session) {
        this.session = session;
    }

    public Workbook openExlFile(String filePath){
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

        if(rsColumns > 11)
            return readExl(new r12File());
        else if(rsColumns == 9)
            return readExl(new r9File());
        else
            return null;

    }


    public List<FileInfo> readExl(FileType fileType){

        List<FileInfo> fileInfos = new ArrayList<FileInfo>();


        // 遍历每一个Sheet
        for(int sheetNum = 0; sheetNum < this.sheets.length; ++ sheetNum){

            // 当前要处理的sheet
            Sheet readSheet = sheets[sheetNum];
            // 每一Sheet对应的文件信息
            FileInfo fileInfo = fileType.setMajorInfo(readSheet,session);


            // 获取Sheet表中所包含的总列数
            int columns = fileType.getColumns();
            // 获取Sheet表中所包含的总行数
            int rows = readSheet.getRows();
            // 获取表头所在行数
            int pos = fileType.getItemPos();

            //获取指定单元格的对象引用
            for (int i = pos + 1; i < rows; i++) {
                List<String> rowData = this.getRowData(columns,i,readSheet);

                // 无效数据过滤
                if(rowData.get(0).equals("") || rowData.get(0).equals("合计学分") || rowData.get(0).contains("注:")){
                    rowData.clear();
                    continue;
                }

                LessonInfo lessonInfo = this.setLsnInfo(rowData,fileType);
                lessonInfo.setSemester(fileType.getSemester(rowData));
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

    private LessonInfo setLsnInfo(List<String> rowData,FileType fileType){

        LessonInfo lessonInfo = new LessonInfo();
        Map<String,Integer> map = fileType.getItemsMap();
        lessonInfo.setType(rowData.get(map.get("类型")));
        lessonInfo.setCode(rowData.get(map.get("代码")));
        lessonInfo.setName(rowData.get(map.get("名称")));
        lessonInfo.setCredit(rowData.get(map.get("学分")));
        lessonInfo.setCrdtHours(rowData.get(map.get("学时")));
        lessonInfo.setExamine(rowData.get(map.get("考核")));
        lessonInfo.setRemark(rowData.get(map.get("备注")));

        return lessonInfo;
    }

}
