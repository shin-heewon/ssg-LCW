package lib;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class ObjectDBIO {

  private Connection connection = null;

  private String db_url = "jdbc:mysql://localhost:3306/employees?characterEncoding=UTF-8&serverTimezone=UTC";
  private String db_id = "root";
  private String db_pwd = "asd1234";

  //db_url, db_id , db_pwd Setter

  public void setDb_url(String db_url) {
    this.db_url = db_url;
  }

  public void setDb_id(String db_id) {
    this.db_id = db_id;
  }

  public void setDb_pwd(String db_pwd) {
    this.db_pwd = db_pwd;
  }

  //DB Connection
  private boolean open() {
    try {
      Class.forName("com.mysql.cj.jdbc.Driver");
      connection = DriverManager.getConnection(db_url, db_id, db_pwd);
      return true;
    } catch (ClassNotFoundException e) {
      System.err.println(e.getMessage());
      return false;
    } catch (SQLException e) {
      System.err.println(e.getMessage());
      return false;
    }
  }


  //DB Disconnection
  protected boolean close() {

    try {
      connection.close();
      return true;
    } catch (SQLException e) {
      System.err.println(e.getMessage());
      return false;
    }

  }


  //query 실행시 search => select 쿼리문
  protected ResultSet execute(String query, ResultSet rs) {
    try {
      open();
      Statement obj = connection.createStatement();
      rs = obj.executeQuery(query);
      close();
    } catch (SQLException e) {
      System.err.println(e.getMessage());
    }
    return rs;
  }


  //query 실행  insert, delete,update 쿼리문
  protected boolean execute(String query) {
    boolean result1 = false;
    try {
      open();
      Statement obj = connection.createStatement();

      int result = obj.executeUpdate(query);
      if (result == 1) {
        result1 = true;
      } else {
        result1 = false;
      }
      close();
    } catch (SQLException e) {
      System.err.println(e.getMessage());
      return result1;
    }
    return result1;
  }
}
