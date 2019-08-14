import model.TaskRecord;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class CreateHtml {

    private static String DATABASE_FILE = "d:/Test/English/info/data.xls";
    private static String SETS_FILE = "set.txt";

    public static void main(String[] args) throws IOException {
        // Read XSL file

        List<String> setList = Files.readAllLines(Paths.get(SETS_FILE));
        Map<String,String[]> filesMap = new HashMap<>();
        for (String set: setList){
            if (set.startsWith("#")){
                continue;
            }
            String key = set.split(":")[0];
            String[] value = set.split(":")[1].trim().split("\\s*,\\s*");
            filesMap.put(key,value);
        }

        for (Map.Entry<String ,String[]> entry: filesMap.entrySet()){
            System.out.println("Current set: "+entry.getKey()+" "+ Arrays.toString(entry.getValue()));

            List<TaskRecord> recordList = new ArrayList<>();

            // get needed records from xls-database
            FileInputStream inputStream = new FileInputStream(new File(DATABASE_FILE));
            HSSFWorkbook workbook = new HSSFWorkbook(inputStream);
            HSSFSheet sheet = workbook.getSheetAt(0);

            int rowNum = sheet.getLastRowNum()+1;
            for (int index = 1; index < rowNum; index++){
                TaskRecord record = getTaskRecordFromXlsSheet(sheet, index);
                for (String word: entry.getValue()){
                    if (word.equals(record.getWord())){
                        recordList.add(record);
                        System.out.println(" --> "+record.getEnglish());
                        break;
                    }
                }
            }

            Collections.shuffle(recordList);

            // create html file
            FileWriter fw = createFileAndWriteHeader(entry.getKey());
            int counter = 1;
            for (TaskRecord record: recordList){
                flushRecordToHtml(fw, counter++, record);
            }
            writeFooterAndCloseCurrentFile(fw);
        }
    }

    private static FileWriter createFileAndWriteHeader(String currentWord) throws IOException {
        FileWriter fw;
        fw = new FileWriter("d:/Test/English/" + currentWord + ".html", false);
        fw.write(STR_HEADER);
        return fw;
    }

    private static void writeFooterAndCloseCurrentFile(FileWriter fw) throws IOException {
        if (fw!=null){
            fw.write(STR_FOOTER);
            fw.flush();
            fw.close();
        }
    }

    private static void flushRecordToHtml(FileWriter fw, int i, TaskRecord record) throws IOException {
        fw.write("<tr>\n");
        fw.write(String.format("<td class=\"center\">%d</td>\n",i));
        fw.write("<td>\n");
        fw.write(String.format("<span>%s</span>\n",record.getRussian()));

        if (!record.getRule().isEmpty()){
            fw.write(String.format("<span class='support' tabindex=\"%d\" data-title='%s'>\n",10000+i,record.getRule()));
            fw.write("<em>?</em>\n");
            fw.write("</span>\n");
        }

        fw.write(String.format("<input class='input-field' type=\"text\" placeholder=\"Введите перевод\" tabindex=\"%d\">\n",i));
        fw.write(String.format("<span class='tip'>%s</span>\n",record.getEnglish()));

        fw.write("<div class='check'> </div>\n");
        fw.write("</td>\n");
        fw.write("<td class=\"center\">\n");
        fw.write("<a class=\"music-button\"><img src=\"img/player_play.png\"></a>\n");
        fw.write(String.format("<audio class=\"player\" src=\"%s\" type=\"audio/mpeg\"></audio>\n",record.getMp3()));
        fw.write("<br>");
        fw.write("<input type=\"range\" step=\"0.1\" min=\"0.2\" max=\"2\" value=\"1\" class=\"speed\"/>\n");
        fw.write("<br>");
        fw.write("<span class=\"speed-text\">1.0</span>\n");
        fw.write("</td>\n");
        fw.write("</tr>\n");
        fw.flush();
    }

    private static TaskRecord getTaskRecordFromXlsSheet(HSSFSheet sheet, int i) {
        TaskRecord result = new TaskRecord();
        Row row = sheet.getRow(i);
        result.setWord(row.getCell(0).getStringCellValue());
        result.setRussian(row.getCell(1).getStringCellValue());
        result.setEnglish(row.getCell(2).getStringCellValue());
        result.setMp3("words/"+result.getWord()+"/"+row.getCell(3).getStringCellValue());
        try{
            result.setRule(row.getCell(4).getStringCellValue());
        }catch (NullPointerException e){
            result.setRule("");
        }
        return result;
    }

    public static final String STR_HEADER = "<!DOCTYPE html>\n" +
            "<html>\n" +
            "\n" +
            "<head>\n" +
            "\n" +
            "    <meta charset=\"utf-8\">\n" +
            "    <title>English phrases</title>\n" +
            "    <script src=\"https://ajax.googleapis.com/ajax/libs/jquery/2.1.1/jquery.min.js\"></script>\n" +
            "\n" +
            "    <link href=\"css/style.css\" rel=\"stylesheet\">\n" +
            "</head>\n" +
            "\n" +
            "<body>\n" +
            "    <table class=\"table-sentences\">\n" +
            "        <caption>\n" +
            "            <h3>Английские фразы</h3>\n" +
            "        </caption>\n" +
            "        <tr>\n" +
            "            <th>№ п/п</th>\n" +
            "            <th>Русская версия / Английская версия</th>\n" +
            "            <th>Проигрыватель</th>\n" +
            "        </tr>\n";
    public static final String STR_FOOTER = "    </table>\n" +
            "</body>\n" +
            "<script src=\"js/scripts.js\"></script>\n" +
            "\n" + "<script src=\"js/check-translate.js\"></script>" +
            "</html>";
}
