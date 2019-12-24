package by.zapolski.database.service;

import by.zapolski.database.dao.ConnectorDB;
import by.zapolski.database.dao.RecordDao;
import by.zapolski.database.model.Record;

import java.io.File;
import java.util.List;

public class ServiceUtils {

    private ServiceUtils() {
    }

    private static final String WORDS_PATH = "d:\\test\\English\\words";

    public static void checkEmptyDirectoriesWithWords() {
        File directory = new File(WORDS_PATH);
        int dirCount = 0;
        int dirRemovedCount = 0;
        for (File currentDir : directory.listFiles()) {
            dirCount++;
            if (currentDir.isDirectory() && currentDir.listFiles().length == 0) {
                //currentDir.delete();
                System.out.println("Empty catalog: [" + currentDir.getName() + "] is removed");
                dirRemovedCount++;
                dirCount--;
            }
        }
        System.out.println("Total filled directories with words: " + dirCount);
        System.out.println("    Removed empty directories: " + dirRemovedCount);
    }

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
        System.out.println("    Missing records: " + missedCount);
        connectorDB.close();
    }

    public static void compareDbRecordsWithFiles() {
        ConnectorDB connectorDB = new ConnectorDB();
        RecordDao recordDao = new RecordDao(connectorDB);
        List<Record> records = recordDao.getAll();

        int count = 0;
        for (Record record : records) {
            File file = new File(WORDS_PATH + "\\" + record.getWord() + "\\" + record.getSoundPath());
            if (!file.exists()) {
                recordDao.removeById(record.getId());
                count++;
                System.out.println("For record with sound file [" + record.getSoundPath() + "] sound file is missing.");
            }
        }
        System.out.println("Total records: " + records.size());
        System.out.println("    Missing files: " + count + " was removed");
        connectorDB.close();
    }

    public static void main(String[] args) {
        ServiceUtils.checkEmptyDirectoriesWithWords();
        ServiceUtils.compareSoundFilesWithDbRecords();
        ServiceUtils.compareDbRecordsWithFiles();
    }
}
