package test_sqlparse;

public class TestSqls {
  public static final String s01 = "SELECT t.device_name as tdn,\n" +
      "       t.new_user_cnt as tnuc,\n" +
      "       t.hispace_new_user_cnt,\n" +
      "       t.statis_content\n" +
      "FROM\n" +
      "(\n" +
      "    SELECT 'ALL_DEVICE' AS device_name,\n" +
      "        CAST(SUM(new_user_cnt) AS INT) AS new_user_cnt,\n" +
      "        CAST(SUM(hispace_new_user_cnt) AS INT) AS hispace_new_user_cnt,\n" +
      "        'ALL_DEVICE' AS statis_content\n" +
      "    FROM temp.tmp_ads_hispace_hota_user_active_dm\n" +
      "    UNION ALL\n" +
      "        SELECT\n" +
      "            device_name,\n" +
      "            new_user_cnt,\n" +
      "            hispace_new_user_cnt,\n" +
      "            'hispace_user_top10' AS statis_content\n" +
      "        FROM temp.tmp_ads_hispace_hota_user_active_dm\n" +
      "        ORDER BY hispace_new_user_cnt DESC \n" +
      "        LIMIT 10\n" +
      ") t";
  public static final String s02 = "select\n" +
      "    t1.imei,\n" +
      "    t1.area_info                                                                                              as area_info,\n" +
      "    if(t1.net_type_cd is null,-1,t1.net_type_cd)                                                              as net_type_cd,\n" +
      "    ServiceEntry(if(isempty(t1.imei_c30),'null',t1.imei_c30),if(t1.channel_id is null,4000000,t1.channel_id)) as channel_id,\n" +
      "    if(t2.client_ver_id is null,-999,t2.client_ver_id)                                                        as client_ver_id,\n" +
      "    t1.logins                                                                                                 as login_cnt,\n" +
      "    t1.sim_mobile_oper                                                                                        as mobile_carrier,\n" +
      "    t1.hispace_client_type_cd\n" +
      "from\n" +
      "(\n" +
      "    select \n" +
      "        imei                                         as imei,\n" +
      "        deviceId(if(IsEmpty(logon_id) or size(split(logon_id, '@')) <= 1 or length(split(logon_id, '@')[1]) < 32, logon_id, concat(split(logon_id, '@')[0], '@', aesDecrypt(split(logon_id, '@')[1], 'hispace')))) imei_c30,\n" +
      "        deviceId(logon_id,'@',2)                 as service_client_code,\n" +
      "        deviceId(logon_id,'@','sim_mobile_oper') as sim_mobile_oper,\n" +
      "        channel_id,\n" +
      "        net_type_cd,\n" +
      "        if(isempty(aesdecrypt4ad(user_ip_addr)) or aesdecrypt4ad(user_ip_addr)='0','',Ip2AreaInfo(aesdecrypt4ad(user_ip_addr))) as area_info,\n" +
      "        count(1) as logins,\n" +
      "        hispace_client_type_cd\n" +
      "    from bicoredata.dwd_evt_hispace_oper_log_dm \n" +
      "    where pt_d='$date' \n" +
      "      and hispace_oper_type_cd in ('0','61','6','10','11') \n" +
      "      and !(hispace_oper_type_cd = '61' and lower(oper_src) = 'renew')\n" +
      "    group by imei,\n" +
      "             deviceId(if(IsEmpty(logon_id) or size(split(logon_id, '@')) <= 1 or length(split(logon_id, '@')[1]) < 32, logon_id, concat(split(logon_id, '@')[0], '@', aesDecrypt(split(logon_id, '@')[1], 'hispace')))),\n" +
      "             deviceId(logon_id,'@',2),\n" +
      "             deviceId(logon_id,'@','sim_mobile_oper'),\n" +
      "             channel_id,\n" +
      "             net_type_cd,\n" +
      "             if(isempty(aesdecrypt4ad(user_ip_addr)) or aesdecrypt4ad(user_ip_addr)='0','',Ip2AreaInfo(aesdecrypt4ad(user_ip_addr))),\n" +
      "             hispace_client_type_cd\n" +
      ") t1\n" +
      "left outer join biads.ads_hispace_service_client_ds t2\n" +
      "on t1.service_client_code = t2.client_ver_cd";
  public static final String s03 = "SELECT \n" +
      "    pay_flg,\n" +
      "    SUM(pay_up_ids) AS pay_up_ids,\n" +
      "    SUM(new_pay_up_id) AS new_pay_up_id\n" +
      "FROM\n" +
      "    (\n" +
      "        SELECT  \n" +
      "            pay_flg ,     \n" +
      "            COUNT(1) pay_up_ids,\n" +
      "            0 AS new_pay_up_id   \n" +
      "        FROM\n" +
      "            (\n" +
      "                SELECT \n" +
      "                    pay_flg,\n" +
      "                    pay_up_id \n" +
      "                FROM \n" +
      "                    biads.ads_hispace_pay_up_dm\n" +
      "                WHERE \n" +
      "                    pt_d BETWEEN regexp_replace(date_sub('$date_ep',6),'-','') AND '$date'\n" +
      "                    AND pay_up_id is not null\n" +
      "                GROUP BY pay_flg,pay_up_id\n" +
      "            )a\n" +
      "        GROUP BY pay_flg\n" +
      "        UNION ALL\n" +
      "        SELECT  \n" +
      "            pay_flg ,    \n" +
      "            0 AS pay_up_ids, \n" +
      "            COUNT(1) new_pay_up_id \n" +
      "        FROM\n" +
      "            (\n" +
      "                SELECT \n" +
      "                    pay_flg,\n" +
      "                    new_pay_up_id   \n" +
      "                FROM \n" +
      "                    biads.ads_hispace_pay_up_dm\n" +
      "                WHERE \n" +
      "                    pt_d BETWEEN regexp_replace(date_sub('$date_ep',6),'-','') AND '$date'\n" +
      "                    AND new_pay_up_id is not null\n" +
      "                GROUP BY pay_flg,\n" +
      "                         new_pay_up_id\n" +
      "            )a\n" +
      "        GROUP BY pay_flg\n" +
      "        UNION ALL\n" +
      "        SELECT  \n" +
      "            '3' pay_flg,    \n" +
      "            COUNT(*) pay_up_ids,\n" +
      "            0 AS new_pay_up_id\n" +
      "        FROM \n" +
      "            (\n" +
      "                SELECT \n" +
      "                    pay_up_id\n" +
      "                FROM \n" +
      "                    biads.ads_hispace_pay_up_dm\n" +
      "                WHERE \n" +
      "                    pt_d BETWEEN regexp_replace(date_sub('$date_ep',6),'-','') AND '$date'\n" +
      "                    AND pay_up_id is not null\n" +
      "                GROUP BY pay_up_id\n" +
      "            ) a \n" +
      "        UNION ALL\n" +
      "        SELECT  \n" +
      "            '3' pay_flg,  \n" +
      "            0 AS pay_up_ids,    \n" +
      "            COUNT(*) new_pay_up_id\n" +
      "        FROM \n" +
      "            (\n" +
      "                SELECT \n" +
      "                    new_pay_up_id\n" +
      "                  FROM \n" +
      "                      biads.ads_hispace_pay_up_dm\n" +
      "                WHERE \n" +
      "                    pt_d BETWEEN regexp_replace(date_sub('$date_ep',6),'-','') AND '$date'\n" +
      "                    AND new_pay_up_id is not null\n" +
      "                GROUP BY new_pay_up_id\n" +
      "            ) a\n" +
      "    )t \n" +
      "GROUP BY pay_flg";
  public static final String s04 = "select id, col[0][0] as col, split(name, ',')[0][0] as aaa, if(len(name) > 0, 1, 1.0) as name, CAST(age as STRING) as age from tab1";
  public static final String s06 = "create table temp.hjii (id int, name string)";
  public static final String s07 = "SELECT\n" +
      "        imei\n" +
      "    FROM \n" +
      "        bicoredata.dws_device_service_active_dm\n" +
      "    WHERE pt_d='20180723'\n" +
      "          AND '20180723'='20180722'\n" +
      "    GROUP BY imei";
  public static final String s05 = "SELECT CASE WHEN oper_id='250101' THEN fun_tab\n" +
      "\t\t\tWHEN oper_id THEN oper_id\n" +
      "            WHEN oper_id='250201' AND SPLIT(non_stru_field,'\\\\\\\\|')[0]='10' THEN 'video_direct'\n" +
      "            ELSE 'other' END AS search_module \n" +
      " FROM dwd_evt_bisdk_customize_dm";
  public static final String s08 = "SELECT\n" +
      "    split(item, ',')[0] AS key,\n" +
      "    split(item, ',')[1] AS value\n" +
      "FROM\n" +
      "(\n" +
      "    SELECT array(CONCAT('wallet_recharge', ',',CAST(wallet_recharge AS STRING)),\n" +
      "                 CONCAT('gift_recharge', ',',CAST(gift_recharge AS STRING)),\n" +
      "                 CONCAT('recharge', ',',CAST(recharge AS STRING)),\n" +
      "                 CONCAT('gift_huabi_consume', ',',CAST(gift_huabi_consume AS STRING))\n" +
      "                ) as ary\n" +
      "    FROM\n" +
      "    (\n" +
      "        SELECT SUM(CASE WHEN service_catalog = 'H0' AND pay_mode not in ('华为充值卡','花币') AND order_type = 'PURCHASE' THEN pay_amt\n" +
      "                        WHEN service_catalog = 'H0' AND pay_mode not in ('华为充值卡','花币') AND order_type != 'PURCHASE' THEN 0-pay_amt\n" +
      "                ELSE 0.0  END * 100) AS wallet_recharge,\n" +
      "                SUM(CASE WHEN service_catalog = 'H0' AND pay_mode in ('华为充值卡','花币') and  order_type = 'PURCHASE' THEN pay_amt\n" +
      "                         WHEN service_catalog = 'H0' AND pay_mode in ('华为充值卡','花币') and  order_type != 'PURCHASE' THEN 0-pay_amt\n" +
      "                ELSE 0.0  END * 100) AS gift_recharge,\n" +
      "                SUM(CASE WHEN service_catalog = 'H0'  AND order_type = 'PURCHASE' THEN pay_amt\n" +
      "                        WHEN service_catalog = 'H0' AND order_type != 'PURCHASE' THEN -pay_amt\n" +
      "                ELSE 0.0  END * 100) AS recharge,\n" +
      "                SUM(CASE WHEN service_catalog != 'H0' AND pay_mode='花币' AND order_type = 'PURCHASE' THEN hw_received_amt\n" +
      "                         WHEN service_catalog != 'H0' AND pay_mode='花币' AND order_type != 'PURCHASE' THEN 0-hw_received_amt\n" +
      "                ELSE 0.0  END * 100) AS gift_huabi_consume\n" +
      "        FROM  bicoredata.dwd_sal_order_pay_ds                              \n" +
      "        WHERE pt_d='$date'\n" +
      "              AND pay_mode != '预付款'\n" +
      "              AND pay_status_cd ='0'\n" +
      "              AND to_date(txn_finish_time) = '$date_ep'\n" +
      "              AND pay_up_id not in (sha256('260086000119087827'),sha256('260086000119084252'),sha256('200086000021236389'),sha256('260086000227739337'),sha256('60704290'),\n" +
      "    sha256('260086000227794582'),sha256('10086000135526733'),sha256('10086000135837165'),sha256('10086000135837272'),sha256('10086000135837319'),sha256('10086000135837353'),sha256('10086000135837377'), sha256('10086000135837408'),sha256('10086000135837445'),sha256('10086000135837497'),sha256('10086000135837994'),sha256('260086000067193729'))\n" +
      "    )t1\n" +
      ") t LATERAL VIEW explode(ary) arrayTable AS item";
  public static final String play = "SELECT t1.imei,\n" +
      "       t1.client_ver_id,\n" +
      "       t1.first_login_time,\n" +
      "       t2.client_ver_id as last_client_ver_id,\n" +
      "       t3.up_id,\n" +
      "       t3.register_time\n" +
      "FROM\n" +
      "(       \n" +
      "    SELECT \n" +
      "        t11.imei,\n" +
      "        t12.client_ver_id,\n" +
      "        t11.first_login_time\n" +
      "    FROM\n" +
      "    (\n" +
      "        SELECT \n" +
      "            imei,\n" +
      "            client_ver_cd,\n" +
      "            first_login_time\n" +
      "        FROM biads.ads_hispace_user_ds\n" +
      "        WHERE DATEDIFF(first_login_time,'$date_ep') <=0\n" +
      "    )t11\n" +
      "    LEFT OUTER JOIN biads.ads_hispace_service_client_ds t12\n" +
      "    on t11.client_ver_cd = t12.client_ver_cd\n" +
      ") t1\n" +
      "left join  temp.tmp_hispace_user_snap_dm1 t2\n" +
      "on t1.imei=t2.imei\n" +
      "left join  temp.tmp_hispace_user_snap_dm2 t3\n" +
      "on t1.imei=t3.imei";

  public static final String play01 = "SELECT \n" +
      "        t11.imei,\n" +
      "        t12.client_ver_id,\n" +
      "        t11.first_login_time\n" +
      "    FROM\n" +
      "    (\n" +
      "        SELECT \n" +
      "            imei,\n" +
      "            client_ver_cd,\n" +
      "            first_login_time\n" +
      "        FROM biads.ads_hispace_user_ds\n" +
      "        WHERE DATEDIFF(first_login_time,'$date_ep') <=0\n" +
      "    )t11\n" +
      "    LEFT OUTER JOIN biads.ads_hispace_service_client_ds t12\n" +
      "    on t11.client_ver_cd = t12.client_ver_cd";
  public static final String play02 = "select t.id, t.name from biads.testtab t left outer join (select id, name from biads.test02)t2 on t.id = t2.id";
  public static final String play03 = "select t.id, t.name from biads.testtab t left outer join (select id, name from biads.test02)t2 on t.id = t2.id left outer join (select id, name from biads.test03)t3 on t2.id = t3.id";
  public static final String play04 = "create table test_wb AS\nselect bicoredata.DateUtil(111111111, 222222222, 'YYYYMM')";
  public static final String route01 = "SELECT t1.imei,\n" +
      "       t1.client_ver_id,\n" +
      "       t1.first_login_time,\n" +
      "       t2.client_ver_id as last_client_ver_id,\n" +
      "       t3.up_id,\n" +
      "       t3.register_time\n" +
      "FROM\n" +
      "(       \n" +
      "    SELECT \n" +
      "        t11.imei,\n" +
      "        t12.client_ver_id,\n" +
      "        t11.first_login_time\n" +
      "    FROM\n" +
      "    (\n" +
      "        SELECT \n" +
      "            t12.imei,\n" +
      "            client_ver_cd,\n" +
      "            biads.ads_hispace_user_ds.first_login_time\n" +
      "        FROM biads.ads_hispace_user_ds t12\n" +
      "        WHERE DATEDIFF(t12.first_login_time,'$date_ep') <=0\n" +
      "    )t11\n" +
      "    LEFT OUTER JOIN biads.ads_hispace_service_client_ds\n" +
      "    on t11.client_ver_cd = t12.client_ver_cd\n" +
      ") t1\n" +
      "left join  temp.tmp_hispace_user_snap_dm1 t2\n" +
      "on t1.imei=t2.imei\n" +
      "left join  temp.tmp_hispace_user_snap_dm2 t3\n" +
      "on t1.imei=t3.imei";
  public static final String s10 = "INSERT OVERWRITE TABLE temp.temp_game_pay_detail_dm\n" +
      "PARTITION(pt_d='20180725')\n" +
      "SELECT\n" +
      "    IF(t1.package_name IN ('com.huawei.wallet','com.huawei.appmarket.wallet','com.huawei.android.hwpay','com.huawei.gamebox'),t2.app_id,t3.app_id) AS dev_app_id,\n" +
      "    IF(t1.package_name IN ('com.huawei.wallet','com.huawei.appmarket.wallet','com.huawei.android.hwpay','com.huawei.gamebox'),t2.app_cn_name,t3.app_cn_name) AS app_cn_name,\n" +
      "    t1.pay_up_id                 AS pay_up_id,\n" +
      "    t1.imei                      AS imei,\n" +
      "    t1.pay_amt                   AS pay_amt,\n" +
      "    t1.pay_mode                  AS pay_mode,\n" +
      "    t1.booking_time              AS booking_time,\n" +
      "    t1.txn_finish_time           AS txn_finish_time,\n" +
      "    t1.extnal_plat_txn_id        AS extnal_plat_txn_id,\n" +
      "    t1.pay_id                    AS pay_id,\n" +
      "    t2.game_type_cd              AS game_sign\n" +
      "FROM\n" +
      "(\n" +
      "        select\n" +
      "            pay_up_id,       \n" +
      "            imei,\n" +
      "            getHiCloudAppId(dev_app_id) AS dev_app_id,\n" +
      "            IF(UPPER(order_type) = 'REFUND',0-CAST(pay_amt AS DOUBLE),CAST(pay_amt AS DOUBLE)) AS pay_amt,\n" +
      "            txn_finish_time AS txn_finish_time,\n" +
      "            pay_id,\n" +
      "            pay_mode,\n" +
      "            extnal_plat_txn_id,\n" +
      "            booking_time,\n" +
      "            package_name\n" +
      "        from dwd_sal_order_pay_ds\n" +
      "        where pt_d = '$date' and pay_status_cd in ('0','3')\n" +
      ")t1\n" +
      "left outer join\n" +
      "(\n" +
      "     select package_name,app_id,app_cn_name\n" +
      "    from\n" +
      "    (\n" +
      "    select \n" +
      "        package_name,\n" +
      "        SUBSTR(MAX(CONCAT(IF(LENGTH(app_upload_time) < 19 ,'0000-00-00 00:00:00',SUBSTR(app_upload_time,1,19)),IF(substr(app_id,1,1)='S',substr(app_id,2),app_id))),20) AS app_id,\n" +
      "        SUBSTR(MAX(CONCAT(IF(LENGTH(app_upload_time) < 19 ,'0000-00-00 00:00:00',SUBSTR(app_upload_time,1,19)),app_cn_name)),20) AS app_cn_name,\n" +
      "        SUBSTR(MAX(CONCAT(IF(LENGTH(app_upload_time) < 19 ,'0000-00-00 00:00:00',SUBSTR(app_upload_time,1,19)),app_first_class)),20) AS app_first_class,\n" +
      "        SUBSTR(MAX(CONCAT(IF(LENGTH(app_upload_time) < 19 ,'0000-00-00 00:00:00',SUBSTR(app_upload_time,1,19)),game_type_cd)),20) AS game_type_cd\n" +
      "    from dwd_onl_hispace_app_info_ds \n" +
      "    where   pt_d = '20180725' and !isEmpty(app_id)    \n" +
      "    group by package_name\n" +
      "    )tt\n" +
      "    where app_first_class = '游戏' and game_type_cd in ('1','2')    \n" +
      ")t3\n" +
      "on t1.package_name = t3.package_name\n" +
      "left outer join\n" +
      "(    \n" +
      "    select \n" +
      "        IF(substr(app_id,1,1)='S',substr(app_id,2),app_id) AS app_id,\n" +
      "        SUBSTR(MAX(CONCAT(IF(LENGTH(app_upload_time) < 19 ,'0000-00-00 00:00:00',SUBSTR(app_upload_time,1,19)),app_cn_name)),20) AS app_cn_name,\n" +
      "        MAX(game_type_cd) AS game_type_cd\n" +
      "    from dwd_onl_hispace_app_info_ds    \n" +
      "    where pt_d = '$date' and app_first_class = '游戏' and !isEmpty(app_id) and game_type_cd in ('1','2')\n" +
      "    group by app_id\n" +
      ")t2\n" +
      "ON substr(t1.dev_app_id,2) = substr(t2.app_id,2)\n" +
      "where (!isEmpty(t2.app_id) or !isEmpty(t3.package_name))";
  public static final String s11 = "INSERT OVERWRITE TABLE temp.temp_game_pay_detail_dm\n" +
      "PARTITION(pt_d='20180725')\n" +
      "SELECT c1 from t1 union all select c1 from t2";
  public static final String s09 = "select a.b.c.d.col1, d.col2, col3 from testTab d";
}