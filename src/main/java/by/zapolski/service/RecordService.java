package by.zapolski.service;

import by.zapolski.database.model.Record;

import java.util.List;
import java.util.Set;

public interface RecordService {
    Set<Record> getRecordsByIds(int[] ids);

    List<Record> getRecordsByWord(String word);
}
