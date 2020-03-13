package by.zapolski.capture;

import by.zapolski.database.dao.ConnectorDB;
import by.zapolski.database.dao.RecordDao;
import by.zapolski.database.dao.WordDao;
import by.zapolski.database.model.Record;
import by.zapolski.database.model.Word;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ExtractorFromDbToXLS {

    private ConnectorDB connectorDB;
    private String destinationPath;

    private static final Logger LOG = Logger.getLogger(ExtractorFromDbToXLS.class.getName());

    public ExtractorFromDbToXLS(ConnectorDB connectorDB, String destinationPath) {
        this.connectorDB = connectorDB;
        this.destinationPath = destinationPath;
    }


    private void extract(List<String> words) throws IOException {
        try (HSSFWorkbook workbook = new HSSFWorkbook()) {
            LOG.log(Level.INFO, "Creating xls-file in memory");
            HSSFSheet sheet = workbook.createSheet("From DB");
            int rowNum = 0;
            LOG.log(Level.INFO, "Creating header row.");
            Row row = sheet.createRow(rowNum);
            row.createCell(0).setCellValue("Word");
            row.createCell(1).setCellValue("Russian");
            row.createCell(2).setCellValue("English");
            row.createCell(3).setCellValue("Sound");
            row.createCell(4).setCellValue("Rule");

            for (String word : words) {
                writeRecordsInWorkbook(word, workbook);
            }

            LOG.log(Level.INFO, "Writing filled file on disk, destination file: [{0}]", destinationPath);
            try (FileOutputStream out = new FileOutputStream(new File(destinationPath))) {
                workbook.write(out);
            } catch (IOException e) {
                LOG.log(Level.INFO, "Error during writing xls-file.", e);

            }
        }
    }

    private void writeRecordsInWorkbook(String word, HSSFWorkbook workbook) {
        RecordDao recordDao = new RecordDao(connectorDB);
        LOG.log(Level.INFO, "Processing word: [{0}]", word);
        List<Record> recordsByWord = recordDao.getRecordsByWord(word);
        LOG.log(Level.INFO, "Found out {0} records", recordsByWord.size());
        if (!recordsByWord.isEmpty()) {
            Row row;
            Cell cell;
            HSSFSheet sheet = workbook.getSheetAt(0);
            int rowNum = sheet.getLastRowNum() + 1;
            for (Record record : recordsByWord) {
                row = sheet.createRow(rowNum++);
                cell = row.createCell(0);
                cell.setCellValue(record.getWord());
                cell = row.createCell(1);
                cell.setCellValue(record.getRussian());
                cell = row.createCell(2);
                cell.setCellValue(record.getEnglish());
                cell = row.createCell(3);
                cell.setCellValue(record.getSoundPath());
                cell = row.createCell(4);
                cell.setCellValue(record.getRule());
            }
            LOG.log(Level.INFO, "All records for word [{0}] was extracted", word);
        }
    }

    public static void main(String[] args) throws IOException {
        try (ConnectorDB connectorDB = new ConnectorDB()) {
            ExtractorFromDbToXLS extractor = new ExtractorFromDbToXLS(connectorDB, "d:\\test\\EnglishPhrases\\info\\backup.xls");
            WordDao wordDao = new WordDao(connectorDB);
            List<String> words = wordDao.getAll().stream().map(Word::getValue).collect(Collectors.toList());
            extractor.extract(words);
        }
    }

}
