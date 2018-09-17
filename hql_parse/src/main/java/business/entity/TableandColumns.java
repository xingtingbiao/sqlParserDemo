package business.entity;

import java.util.ArrayList;
import java.util.List;

public class TableandColumns {
  private String databaseName = "";

  private String tableName = "";

  private List<String> userColumns = new ArrayList<String>();

  private int columnCount = 0;

  public int getColumnCount() {
    return columnCount;
  }


  public void setColumnCount(int columnCount) {
    this.columnCount = columnCount;
  }

  public String getDatabaseName() {
    return databaseName;
  }

  public void setDatabaseName(String databaseName) {
    this.databaseName = databaseName;
  }


  public String getTableName() {
    return tableName;
  }

  public void setTableName(String tableName) {
    this.tableName = tableName;
  }

  public List<String> getUserColumns() {
    return userColumns;
  }

  public void setUserColumns(List<String> userColumns) {
    this.userColumns = userColumns;
  }

  @Override
  public String toString() {
    return "TableandColumns [databaseName=" + databaseName + ", tableName="
        + tableName + ", userColumns=" + userColumns + ", columnCount="
        + columnCount + "]";
  }

}
