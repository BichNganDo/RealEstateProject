package model;

import client.MysqlClient;
import common.ErrorCode;
import entity.news.FilterNews;
import entity.news.News;
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

public class NewsModel {

    private static final MysqlClient dbClient = MysqlClient.getMysqlCli();
    private final String NAMETABLE = "news";
    final static Logger logger = Logger.getLogger(NewsModel.class);
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    public static NewsModel INSTANCE = new NewsModel();

    private NewsModel() {
        PropertyConfigurator.configure("log4j.properties");
    }

    public List<News> getSliceNews(FilterNews filterNews) {
        List<News> resultListNews = new ArrayList<>();
        Connection conn = null;
        try {
            conn = dbClient.getDbConnection();
            if (null == conn) {
                return resultListNews;

            }
            String sql = "SELECT news.*, category_news.cate_name_news AS `category_news` "
                    + "FROM news "
                    + "INNER JOIN category_news ON news.id_cate_news= category_news.id  WHERE 1=1";

            if (StringUtils.isNotEmpty(filterNews.getSearchQuery())) {
                sql = sql + " AND news.title LIKE ? ";
            }

            if (filterNews.getStatus() > 0) {
                sql = sql + " AND news.status = ? ";
            }

            if (filterNews.getSearchProperty() > 0) {
                sql = sql + " AND news.property = ? ";
            }

            if (filterNews.getSearchCategoryNews() > 0) {
                sql = sql + " AND news.id_cate_news = ? ";
            }

            sql = sql + " ORDER BY `orders` ASC LIMIT ? OFFSET ? ";

            PreparedStatement ps = conn.prepareStatement(sql);
            int param = 1;

            if (StringUtils.isNotEmpty(filterNews.getSearchQuery())) {
                ps.setString(param++, "%" + filterNews.getSearchQuery() + "%");
            }

            if (filterNews.getStatus() > 0) {
                ps.setInt(param++, filterNews.getStatus());
            }

            if (filterNews.getSearchProperty() > 0) {
                ps.setInt(param++, filterNews.getSearchProperty());
            }

            if (filterNews.getSearchCategoryNews() > 0) {
                ps.setInt(param++, filterNews.getSearchCategoryNews());
            }

            ps.setInt(param++, filterNews.getLimit());
            ps.setInt(param++, filterNews.getOffset());
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                News news = new News();

                news.setId(rs.getInt("id"));
                news.setTitle(rs.getString("title"));
                news.setDescription(rs.getString("description"));
                news.setContent(rs.getString("content"));
                news.setIdCategoryNews(rs.getInt("id_cate_news"));
                news.setCategoryNewsName(rs.getString("category_news"));
                news.setImageUrlWithBaseDomain(rs.getString("image"));
                news.setStatus(rs.getInt("status"));
                news.setProperty(rs.getInt("property"));
                news.setOrders(rs.getInt("orders"));

                long currentTimeMillis = rs.getLong("created_date");
                Date date = new Date(currentTimeMillis);
                String dateStringCreated = sdf.format(date);
                news.setCreatedDate(dateStringCreated);

                long currentTimeUpdated = rs.getLong("updated_date");
                Date dateUpdated = new Date(currentTimeUpdated);
                String dateStringUpdated = sdf.format(dateUpdated);
                news.setUpdatedDate(dateStringUpdated);

                resultListNews.add(news);
            }

            return resultListNews;
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            dbClient.releaseDbConnection(conn);
        }

        return resultListNews;
    }

    public int getTotalNews(FilterNews filterNews) {
        int total = 0;
        Connection conn = null;
        try {
            conn = dbClient.getDbConnection();
            if (null == conn) {
                return total;
            }
            String sql = "SELECT COUNT(id) AS total FROM `" + NAMETABLE + "` WHERE 1=1";
            if (StringUtils.isNotEmpty(filterNews.getSearchQuery())) {
                sql = sql + " AND news.title LIKE ? ";
            }

            if (filterNews.getStatus() > 0) {
                sql = sql + " AND news.status = ? ";
            }

            if (filterNews.getSearchProperty() > 0) {
                sql = sql + " AND news.property = ? ";
            }

            if (filterNews.getSearchCategoryNews() > 0) {
                sql = sql + " AND news.id_cate_news = ? ";
            }

            PreparedStatement ps = conn.prepareStatement(sql);
            int param = 1;

            if (StringUtils.isNotEmpty(filterNews.getSearchQuery())) {
                ps.setString(param++, "%" + filterNews.getSearchQuery() + "%");
            }

            if (filterNews.getStatus() > 0) {
                ps.setInt(param++, filterNews.getStatus());
            }

            if (filterNews.getSearchProperty() > 0) {
                ps.setInt(param++, filterNews.getSearchProperty());
            }

            if (filterNews.getSearchCategoryNews() > 0) {
                ps.setInt(param++, filterNews.getSearchCategoryNews());
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

    public News getNewsByID(int id) {
        News result = new News();
        Connection conn = null;
        try {
            conn = dbClient.getDbConnection();
            if (null == conn) {
                return result;
            }
            PreparedStatement getNewsByIdStmt = conn.prepareStatement("SELECT * FROM `" + NAMETABLE + "` WHERE id = ? ");
            getNewsByIdStmt.setInt(1, id);

            ResultSet rs = getNewsByIdStmt.executeQuery();

            if (rs.next()) {
                result.setId(rs.getInt("id"));
                result.setTitle(rs.getString("title"));
                result.setDescription(rs.getString("description"));
                result.setContent(rs.getString("content"));
                result.setIdCategoryNews(rs.getInt("id_cate_news"));
                result.setImageUrlWithBaseDomain(rs.getString("image"));
                result.setStatus(rs.getInt("status"));
                result.setProperty(rs.getInt("property"));
                result.setOrders(rs.getInt("orders"));

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

    public boolean isExistTitle(int id, String title) {
        Connection conn = null;
        try {
            conn = dbClient.getDbConnection();
            if (null == conn) {
                return false;
            }

            PreparedStatement isExistBannerNameSlugStmt = conn.prepareStatement("SELECT * FROM `" + NAMETABLE + "` WHERE `title` = ? AND `id` <> ?");
            isExistBannerNameSlugStmt.setString(1, title);
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

    public int addNews(News news) {
        Connection conn = null;
        try {
            conn = dbClient.getDbConnection();
            if (null == conn) {
                return ErrorCode.CONNECTION_FAIL.getValue();
            }
            PreparedStatement addStmt = conn.prepareStatement("INSERT INTO `" + NAMETABLE + "` (id_cate_news, title, description, "
                    + "content, image, status, orders, property, "
                    + "created_date, updated_date) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            addStmt.setInt(1, news.getIdCategoryNews());
            addStmt.setString(2, news.getTitle());
            addStmt.setString(3, news.getDescription());
            addStmt.setString(4, news.getContent());
            addStmt.setString(5, news.getImage());
            addStmt.setInt(6, news.getStatus());
            addStmt.setInt(7, news.getOrders());
            addStmt.setInt(8, news.getProperty().getValue());
            addStmt.setString(9, System.currentTimeMillis() + "");
            addStmt.setString(10, System.currentTimeMillis() + "");

            int rs = addStmt.executeUpdate();

            return rs;
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            dbClient.releaseDbConnection(conn);
        }
        return ErrorCode.FAIL.getValue();
    }

    public int editNews(News news) {
        Connection conn = null;
        try {
            conn = dbClient.getDbConnection();
            if (null == conn) {
                return ErrorCode.CONNECTION_FAIL.getValue();
            }

            PreparedStatement editStmt = conn.prepareStatement("UPDATE `" + NAMETABLE + "` SET id_cate_news = ?, title = ?, description = ?, "
                    + "content = ?, image = ?, status = ?, orders = ?, property = ?, updated_date = ? WHERE id = ? ");
            editStmt.setInt(1, news.getIdCategoryNews());
            editStmt.setString(2, news.getTitle());
            editStmt.setString(3, news.getDescription());
            editStmt.setString(4, news.getContent());
            editStmt.setString(5, news.getImage());
            editStmt.setInt(6, news.getStatus());
            editStmt.setInt(7, news.getOrders());
            editStmt.setInt(8, news.getProperty().getValue());
            editStmt.setString(9, System.currentTimeMillis() + "");
            editStmt.setInt(10, news.getId());

            int rs = editStmt.executeUpdate();

            return rs;
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            dbClient.releaseDbConnection(conn);
        }
        return ErrorCode.FAIL.getValue();
    }

    public int deleteNews(int id) {
        Connection conn = null;
        try {
            conn = dbClient.getDbConnection();
            if (null == conn) {
                return ErrorCode.CONNECTION_FAIL.getValue();
            }

            News newsByID = getNewsByID(id);
            if (newsByID.getId() == 0) {
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
