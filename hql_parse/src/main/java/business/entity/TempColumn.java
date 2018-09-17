package business.entity;

public class TempColumn {
  private String id;
  private String colName;
  private int sensitive;
  private int number;
  private String tabId;

  public TempColumn() {
  }

  public TempColumn(String colName, int sensitive, int number) {
    this.colName = colName;
    this.sensitive = sensitive;
    this.number = number;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getColName() {
    return colName;
  }

  public void setColName(String colName) {
    this.colName = colName;
  }

  public int getSensitive() {
    return sensitive;
  }

  public void setSensitive(int sensitive) {
    this.sensitive = sensitive;
  }

  public int getNumber() {
    return number;
  }

  public void setNumber(int number) {
    this.number = number;
  }

  public String getTabId() {
    return tabId;
  }

  public void setTabId(String tabId) {
    this.tabId = tabId;
  }
}
