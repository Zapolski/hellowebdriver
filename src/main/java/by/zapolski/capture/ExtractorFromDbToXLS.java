package by.zapolski.capture;

import by.zapolski.capture.model.dto.SentenceInfo;
import by.zapolski.database.dao.ConnectorDB;
import by.zapolski.database.dao.RecordDao;
import by.zapolski.database.dao.WordDao;
import by.zapolski.database.model.Record;
import by.zapolski.database.model.Word;
import com.google.gson.Gson;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
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
            row.createCell(5).setCellValue("Rank");
            row.createCell(6).setCellValue("Id");

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

    private void writeRecordsInWorkbook(String word, HSSFWorkbook workbook) throws IOException {
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
                cell.setCellValue(removeUnsupportedSymbols(record.getRussian()));
                cell = row.createCell(2);
                cell.setCellValue(removeUnsupportedSymbols(record.getEnglish()));
                cell = row.createCell(3);
                cell.setCellValue(record.getSoundPath());
                cell = row.createCell(4);
                cell.setCellValue(record.getRule());
                cell = row.createCell(5);
                cell.setCellValue(getSentenceRank(record.getEnglish()));
                cell = row.createCell(6);
                cell.setCellValue(record.getId());
            }
            LOG.log(Level.INFO, "All records for word [{0}] was extracted", word);
        }
    }

    private static String removeUnsupportedSymbols(String source) {
        return source.replaceAll("‘", "'")
                .replaceAll("’", "'")
                .replaceAll("\"", "'")
                .replaceAll("\\(=.*\\)", "");
    }

    private Integer getSentenceRank(String sentence) throws IOException {
        URL obj = new URL("http://localhost:8080/sentences/check");
        HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json; utf-8");
        connection.setRequestProperty("Accept", "application/json");
        connection.setDoOutput(true);

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = sentence.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        StringBuilder response = new StringBuilder();

        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
        }

        Integer result;
        Gson g = new Gson();
        SentenceInfo SentenceInfo = g.fromJson(response.toString(), SentenceInfo.class);
        result = SentenceInfo.getRank();

        return result;
    }

    public static void main(String[] args) throws IOException {
        try (ConnectorDB connectorDB = new ConnectorDB()) {
            ExtractorFromDbToXLS extractor = new ExtractorFromDbToXLS(connectorDB, "d:/test/EnglishPhrases/info/backup.xls");
            WordDao wordDao = new WordDao(connectorDB);
            List<String> words = wordDao.getAll().stream().map(Word::getValue).collect(Collectors.toList());
            extractor.extract(words);
        }
    }

}
