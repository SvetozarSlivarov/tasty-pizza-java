package com.example.dao.base;

import com.example.db.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractDao {

    @FunctionalInterface
    public interface Binder {
        void bind(PreparedStatement ps) throws SQLException;
    }

    @FunctionalInterface
    public interface Mapper<T> {
        T map(ResultSet rs) throws SQLException;
    }

    protected <T> List<T> queryList(String sql, Binder binder, Mapper<T> mapper) throws SQLException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            if (binder != null) binder.bind(ps);
            try (ResultSet rs = ps.executeQuery()) {
                List<T> out = new ArrayList<>();
                while (rs.next()) out.add(mapper.map(rs));
                return out;
            }
        }
    }

    protected <T> T queryOne(String sql, Binder binder, Mapper<T> mapper) throws SQLException {
        List<T> list = queryList(sql, binder, mapper);
        return list.isEmpty() ? null : list.get(0);
    }

    protected int update(String sql, Binder binder) throws SQLException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            if (binder != null) binder.bind(ps);
            return ps.executeUpdate();
        }
    }

    protected int updateReturningId(String sql, Binder binder) throws SQLException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            if (binder != null) binder.bind(ps);
            int rows = ps.executeUpdate();
            if (rows > 0) try (ResultSet k = ps.getGeneratedKeys()) { return k.next() ? k.getInt(1) : 0; }
            return 0;
        }
    }
}
