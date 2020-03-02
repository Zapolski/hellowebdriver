package by.zapolski.database.service;

import by.zapolski.database.dao.ConnectorDB;
import by.zapolski.database.dao.RecordDao;
import by.zapolski.database.model.Record;

import java.io.File;
import java.util.List;

public class ServiceUtils {

    private ServiceUtils() {
    }

    private static final String WORDS_PATH = "d:\\Test\\EnglishPhrases\\words";

    // бежим по каталогам с озвучкой и удаляем пустые каталоги, если actionFlag = true;
    public static void checkEmptyDirectoriesWithWords(boolean actionFlag) {
        File directory = new File(WORDS_PATH);
        int dirCount = 0;
        int dirRemovedCount = 0;
        for (File currentDir : directory.listFiles()) {
            dirCount++;
            if (currentDir.isDirectory() && currentDir.listFiles().length == 0) {
                System.out.println("Empty catalog: [" + currentDir.getName() + "]");
                if (actionFlag) {
                    currentDir.delete();
                    System.out.println("Empty catalog: [" + currentDir.getName() + "] has been removed");
                }
                dirRemovedCount++;
                dirCount--;
            }
        }
        System.out.println("Total filled directories with words: " + dirCount);
        System.out.println("    Removed empty directories: " + dirRemovedCount);
    }

    // бежит по файлам и смотрить есть ли в базе запись
    public static void compareSoundFilesWithDbRecords() {
        ConnectorDB connectorDB = new ConnectorDB();
        RecordDao recordDao = new RecordDao(connectorDB);
        List<Record> records = recordDao.getAll();

        File directory = new File(WORDS_PATH);
        int missedCount = 0;
        int fileCount = 0;
        for (File currentDir : directory.listFiles()) {
            if (currentDir.isDirectory() && currentDir.listFiles().length != 0) {
                File[] files = currentDir.listFiles();
                for (File file : files) {
                    fileCount++;
                    if (records.stream().noneMatch(rec -> file.getName().equals(rec.getSoundPath()))) {
                        missedCount++;
                        System.out.println("Record for [" + file.getName() + "] is missing in DB");
                    }
                }
            }
        }
        System.out.println("Total files with sound: " + fileCount);
        System.out.println("    Amount of missed records in DB for these files: " + missedCount);
        connectorDB.close();
    }

    // бежит по базе и смотрит если файлы с озвучкой на диске и удаляем записи из базы если actionFlag = true;
    public static void compareDbRecordsWithFiles(boolean actionFlag) {
        ConnectorDB connectorDB = new ConnectorDB();
        RecordDao recordDao = new RecordDao(connectorDB);
        List<Record> records = recordDao.getAll();

        int count = 0;
        for (Record record : records) {
            File file = new File(WORDS_PATH + "\\" + record.getWord() + "\\" + record.getSoundPath());
            if (!file.exists()) {
                System.out.println("For record [id=" + record.getId() + "] with sound file [" + record.getSoundPath() + "] sound file is missing.");
                if (actionFlag) {
                    System.out.println("    Record [id=" + record.getId() + "] with sound file [" + record.getSoundPath() + "] has been removed.");
                    recordDao.removeById(record.getId());
                    count++;
                }
            }
        }
        System.out.println("Current amount of records in DB: " + (records.size() - count));
        System.out.println("    Actions: " + count + " records from DB was removed.");
        connectorDB.close();
    }

    public static void main(String[] args) {
        //ServiceUtils.checkEmptyDirectoriesWithWords(true);
        //ServiceUtils.compareSoundFilesWithDbRecords();
        ServiceUtils.compareDbRecordsWithFiles(false);
    }
}
