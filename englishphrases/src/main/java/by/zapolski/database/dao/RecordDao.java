package by.zapolski.database.dao;

import by.zapolski.database.exception.DaoBusinessException;
import by.zapolski.database.exception.DaoSystemException;
import by.zapolski.database.model.Example;
import by.zapolski.database.model.Record;
import by.zapolski.database.model.Rule;
import by.zapolski.database.model.Word;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class RecordDao {

    private static final String SQL_SELECT_BY_ID = "SELECT example.id, word.value, example.russian, example.english, example.sound, rule.value \n" +
            "FROM word\n" +
            "JOIN example ON word.id = example.word_id\n" +
            "JOIN rule ON example.rule_id = rule.id\n" +
            "WHERE example.id = ?;";

    private static final String SQL_SELECT_BY_SOUND_PATH = "SELECT example.id, word.value, example.russian, example.english, example.sound, rule.value \n" +
            "FROM word\n" +
            "JOIN example ON word.id = example.word_id\n" +
            "JOIN rule ON example.rule_id = rule.id\n" +
            "WHERE example.sound = ?;";

    private static final String SQL_SELECT_BY_WORD = "SELECT example.id, word.value, example.russian, example.english, example.sound, rule.value \n" +
            "FROM word\n" +
            "JOIN example ON word.id = example.word_id\n" +
            "JOIN rule ON example.rule_id = rule.id\n" +
            "WHERE word.value = ?\n" +
            "ORDER BY example.id;";

    private static final String SQL_SELECT_ALL = "SELECT example.id, word.value, example.russian, example.english, example.sound, rule.value \n" +
            "FROM word\n" +
            "JOIN example ON word.id = example.word_id\n" +
            "JOIN rule ON example.rule_id = rule.id;";

    private static final String SQL_SELECT_ALL_WITH_LIKE = "SELECT example.id, word.value, example.russian, example.english, example.sound, rule.value\n" +
            "FROM word\n" +
            "JOIN example ON word.id = example.word_id\n" +
            "JOIN rule ON example.rule_id = rule.id\n" +
            //"WHERE UPPER(example.english) LIKE UPPER(?)\n" +
            "WHERE example.english ~ ?\n" +
            "ORDER BY example.id;";



    @Autowired
    private WordDao wordDao;
    @Autowired
    private RuleDao ruleDao;
    @Autowired
    private ExampleDao exampleDao;
    @Autowired
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

        if (record.getWord() != null && !record.getWord().isEmpty()) {
            Word word = wordDao.getByValue(record.getWord());
            if (word == null) {
                word = new Word();
                word.setValue(record.getWord());
                wordDao.create(word);
            }
            example.setWordId(word.getId());
        } else {
            throw new DaoBusinessException("Error during inserting RECORD: field [word] is null or empty");
        }

        if (record.getRule() != null) {
            Rule rule = ruleDao.getByValue(record.getRule());
            if (rule == null) {
                rule = new Rule();
                rule.setValue(record.getRule());
                ruleDao.create(rule);
            }
            example.setRuleId(rule.getId());
        } else {
            throw new DaoBusinessException("Error during inserting RECORD: field [rule] is null");
        }

        example.setEnglish(record.getEnglish());
        example.setRussian(record.getRussian());
        example.setSound(record.getSoundPath());

        exampleDao.create(example);
        record.setId(example.getId());

        connectorDB.commit();
        connectorDB.setAutoCommit(true);
        return true;
    }

    public List<Record> getRecordsByEnglishValueWithSqlLike(String query, int param) {
        query = "\\m"+query+"\\M";
        List<Record> result = new ArrayList<>();
        try (PreparedStatement stmt = connectorDB.getPreparedStatement(SQL_SELECT_ALL_WITH_LIKE)) {
            stmt.setString(1, query);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Record record = new Record();
                    record.setId(rs.getInt(1));
                    record.setWord(rs.getString(2));
                    record.setRussian(rs.getString(3));
                    record.setEnglish(rs.getString(4));
                    record.setSoundPath(rs.getString(5));
                    record.setRule(rs.getString(6));
                    result.add(record);
                }
                return result;
            }
        } catch (SQLException e) {
            throw new DaoSystemException("Error during [getRecordsByEnglishValueWithSqlLike] from word", e);
        }
    }

    public List<Record> getRecordsByWord(String queryWord) {
        List<Record> result = new ArrayList<>();
        try (PreparedStatement stmt = connectorDB.getPreparedStatement(SQL_SELECT_BY_WORD)) {
            stmt.setString(1, queryWord);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Record record = new Record();
                    record.setId(rs.getInt(1));
                    record.setWord(rs.getString(2));
                    record.setRussian(rs.getString(3));
                    record.setEnglish(rs.getString(4));
                    record.setSoundPath(rs.getString(5));
                    record.setRule(rs.getString(6));
                    result.add(record);
                }
                return result;
            }
        } catch (SQLException e) {
            throw new DaoSystemException("Error during [getRecordsByWord] from word", e);
        }
    }

    public Record getRecordsById(int id) {
        Record result = new Record();
        try (PreparedStatement stmt = connectorDB.getPreparedStatement(SQL_SELECT_BY_ID)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    result.setId(rs.getInt(1));
                    result.setWord(rs.getString(2));
                    result.setRussian(rs.getString(3));
                    result.setEnglish(rs.getString(4));
                    result.setSoundPath(rs.getString(5));
                    result.setRule(rs.getString(6));
                }
                return result;
            }
        } catch (SQLException e) {
            throw new DaoSystemException("Error during [getRecordsById] from word", e);
        }
    }

    public Record getRecordsBySoundPath(String soundPath) {
        Record result = new Record();
        try (PreparedStatement stmt = connectorDB.getPreparedStatement(SQL_SELECT_BY_SOUND_PATH)) {
            stmt.setString(1, soundPath);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    result.setId(rs.getInt(1));
                    result.setWord(rs.getString(2));
                    result.setRussian(rs.getString(3));
                    result.setEnglish(rs.getString(4));
                    result.setSoundPath(rs.getString(5));
                    result.setRule(rs.getString(6));
                }
                return result;
            }
        } catch (SQLException e) {
            throw new DaoSystemException("Error during [getRecordsBySoundPath] from word", e);
        }
    }

    public List<Record> getAll() {
        List<Record> result = new ArrayList<>();
        try (PreparedStatement stmt = connectorDB.getPreparedStatement(SQL_SELECT_ALL);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Record record = new Record();
                record.setId(rs.getInt(1));
                record.setWord(rs.getString(2));
                record.setRussian(rs.getString(3));
                record.setEnglish(rs.getString(4));
                record.setSoundPath(rs.getString(5));
                record.setRule(rs.getString(6));
                result.add(record);
            }
            return result;
        } catch (SQLException e) {
            throw new DaoSystemException("Error during [getRecordsByWord] from word", e);
        }
    }

    public void removeById(int id) {
        exampleDao.remove(id);
    }

    public Record update(Record record) throws SQLException {
        connectorDB.setAutoCommit(false);
        Example example = new Example();

        if (record.getWord() != null && !record.getWord().isEmpty()) {
            Word word = wordDao.getByValue(record.getWord());
            if (word == null) {
                word = new Word();
                word.setValue(record.getWord());
                wordDao.create(word);
            }
            example.setWordId(word.getId());
        } else {
            throw new DaoBusinessException("Error during updating RECORD: field [word] is null or empty");
        }

        if (record.getRule() != null) {
            Rule rule = ruleDao.getByValue(record.getRule());
            if (rule == null) {
                rule = new Rule();
                rule.setValue(record.getRule());
                ruleDao.create(rule);
            }
            example.setRuleId(rule.getId());
        } else {
            throw new DaoBusinessException("Error during updating RECORD: field [rule] is null");
        }

        example.setEnglish(record.getEnglish());
        example.setRussian(record.getRussian());
        example.setSound(record.getSoundPath());
        example.setId(record.getId());

        exampleDao.update(example);
        record.setId(example.getId());

        connectorDB.commit();
        connectorDB.setAutoCommit(true);
        return record;
    }
}
