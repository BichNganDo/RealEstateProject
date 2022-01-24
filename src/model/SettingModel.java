package model;

import client.MysqlClient;
import common.ErrorCode;
import entity.banner.Banner;
import entity.banner.FilterBanner;
import entity.setting.FilterSetting;
import entity.setting.Setting;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class SettingModel {

    private static final MysqlClient dbClient = MysqlClient.getMysqlCli();
    private final String NAMETABLE = "setting";
    final static Logger logger = Logger.getLogger(SettingModel.class);
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    public static SettingModel INSTANCE = new SettingModel();

    private SettingModel() {
        PropertyConfigurator.configure("log4j.properties");
    }

    public List<Setting> getSliceSetting(FilterSetting filterSetting) {
        List<Setting> resultListSetting = new ArrayList<>();
        Connection conn = null;
        try {
            conn = dbClient.getDbConnection();
            if (null == conn) {
                return resultListSetting;

            }
            String sql = "SELECT * FROM `" + NAMETABLE + "` "
                    + " WHERE 1=1";

            if (StringUtils.isNotEmpty(filterSetting.getSearchQuery())) {
                sql = sql + " AND (key_word LIKE ? OR group_word LIKE ? OR value LIKE ?) ";
            }

            if (filterSetting.getSearchStatus() > 0) {
                sql = sql + " AND status = ? ";
            }

            sql = sql + " LIMIT ? OFFSET ? ";

            PreparedStatement ps = conn.prepareStatement(sql);
            int param = 1;

            if (StringUtils.isNotEmpty(filterSetting.getSearchQuery())) {
                ps.setString(param++, "%" + filterSetting.getSearchQuery() + "%");
                ps.setString(param++, "%" + filterSetting.getSearchQuery() + "%");
                ps.setString(param++, "%" + filterSetting.getSearchQuery() + "%");
            }

            if (filterSetting.getSearchStatus() > 0) {
                ps.setInt(param++, filterSetting.getSearchStatus());
            }

            ps.setInt(param++, filterSetting.getLimit());
            ps.setInt(param++, filterSetting.getOffset());
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Setting setting = new Setting();

                setting.setId(rs.getInt("id"));
                setting.setKey(rs.getString("key_word"));
                setting.setValue(rs.getString("value"));
                setting.setStatus(rs.getInt("status"));
                setting.setGroup(rs.getString("group_word"));

                long currentTimeMillis = rs.getLong("created_date");
                Date date = new Date(currentTimeMillis);
                String dateStringCreated = sdf.format(date);
                setting.setCreatedDate(dateStringCreated);

                long currentTimeUpdated = rs.getLong("updated_date");
                Date dateUpdated = new Date(currentTimeUpdated);
                String dateStringUpdated = sdf.format(dateUpdated);
                setting.setUpdatedDate(dateStringUpdated);

                resultListSetting.add(setting);
            }

            return resultListSetting;
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            dbClient.releaseDbConnection(conn);
        }

        return resultListSetting;
    }

    public int getTotalSetting(FilterSetting filterSetting) {
        int total = 0;
        Connection conn = null;
        try {
            conn = dbClient.getDbConnection();
            if (null == conn) {
                return total;
            }
            String sql = "SELECT COUNT(id) AS total FROM `" + NAMETABLE + "` WHERE 1=1";

            if (StringUtils.isNotEmpty(filterSetting.getSearchQuery())) {
                sql = sql + " AND (key_word LIKE ? OR group_word LIKE ? OR value LIKE ?) ";
            }

            if (filterSetting.getSearchStatus() > 0) {
                sql = sql + " AND status = ? ";
            }

            PreparedStatement ps = conn.prepareStatement(sql);
            int param = 1;

            if (StringUtils.isNotEmpty(filterSetting.getSearchQuery())) {
                ps.setString(param++, "%" + filterSetting.getSearchQuery() + "%");
                ps.setString(param++, "%" + filterSetting.getSearchQuery() + "%");
                ps.setString(param++, "%" + filterSetting.getSearchQuery() + "%");
            }

            if (filterSetting.getSearchStatus() > 0) {
                ps.setInt(param++, filterSetting.getSearchStatus());
            }

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                total = rs.getInt("total");
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            dbClient.releaseDbConnection(conn);
        }

        return total;
    }

    public Setting getSettingByID(int id) {
        Setting result = new Setting();
        Connection conn = null;
        try {
            conn = dbClient.getDbConnection();
            if (null == conn) {
                return result;
            }
            PreparedStatement getSettingByIdStmt = conn.prepareStatement("SELECT * FROM `" + NAMETABLE + "` WHERE id = ? ");
            getSettingByIdStmt.setInt(1, id);

            ResultSet rs = getSettingByIdStmt.executeQuery();

            if (rs.next()) {
                result.setId(rs.getInt("id"));
                result.setKey(rs.getString("key_word"));
                result.setValue(rs.getString("value"));
                result.setStatus(rs.getInt("status"));
                result.setGroup(rs.getString("group_word"));

                long currentTimeMillis = rs.getLong("created_date");
                Date date = new Date(currentTimeMillis);
                String dateStringCreated = sdf.format(date);
                result.setCreatedDate(dateStringCreated);

                long currentTimeUpdated = rs.getLong("updated_date");
                Date dateUpdated = new Date(currentTimeUpdated);
                String dateStringUpdated = sdf.format(dateUpdated);
                result.setUpdatedDate(dateStringUpdated);
            }

            return result;
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            dbClient.releaseDbConnection(conn);
        }
        return result;
    }

    public Setting getSettingByKey(String key) {
        Setting result = new Setting();
        Connection conn = null;
        try {
            conn = dbClient.getDbConnection();
            if (null == conn) {
                return result;
            }
            PreparedStatement getSettingByIdStmt = conn.prepareStatement("SELECT * FROM `" + NAMETABLE + "` WHERE key_word = ? ");
            getSettingByIdStmt.setString(1, key);

            ResultSet rs = getSettingByIdStmt.executeQuery();

            if (rs.next()) {
                result.setId(rs.getInt("id"));
                result.setKey(rs.getString("key_word"));
                result.setValue(rs.getString("value"));
                result.setStatus(rs.getInt("status"));
                result.setGroup(rs.getString("group_word"));

                long currentTimeMillis = rs.getLong("created_date");
                Date date = new Date(currentTimeMillis);
                String dateStringCreated = sdf.format(date);
                result.setCreatedDate(dateStringCreated);

                long currentTimeUpdated = rs.getLong("updated_date");
                Date dateUpdated = new Date(currentTimeUpdated);
                String dateStringUpdated = sdf.format(dateUpdated);
                result.setUpdatedDate(dateStringUpdated);
            }

            return result;
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            dbClient.releaseDbConnection(conn);
        }
        return result;
    }

    public boolean isExistKey(int id, String key) {
        Connection conn = null;
        try {
            conn = dbClient.getDbConnection();
            if (null == conn) {
                return false;
            }

            PreparedStatement isExistKeyStmt = conn.prepareStatement("SELECT * FROM `" + NAMETABLE + "` WHERE `key_word` = ? AND `id` <> ?");
            isExistKeyStmt.setString(1, key);
            isExistKeyStmt.setInt(2, id);

            ResultSet rs = isExistKeyStmt.executeQuery();
            if (rs.next()) {
                return true;
            }

            return false;
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            dbClient.releaseDbConnection(conn);
        }

        return false;
    }

    public int addSetting(Setting setting) {
        Connection conn = null;
        boolean existKey = INSTANCE.isExistKey(0, setting.getKey());
        if (existKey == true) {
            return ErrorCode.EXIST.getValue();
        }
        try {
            conn = dbClient.getDbConnection();
            if (null == conn) {
                return ErrorCode.CONNECTION_FAIL.getValue();
            }

            PreparedStatement addStmt = conn.prepareStatement("INSERT INTO `" + NAMETABLE + "` (key_word, value, "
                    + "status, group_word, created_date, updated_date)"
                    + "VALUES (?, ?, ?, ?, ?, ?)");
            addStmt.setString(1, setting.getKey());
            addStmt.setString(2, setting.getValue());
            addStmt.setInt(3, setting.getStatus());
            addStmt.setString(4, setting.getGroup());
            addStmt.setString(5, System.currentTimeMillis() + "");
            addStmt.setString(6, System.currentTimeMillis() + "");
            int result = addStmt.executeUpdate();

            return result;
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            dbClient.releaseDbConnection(conn);
        }

        return ErrorCode.FAIL.getValue();
    }

    public int editSetting(Setting setting) {
        Connection conn = null;
        try {
            conn = dbClient.getDbConnection();
            if (null == conn) {
                return ErrorCode.CONNECTION_FAIL.getValue();
            }

            PreparedStatement editStmt = conn.prepareStatement("UPDATE `" + NAMETABLE + "` SET key_word = ?, value = ?, "
                    + "status = ?, group_word = ?, updated_date = ? WHERE id = ?");
            editStmt.setString(1, setting.getKey());
            editStmt.setString(2, setting.getValue());
            editStmt.setInt(3, setting.getStatus());
            editStmt.setString(4, setting.getGroup());
            editStmt.setString(5, System.currentTimeMillis() + "");
            editStmt.setInt(6, setting.getId());

            int rs = editStmt.executeUpdate();

            return rs;
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            dbClient.releaseDbConnection(conn);
        }
        return ErrorCode.FAIL.getValue();
    }

    public int deleteSetting(int id) {
        Connection conn = null;
        try {
            conn = dbClient.getDbConnection();
            if (null == conn) {
                return ErrorCode.CONNECTION_FAIL.getValue();
            }
            Setting settingByID = getSettingByID(id);

            if (settingByID.getId() == 0) {
                return ErrorCode.NOT_EXIST.getValue();
            }
            PreparedStatement deleteStmt = conn.prepareStatement("DELETE FROM `" + NAMETABLE + "` WHERE id = ?");
            deleteStmt.setInt(1, id);
            int rs = deleteStmt.executeUpdate();

            return rs;
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            dbClient.releaseDbConnection(conn);
        }
        return ErrorCode.FAIL.getValue();
    }

}
