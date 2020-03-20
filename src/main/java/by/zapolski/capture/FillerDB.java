package by.zapolski.capture;

import by.zapolski.database.dao.ConnectorDB;
import by.zapolski.database.dao.RecordDao;
import by.zapolski.database.model.Record;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class FillerDB {

    private ConnectorDB connectorDB;
    private static final String EXCEL_FILE = "d:/test/EnglishPhrases/info/current.xls";

    public FillerDB(ConnectorDB connectorDB) {
        this.connectorDB = connectorDB;
    }

    public void fillFromExcelFile(String filePath) throws IOException, SQLException {
        File file = new File(filePath);
        FileInputStream inputStream = new FileInputStream(file);
        try (HSSFWorkbook workbook = new HSSFWorkbook(inputStream)) {
            HSSFSheet sheet = workbook.getSheetAt(0);
            int rowNum = sheet.getLastRowNum() + 1;

            RecordDao recordDao = new RecordDao(connectorDB);
            for (int index = 1; index < rowNum; index++) {
                Record record = getTaskRecordFromXlsSheet(sheet, index);
                recordDao.create(record);
                System.out.println(record + " was added.");
            }
        }
    }

    private Record getTaskRecordFromXlsSheet(HSSFSheet sheet, int i) {
        Record result = new Record();
        Row row = sheet.getRow(i);
        result.setWord(row.getCell(0).getStringCellValue());
        result.setRussian(row.getCell(1).getStringCellValue());
        result.setEnglish(row.getCell(2).getStringCellValue());
        result.setSoundPath(row.getCell(3).getStringCellValue());
        if (row.getCell(4) != null) {
            result.setRule(row.getCell(4).getStringCellValue());
        } else {
            result.setRule("");
        }
        return result;
    }

    public void fillFromLongmanSiteByList(List<String> wordList) {

    }

    public static void main(String[] args) throws IOException, SQLException {
        try (ConnectorDB connectorDB = new ConnectorDB()) {
            FillerDB fillerDB = new FillerDB(connectorDB);
            fillerDB.fillFromExcelFile(EXCEL_FILE);
        }
    }
}
