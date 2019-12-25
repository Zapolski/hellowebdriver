package by.zapolski.controller;

import by.zapolski.database.dao.RecordDao;
import by.zapolski.database.model.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class RecordController {

    @Autowired
    private RecordDao recordDao;

    @GetMapping("/records/{word}")
    public List<Record> getAllRecordsByWord(@PathVariable String word) {
        return recordDao.getRecordsByWord(word);
    }

}
