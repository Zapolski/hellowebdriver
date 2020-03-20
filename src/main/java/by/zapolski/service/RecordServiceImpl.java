package by.zapolski.service;

import by.zapolski.database.dao.RecordDao;
import by.zapolski.database.model.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class RecordServiceImpl implements RecordService {

    @Autowired
    private RecordDao recordDao;

    public Set<Record> getRecordsByIds(int[] ids) {
        Set<Record> result = new HashSet<>();
        for (int id : ids) {
            Record record = recordDao.getRecordsById(id);
            if (record.getId() != 0) {
                result.add(record);
            }
        }
        return result;
    }
}
