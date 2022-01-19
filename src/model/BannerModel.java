package model;

import client.MysqlClient;
import common.ErrorCode;
import entity.banner.Banner;
import entity.banner.FilterBanner;
import entity.category.Category;
import entity.category.FilterCategory;
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

public class BannerModel {

    private static final MysqlClient dbClient = MysqlClient.getMysqlCli();
    private final String NAMETABLE = "banner";
    final static Logger logger = Logger.getLogger(BannerModel.class);
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    public static BannerModel INSTANCE = new BannerModel();

    private BannerModel() {
        PropertyConfigurator.configure("log4j.properties");
    }

    public List<Banner> getSliceBanner(FilterBanner filterBanner) {
        List<Banner> resultListBanner = new ArrayList<>();
        Connection conn = null;
        try {
            conn = dbClient.getDbConnection();
            if (null == conn) {
                return resultListBanner;

            }
            String sql = "SELECT * FROM `" + NAMETABLE + "` "
                    + " WHERE 1=1";

            if (StringUtils.isNotEmpty(filterBanner.getSearchQuery())) {
                sql = sql + " AND banner_name LIKE ? ";
            }

            if (filterBanner.getStatus() > 0) {
                sql = sql + " AND status = ? ";
            }

            sql = sql + " ORDER BY `orders` ASC LIMIT ? OFFSET ? ";

            PreparedStatement ps = conn.prepareStatement(sql);
            int param = 1;

            if (StringUtils.isNotEmpty(filterBanner.getSearchQuery())) {
                ps.setString(param++, "%" + filterBanner.getSearchQuery() + "%");
            }

            if (filterBanner.getStatus() > 0) {
                ps.setInt(param++, filterBanner.getStatus());
            }

            ps.setInt(param++, filterBanner.getLimit());
            ps.setInt(param++, filterBanner.getOffset());
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Banner banner = new Banner();

                banner.setId(rs.getInt("id"));
                banner.setBannerName(rs.getString("banner_name"));
                banner.setAction(rs.getString("action"));
                banner.setPosition(rs.getInt("position"));
                banner.setStatus(rs.getInt("status"));
                banner.setOrders(rs.getInt("orders"));
                banner.setImageUrlWithBaseDomain(rs.getString("image"));
                
                long currentTimeMillis = rs.getLong("created_date");
                Date date = new Date(currentTimeMillis);
                String dateStringCreated = sdf.format(date);
                banner.setCreatedDate(dateStringCreated);

                long currentTimeUpdated = rs.getLong("updated_date");
                Date dateUpdated = new Date(currentTimeUpdated);
                String dateStringUpdated = sdf.format(dateUpdated);
                banner.setUpdatedDate(dateStringUpdated);

                resultListBanner.add(banner);
            }

            return resultListBanner;
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            dbClient.releaseDbConnection(conn);
        }

        return resultListBanner;
    }

    public int getTotalBanner(FilterBanner filterBanner) {
        int total = 0;
        Connection conn = null;
        try {
            conn = dbClient.getDbConnection();
            if (null == conn) {
                return total;
            }
            String sql = "SELECT COUNT(id) AS total FROM `" + NAMETABLE + "` WHERE 1=1";
            if (StringUtils.isNotEmpty(filterBanner.getSearchQuery())) {
                sql = sql + " AND banner_name LIKE ? ";
            }

            if (filterBanner.getStatus() > 0) {
                sql = sql + " AND status = ? ";
            }

            PreparedStatement ps = conn.prepareStatement(sql);
            int param = 1;

            if (StringUtils.isNotEmpty(filterBanner.getSearchQuery())) {
                ps.setString(param++, "%" + filterBanner.getSearchQuery() + "%");
            }

            if (filterBanner.getStatus() > 0) {
                ps.setInt(param++, filterBanner.getStatus());
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

    public Banner getBannerByID(int id) {
        Banner result = new Banner();
        Connection conn = null;
        try {
            conn = dbClient.getDbConnection();
            if (null == conn) {
                return result;
            }
            PreparedStatement getBannerByIdStmt = conn.prepareStatement("SELECT * FROM `" + NAMETABLE + "` WHERE id = ? ");
            getBannerByIdStmt.setInt(1, id);

            ResultSet rs = getBannerByIdStmt.executeQuery();

            if (rs.next()) {
                result.setId(rs.getInt("id"));
                result.setBannerName(rs.getString("banner_name"));
                result.setAction(rs.getString("action"));
                result.setPosition(rs.getInt("position"));
                result.setStatus(rs.getInt("status"));
                result.setOrders(rs.getInt("orders"));
                result.setImageUrlWithBaseDomain(rs.getString("image"));

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


    public boolean isExistBannerName(int id, String bannerName) {
        Connection conn = null;
        try {
            conn = dbClient.getDbConnection();
            if (null == conn) {
                return false;
            }

            PreparedStatement isExistBannerNameSlugStmt = conn.prepareStatement("SELECT * FROM `" + NAMETABLE + "` WHERE `banner_name` = ? AND `id` <> ?");
            isExistBannerNameSlugStmt.setString(1, bannerName);
            isExistBannerNameSlugStmt.setInt(2, id);

            ResultSet rs = isExistBannerNameSlugStmt.executeQuery();
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

    public int addBanner(Banner banner) {
        Connection conn = null;
        boolean existBannerName = INSTANCE.isExistBannerName(0, banner.getBannerName());
        if (existBannerName == true) {
            return ErrorCode.EXIST.getValue();
        }
        try {
            conn = dbClient.getDbConnection();
            if (null == conn) {
                return ErrorCode.CONNECTION_FAIL.getValue();
            }

            PreparedStatement addStmt = conn.prepareStatement("INSERT INTO `" + NAMETABLE + "` (banner_name, action, position, image, "
                    + "status, orders, created_date, updated_date)"
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
            addStmt.setString(1, banner.getBannerName());
            addStmt.setString(2, banner.getAction());
            addStmt.setInt(3, banner.getPosition());
            addStmt.setString(4, banner.getImage());
            addStmt.setInt(5, banner.getStatus());
            addStmt.setInt(6, banner.getOrders());
            addStmt.setString(7, System.currentTimeMillis() + "");
            addStmt.setString(8, System.currentTimeMillis() + "");
            int result = addStmt.executeUpdate();

            return result;
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            dbClient.releaseDbConnection(conn);
        }

        return ErrorCode.FAIL.getValue();
    }

    public int editBanner(Banner banner) {
        Connection conn = null;
        try {
            conn = dbClient.getDbConnection();
            if (null == conn) {
                return ErrorCode.CONNECTION_FAIL.getValue();
            }

            PreparedStatement editStmt = conn.prepareStatement("UPDATE `" + NAMETABLE + "` SET banner_name = ?, action = ?, position = ?, "
                    + "image = ?, status = ?, orders = ?, updated_date = ? WHERE id = ?");
            editStmt.setString(1, banner.getBannerName());
            editStmt.setString(2, banner.getAction());
            editStmt.setInt(3, banner.getPosition());
            editStmt.setString(4, banner.getImage());
            editStmt.setInt(5, banner.getStatus());
            editStmt.setInt(6, banner.getOrders());
            editStmt.setString(7, System.currentTimeMillis() + "");
            editStmt.setInt(8, banner.getId());

            int rs = editStmt.executeUpdate();

            return rs;
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            dbClient.releaseDbConnection(conn);
        }
        return ErrorCode.FAIL.getValue();
    }

    public int deleteBanner(int id) {
        Connection conn = null;
        try {
            conn = dbClient.getDbConnection();
            if (null == conn) {
                return ErrorCode.CONNECTION_FAIL.getValue();
            }
            Banner bannerByID = getBannerByID(id);

            if (bannerByID.getId() == 0) {
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
