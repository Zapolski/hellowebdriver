package by.zapolski.generate;

import by.zapolski.database.dao.ConnectorDB;
import by.zapolski.database.dao.RecordDao;
import by.zapolski.database.model.Record;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class HtmlCreator {

    private static final String SETS_FILE = "set.txt";
    private static final String PATH_FOR_HTML = "d:/Test/English/";

    public static void main(String[] args) throws IOException {

        List<String> setList = Files.readAllLines(Paths.get(SETS_FILE));
        Map<String, String[]> filesMap = getMapWithSets(setList);

        for (Map.Entry<String, String[]> entry : filesMap.entrySet()) {
            System.out.println("Current set: " + entry.getKey() + " " + Arrays.toString(entry.getValue()));

            List<Record> recordList = new ArrayList<>();
            try (ConnectorDB connectorDB = new ConnectorDB()) {
                RecordDao recordDao = new RecordDao(connectorDB);
                for (String word : entry.getValue()) {
                    recordList.addAll(recordDao.getRecordsByWord(word, 0, 60000));
                }
            }

            Collections.shuffle(recordList);

            String fileName = PATH_FOR_HTML + entry.getKey() + " (" + recordList.size() + ")" + ".html";
            createHtml(recordList, fileName);
        }
    }

    private static Map<String, String[]> getMapWithSets(List<String> setList) {
        Map<String, String[]> filesMap = new HashMap<>();
        for (String set : setList) {
            if (set.startsWith("#")) {
                continue;
            }
            String key = set.split(":")[0];
            String[] value = set.split(":")[1].trim().split("\\s*,\\s*");
            filesMap.put(key, value);
        }
        return filesMap;
    }

    private static void createHtml(List<Record> recordList, String fileName) throws IOException {
        try (FileWriter fileWriter = new FileWriter(fileName, false)) {
            fileWriter.write(STR_HEADER);
            int counter = 1;
            for (Record record : recordList) {
                flushRecordToHtml(fileWriter, counter++, record);
            }
            fileWriter.write(STR_FOOTER);
            fileWriter.flush();
        }
    }

    private static void flushRecordToHtml(FileWriter fw, int i, Record record) throws IOException {
        fw.write("<tr>\n");

        fw.write("<td class=\"center\">\n");
        fw.write("<input class=\"show_question\" type=\"checkbox\">\n");
        fw.write(String.format("<span>%s</span>", i));
        fw.write("</td>\n");

        fw.write("<td>\n");
        fw.write(String.format("<span class=\"question question_hidden\">%s</span>\n", getStringWithFirstCaptialLetter(record.getRussian())));

        if (!record.getRule().isEmpty()) {
            fw.write(String.format("<span class='support question_hidden' tabindex=\"%d\" data-title='%s'>\n", 10000 + i, record.getRule()));
            fw.write("<em>?</em>\n");
            fw.write("</span>\n");
        }

        fw.write(String.format("<input class='input-field' type=\"text\" placeholder=\"Введите перевод\" tabindex=\"%d\">\n", i));

        String english = record.getEnglish();
        english = english.replaceAll("(\\[.*?\\])", "</span><span class='tip show'>$1</span><span class='tip hide'>");
        english = getStringWithFirstCaptialLetter(english);

        fw.write(String.format("<span class='tip hide'>%s</span>\n", english));

        fw.write("<div class='check'> </div>\n");
        fw.write("</td>\n");
        fw.write("<td class=\"center\">\n");
        fw.write("<a class=\"music-button\"><img src=\"img/player_play.png\"></a>\n");
        fw.write(String.format("<audio class=\"player\" src=\"%s\" type=\"audio/mpeg\"></audio>\n", record.getSoundPath()));
        fw.write("<br>");
        fw.write("<input type=\"range\" step=\"0.1\" min=\"0.2\" max=\"2\" value=\"1\" class=\"speed\"/>\n");
        fw.write("<br>");
        fw.write("<span class=\"speed-text\">1.0</span>\n");
        fw.write("</td>\n");
        fw.write("</tr>\n");
        fw.flush();
    }

    private static final String STR_HEADER = "<!DOCTYPE html>\n" +
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
            "<section id=\"content\">\n" +
            "    <input class=\"shuffle_button\" type=\"button\" value=\"Shuffle\"/>\n" +
            "    <input class=\"show_button\" type=\"button\" value=\"Show all questions\"/>\n" +
            "    <input class=\"hide_button\" type=\"button\" value=\"Hide all questions\"/>\n" +
            "    <table class=\"table-sentences\">\n" +
            "        <caption>\n" +
            "            <h3>Английские фразы</h3>\n" +
            "        </caption>\n" +
            "        <tr>\n" +
            "            <th>Номер</th>\n" +
            "            <th>Русская версия / Английская версия</th>\n" +
            "            <th>Проигрыватель</th>\n" +
            "        </tr>\n";

    private static final String STR_FOOTER = "    </table>\n" +
            "</section>\n" +
            "</body>\n" +
            "<script src=\"js/scripts.js\"></script>\n" +
            "\n" + "<script src=\"js/utils.js\"></script>" +
            "</html>";

    private static String getStringWithFirstCaptialLetter(String source) {
        return Character.toUpperCase(source.charAt(0)) + source.substring(1);
    }
}

