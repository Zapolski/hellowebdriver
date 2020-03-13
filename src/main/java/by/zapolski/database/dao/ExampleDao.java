package by.zapolski.database.dao;

import by.zapolski.database.exception.DaoSystemException;
import by.zapolski.database.model.Example;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Component
public class ExampleDao implements Dao<Integer, Example> {

    private static final String TABLE_NAME = "example";

    private static final String SQL_SELECT_ALL = "SELECT * FROM example";
    private static final String SQL_SELECT_BY_ID = "SELECT id,word_id,russian,english,sound,rule_id FROM example WHERE id = ?";
    private static final String SQL_INSERT_RECORD = "INSERT INTO example (word_id,russian,english,sound,rule_id) VALUES (?,?,?,?,?)";
    private static final String SQL_UPDATE_BY_ID = "UPDATE example SET word_id = ?, russian = ?, english = ?,sound = ?,rule_id = ? where id = ?;";
    private static final String SQL_DELETE_BY_ID = "DELETE FROM example WHERE id = ?;";
    private static final String SQL_SELECT_ALL_BY_WORD_ID = "select * from example where word_id = ?;";
    private static final String SQL_CLEAR_TABLE = "DELETE FROM " + TABLE_NAME;

    private ConnectorDB conn;

    public ExampleDao() {
        conn = new ConnectorDB();
    }

    public ExampleDao(ConnectorDB conn) {
        this.conn = conn;
    }

    @Override
    public List<Example> getAll() {
        List<Example> list = new ArrayList<>();
        try (PreparedStatement stmt = conn.getPreparedStatement(SQL_SELECT_ALL);
             ResultSet rs = stmt.executeQuery();) {
            while (rs.next()) {
                Example example = new Example();
                example.setId(rs.getInt(1));
                example.setWordId(rs.getInt(2));
                example.setRussian(rs.getString(3));
                example.setEnglish(rs.getString(4));
                example.setSound(rs.getString(5));
                example.setRuleId(rs.getInt(6));
                list.add(example);
            }
            return list;
        } catch (SQLException e) {
            throw new DaoSystemException("Error during getAll from word", e);
        }
    }

    @Override
    public boolean create(Example entity) {
        boolean flag = false;
        try (PreparedStatement stmt = conn.getPreparedStatement(SQL_INSERT_RECORD, Statement.RETURN_GENERATED_KEYS);) {
            stmt.setInt(1, entity.getWordId());
            stmt.setString(2, entity.getRussian());
            stmt.setString(3, entity.getEnglish());
            stmt.setString(4, entity.getSound());
            stmt.setInt(5, entity.getRuleId());
            stmt.execute();
            try (ResultSet generatedKeys = stmt.getGeneratedKeys();) {
                if (generatedKeys.next()) {
                    entity.setId(generatedKeys.getInt(1));
                }
            }
            flag = true;
        } catch (SQLException e) {
            throw new DaoSystemException("Error during creating record", e);
        }
        return flag;
    }

    @Override
    public Example getById(Integer id) {
        try (PreparedStatement stmt = conn.getPreparedStatement(SQL_SELECT_BY_ID);) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                Example example = new Example();
                example.setId(rs.getInt(1));
                example.setWordId(rs.getInt(2));
                example.setRussian(rs.getString(3));
                example.setEnglish(rs.getString(4));
                example.setSound(rs.getString(5));
                example.setId(rs.getInt(6));
                return example;
            }
        } catch (SQLException e) {
            throw new DaoSystemException("Error during select word by value", e);
        }
    }

    @Override
    public boolean update(Example entity) {
        boolean flag = false;
        try (PreparedStatement stmt = conn.getPreparedStatement(SQL_UPDATE_BY_ID);) {
            stmt.setInt(1, entity.getWordId());
            stmt.setString(2, entity.getRussian());
            stmt.setString(3, entity.getEnglish());
            stmt.setString(4, entity.getSound());
            stmt.setInt(5, entity.getRuleId());
            stmt.setInt(6, entity.getId());
            stmt.executeUpdate();
            flag = true;
        } catch (SQLException e) {
            throw new DaoSystemException("Error during updating record", e);
        }
        return flag;
    }

    @Override
    public boolean remove(Integer id) {
        boolean flag = false;
        try (PreparedStatement stmt = conn.getPreparedStatement(SQL_DELETE_BY_ID);) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
            flag = true;
        } catch (SQLException e) {
            throw new DaoSystemException("Error during removing record", e);
        }
        return flag;
    }

    public List<Example> getExamplesByWordId(Integer id) {
        List<Example> result = new ArrayList<>();
        try (PreparedStatement stmt = conn.getPreparedStatement(SQL_SELECT_ALL_BY_WORD_ID)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Example example = new Example();
                    example.setId(rs.getInt(1));
                    example.setWordId(rs.getInt(2));
                    example.setRussian(rs.getString(3));
                    example.setEnglish(rs.getString(4));
                    example.setSound(rs.getString(5));
                    example.setRuleId(rs.getInt(6));
                    result.add(example);
                }
                return result;
            }
        } catch (SQLException e) {
            throw new DaoSystemException("Error during getExamplesByWordId from word", e);
        }
    }

    public void clearTable() {
        try (PreparedStatement stmt = conn.getPreparedStatement(SQL_CLEAR_TABLE)) {
            stmt.execute();
        } catch (SQLException e) {
            throw new DaoSystemException("Error during clearing [" + TABLE_NAME + "] table", e);
        }
    }

    @Override
    public void close() {
        conn.close();
    }
}
