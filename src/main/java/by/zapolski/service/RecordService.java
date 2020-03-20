package by.zapolski.service;

import by.zapolski.database.model.Record;

import java.util.Set;

public interface RecordService {
    Set<Record> getRecordsByIds(int[] ids);
}
