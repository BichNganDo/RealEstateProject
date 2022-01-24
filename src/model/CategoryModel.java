package model;

import client.MysqlClient;
import common.ErrorCode;
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

public class CategoryModel {

    private static final MysqlClient dbClient = MysqlClient.getMysqlCli();
    private final String NAMETABLE = "category";
    final static Logger logger = Logger.getLogger(CategoryModel.class);
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    public static CategoryModel INSTANCE = new CategoryModel();

    private CategoryModel() {
        PropertyConfigurator.configure("log4j.properties");
    }


    public List<Category> getSliceCategory(FilterCategory filterCategory) {
        List<Category> resultListCategory = new ArrayList<>();
        Connection conn = null;
        try {
            conn = dbClient.getDbConnection();
            if (null == conn) {
                return resultListCategory;

            }
            String sql = "SELECT c.*, p.cate_name cate_name_parent FROM `" + NAMETABLE + "` "
                    + "c LEFT JOIN `" + NAMETABLE + "` p ON c.id_parent = p.id "
                    + " WHERE 1=1";

            if (StringUtils.isNotEmpty(filterCategory.getSearchQuery())) {
                sql = sql + " AND c.cate_name LIKE ? ";
            }

            if (filterCategory.getStatus() > 0) {
                sql = sql + " AND c.status = ? ";
            }

            if (filterCategory.getSearchProperty() > 0) {
                sql = sql + " AND c.property = ? ";
            }

            sql = sql + " ORDER BY `orders` ASC LIMIT ? OFFSET ? ";

            PreparedStatement ps = conn.prepareStatement(sql);
            int param = 1;

            if (StringUtils.isNotEmpty(filterCategory.getSearchQuery())) {
                ps.setString(param++, "%" + filterCategory.getSearchQuery() + "%");
            }


            if (filterCategory.getStatus() > 0) {
                ps.setInt(param++, filterCategory.getStatus());
            }

            if (filterCategory.getSearchProperty() > 0) {
                ps.setInt(param++, filterCategory.getSearchProperty());
            }

            ps.setInt(param++, filterCategory.getLimit());
            ps.setInt(param++, filterCategory.getOffset());
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Category category = new Category();

                category.setId(rs.getInt("id"));
                category.setCateName(rs.getString("cate_name"));
                //Cate Name Slug
//                String cateNameSlug = ServletUtil.toSlug(category.getCateName());
                category.setCateNameSlug(rs.getString("cate_name_slug"));

                category.setParentId(rs.getInt("id_parent"));
                category.setParentName(rs.getString("cate_name_parent"));

                category.setStatus(rs.getInt("status"));
                category.setProperty(rs.getInt("property"));
                category.setOrders(rs.getInt("orders"));

                long currentTimeMillis = rs.getLong("created_date");
                Date date = new Date(currentTimeMillis);
                String dateStringCreated = sdf.format(date);
                category.setCreatedDate(dateStringCreated);

                long currentTimeUpdated = rs.getLong("updated_date");
                Date dateUpdated = new Date(currentTimeUpdated);
                String dateStringUpdated = sdf.format(dateUpdated);
                category.setUpdatedDate(dateStringUpdated);

                resultListCategory.add(category);
            }

            return resultListCategory;
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            dbClient.releaseDbConnection(conn);
        }

        return resultListCategory;
    }

    public int getTotalCategory(FilterCategory filterCategory) {
        int total = 0;
        Connection conn = null;
        try {
            conn = dbClient.getDbConnection();
            if (null == conn) {
                return total;
            }
            String sql = "SELECT COUNT(id) AS total FROM `" + NAMETABLE + "` WHERE 1=1";
            if (StringUtils.isNotEmpty(filterCategory.getSearchQuery())) {
                sql = sql + " AND cate_name LIKE ? ";
            }

            if (filterCategory.getStatus() > 0) {
                sql = sql + " AND status = ? ";
            }

            if (filterCategory.getSearchProperty() > 0) {
                sql = sql + " AND property = ? ";
            }

            PreparedStatement ps = conn.prepareStatement(sql);
            int param = 1;

            if (StringUtils.isNotEmpty(filterCategory.getSearchQuery())) {
                ps.setString(param++, "%" + filterCategory.getSearchQuery() + "%");
            }

            if (filterCategory.getStatus() > 0) {
                ps.setInt(param++, filterCategory.getStatus());
            }

            if (filterCategory.getSearchProperty() > 0) {
                ps.setInt(param++, filterCategory.getSearchProperty());
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

    public Category getCategoryByID(int id) {
        Category result = new Category();
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
                result.setCateName(rs.getString("cate_name"));
                result.setCateNameSlug(rs.getString("cate_name_slug"));

                result.setParentId(rs.getInt("id_parent"));

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

    public int getIdCateByNameSlug(String nameSlug) {
        int idCate = 0;
        Connection conn = null;
        try {
            conn = dbClient.getDbConnection();
            if (null == conn) {
                return idCate;
            }
            PreparedStatement getIdCateByCategoryStmt = conn.prepareStatement("SELECT category.id FROM `" + NAMETABLE + "` WHERE cate_name_slug = ? ");
            getIdCateByCategoryStmt.setString(1, nameSlug);

            ResultSet rs = getIdCateByCategoryStmt.executeQuery();

            if (rs.next()) {
                idCate = rs.getInt("id");
            }

        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            dbClient.releaseDbConnection(conn);
        }

        return idCate;
    }

    public int getIdCateByCategory(String categoryName) {
        int idCate = 0;
        Connection conn = null;
        try {
            conn = dbClient.getDbConnection();
            if (null == conn) {
                return idCate;
            }
            PreparedStatement getIdCateByCategoryStmt = conn.prepareStatement("SELECT category.id FROM `" + NAMETABLE + "` WHERE cate_name = ? ");
            getIdCateByCategoryStmt.setString(1, categoryName);

            ResultSet rs = getIdCateByCategoryStmt.executeQuery();

            if (rs.next()) {
                idCate = rs.getInt("id");
            }

        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            dbClient.releaseDbConnection(conn);
        }

//        if (idCate > 0) {
//            return idCate;
//        }
//
//        String nameSlug = ServletUtil.toSlug(categoryName);
//        idCate = INSTANCE.addCategoryFlm(categoryName, nameSlug, 1);

        return idCate;

    }

    public boolean isExistCateNameSlug(int id, String cateNameSlug) {
        Connection conn = null;
        try {
            conn = dbClient.getDbConnection();
            if (null == conn) {
                return false;
            }

            PreparedStatement isExistCateNameSlugStmt = conn.prepareStatement("SELECT * FROM `" + NAMETABLE + "` WHERE `cate_name_slug` = ? AND `id` <> ?");
            isExistCateNameSlugStmt.setString(1, cateNameSlug);
            isExistCateNameSlugStmt.setInt(2, id);

            ResultSet rs = isExistCateNameSlugStmt.executeQuery();
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

    public int addCategory(Category category) {
        Connection conn = null;
        boolean existCateNameSlug = INSTANCE.isExistCateNameSlug(0, category.getCateNameSlug());
        if (existCateNameSlug == true) {
            return ErrorCode.EXIST.getValue();
        }
        try {
            conn = dbClient.getDbConnection();
            if (null == conn) {
                return ErrorCode.CONNECTION_FAIL.getValue();
            }

            PreparedStatement addStmt = conn.prepareStatement("INSERT INTO `" + NAMETABLE + "` (id_parent, cate_name, cate_name_slug, "
                    + "status, property, orders, created_date, updated_date)"
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
            addStmt.setInt(1, category.getParentId());
            addStmt.setString(2, category.getCateName());
            addStmt.setString(3, category.getCateNameSlug());
            addStmt.setInt(4, category.getStatus());
            addStmt.setInt(5, category.getProperty().getValue());
            addStmt.setInt(6, category.getOrders());
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

    public int editCategory(Category category) {
        Connection conn = null;
        try {
            conn = dbClient.getDbConnection();
            if (null == conn) {
                return ErrorCode.CONNECTION_FAIL.getValue();
            }

            PreparedStatement editStmt = conn.prepareStatement("UPDATE `" + NAMETABLE + "` SET id_parent = ?, cate_name = ?, cate_name_slug = ?, "
                    + "status = ?, property = ?, orders = ?, updated_date = ? WHERE id = ?");
            editStmt.setInt(1, category.getParentId());
            editStmt.setString(2, category.getCateName());
            editStmt.setString(3, category.getCateNameSlug());
            editStmt.setInt(4, category.getStatus());
            editStmt.setInt(5, category.getProperty().getValue());
            editStmt.setInt(6, category.getOrders());
            editStmt.setString(7, System.currentTimeMillis() + "");
            editStmt.setInt(8, category.getId());

            int rs = editStmt.executeUpdate();
            if (rs >= 0) {
                logger.info("editCategory - Success | Sql: " + editStmt);
            } else {
                logger.error("editCategory - Fail | Sql: " + editStmt);
            }
            return rs;
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            dbClient.releaseDbConnection(conn);
        }
        return ErrorCode.FAIL.getValue();
    }

    public int deleteCategory(int id) {
        Connection conn = null;
        try {
            conn = dbClient.getDbConnection();
            if (null == conn) {
                return ErrorCode.CONNECTION_FAIL.getValue();
            }
            Category categoryByID = getCategoryByID(id);

            if (categoryByID.getId() == 0) {
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
