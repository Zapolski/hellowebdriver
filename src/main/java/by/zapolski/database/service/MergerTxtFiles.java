package by.zapolski.database.service;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class MergerTxtFiles {

    private static final String SOURCE_PATH = "d:/Test/EnglishPhrases/info/sets/DB search/";
    //private static final String SOURCE_PATH = "d:/Test/EnglishPhrases/info/sets/Full search/";

    public static void main(String[] args) throws Exception {
        System.out.println(String.format("Каталог для обработки: [%s]", SOURCE_PATH));
        File[] files = new File(SOURCE_PATH)
                .listFiles(pathname -> pathname.getName().endsWith(".txt")
                        && !pathname.getName().contains("result")
                        && !pathname.getName().contains(","));

        Path output = Paths.get(SOURCE_PATH + "result.txt");

        if (files != null) {
            System.out.println(String.format("Найдено файлов для слияния: %d.", files.length));
            List<String> list = new ArrayList<>();
            for (File file : files) {
                list.addAll(Files.readAllLines(file.toPath()));
            }
            Files.write(output, list, StandardCharsets.UTF_8, StandardOpenOption.CREATE);
            System.out.println(String.format("Сформирован [%s]; записей: %d.", output.getFileName(), list.size()));
        } else {
            System.out.println("Не найдено ни одно файла для слияния.");
        }
    }
}
