package business.entity;

public class AutoRoute {
  /**
   * 原始库名
   */
  private String originalBaseName;
  /**
   * 原始表名
   */
  private String originalTabName;
  /**
   * 当前库名
   */
  private String currentBaseName;
  /**
   * 路由到的库名
   */
  private String RouteBaseName;
  /**
   * 路由到的表名
   */
  private String RouteTabName;

  public AutoRoute() {
  }

  public AutoRoute(String routeBaseName, String routeTabName) {
    RouteBaseName = routeBaseName;
    RouteTabName = routeTabName;
  }

  public AutoRoute(String originalBaseName, String originalTabName, String currentBaseName, String routeBaseName, String routeTabName) {
    this.originalBaseName = originalBaseName;
    this.originalTabName = originalTabName;
    this.currentBaseName = currentBaseName;
    RouteBaseName = routeBaseName;
    RouteTabName = routeTabName;
  }

  public String getOriginalBaseName() {
    return originalBaseName;
  }

  public void setOriginalBaseName(String originalBaseName) {
    this.originalBaseName = originalBaseName;
  }

  public String getOriginalTabName() {
    return originalTabName;
  }

  public void setOriginalTabName(String originalTabName) {
    this.originalTabName = originalTabName;
  }

  public String getCurrentBaseName() {
    return currentBaseName;
  }

  public void setCurrentBaseName(String currentBaseName) {
    this.currentBaseName = currentBaseName;
  }

  public String getRouteBaseName() {
    return RouteBaseName;
  }

  public void setRouteBaseName(String routeBaseName) {
    RouteBaseName = routeBaseName;
  }

  public String getRouteTabName() {
    return RouteTabName;
  }

  public void setRouteTabName(String routeTabName) {
    RouteTabName = routeTabName;
  }
}
