package by.zapolski.database.dao;

import by.zapolski.database.exception.DaoSystemException;
import by.zapolski.database.model.Word;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Component
public class WordDao implements Dao<Integer, Word> {

    private static final String TABLE_NAME = "word";
    private static final String FIELD_ID = "id";
    private static final String FIELD_VALUE = "value";

    private static final String ALL_FIELDS_FOR_INSERT = "(" +
            FIELD_VALUE +
            ")";
    private static final String ALL_FIELDS_FOR_UPDATE = FIELD_VALUE + " = ?";

    private static final String SQL_SELECT_ALL = "SELECT * FROM " + TABLE_NAME;
    private static final String SQL_SELECT_BY_ID = "SELECT * FROM " + TABLE_NAME + " WHERE " + FIELD_ID + " = ?";
    private static final String SQL_INSERT_RECORD = "INSERT INTO " + TABLE_NAME + " " + ALL_FIELDS_FOR_INSERT + " VALUES (?)";
    private static final String SQL_UPDATE_BY_ID = "UPDATE " + TABLE_NAME + " SET " + ALL_FIELDS_FOR_UPDATE + " where id = ?;";
    private static final String SQL_DELETE_BY_ID = "DELETE FROM " + TABLE_NAME + " WHERE id = ?;";
    private static final String SQL_CLEAR_TABLE = "DELETE FROM " + TABLE_NAME;
    private static final String SQL_SELECT_BY_VALUE = "SELECT * FROM " + TABLE_NAME + " WHERE value = ?";

    private ConnectorDB conn;

    public WordDao() {
        conn = new ConnectorDB();
    }

    public WordDao(ConnectorDB conn) {
        this.conn = conn;
    }

    @Override
    public List<Word> getAll() {
        List<Word> list = new ArrayList<>();
        try (PreparedStatement stmt = conn.getPreparedStatement(SQL_SELECT_ALL);
             ResultSet rs = stmt.executeQuery();) {
            while (rs.next()) {
                Word word = new Word(rs.getInt(FIELD_ID), rs.getString(FIELD_VALUE));
                list.add(word);
            }
            return list;
        } catch (SQLException e) {
            throw new DaoSystemException("Error during getAll from word", e);
        }
    }

    @Override
    public boolean create(Word entity) {
        boolean flag = false;
        try (PreparedStatement stmt = conn.getPreparedStatement(SQL_INSERT_RECORD, Statement.RETURN_GENERATED_KEYS);) {
            stmt.setString(1, entity.getValue());
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
    public boolean update(Word entity) {
        boolean flag = false;
        try (PreparedStatement stmt = conn.getPreparedStatement(SQL_UPDATE_BY_ID);) {
            stmt.setString(1, entity.getValue());
            stmt.setInt(2, entity.getId());
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

    @Override
    public Word getById(Integer id) {
        try (PreparedStatement stmt = conn.getPreparedStatement(SQL_SELECT_BY_ID);) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                return new Word(rs.getInt(FIELD_ID), rs.getString(FIELD_VALUE));
            }
        } catch (SQLException e) {
            throw new DaoSystemException("Error during getting record by [" + FIELD_ID + "]", e);
        }
    }

    public Word getByValue(String word) {
        try (PreparedStatement stmt = conn.getPreparedStatement(SQL_SELECT_BY_VALUE)) {
            stmt.setString(1, word);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                return new Word(rs.getInt(FIELD_ID), rs.getString(FIELD_VALUE));
            }
        } catch (SQLException e) {
            throw new DaoSystemException("Error during getting record by [" + FIELD_VALUE + "]", e);
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
