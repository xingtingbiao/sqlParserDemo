package test_sqlparse;

import business.Business;
import org.apache.hadoop.hive.ql.parse.ASTNode;
import org.apache.hadoop.hive.ql.parse.ParseDriver;
import org.apache.hadoop.hive.ql.parse.ParseException;
import org.junit.Test;
import parser.base.TypeOne;
import parser.statement.TCustomSqlStatement;
import util.BusinessUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyTest {

  @Test
  public void test001() throws Exception {
    ParseDriver parse = new ParseDriver();
    try {
      String str02 = getStr07();
      ASTNode node = parse.parse(getStr02());
      ASTNode node01 = parse.parse("set role admin");
      // ASTNode node012 = parse.parse("set rowcount 0");
      ASTNode node011 = parse.parse("show tables");
      // System.out.println(node.toStringTree());
      ASTNode node2 = parse.parse("select count(1) as num, concat('a', 'b') from test where pt_d >= '20180502'or pt_d <= '20180601'");
      // ASTNode node5 = parse.parse("INSERT INTO `report_field_cfg` VALUES ('1', 'ODS_GAME_COUPON_ALL_INFO_DM', '1', 'id', '^[0-9]+$', null)");
      ASTNode node60 = parse.parse("with q1 as (select * from src where key= '5') from q1 select *");
      ASTNode node61 = parse.parse("with q1 as (with q2 as (select * from src where key= '5') from q2 select *) from q1 select *");
      ASTNode node62 = parse.parse("with q1 as (select * from src where key= '5'), q2 as (select * from src s2 where key = '4') select * from q1 union all select * from q2");
      ASTNode node51 = parse.parse("insert overwrite table test_insert select * from test_table");
      ASTNode node52 = parse.parse("insert into table test_insert select * from test_table");
      // ASTNode node22 = parse.parse("select * from test");
      ASTNode node23 = parse.parse("select t1.* from (select * from tmp.test) t1");
      // ASTNode node24 = parse.parse("select * from (select empno,ename,sal from emp)");
      // ASTNode node231 = parse.parse("select * from A where field1 ='34;12'");
      // ASTNode node3 = parse.parse("select a1,a2 from temp_uniontest_ta union select a1,a2 from temp_uniontest_tb");
//      ASTNode node4 = parse.parse("select id, name, age from test where (id = 1 or name like '%abc%') and age > 20");
//      ASTNode node40 = parse.parse("select id, name, age from test where id = 1");
//      ASTNode node401 = parse.parse("select id, name, age from test where id = 1 and name like '%abc%'");
//      ASTNode node402 = parse.parse("select id, name, age from test where id = 1 or name like '%abc%'");
//      ASTNode node403 = parse.parse("select id, name, age from test where count(id) = 1");
//      ASTNode node405 = parse.parse("select id, name, age from test where (id = 1 or name like '%abc%')");
//      ASTNode node41 = parse.parse("select id, name, age from test where (id = 1 or name like '%abc%') and (age > 20 or age < 30)");
//      ASTNode node42 = parse.parse("select id, name, age from test where (id = 1 or name like '%abc%') or (age > 20 and age < 30)");
      // ASTNode node43 = parse.parse("select id, name, age from test where (id = 1 or name like '%abc%') or (age > 20 and age < 30) and age = 18");
      node.toString();
      // TCustomSqlStatement statement = parseTree(node);
      TCustomSqlStatement statement = TCustomSqlStatement.parseASTNode(node);
      // BloodRelation bloodRelation = new BloodRelation((TSelectSqlStatement) statement);
      // bloodRelation.analysisBlood();
      System.out.println(node.toStringTree());
      System.out.println(node.dump());
    } catch (ParseException e) {
      System.out.println("error---> " + e.getMessage());
      e.printStackTrace();
    }
  }


  @Test
  public void test002() throws Exception {
    HashMap<String, List<String>> mapFields = new HashMap<>();
    List<String> cols = new ArrayList<>();
    cols.add("name");
    cols.add("pass");
    cols.add("imei");
    // mapFields.put("ut", cols);
    // mapFields.put("dwd_eqp_dev_id_mapping_ds", cols);
    mapFields.put("dwd_eqp_device_ds_his", cols);
    HashMap<String, Integer> mapFuns = new HashMap<>();
    mapFuns.put("count", 0);
    // List<TResultColumn> columns = new BloodRelation().filterSensitiveFields(getStr05(), mapFields, mapFuns);
    // System.out.println(columns);
  }

  @Test
  public void test003() throws Exception {
    List<String> strings = BusinessUtil.splitStatements(getStr06());
    System.out.println(strings);
  }

  @Test
  public void test004() throws Exception {
    List<TypeOne> atomics = new Business().getAtomics(getStr05());
    System.out.println(atomics);
  }

  @Test
  public void test005() throws ParseException {
    Map<String, Integer> whiteFunctions = new HashMap<>();
    Map<String, List<String>> tableandSensitiveColumns = new HashMap<>();
    List<String> lists = new ArrayList<>();
    lists.add("device_name");
    lists.add("new_user_cnt");
    tableandSensitiveColumns.put("tmp_ads_hispace_hota_user_active_dm", lists);
    Business business = new Business();
    business.filterSensitiveFields(TestSqls.s01, tableandSensitiveColumns, whiteFunctions);
    // String regex = "[&<>\"':\\[\\]$()%+\\/\\\\#`*,-;=|^]";
    System.out.println("done");
  }

  @Test
  public void test006() throws ParseException {
    Map<String, Integer> whiteFunctions = new HashMap<>();
    Map<String, List<String>> tableandSensitiveColumns = new HashMap<>();
    List<String> lists = new ArrayList<>();
    lists.add("belong_area_cn_name");
    tableandSensitiveColumns.put("dwd_loc_country_ds", lists);
    Business business = new Business();
    business.filterSensitiveFields(TestSqls.s1011, tableandSensitiveColumns, whiteFunctions);
    // String regex = "[&<>\"':\\[\\]$()%+\\/\\\\#`*,-;=|^]";
    System.out.println("done");
  }

  private String getStr02() {
    String s01 = "select\n" +
        "    count(t1.imei)  as taobao_users, t1.name\n" +
        "FROM\n" +
        "(\n" +
        "    select\n" +
        "        imei, name\n" +
        "    from temp.tmp_dwx383983_apps_preinstall_and_hot_users_3 t\n" +
        "    where lower(package_name)='com.taobao.taobao' and app_ver='5.9.2.7' or aaa = 'bbb'\n" +
        ")t1\n" +
        "where !IsEmpty(t1.imei)";
    String s02 = "select id, name from tmp.t1";
    String s021 = "select t.id, t.name from testTable t";
    String s022 = "select tmp.count1(id), name from tmp.t1";
    String s023 = "select tmp.count1(id) as ct, fun1(fun2(fun3(tmp.name))), fun4(v1, v2, v3), CASE WHEN card_type='银行卡' THEN 'hwpaybankcard_30days_all_pay_users' WHEN card_type='交通卡' THEN 'hwpaybuscard_30days_all_pay_users' end as dim_code, temp.tmp.t1.name, age from tmp.t1 where id = '1' group by name having count1(id) = 5 order by id desc";
    String s031 = "select * from (select id, name from t1) tmp";
    String s032 = "select tmp.* from (select t1.id, t1.name from (select id, name from test1) t1) tmp";
    String s03 = "select tmp.id, tmp.name from (select id, name from t1)tmpTable";
    String s04 = "select tmp.a1, tmp.a2 from (select a1,a2 from temp_uniontest_ta union all select a1,a2 from temp_uniontest_tb union all select a1,a2 from temp_uniontest_tc union all select a1,a2 from temp_uniontest_td)tmp";
    String s041 = "select a1,a2 from temp_uniontest_ta union select a1,a2 from temp_uniontest_tb union all select a1,a2 from temp_uniontest_tc union all select a1,a2 from temp_uniontest_td";
    String s0411 = "select a1,a2 from temp_uniontest_ta union select a1,a2 from temp_uniontest_tb union all select a1,a2 from temp_uniontest_tc union all select a1,a2 from temp_uniontest_td union all select a1,a2 from temp_uniontest_te";
    String s042 = "select a1,a2 from temp_uniontest_ta union select a1,a2 from temp_uniontest_tb union distinct select a1,a2 from temp_uniontest_tc";
    String s051 = "SELECT a.val, b.val, c.val FROM a left semi JOIN b ON (a.key = b.key1 or a.id = b.id) JOIN (select val from test)c";
    String s05 = "SELECT a.val, b.val, c.val FROM a left JOIN b ON (a.key = b.key1) JOIN c ON (c.key = b.key1)";
    String s06 = "select t1.id, t1.name, t1.age from userTable t1 where t1.id = '1' or t1.id <> 2 or t1.id != 1 and t1.name like '%abc%' or t1.age <= 20 and t1.age >= 10 or t1.age < 20 and t1.age > 10 or t1.age between 10 and 20";
    String s07 = "select t2.id, fun1(count(t2.name), t2.name) as cn, t2.age, t2.pass from (select t1.id, t1.name, t1.age, t1.pass from (select id, name, age, pass from ut) t1) t2";
    String s08 = "DROP TABLE IF EXISTS temp.tmp_ads_cloudservice_kpi2_otherservice_value_dm_pay";
    String s09 = "SET mapred.reduce.tasks = 1 ";
    return s0411;
  }

  private String getStr03() {
    String s01 = "create table IF NOT EXISTS temp.tmp_lwx509142_apps_preinstall_and_hot_users_4 as\n" +
        "select \n" +
        "    package_name,\n" +
        "    app_ver,\n" +
        "    imei\n" +
        "from dwd_onl_device_app_install_statis_dm t";
    String s03 = "create table if not exists temp.tmp_xwx526227_20180118_down_time as  \n" +
        "                  SELECT      t121.device_id AS device_id, \n" +
        "                   t121.dev_app_id AS dev_app_id, \n" +
        "                   IF(t121.message_source = 0,'app_market', IF(t121.message_source IN(1,5), 'game_center', 'other')) AS message_source, \n" +
        "                   SUM(IF(t122.oper_type = '1' AND t122.info6 LIKE '%RENEW%' AND t122.info6 LIKE '%UPGRADE%' AND t122.info7 LIKE '%RENEW%' AND t122.info7 LIKE '%UPGRADE%'AND t122.result = '0' and !(t122.Info7 rlike 'package-'),t122.info3,0))/count(1) AS not_upgrade_apk_size, \n" +
        "                   SUM(IF(t122.oper_type = '1' AND t122.info6 LIKE '%RENEW%' AND t122.info6 LIKE '%UPGRADE%' AND t122.info7 LIKE '%RENEW%' AND t122.info7 LIKE '%UPGRADE%' AND t122.result = '0' and !(t122.Info7 rlike 'package-'),t122.info2 - t122.info1,0))/count(1) AS not_upgrade_apk_download_time, \n" +
        "                   SUM(IF(t122.oper_type = '1' AND (t122.info6 LIKE '%RENEW%' OR t122.info6 LIKE '%UPGRADE%' OR t122.info7 LIKE '%RENEW%'  OR t122.info7 LIKE '%UPGRADE%')AND t122.result = '0',t122.info3,0))/count(1) AS upgrade_apk_size, \n" +
        "                   SUM(IF(t121.oper_type = '1'AND (t121.info6 LIKE '%RENEW%' OR t121.info6 LIKE '%UPGRADE%' OR t121.info7 LIKE '%RENEW%' OR t121.info7 LIKE '%UPGRADE%') AND t121.result = '0',t121.info2 - t121.info1,0))/count(1) AS upgrade_apk_download_time \n" +
        "                  FROM    (     SELECT \n" +
        "                      dev_app_id AS dev_app_id     FROM    (SELECT \n" +
        "                      t2.app_unique_id     AS hispace_app_id, \n" +
        "                      t1.app_id            AS dev_app_id    FROM    ( \n" +
        "                      SELECT            test.app_id        FROM  \n" +
        "                          bicoredata.dwd_onl_hispace_app_info_ds as test \n" +
        "                      WHERE \n" +
        "                          test.app_first_class = '游戏' AND test.pt_d = '$date' \n" +
        "                      GROUP BY test.app_id    )t1    LEFT OUTER JOIN \n" +
        "                  (        SELECT             *, \n" +
        "                          app_unique_id        FROM  \n" +
        "                          bicoredata.dwd_onl_disting_ver_app_ds \n" +
        "                      WHERE             pt_d = '$date' \n" +
        "                      GROUP BY app_id,app_unique_id    )t2 \n" +
        "                  ON t1.app_id = t2.app_id) a    GROUP BY dev_app_id \n" +
        "                  ) t122            LEFT OUTER JOIN            ( \n" +
        "                              SELECT  \n" +
        "                                  IF(SUBSTR(app_id,1,1)='S',SUBSTR(app_id,2),app_id)      AS dev_app_id, \n" +
        "                                  imei                                                    AS device_id, \n" +
        "                                  CAST(hispace_client_type_cd AS INT)                     AS message_source, \n" +
        "                                  oper_result_cd                                    AS result, \n" +
        "                                  down_install_oper_type_cd                               AS oper_type, \n" +
        "                                  CAST(non_stru_field1 AS BIGINT)                         AS info1, \n" +
        "                                  CAST(non_stru_field2 AS BIGINT)                         AS info2, \n" +
        "                                  CAST(non_stru_field3 AS BIGINT)                         AS info3, \n" +
        "                                  AVG(non_stru_field5)                                  AS info5, \n" +
        "                                  UPPER(non_stru_field6)                                  AS info6, \n" +
        "                                  UPPER(non_stru_field7)                                  AS info7 \n" +
        "                              FROM  \n" +
        "                                  bicoredata.dwd_evt_hispace_down_install_log_hm \n" +
        "                              WHERE  \n" +
        "                   pt_d< 20180104 and pt_d > 20180101 \n" +
        "                                  AND down_install_oper_type_cd IN ('1','2') \n" +
        "                          )t121 \n" +
        "                          ON t122.dev_app_id = t121.dev_app_id \n" +
        "                          GROUP BY                t121.device_id, \n" +
        "                              t121.dev_app_id, \n" +
        "                              IF(t121.message_source = 0,'app_market', IF(t121.message_source IN(1,5), 'game_center', 'other'))";
    String s02 = "CREATE EXTERNAL TABLE IF NOT EXISTS biads.ads_cloudservice_kpi2_otherservice_value_dm \n" +
        "(\n" +
        "    dim_code    VARCHAR(128) COMMENT '维度代号',\n" +
        "    dim_name    VARCHAR(256) COMMENT '维度名称',\n" +
        "    dim_val     DOUBLE       COMMENT '维度值'\n" +
        ")\n" +
        "COMMENT 'KPI2.0其他非通用业务统计结果表'\n" +
        "PARTITIONED BY (pt_d VARCHAR(8) COMMENT '天分区',pt_service VARCHAR(256) COMMENT '业务分区')\n" +
        "ROW FORMAT DELIMITED\n" +
        "FIELDS TERMINATED BY '\\001'                                        \n" +
        "LINES TERMINATED BY '\\n'\n" +
        "STORED AS ORC\n" +
        "LOCATION '/AppData/BIProd/ADS/Common/cloudservice/ads_cloudservice_kpi2_otherservice_value_dm'\n" +
        "TBLPROPERTIES('orc.compress'='ZLIB')";
    return s03;
  }

  private String getStr04() {
    String s01 = "INSERT INTO tmp.tTest (a, b, c, d, e, f) VALUES ('1', 'ODS_GAME_COUPON_ALL_INFO_DM', '1', 'id', '^[0-9]+$', null)";
    String s02 = "INSERT OVERWRITE TABLE biads.ads_cloudservice_kpi2_otherservice_value_dm\n" +
        "PARTITION (pt_d='$date', pt_service='pay')\n" +
        "SELECT\n" +
        "    dim_code\n" +
        "    ,dim_name\n" +
        "    ,dim_val\n" +
        "FROM biads.ads_trade_bus_cnt_dm";

    return s01;
  }

  private String getStr05() {
    String s01 = "ALTER TABLE employee RENAME TO emp";
    String s02 = "SELECT\r\n" +
        "    t.imei  AS  all_user_cnt\r\n" +
        "FROM\r\n" +
        "(\r\n" +
        "SELECT\r\n" +
        "    IF(!ISEMPTY(t2.uuid),t2.uuid,t1.imei)     AS  imei\r\n" +
        "FROM\r\n" +
        "(\r\n" +
        "    SELECT\r\n" +
        "        IF(!ISEMPTY(imei),imei,android_id)     AS   imei\r\n" +
        "    FROM dwd_cam_adv_show_log_dm\r\n" +
        "    WHERE pt_d >= '20171101' \r\n" +
        "          AND pt_d <= '20171111' \r\n" +
        "          AND adv_type='splash'\r\n" +
        "          AND adv_bill_mode_cd = 'CPT'\r\n" +
        "          AND package_name in ('com.android.mediacenter','com.huawei.hwvplayer','com.huawei.hwvplayer.youku','com.huawei.himovie','com.huawei.hwireader','com.huawei.hnreader')\r\n" +
        "    GROUP BY IF(!ISEMPTY(imei),imei,android_id)\r\n" +
        ")t1\r\n" +
        "\r\n" +
        "LEFT OUTER JOIN\r\n" +
        "\r\n" +
        "(\r\n" +
        "    SELECT\r\n" +
        "        TRIM(imei)                         AS           imei\r\n" +
        "       ,LOWER(TRIM(did))                   AS           uuid\r\n" +
        "    FROM dwd_eqp_dev_id_mapping_ds\r\n" +
        "    WHERE pt_d = '20171111'\r\n" +
        "        AND !IsEmpty(TRIM(imei))\r\n" +
        "        AND IsDeviceIdLegal(imei)\r\n" +
        "    GROUP BY LOWER(TRIM(did)),TRIM(imei)\r\n" +
        ")t2\r\n" +
        "\r\n" +
        "ON t1.imei = t2.imei\r\n" +
        "\r\n" +
        "GROUP BY IF(!ISEMPTY(t2.uuid),t2.uuid,t1.imei)\r\n" +
        ")t";
    String s03 = "SELECT \n"
        + "SUM(IF(a1.create_date = '2017-08-07', 1, 0))                                                      AS inc_users,\n"
        + "SUM(IF(a1.create_date = DATE_SUB('2017-08-07',1) AND a2.update_date = '2017-08-07', 1, 0))         AS new_user_1dy_retain_cnt,\n"
        + "SUM(IF(a1.create_date = DATE_SUB('2017-08-07',3) AND a2.update_date = '2017-08-07', 1, 0))         AS new_user_3dy_retain_cnt,\n"
        + "SUM(IF(a1.create_date = DATE_SUB('2017-08-07',7) AND a2.update_date = '2017-08-07', 1, 0))         AS new_user_7dy_retain_cnt,\n"
        + "SUM(IF(a1.create_date = DATE_SUB('2017-08-07',7) AND a2.update_date = '2017-08-07', 1, 0))         AS new_user_7dy_retain_cnt,\n"
        + "SUM(IF(a1.create_date = DATE_SUB('2017-08-07',17) AND a2.update_date = '2017-08-07', 1, 0))        AS new_user_17dy_retain_cnt,\n"
        + "SUM(IF(a1.create_date = DATE_SUB('2017-08-07',30) AND a2.update_date = '2017-08-07', 1, 0))        AS new_user_30dy_retain_cnt,\n"
        + "a1.pt_service                                                                                      AS pt_service\n"
        + "FROM\n" + "(\n" + "SELECT\n"
        + "up_id                                                           AS id,\n"
        + "MIN(first_usage_time)                                           AS create_date,\n"
        + "pt_service\n"
        + "FROM biads.ads_cloudservice_kpi2_user_info_dm\n"
        + "WHERE pt_d = '20170807' AND pt_type = 'B0' AND pt_service IN ('hw_cloud','phoneservice')\n"
        + "GROUP BY pt_service,up_id\n" + "HAVING id IS NOT NULL\n"
        + ") a1\n" + "LEFT JOIN\n" + "(\n" + "SELECT\n"
        + "up_id                                                   AS id,\n"
        + "MAX(last_usage_time)                                    AS update_date,\n"
        + "pt_service                                              as service\n"
        + "FROM biads.ads_cloudservice_kpi2_user_info_dm\n"
        + "WHERE pt_d = '20170807' AND pt_type = 'B2' AND pt_service IN ('hw_cloud','phoneservice')\n"
        + "GROUP BY pt_service,up_id\n" + "HAVING id IS NOT NULL\n"
        + ") a2\n" + "ON a1.pt_service=a2.service and a1.id=a2.id\n"
        + "group by pt_service\n" + "limit 30";
    String s04 = "select * from (select \r\n" +
        " t1.imei ,  t2.imei as imei2,t2.prod_name \r\n" +
        " FROM(SELECT imei\r\n" +
        " FROM bicoredata.dwd_sal_order_pay_ds\r\n" +
        " WHERE pt_d='20180410'  and to_date(txn_finish_time)='2018-04-10'\r\n" +
        " AND(dev_app_id='10393298' OR dev_app_id='10733029')   AND pay_status_cd in ('0','3') AND !IsEmpty(pay_up_id))t1\r\n" +
        " LEFT OUTER JOIN\r\n" +
        " (SELECT  imei,prod_name\r\n" +
        " FROM bicoredata.dwd_eqp_device_ds_his\r\n" +
        " WHERE end_date>'20180410' and start_date<='20180410')t2\r\n" +
        " ON t1.imei=t2.imei)t \r\n" +
        " where isempty(prod_name)  limit 100";
    return s04;
  }

  private String getStr06() {
    String s01 = "with ssci as (\n" +
        "   select ss_customer_sk customer_sk\n" +
        "         ,ss_item_sk item_sk\n" +
        "   from store_sales,date_dim\n" +
        "   where ss_sold_date_sk = d_date_sk\n" +
        "     and d_month_seq between 1211 and 1211 + 11\n" +
        "     and store_sales.pt_d=20180516\n" +
        "     and date_dim.pt_d=20180516\n" +
        "   group by ss_customer_sk\n" +
        "           ,ss_item_sk),\n" +
        "   csci as(\n" +
        "    select cs_bill_customer_sk customer_sk\n" +
        "         ,cs_item_sk item_sk\n" +
        "   from catalog_sales,date_dim\n" +
        "   where cs_sold_date_sk = d_date_sk\n" +
        "     and d_month_seq between 1211 and 1211 + 11\n" +
        "     and date_dim.pt_d=20180516\n" +
        "     and catalog_sales.pt_d=20180516\n" +
        "   group by cs_bill_customer_sk\n" +
        "           ,cs_item_sk)\n" +
        "    select sum(case when ssci.customer_sk is not null and csci.customer_sk is null then 1 else 0 end) store_only\n" +
        "         ,sum(case when ssci.customer_sk is null and csci.customer_sk is not null then 1 else 0 end) catalog_only\n" +
        "         ,sum(case when ssci.customer_sk is not null and csci.customer_sk is not null then 1 else 0 end) store_and_catalog\n" +
        "   from ssci full outer join csci on (ssci.customer_sk=csci.customer_sk\n" +
        "                                  and ssci.item_sk = csci.item_sk)\n" +
        "   limit 100";
    return s01;
  }

  private String getStr07() {
    String s01 = "FROM   temp.tmp_20180618_00385648_vmall_flow_5 t\n" +
        "INSERT OVERWRITE TABLE temp.tmp_20180618_00385648_vmall_flow_0 PARTITION(pt_d='20180101') " +
        "select plat,cook_id,row_id,follow_page_url,follow_page,follow_page_type,super_page_url,super_page,super_page_type,access_mode,provider_name,channel_name where t.pt_d='20180101'\n" +
        "INSERT OVERWRITE TABLE temp.tmp_20180618_00385648_vmall_flow_0 PARTITION(pt_d='20180102') " +
        "select plat,cook_id,row_id,follow_page_url,follow_page,follow_page_type,super_page_url,super_page,super_page_type,access_mode,provider_name,channel_name where t.pt_d='20180102'";

    String s02 = "FROM   temp.tmp_20180618_00385648_vmall_flow_5 t\n" +
        "INSERT OVERWRITE TABLE temp.tmp_20180618_00385648_vmall_flow_0" +
        "select plat,cook_id,row_id,follow_page_url,follow_page,follow_page_type,super_page_url,super_page,super_page_type,access_mode,provider_name,channel_name where t.pt_d='20180101'\n" +
        "INSERT OVERWRITE TABLE temp.tmp_20180618_00385648_vmall_flow_1" +
        "select plat,cook_id,row_id,follow_page_url,follow_page,follow_page_type,super_page_url,super_page,super_page_type,access_mode,provider_name,channel_name where t.pt_d='20180102'";
    return s01;
  }

  private String getStr08() {
    String s01 = "select \n" +
        "    t21.app_unique_id AS app_unique_id,\n" +
        "    t21.app_id AS app_id,\n" +
        "    t22.app_cn_name AS app_cn_name,\n" +
        "    t22.game_type_cd AS game_sign\n" +
        "FROM \n" +
        "(\n" +
        "    select \n" +
        "        app_unique_id,\n" +
        "        SUBSTR(MAX(CONCAT(IF(LENGTH(app_upload_time) < 19 ,'0000-00-00 00:00:00',SUBSTR(app_upload_time,1,19)),IF(substr(app_upload_time,1,1)='S',substr(app_id,2),app_id))),20) AS app_id\n" +
        "    FROM dwd_onl_disting_ver_app_ds\n" +
        "    where pt_d = '$date' and !isEmpty(app_id) and !isEmpty(app_unique_id)\n" +
        "    GROUP BY app_unique_id\n" +
        ")t21\n" +
        "JOIN \n" +
        "(        \n" +
        "    select \n" +
        "        package_name,\n" +
        "        SUBSTR(MAX(CONCAT(IF(LENGTH(app_upload_time) < 19 ,'0000-00-00 00:00:00',SUBSTR(app_upload_time,1,19)),IF(substr(app_upload_time,1,1)='S',substr(app_id,2),app_id))),20) AS app_id,\n" +
        "        SUBSTR(MAX(CONCAT(IF(LENGTH(app_upload_time) < 19 ,'0000-00-00 00:00:00',SUBSTR(app_upload_time,1,19)),app_cn_name)),20) AS app_cn_name,\n" +
        "        MAX(game_type_cd) AS game_type_cd\n" +
        "    FROM dwd_onl_hispace_app_info_ds    \n" +
        "    where pt_d = '$date' and app_first_class = '游戏' and !isEmpty(app_id) and !isEmpty(app_cn_name) and !isEmpty(package_name) and game_type_cd in ('1','2')\n" +
        "    GROUP BY package_name\n" +
        ")t22\n" +
        "on t21.app_id = t22.app_id";
    String s02 = "SELECT\n" +
        "    sum_up_ids,\n" +
        "    inc_up_login_cnt,\n" +
        "    daily_active_up_ids,\n" +
        "    30dy_active_up_ids,\n" +
        "    7dy_active_up_ids,\n" +
        "    ROUND(IF(ISNULL(ytday_inc_up_user_cnt) OR ytday_inc_up_user_cnt = 0,  0, COALESCE(one_day_retentions, CAST(0 AS BIGINT)) / ytday_inc_up_user_cnt) ,4) AS up_user_1dy_retain_rate,\n" +
        "    ROUND(IF(ISNULL(three_day_ago_new_users) OR three_day_ago_new_users = 0,  0, COALESCE(three_day_retentions, CAST(0 AS BIGINT)) / three_day_ago_new_users) ,4) AS up_user_3dy_retain_rate,\n" +
        "    ROUND(IF(ISNULL(seven_day_ago_new_users) OR seven_day_ago_new_users = 0,  0, COALESCE(seven_day_retentions, CAST(0 AS BIGINT)) / seven_day_ago_new_users) ,4) AS up_user_7dy_retain_rate,\n" +
        "    pt_service\n" +
        "FROM\n" +
        "(\n" +
        "    SELECT\n" +
        "        pt_service,\n" +
        "        SUM(1)                                    AS sum_up_ids,\n" +
        "        SUM(IF(first_access_date='$date_ep',last_access_date,0)) AS inc_up_login_cnt,\n" +
        "        SUM(IF(last_access_date='$date_ep',last_access_date,0))  AS daily_active_up_ids,       \n" +
        "        SUM(IF(last_access_date>=DATE_SUB('$date_ep',29) AND last_access_date <= '$date_ep', 1, 0)) AS 30dy_active_up_ids,\n" +
        "        SUM(IF(last_access_date>=DATE_SUB('$date_ep',6) AND last_access_date <= '$date_ep', 1, 0))  AS 7dy_active_up_ids,\n" +
        "        SUM(IF(first_access_date=DATE_SUB('$date_ep',1), 1, 0)) AS ytday_inc_up_user_cnt,\n" +
        "        SUM(IF(first_access_date=DATE_SUB('$date_ep',1) AND last_access_date = '$date_ep', 1, 0))  AS one_day_retentions,\n" +
        "        SUM(IF(first_access_date=DATE_SUB('$date_ep',3), 1, 0)) AS three_day_ago_new_users,\n" +
        "        SUM(IF(first_access_date=DATE_SUB('$date_ep',3) AND last_access_date= '$date_ep', 1, 0))  AS three_day_retentions,\n" +
        "        SUM(IF(first_access_date=DATE_SUB('$date_ep',7), 1, 0)) AS seven_day_ago_new_users,\n" +
        "        SUM(IF(first_access_date=DATE_SUB('$date_ep',7) AND last_access_date = '$date_ep', 1, 0)) AS seven_day_retentions\n" +
        "    FROM\n" +
        "    (\n" +
        "        SELECT \n" +
        "            pt_service,\n" +
        "            up_id,\n" +
        "            MIN(TO_DATE(first_usage_time)) AS first_access_date,\n" +
        "            MAX(TO_DATE(last_usage_time))  AS last_access_date\n" +
        "        FROM\n" +
        "            dws_up_service_active_dm\n" +
        "        WHERE \n" +
        "            pt_d='$date'\n" +
        "        AND pt_service IN ('youkuvideo','music','movie','hwread','hnread','sohuvideo')\n" +
        "        GROUP BY pt_service,up_id\n" +
        "                 \n" +
        "        UNION ALL\n" +
        "\n" +
        "        SELECT \n" +
        "            CASE WHEN pt_service IN ('youkuvideo','sohuvideo') THEN 'video'\n" +
        "                 WHEN pt_service IN ('hwread','hnread')        THEN 'reader'\n" +
        "                 ELSE NULL END AS pt_service,\n" +
        "            up_id,\n" +
        "            MIN(TO_DATE(first_usage_time)) AS first_access_date,\n" +
        "            MAX(TO_DATE(last_usage_time))  AS last_access_date\n" +
        "        FROM\n" +
        "            dws_up_service_active_dm\n" +
        "        WHERE \n" +
        "            pt_d='$date'\n" +
        "        AND pt_service IN ('youkuvideo','sohuvideo','hwread','hnread')\n" +
        "        GROUP BY CASE WHEN pt_service IN ('youkuvideo','sohuvideo') THEN 'video'\n" +
        "                 WHEN pt_service IN ('hwread','hnread')        THEN 'reader'\n" +
        "                 ELSE NULL END,up_id\n" +
        "    )t\n" +
        "    GROUP BY pt_service\n" +
        ")tt";
    String s03 = "select CONCAT(\n" +
        "                COUNT(t1.imei),'-','login_device_cnt,',\n" +
        "                COUNT(IF(t1.first_usage_time = '$date_ep',t1.imei,NULL)),'-','inc_device_cnt,',\n" +
        "                COUNT(t1.imei) ,'-','lost_device_cnt,',\n" +
        "                COUNT(IF(t1.imei is NULL AND t1.first_usage_time <= DATE_SUB('$date_ep',8),t1.imei,NULL)),'-','return_device_cnt,',\n" +
        "                COUNT(t1.imei ) ,'-','pay_device_cnt,'\n" +
        "            ) AS value from t1";
    String s04 = "SELECT \n" +
        "    SUM(login_up_ids) AS login_up_ids,\n" +
        "    SUM(inc_up_ids) AS inc_up_ids,\n" +
        "    SUM(lost_up_ids) AS lost_up_ids,\n" +
        "    SUM(return_up_ids) AS return_up_ids,\n" +
        "    SUM(pay_amt) AS pay_amt,\n" +
        "    game_rank_cd\n" +
        "FROM\n" +
        "(\n" +
        "    SELECT \n" +
        "        COUNT(t1.up_id) AS login_up_ids,\n" +
        "        COUNT(IF(t2.first_usage_time = '$date_ep' AND t2.up_id IS NOT NULL,t1.up_id,NULL)) AS inc_up_ids,\n" +
        "        0 AS lost_up_ids,\n" +
        "        COUNT(IF(t4.up_id IS NULL AND t2.first_usage_time <= DATE_SUB('$date_ep',8),t1.up_id,NULL)) AS return_up_ids,\n" +
        "        SUM(IF(t7.up_id IS NOT NULL,t7.pay_amt,0)) AS pay_amt,\n" +
        "        IF(t5.game_rank_cd IS NULL,'1',t5.game_rank_cd) AS game_rank_cd\n" +
        "    FROM    \n" +
        "    (\n" +
        "        SELECT\n" +
        "            up_id\n" +
        "        FROM biads.ads_game_user_snap_dm\n" +
        "        WHERE pt_d = '$date' AND game_sign = 1 AND !isEmpty(up_id)\n" +
        "        GROUP BY up_id\n" +
        "    )t1\n" +
        "    LEFT OUTER JOIN \n" +
        "    (\n" +
        "        SELECT \n" +
        "            min(TO_DATE(first_usage_time)) AS first_usage_time,\n" +
        "            up_id\n" +
        "        FROM biads.ads_game_all_user_up_imei_ds\n" +
        "        where game_sign = 1\n" +
        "        GROUP BY up_id\n" +
        "    )t2\n" +
        "    ON t1.up_id = t2.up_id\n" +
        "    LEFT OUTER JOIN\n" +
        "    (\n" +
        "        SELECT\n" +
        "           up_id\n" +
        "        FROM biads.ads_game_user_snap_dm\n" +
        "        WHERE  pt_d >= regexp_replace(DATE_SUB('$date_ep',7),'-','') AND pt_d <= '$last_date' AND game_sign = 1 AND !isEmpty(up_id)\n" +
        "        GROUP BY up_id\n" +
        "    )t4\n" +
        "    on t1.up_id = t4.up_id\n" +
        "    LEFT OUTER JOIN\n" +
        "    (\n" +
        "        SELECT up_id,MAX(game_rank_cd) AS game_rank_cd\n" +
        "        FROM dwd_pty_game_user_rank_ds_his\n" +
        "        WHERE '$date' >= start_date AND end_date > '$date'\n" +
        "        GROUP BY up_id\n" +
        "    )t5\n" +
        "    ON t1.up_id = t5.up_id\n" +
        "    FULL OUTER JOIN\n" +
        "    (\n" +
        "        SELECT pay_up_id AS up_id,SUM(pay_amt) AS pay_amt\n" +
        "        FROM biads.ads_game_pay_detail_dm\n" +
        "        WHERE pt_d ='$date' AND game_sign = 1 AND pay_amt > 0 AND to_date(txn_finish_time) = '$date_ep' AND substr(dev_app_id,1,1)<>'H'\n" +
        "        GROUP BY pay_up_id\n" +
        "    )t7\n" +
        "    on t5.up_id = t7.up_id\n" +
        "    GROUP BY IF(t5.game_rank_cd IS NULL,'1',t5.game_rank_cd)\n" +
        "    UNION ALL\n" +
        "    SELECT \n" +
        "        0 AS login_up_ids,\n" +
        "        0 AS inc_up_ids,\n" +
        "        COUNT(IF(t11.up_id IS NOT NULL AND t3.up_id IS NULL,t11.up_id,NULL)) AS lost_up_ids,\n" +
        "        0 AS return_up_ids, \n" +
        "        0.0 AS pay_amt,\n" +
        "        IF(t8.game_rank_cd IS NOT NULL,t8.game_rank_cd,'1') AS game_rank_cd\n" +
        "    FROM \n" +
        "    (\n" +
        "        SELECT\n" +
        "            up_id\n" +
        "        FROM biads.ads_game_user_snap_dm\n" +
        "        WHERE pt_d = regexp_replace(DATE_SUB('$date_ep',7),'-','') AND game_sign = 1 AND !isEmpty(up_id)\n" +
        "        GROUP BY up_id\n" +
        "    )t11\n" +
        "    FULL OUTER JOIN \n" +
        "    (\n" +
        "        SELECT\n" +
        "            up_id\n" +
        "        FROM biads.ads_game_user_snap_dm\n" +
        "        WHERE pt_d >= regexp_replace(DATE_SUB('$date_ep',6),'-','') AND pt_d <= '$date' AND game_sign = 1 AND !isEmpty(up_id) \n" +
        "        GROUP BY up_id\n" +
        "    )t3\n" +
        "    ON t11.up_id = t3.up_id\n" +
        "    LEFT OUTER JOIN\n" +
        "    (       \n" +
        "        SELECT \n" +
        "            up_id,\n" +
        "            MAX(game_rank_cd) AS game_rank_cd\n" +
        "        FROM dwd_pty_game_user_rank_ds_his\n" +
        "        WHERE regexp_replace(DATE_SUB('$date_ep',7),'-','') >= start_date AND end_date > regexp_replace(DATE_SUB('$date_ep',7),'-','')\n" +
        "        GROUP BY up_id\n" +
        "    )t8\n" +
        "    ON t11.up_id = t8.up_id\n" +
        "    GROUP BY IF(t8.game_rank_cd IS NOT NULL,t8.game_rank_cd,'1')\n" +
        ")t\n" +
        "GROUP BY game_rank_cd";
    String s05 = "SELECT t.device_name,\n" +
        "       t.new_user_cnt,\n" +
        "       t.hispace_new_user_cnt,\n" +
        "       t.statis_content\n" +
        "FROM\n" +
        "(\n" +
        "    #合计\n" +
        "    SELECT 'ALL_DEVICE' AS device_name,\n" +
        "        CAST(SUM(new_user_cnt) AS INT) AS new_user_cnt,\n" +
        "        CAST(SUM(hispace_new_user_cnt) AS INT) AS hispace_new_user_cnt,\n" +
        "        'ALL_DEVICE' AS statis_content\n" +
        "    FROM temp.tmp_ads_hispace_hota_user_active_dm\n" +
        "    UNION ALL\n" +
        "    #智汇云激活量TOP10\n" +
        "    SELECT device_name,\n" +
        "            new_user_cnt,\n" +
        "            hispace_new_user_cnt,\n" +
        "            statis_content\n" +
        "    FROM\n" +
        "    (\n" +
        "        SELECT\n" +
        "            device_name,\n" +
        "            new_user_cnt,\n" +
        "            hispace_new_user_cnt,\n" +
        "            'hispace_user_top10' AS statis_content\n" +
        "        FROM temp.tmp_ads_hispace_hota_user_active_dm\n" +
        "        ORDER BY hispace_new_user_cnt DESC \n" +
        "        LIMIT 10\n" +
        "    ) tt1\n" +
        "    UNION ALL\n" +
        "    #激活率转化率Top10\n" +
        "    SELECT device_name,\n" +
        "            new_user_cnt,\n" +
        "            hispace_new_user_cnt,\n" +
        "            statis_content\n" +
        "    FROM\n" +
        "    (\n" +
        "        SELECT\n" +
        "            device_name,\n" +
        "            new_user_cnt,\n" +
        "            hispace_new_user_cnt,\n" +
        "            'transform_rate_top10' AS statis_content\n" +
        "        FROM temp.tmp_ads_hispace_hota_user_active_dm\n" +
        "        ORDER BY hispace_new_user_cnt/new_user_cnt DESC \n" +
        "        LIMIT 10\n" +
        "    ) tt2\n" +
        "    UNION ALL\n" +
        "    # 激活转化率排名最低机型信息：开机量在1000个之上的（开机>=1000）、激活转化率排名最低Top10的机型信息\n" +
        "    SELECT device_name,\n" +
        "            new_user_cnt,\n" +
        "            hispace_new_user_cnt,\n" +
        "            statis_content\n" +
        "    FROM\n" +
        "    (\n" +
        "        SELECT\n" +
        "            device_name,\n" +
        "            new_user_cnt,\n" +
        "            hispace_new_user_cnt,\n" +
        "            'transform_rate_lower10' AS statis_content\n" +
        "        FROM temp.tmp_ads_hispace_hota_user_active_dm\n" +
        "        WHERE new_user_cnt >= 1000 \n" +
        "        ORDER BY hispace_new_user_cnt/new_user_cnt ASC \n" +
        "        LIMIT 10\n" +
        "    )tt3\n" +
        ") t";
    return s04;
  }
}
