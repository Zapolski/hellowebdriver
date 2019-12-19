package by.zapolski.database.dao;

import by.zapolski.database.exception.DaoBusinessException;
import by.zapolski.database.model.Example;
import by.zapolski.database.model.Record;
import by.zapolski.database.model.Rule;
import by.zapolski.database.model.Word;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RecordDao {

    private WordDao wordDao;
    private RuleDao ruleDao;
    private ExampleDao exampleDao;
    private ConnectorDB connectorDB;

    public RecordDao(ConnectorDB connectorDB) {
        this.connectorDB = connectorDB;
        wordDao = new WordDao(connectorDB);
        ruleDao = new RuleDao(connectorDB);
        exampleDao = new ExampleDao(connectorDB);
    }

    public boolean create(Record record) throws SQLException {
        connectorDB.setAutoCommit(false);
        Example example = new Example();

        if (record.getWord() != null && !record.getWord().isEmpty()){
            Word word = wordDao.getByValue(record.getWord());
            if (word == null){
                word = new Word();
                word.setValue(record.getWord());
                wordDao.create(word);
            }
            example.setWordId(word.getId());
        }else {
            throw new DaoBusinessException("Error during inserting RECORD: field [word] is null or empty");
        }

        if (record.getRule() != null){
            Rule rule = ruleDao.getByValue(record.getRule());
            if (rule == null){
                rule = new Rule();
                rule.setValue(record.getRule());
                ruleDao.create(rule);
            }
            example.setRuleId(rule.getId());
        }else {
            throw new DaoBusinessException("Error during inserting RECORD: field [rule] is null");
        }

        example.setEnglish(record.getEnglish());
        example.setRussian(record.getRussian());
        example.setSound("words/"+record.getWord()+"/"+record.getSoundPath());

        exampleDao.create(example);

        connectorDB.commit();
        connectorDB.setAutoCommit(true);
        return true;
    }

    public List<Record> getRecordsByWord(String queryWord){
        List<Record> records = new ArrayList<>();

        Word word = wordDao.getByValue(queryWord);
        if (word == null){
            System.out.println("There isn't word ["+queryWord+"] in DB.");
            return records;
        }

        System.out.println("Process word: "+word);
        List<Example> examples = exampleDao.getExamplesByWordId(word.getId());
        System.out.println("Founded "+examples.size()+" examples.");

        for (Example example: examples){
            Record record = new Record();
            record.setId(example.getId());
            record.setWord(word.getValue());
            record.setSoundPath(example.getSound());
            record.setEnglish(example.getEnglish());
            record.setRussian(example.getRussian());
            record.setRule(ruleDao.getById(example.getRuleId()).getValue());
            records.add(record);
        }
        return records;
    }
}
