package com.course.spring05.jdbc10;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Main {
  private static final Connection connection = JDBCUtil.getConnection();

  public static void main(String[] args) throws SQLException {
    try {
      connection.setAutoCommit(false);
      insert();
      select();
      update();
      connection.commit();
    } catch (Exception e) {
      connection.rollback();
    } finally {
      connection.close();
    }
  }

  private static void insert() throws SQLException {
    final String sql = "INSERT INTO students (name, grade) VALUES (?, ?);";
    PreparedStatement statement = connection.prepareStatement(sql);
    statement.setString(1, "prepared insert");
    statement.setInt(2, 2);
    statement.execute();
    log.info("数据添加成功!");
    statement.close();
  }

  private static void select() throws SQLException {
    final String sql = "SELECT * FROM students WHERE id = ?";
    final PreparedStatement statement = connection.prepareStatement(sql);
    statement.setInt(1, 1);
    ResultSet resultSet = statement.executeQuery();
    if (resultSet.next()) {
      int cid = resultSet.getInt("id");
      String cname = resultSet.getString("name");
      log.info(cid + "\t" + cname);
    } else {
      log.info("没有查询到结果!");
    }
    resultSet.close();
    statement.close();
  }

  private static void update() throws SQLException {
    final String sql = "UPDATE students SET name=? WHERE id=?";
    final PreparedStatement statement = connection.prepareStatement(sql);
    statement.setString(1, "update");
    statement.setInt(2, 1);
    int row = statement.executeUpdate();
    log.info(row + "行数据修改成功!");
    statement.close();
  }
}
