package business.entity;

import java.util.List;

public class TempTable {
  private String id;
  private String baseName;
  private String tabName;
  private List<TempColumn> tempColumns;

  public TempTable() {
  }

  public TempTable(String baseName, String tabName) {
    this.baseName = baseName;
    this.tabName = tabName;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getBaseName() {
    return baseName;
  }

  public void setBaseName(String baseName) {
    this.baseName = baseName;
  }

  public String getTabName() {
    return tabName;
  }

  public void setTabName(String tabName) {
    this.tabName = tabName;
  }

  public List<TempColumn> getTempColumns() {
    return tempColumns;
  }

  public void setTempColumns(List<TempColumn> tempColumns) {
    this.tempColumns = tempColumns;
  }
}
