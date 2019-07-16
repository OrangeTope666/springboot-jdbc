package com.sucsoft.wwfb.service;

import com.sucsoft.wwfb.model.Xzqy;
import com.sucsoft.wwfb.utils.DBConnectionUtil;
import com.sucsoft.wwfb.utils.PropertiesUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.*;


@Service
public class NewWwfbService {

    private static String gljb = "1";

    static {
        Properties properties = PropertiesUtil.getPro("application.properties");
        //获取key对应的value值
        gljb = properties.getProperty("gljb");
    }

    /**
     * 获取行政区域
     *
     * @return
     */
    public List<Xzqy> getXzqy() {
        List<Xzqy> list = new ArrayList<>();
        Connection conn = DBConnectionUtil.getConnection();
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery("select * from CG_CODE_XZQH where px > 0 order by px");
            while (rs.next()) {
                Xzqy xzqy = new Xzqy();
                xzqy.setXzqyMc(rs.getString("MC"));
                xzqy.setXzqyDm(rs.getString("BM"));
                list.add(xzqy);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnectionUtil.closeResultSet(rs);
            DBConnectionUtil.closeStatement(stmt);
            DBConnectionUtil.closeConnection(conn);
        }
        return list;
    }

    public Map<String, Object> getList(String wrlx, String xzqy, String startTime, String qymc, int pageNo,
                                           int pageSize) {
        if (StringUtils.isNotBlank(startTime)) {
            return getPageList(wrlx, xzqy, startTime, qymc, pageNo, pageSize);
        }
        if ("fs".equals(wrlx)) {
            return getFsNewPageList(xzqy, qymc, pageNo, pageSize);
        }
        return  getFqNewPageList(xzqy, qymc, pageNo, pageSize);
    }

    /**
     * 获取废水企业的最新一条数据
     * @param xzqy
     * @param qymc
     * @param pageNo
     * @param pageSize
     * @return
     */
    public Map<String, Object> getFsNewPageList(String xzqy, String qymc, int pageNo,
                                           int pageSize) {
        Map<String, Object> reslutMap = new HashMap<>();
        List<Map<String, Object>> list = new ArrayList<>();
        Connection conn = DBConnectionUtil.getConnection();
        Statement stmt = null;
        ResultSet rs = null;
        Statement stmt1 = null;
        ResultSet rs1 = null;
        PreparedStatement  stmt2 = null;
        ResultSet rs2 = null;
        int count = 0;
        try {
            String cSql = "select count(*) from (";
            String startSql = "select * from (select a.*, rownum rn from (";
            String endSql = " where rownum <= " + (pageNo * pageSize) + ") where rn >" + (pageNo - 1) * pageSize;
            // 企业、排口sql
            String qysql = "select qy.psname, pk.outputname, qy.pscode, pk.outputcode " +
                    " from ps_base_info qy, ps_water_output pk where pk.pscode = qy.pscode ";
            // 数据sql
            String datasql = "select to_char(d.monitortime, 'yyyy-mm-dd hh24:mi:ss') as monitortime,d.ph, d.nh3, d.cod " +
                    " from ps_water_hour_data d " +
                    " where outputcode =?1 and rownum = 1 order by d.monitortime desc ";

            if (StringUtils.isNotBlank(qymc)) {
                qysql += " and qy.psname like '%" + qymc + "%' ";
            }
            if (StringUtils.isNotBlank(xzqy)) {
                qysql += " and qy.regioncode like '" + xzqy + "%' ";
            }
            // 总数
            stmt1 = conn.createStatement();
            rs1 = stmt1.executeQuery(cSql + qysql + ")");
            while(rs1.next()){
                count = rs1.getInt(1);
            }

            // 企业
            qysql = startSql + qysql + "order by qy.psname, pk.outputname) a" + endSql;
            stmt = conn.createStatement();
            rs = stmt.executeQuery(qysql);

            while (rs.next()) {
                Map<String, Object> map = new HashMap<>();
                String psname=  rs.getString("psname");
                map.put("psname", rs.getString("psname"));
                map.put("outputname", rs.getString("outputname"));
                String pkcode = rs.getString("outputcode");

                // 实时数据
                // 后面参数是为了对结果ResultSet可以移动指针，默认参数只能next()移动
                stmt2 = conn.prepareStatement(datasql, ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
                stmt2.setString(1, pkcode);
                rs2 = stmt2.executeQuery();
                if (!rs2.next()) {
                    map.put("monitortime", "-");
                    map.put("ph", "-");
                    map.put("nh3", "-");
                    map.put("cod", "-");
                    list.add(map);
                    continue;
                } else {
                    rs2.previous();
                }

                while (rs2.next()) {
                    String time = rs2.getString("monitortime");
                    if (StringUtils.isBlank(time)) {
                        time = "-";
                    }
                    map.put("monitortime", time);
                    if (StringUtils.isBlank(rs2.getString("ph"))) {
                        map.put("ph", "-");
                    } else {
                        map.put("ph", rs2.getDouble("ph"));
                    }
                    if (StringUtils.isBlank(rs2.getString("nh3"))) {
                        map.put("nh3", "-");
                    } else {
                        map.put("nh3", rs2.getDouble("nh3"));
                    }
                    if (StringUtils.isBlank(rs2.getString("cod"))) {
                        map.put("cod", "-");
                    } else {
                        map.put("cod", rs2.getDouble("cod"));
                    }
                }
                list.add(map);
            }
            reslutMap.put("count", count);
            reslutMap.put("data", list);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnectionUtil.closeResultSet(rs2);
            DBConnectionUtil.closePreparedStatement(stmt2);
            DBConnectionUtil.closeResultSet(rs1);
            DBConnectionUtil.closeStatement(stmt1);
            DBConnectionUtil.closeResultSet(rs);
            DBConnectionUtil.closeStatement(stmt);
            DBConnectionUtil.closeConnection(conn);
        }
        return reslutMap;
    }

    /**
     * 获取废气最新一条数据
     * @param xzqy
     * @param qymc
     * @param pageNo
     * @param pageSize
     * @return
     */
    public Map<String, Object> getFqNewPageList(String xzqy, String qymc, int pageNo,
                                                int pageSize) {
        Map<String, Object> reslutMap = new HashMap<>();
        List<Map<String, Object>> list = new ArrayList<>();
        Connection conn = DBConnectionUtil.getConnection();
        Statement stmt = null;
        ResultSet rs = null;
        Statement stmt1 = null;
        ResultSet rs1 = null;
        PreparedStatement  stmt2 = null;
        ResultSet rs2 = null;
        int count = 0;
        try {
            String cSql = "select count(*) from (";
            String startSql = "select * from (select a.*, rownum rn from (";
            String endSql = " where rownum <= " + (pageNo * pageSize) + ") where rn >" + (pageNo - 1) * pageSize;
            // 企业、排口sql
            String qysql = "select qy.psname, pk.outputname, qy.pscode, pk.outputcode " +
                    " from ps_base_info qy, ps_gas_output pk where pk.pscode = qy.pscode ";
            // 数据sql
            String datasql = "select to_char(d.monitortime, 'yyyy-mm-dd hh24:mi:ss') as monitortime,d.Noxzs, d.sootzs, d.so2zs " +
                    " from ps_gas_hour_data d " +
                    " where outputcode =?1 and rownum = 1 order by d.monitortime desc ";

            if (StringUtils.isNotBlank(qymc)) {
                qysql += " and qy.psname like '%" + qymc + "%' ";
            }
            if (StringUtils.isNotBlank(xzqy)) {
                qysql += " and qy.regioncode like '" + xzqy + "%' ";
            }
            // 总数
            stmt1 = conn.createStatement();
            rs1 = stmt1.executeQuery(cSql + qysql + ")");
            while(rs1.next()){
                count = rs1.getInt(1);
            }

            // 企业
            qysql = startSql + qysql + "order by qy.psname,pk.outputname) a" + endSql;
            stmt = conn.createStatement();
            rs = stmt.executeQuery(qysql);

            while (rs.next()) {
                Map<String, Object> map = new HashMap<>();
                map.put("psname", rs.getString("psname"));
                map.put("outputname", rs.getString("outputname"));
                String pkcode = rs.getString("outputcode");

                // 实时数据
                stmt2 = conn.prepareStatement(datasql, ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
                stmt2.setString(1, pkcode);
                rs2 = stmt2.executeQuery();
                if (!rs2.next()) {
                    map.put("monitortime", "-");
                    map.put("Noxzs", "-");
                    map.put("sootzs", "-");
                    map.put("so2zs", "-");
                    list.add(map);
                    continue;
                } else {
                    // 指针前移
                    rs2.previous();
                }

                while (rs2.next()) {
                    String time = rs2.getString("monitortime");
                    if (StringUtils.isBlank(time)) {
                        time = "-";
                    }
                    map.put("monitortime", time);
                    if (StringUtils.isBlank(rs2.getString("Noxzs"))) {
                        map.put("Noxzs", "-");
                    } else {
                        map.put("Noxzs", rs2.getDouble("Noxzs"));
                    }
                    if (StringUtils.isBlank(rs2.getString("sootzs"))) {
                        map.put("sootzs", "-");
                    } else {
                        map.put("sootzs", rs2.getDouble("sootzs"));
                    }
                    if (StringUtils.isBlank(rs2.getString("so2zs"))) {
                        map.put("so2zs", "-");
                    } else {
                        map.put("so2zs", rs2.getDouble("so2zs"));
                    }
                }
                list.add(map);
            }
            reslutMap.put("count", count);
            reslutMap.put("data", list);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnectionUtil.closeResultSet(rs2);
            DBConnectionUtil.closePreparedStatement(stmt2);
            DBConnectionUtil.closeResultSet(rs1);
            DBConnectionUtil.closeStatement(stmt1);
            DBConnectionUtil.closeResultSet(rs);
            DBConnectionUtil.closeStatement(stmt);
            DBConnectionUtil.closeConnection(conn);
        }
        return reslutMap;
    }

    public Map<String, Object> getPageList(String wrlx, String xzqy, String startTime, String qymc, int pageNo,
                                           int pageSize) {
        Map<String, Object> reslutMap = new HashMap<>();
        List<Map<String, Object>> list = new ArrayList<>();
        Connection conn = DBConnectionUtil.getConnection();
        Statement stmt = null;
        ResultSet rs = null;
        Statement stmt1 = null;
        ResultSet rs1 = null;
        int count = 0;
        try {
            String sql;
            String cSql = "select count(*) from (";
            String startSql = "select * from (select a.*, rownum rn from (";
            String endSql = " where rownum <= " + (pageNo * pageSize) + ") where rn >" + (pageNo - 1) * pageSize;
            String fqsql =
                    "select qy.psname, to_char(d.monitortime, 'yyyy-mm-dd hh24:mi:ss') as monitortime,d.Noxzs, d.sootzs, d.so2zs, pk.outputname " +
                            "from ps_base_info qy " +
                            "inner join PS_GAS_OUTPUT pk on pk.pscode = qy.pscode " +
                            "left join PS_GAS_HOUR_DATA d on pk.outputcode = d.outputcode " +
                           // "inner join ps_base_info_wwfb w on qy.pscode = w.pscode " +
                            "and d.monitortime = to_date('" + startTime + "','yyyy-mm-dd hh24:mi:ss') " +
                            "where 1= 1  ";
            String fssql =
                    "select qy.psname, to_char(d.monitortime, 'yyyy-mm-dd hh24:mi:ss') as monitortime,d.ph, d.nh3, d.cod, pk.outputname " +
                            "from ps_base_info qy " +
                            "inner join PS_WATER_OUTPUT pk on pk.pscode = qy.pscode " +
                            "left join PS_WATER_HOUR_DATA d on pk.outputcode = d.outputcode " +
                            //"inner join ps_base_info_wwfb w on qy.pscode = w.pscode " +
                            "and d.monitortime = to_date('" + startTime + "','yyyy-mm-dd hh24:mi:ss') " +
                            "where 1= 1  ";
            if ("fs".equalsIgnoreCase(wrlx)) {
                sql = fssql;
            } else {
                sql = fqsql;
            }
            if (StringUtils.isNotBlank(qymc)) {
                sql += " and qy.psname like '%" + qymc + "%' ";
            }
            if (StringUtils.isNotBlank(xzqy)) {
                sql += " and qy.regioncode like '" + xzqy + "%' ";
            }
           // if (StringUtils.isNotBlank(gljb)) {
             //   sql += " and qy.AttentionDegreeCode in (" + gljb + ") ";
          //  }
          //  sql += " and w.wwfb = '1' ";

            stmt1 = conn.createStatement();
            rs1 = stmt1.executeQuery(cSql + sql + ")");
            while(rs1.next()){
                count = rs1.getInt(1);
            }

            sql = startSql + sql + "order by psname) a" + endSql;
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            if ("fs".equalsIgnoreCase(wrlx)) {
                while (rs.next()) {
                    Map<String, Object> map = new HashMap<>();
                    String time = rs.getString("monitortime");
                    if (StringUtils.isBlank(time)) {
                        time = "-";
                    }
                    map.put("psname", rs.getString("psname"));
                    map.put("outputname", rs.getString("outputname"));
                    map.put("monitortime", time);
                    if (StringUtils.isBlank(rs.getString("ph"))) {
                        map.put("ph", "-");
                    } else {
                        map.put("ph", rs.getDouble("ph"));
                    }
                    if (StringUtils.isBlank(rs.getString("nh3"))) {
                        map.put("nh3", "-");
                    } else {
                        map.put("nh3", rs.getDouble("nh3"));
                    }
                    if (StringUtils.isBlank(rs.getString("cod"))) {
                        map.put("cod", "-");
                    } else {
                        map.put("cod", rs.getDouble("cod"));
                    }
                    list.add(map);
                }
            } else {
                while (rs.next()) {
                    Map<String, Object> map = new HashMap<>();
                    String time = rs.getString("monitortime");
                    if (StringUtils.isBlank(time)) {
                        time = "-";
                    }
                    map.put("psname", rs.getString("psname"));
                    map.put("outputname", rs.getString("outputname"));
                    map.put("monitortime", time);
                    if (StringUtils.isBlank(rs.getString("Noxzs"))) {
                        map.put("Noxzs", "-");
                    } else {
                        map.put("Noxzs", rs.getDouble("Noxzs"));
                    }
                    if (StringUtils.isBlank(rs.getString("sootzs"))) {
                        map.put("sootzs", "-");
                    } else {
                        map.put("sootzs", rs.getDouble("sootzs"));
                    }
                    if (StringUtils.isBlank(rs.getString("so2zs"))) {
                        map.put("so2zs", "-");
                    } else {
                        map.put("so2zs", rs.getDouble("so2zs"));
                    }
                    list.add(map);
                }
            }
            reslutMap.put("count", count);
            reslutMap.put("data", list);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnectionUtil.closeResultSet(rs1);
            DBConnectionUtil.closeStatement(stmt1);
            DBConnectionUtil.closeResultSet(rs);
            DBConnectionUtil.closeStatement(stmt);
            DBConnectionUtil.closeConnection(conn);
        }
        return reslutMap;
    }
}
