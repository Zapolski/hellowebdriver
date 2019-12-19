import model.TaskRecord;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class ExcelJoin {

    private static final String EXCEL_FILES_LIST = "excelFileList.txt";
    private static final String RESULT_FILE = "phrases.xls";

    public static void main(String[] args) throws IOException {

        List<String> fileList = Files.readAllLines(Paths.get(EXCEL_FILES_LIST));
        Map<String, TaskRecord> phrases = new HashMap<>();

        for (String file : fileList) {
            System.out.println("Process file: [" + file + "]");
            FileInputStream inputStream = new FileInputStream(new File(file));
            HSSFWorkbook workbook = new HSSFWorkbook(inputStream);
            HSSFSheet sheet = workbook.getSheetAt(0);

            int rowNum = sheet.getLastRowNum() + 1;
            System.out.println("Rows count: [" + rowNum + "]");
            for (int index = 1; index < rowNum; index++) {
                TaskRecord record = getTaskRecordFromXlsSheet(sheet, index);
                phrases.put(record.getMp3(), record);
            }
            workbook.close();
            inputStream.close();
        }

        List<TaskRecord> recordList = new ArrayList<>();
        for (Map.Entry<String, TaskRecord> entry : phrases.entrySet()) {
            recordList.add(entry.getValue());
        }
        recordList.sort(Comparator.comparing(TaskRecord::getWord));
        //recordList.forEach(System.out::println);
        System.out.println("Rows result count: ["+recordList.size()+"]");

        File file = new File(RESULT_FILE);
        FileInputStream inputStream = new FileInputStream(file);
        HSSFWorkbook workbook = new HSSFWorkbook(inputStream);
        HSSFSheet sheet = workbook.getSheetAt(0);
        int rowNum = sheet.getLastRowNum() + 1;
        Cell cell;
        Row row;
        for (TaskRecord taskRecord: recordList){
            row = sheet.createRow(rowNum++);
            cell = row.createCell(0);
            cell.setCellValue(taskRecord.getWord());
            cell = row.createCell(1);
            cell.setCellValue(taskRecord.getRussian());
            cell = row.createCell(2);
            cell.setCellValue(taskRecord.getEnglish());
            cell = row.createCell(3);
            cell.setCellValue(taskRecord.getMp3());
            cell = row.createCell(4);
            cell.setCellValue(taskRecord.getRule());
        }
        FileOutputStream outputStream = new FileOutputStream(file);
        workbook.write(outputStream);
        outputStream.flush();
        outputStream.close();
        workbook.close();



    }

    private static TaskRecord getTaskRecordFromXlsSheet(HSSFSheet sheet, int i) {
        TaskRecord result = new TaskRecord();
        Row row = sheet.getRow(i);
        result.setWord(row.getCell(0).getStringCellValue());
        result.setRussian(row.getCell(1).getStringCellValue());
        result.setEnglish(row.getCell(2).getStringCellValue());
        result.setMp3(row.getCell(3).getStringCellValue());
        if (row.getCell(4 ) != null){
            result.setRule(row.getCell(4).getStringCellValue());
        }else{
            result.setRule("");
        }
        return result;
    }
}
