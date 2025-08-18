package com.example.dao.base;

import com.example.db.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractDao {

    @FunctionalInterface
    public interface Binder {
        void bind(PreparedStatement preparedStatement) throws SQLException;
    }

    @FunctionalInterface
    public interface Mapper<T> {
        T map(ResultSet resultSet) throws SQLException;
    }
    private static ResultSet executeQuery(PreparedStatement ps, Binder binder) throws SQLException {
        if (binder != null) binder.bind(ps);
        return ps.executeQuery();
    }
    protected <T> List<T> queryList(String sql, Binder binder, Mapper<T> mapper) throws SQLException {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = executeQuery(preparedStatement, binder)){
            List<T> out = new ArrayList<>();
            while (resultSet.next()) {
                out.add(mapper.map(resultSet));
            }
            return out;
        }
    }

    protected <T> T queryOne(String sql, Binder binder, Mapper<T> mapper) throws SQLException {
        List<T> list = queryList(sql, binder, mapper);
        return list.isEmpty() ? null : list.get(0);
    }

    protected int update(String sql, Binder binder) throws SQLException {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            if (binder != null) binder.bind(preparedStatement);
            return preparedStatement.executeUpdate();
        }
    }

    protected int updateReturningId(String sql, Binder binder) throws SQLException {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            if (binder != null) binder.bind(preparedStatement);
            int rows = preparedStatement.executeUpdate();
            if (rows > 0) try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                return resultSet.next() ? resultSet.getInt(1) : 0; }
            return 0;
        }
    }
}
