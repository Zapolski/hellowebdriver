package by.zapolski.controller;

import by.zapolski.database.dao.RecordDao;
import by.zapolski.database.dao.WordDao;
import by.zapolski.database.model.Record;
import by.zapolski.database.model.Word;
import by.zapolski.service.RecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;

@RestController
public class RecordController {

    @Autowired
    private RecordService recordService;

    @Autowired
    private RecordDao recordDao;

    @Autowired
    private WordDao wordDao;

    @GetMapping("/records/{word}")
    public List<Record> getAllRecordsByWord(@PathVariable String word) {
        return recordDao.getRecordsByWord(word);
    }

    @GetMapping("/records/words")
    public List<Word> getAllWords() {
        return wordDao.getAll();
    }

    @GetMapping("/records/query/{query}/{param}")
    public List<Record> getAllRecordsQueryString(@PathVariable String query, @PathVariable int param) {
        return recordDao.getRecordsByEnglishValueWithSqlLike(query, param);
    }

    @PutMapping("/records/{id}")
    public ResponseEntity<Record> updateRecord(@RequestBody Record record, @PathVariable int id) {
        Record recordsById = recordDao.getRecordsById(id);
        if (recordsById.getId() == 0) {
            return ResponseEntity.notFound().build();
        }
        try {
            recordDao.update(record);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("records")
    public ResponseEntity<Set<Record>> getRecordsByIds(@RequestParam int[] ids) {
        return new ResponseEntity<>(recordService.getRecordsByIds(ids), HttpStatus.OK);
    }


}
