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
        for (File currentDir : directory.listFiles()) {
            if (currentDir.isDirectory() && currentDir.listFiles().length == 0) {
                currentDir.delete();
                System.out.println("Empty catalog: [" + currentDir.getName() + "] is removed");
            }
        }
    }

    public static void compareSoundFilesWithDbRecords() {
        ConnectorDB connectorDB = new ConnectorDB();
        RecordDao recordDao = new RecordDao(connectorDB);
        File directory = new File(WORDS_PATH);
        int count = 0;
        for (File currentDir : directory.listFiles()) {
            if (currentDir.isDirectory() && currentDir.listFiles().length != 0) {
                File[] files = currentDir.listFiles();
                for (File file : files) {
                    Record record = recordDao.getRecordsBySoundPath(file.getName());
                    if (record.getSoundPath() == null) {
                        count++;
                        System.out.println("Record for [" + file.getName() + "] is missing in DB");
                    }
                }
            }
        }
        System.out.println("Missing records: " + count);
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
                count++;
                System.out.println("For record with sound file [" + record.getSoundPath() + "] sound file is missing.");
            }
        }
        System.out.println("Missing files: " + count);
        connectorDB.close();
    }


    public static void main(String[] args) {
        ServiceUtils.checkEmptyDirectoriesWithWords();
        ServiceUtils.compareSoundFilesWithDbRecords();
        ServiceUtils.compareDbRecordsWithFiles();
    }


}
