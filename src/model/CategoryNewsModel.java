package model;

import client.MysqlClient;
import common.ErrorCode;
import entity.cate_news.CategoryNews;
import entity.cate_news.FilterCategoryNews;
import entity.category.Category;
import entity.category.FilterCategory;
import helper.ServletUtil;
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

public class CategoryNewsModel {

    private static final MysqlClient dbClient = MysqlClient.getMysqlCli();
    private final String NAMETABLE = "category_news";
    final static Logger logger = Logger.getLogger(CategoryNewsModel.class);
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    public static CategoryNewsModel INSTANCE = new CategoryNewsModel();

    private CategoryNewsModel() {
        PropertyConfigurator.configure("log4j.properties");
    }

    public List<CategoryNews> getSliceCategoryNews(FilterCategoryNews filterCategoryNews) {
        List<CategoryNews> resultListCategoryNews = new ArrayList<>();
        Connection conn = null;
        try {
            conn = dbClient.getDbConnection();
            if (null == conn) {
                return resultListCategoryNews;

            }
            String sql = "SELECT * FROM `" + NAMETABLE + "` "
                    + " WHERE 1=1";

            if (StringUtils.isNotEmpty(filterCategoryNews.getSearchQuery())) {
                sql = sql + " AND cate_name_news LIKE ? ";
            }

            if (filterCategoryNews.getStatus() > 0) {
                sql = sql + " AND status = ? ";
            }

            if (filterCategoryNews.getSearchProperty() > 0) {
                sql = sql + " AND property = ? ";
            }

            sql = sql + " ORDER BY `orders` ASC LIMIT ? OFFSET ? ";

            PreparedStatement ps = conn.prepareStatement(sql);
            int param = 1;

            if (StringUtils.isNotEmpty(filterCategoryNews.getSearchQuery())) {
                ps.setString(param++, "%" + filterCategoryNews.getSearchQuery() + "%");
            }

            if (filterCategoryNews.getStatus() > 0) {
                ps.setInt(param++, filterCategoryNews.getStatus());
            }

            if (filterCategoryNews.getSearchProperty() > 0) {
                ps.setInt(param++, filterCategoryNews.getSearchProperty());
            }

            ps.setInt(param++, filterCategoryNews.getLimit());
            ps.setInt(param++, filterCategoryNews.getOffset());
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                CategoryNews categoryNews = new CategoryNews();

                categoryNews.setId(rs.getInt("id"));
                categoryNews.setCategoryNameNews(rs.getString("cate_name_news"));
                categoryNews.setCategoryNameNewsSlug(ServletUtil.toSlug(rs.getString("cate_name_news")));
                categoryNews.setStatus(rs.getInt("status"));
                categoryNews.setProperty(rs.getInt("property"));
                categoryNews.setOrders(rs.getInt("orders"));

                long currentTimeMillis = rs.getLong("created_date");
                Date date = new Date(currentTimeMillis);
                String dateStringCreated = sdf.format(date);
                categoryNews.setCreatedDate(dateStringCreated);

                long currentTimeUpdated = rs.getLong("updated_date");
                Date dateUpdated = new Date(currentTimeUpdated);
                String dateStringUpdated = sdf.format(dateUpdated);
                categoryNews.setUpdatedDate(dateStringUpdated);

                resultListCategoryNews.add(categoryNews);
            }

            return resultListCategoryNews;
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            dbClient.releaseDbConnection(conn);
        }

        return resultListCategoryNews;
    }

    public int getTotalCategoryNews(FilterCategoryNews filterCategoryNews) {
        int total = 0;
        Connection conn = null;
        try {
            conn = dbClient.getDbConnection();
            if (null == conn) {
                return total;
            }
            String sql = "SELECT COUNT(id) AS total FROM `" + NAMETABLE + "` WHERE 1=1";
            if (StringUtils.isNotEmpty(filterCategoryNews.getSearchQuery())) {
                sql = sql + " AND cate_name_news LIKE ? ";
            }

            if (filterCategoryNews.getStatus() > 0) {
                sql = sql + " AND status = ? ";
            }

            if (filterCategoryNews.getSearchProperty() > 0) {
                sql = sql + " AND property = ? ";
            }

            PreparedStatement ps = conn.prepareStatement(sql);
            int param = 1;

            if (StringUtils.isNotEmpty(filterCategoryNews.getSearchQuery())) {
                ps.setString(param++, "%" + filterCategoryNews.getSearchQuery() + "%");
            }

            if (filterCategoryNews.getStatus() > 0) {
                ps.setInt(param++, filterCategoryNews.getStatus());
            }

            if (filterCategoryNews.getSearchProperty() > 0) {
                ps.setInt(param++, filterCategoryNews.getSearchProperty());
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

    public CategoryNews getCategoryNewsByID(int id) {
        CategoryNews result = new CategoryNews();
        Connection conn = null;
        try {
            conn = dbClient.getDbConnection();
            if (null == conn) {
                return result;
            }
            PreparedStatement getCategoryByIdStmt = conn.prepareStatement("SELECT * FROM `" + NAMETABLE + "` WHERE id = ? ");
            getCategoryByIdStmt.setInt(1, id);

            ResultSet rs = getCategoryByIdStmt.executeQuery();

            if (rs.next()) {
                result.setId(rs.getInt("id"));
                result.setCategoryNameNews(rs.getString("cate_name_news"));
                result.setCategoryNameNewsSlug(ServletUtil.toSlug(rs.getString("cate_name_news")));
                result.setOrders(rs.getInt("orders"));
                result.setStatus(rs.getInt("status"));
                result.setProperty(rs.getInt("property"));

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

    public boolean isExistCateNameNews(int id, String cateNameNews) {
        Connection conn = null;
        try {
            conn = dbClient.getDbConnection();
            if (null == conn) {
                return false;
            }

            PreparedStatement isExistCateNewsNameStmt = conn.prepareStatement("SELECT * FROM `" + NAMETABLE + "` WHERE `cate_name_news` = ? AND `id` <> ?");
            isExistCateNewsNameStmt.setString(1, cateNameNews);
            isExistCateNewsNameStmt.setInt(2, id);

            ResultSet rs = isExistCateNewsNameStmt.executeQuery();
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

    public int addCategoryNews(CategoryNews categoryNews) {
        Connection conn = null;
        boolean existCateNameNews = INSTANCE.isExistCateNameNews(categoryNews.getId(), categoryNews.getCategoryNameNews());
        if (existCateNameNews == true) {
            return ErrorCode.EXIST.getValue();
        }
        try {
            conn = dbClient.getDbConnection();
            if (null == conn) {
                return ErrorCode.CONNECTION_FAIL.getValue();
            }

            PreparedStatement addStmt = conn.prepareStatement("INSERT INTO `" + NAMETABLE + "` (cate_name_news, "
                    + "status, property, orders, created_date, updated_date)"
                    + "VALUES (?, ?, ?, ?, ?, ?)");
            addStmt.setString(1, categoryNews.getCategoryNameNews());
            addStmt.setInt(2, categoryNews.getStatus());
            addStmt.setInt(3, categoryNews.getProperty().getValue());
            addStmt.setInt(4, categoryNews.getOrders());
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

    public int editCategoryNews(CategoryNews categoryNews) {
        Connection conn = null;
        try {
            conn = dbClient.getDbConnection();
            if (null == conn) {
                return ErrorCode.CONNECTION_FAIL.getValue();
            }

            PreparedStatement editStmt = conn.prepareStatement("UPDATE `" + NAMETABLE + "` SET cate_name_news = ?, "
                    + "status = ?, property = ?, orders = ?, updated_date = ? WHERE id = ?");
            editStmt.setString(1, categoryNews.getCategoryNameNews());
            editStmt.setInt(2, categoryNews.getStatus());
            editStmt.setInt(3, categoryNews.getProperty().getValue());
            editStmt.setInt(4, categoryNews.getOrders());
            editStmt.setString(5, System.currentTimeMillis() + "");
            editStmt.setInt(6, categoryNews.getId());

            int rs = editStmt.executeUpdate();

            return rs;
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            dbClient.releaseDbConnection(conn);
        }
        return ErrorCode.FAIL.getValue();
    }

    public int deleteCategoryNews(int id) {
        Connection conn = null;
        try {
            conn = dbClient.getDbConnection();
            if (null == conn) {
                return ErrorCode.CONNECTION_FAIL.getValue();
            }
            CategoryNews categoryNewsByID = getCategoryNewsByID(id);

            if (categoryNewsByID.getId() == 0) {
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
