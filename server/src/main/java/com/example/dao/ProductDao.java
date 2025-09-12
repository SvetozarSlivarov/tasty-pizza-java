package com.example.dao;

import java.math.BigDecimal;
import java.sql.Connection;

public interface ProductDao {
    BigDecimal findBasePriceById(Connection cx, int productId) throws Exception;
}