package servlets.admin.api;

import com.google.gson.Gson;
import common.APIResult;
import entity.category.Category;
import entity.category.FilterCategory;
import entity.category.ListCategory;
import helper.ServletUtil;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.CategoryModel;
import org.apache.commons.lang3.math.NumberUtils;

public class APICategoryServlet extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        APIResult result = new APIResult(0, "Success");

        String action = request.getParameter("action");
        switch (action) {
            case "getcategory": {
                int pageIndex = NumberUtils.toInt(request.getParameter("page_index"));
                int limit = NumberUtils.toInt(request.getParameter("limit"), 10);
                String searchQuery = request.getParameter("search_query");
                int searchStatus = NumberUtils.toInt(request.getParameter("search_status"));
                int searchProperty = NumberUtils.toInt(request.getParameter("search_property"));
                int offset = (pageIndex - 1) * limit;

                FilterCategory filterCategory = new FilterCategory();
                filterCategory.setLimit(limit);
                filterCategory.setOffset(offset);
                filterCategory.setSearchProperty(searchProperty);
                filterCategory.setSearchQuery(searchQuery);
                filterCategory.setStatus(searchStatus);

                List<Category> sliceCategory = CategoryModel.INSTANCE.getSliceCategory(filterCategory);
                int totalCategory = CategoryModel.INSTANCE.getTotalCategory(filterCategory);

                ListCategory listCategory = new ListCategory();
                listCategory.setTotal(totalCategory);
                listCategory.setListCategory(sliceCategory);
                listCategory.setItemPerPage(10);

                if (sliceCategory.size() > 0) {
                    result.setErrorCode(0);
                    result.setMessage("Success");
                    result.setData(listCategory);
                } else {
                    result.setErrorCode(-1);
                    result.setMessage("Fail");
                }
                break;
            }
            case "getCategoryById": {
                int idCategory = NumberUtils.toInt(request.getParameter("id_category"));
                Category categoryByID = CategoryModel.INSTANCE.getCategoryByID(idCategory);

                if (categoryByID.getId() > 0) {
                    result.setErrorCode(0);
                    result.setMessage("Success");
                    result.setData(categoryByID);
                } else {
                    result.setErrorCode(-1);
                    result.setMessage("Fail");
                }
                break;
            }

            default:
                throw new AssertionError();
        }

        ServletUtil.printJson(request, response, gson.toJson(result));
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        APIResult result = new APIResult(0, "Success");

        String action = request.getParameter("action");
        switch (action) {
            case "add": {
                String cateName = request.getParameter("cate_name");
                int idParent = NumberUtils.toInt(request.getParameter("id_parent"));
                int orders = NumberUtils.toInt(request.getParameter("orders"));
                int property = NumberUtils.toInt(request.getParameter("property"));
                int status = NumberUtils.toInt(request.getParameter("status"));
                String cateNameSlug = ServletUtil.toSlug(cateName);

                Category category = new Category();
                category.setCateName(cateName);
                category.setParentId(idParent);
                category.setCateNameSlug(cateNameSlug);
                category.setOrders(orders);
                category.setProperty(property);
                category.setStatus(status);

                boolean existCateNameSlug = CategoryModel.INSTANCE.isExistCateNameSlug(0, cateNameSlug);
                if (existCateNameSlug == true) {
                    result.setErrorCode(-4);
                    result.setMessage("Category Name Slug đã tồn tại");
                } else {
                    int addCategory = CategoryModel.INSTANCE.addCategory(category);
                    if (addCategory >= 0) {
                        result.setErrorCode(0);
                        result.setMessage("Thêm Category thành công!");
                    } else {
                        result.setErrorCode(-1);
                        result.setMessage("Thêm Category thất bại!");
                    }
                }

                break;
            }
            case "edit": {
                int id = NumberUtils.toInt(request.getParameter("id"));
                String cateName = request.getParameter("cate_name");
                String cateNameSlug = ServletUtil.toSlug(request.getParameter("cate_name_slug"));
                int idParent = NumberUtils.toInt(request.getParameter("id_parent"));
                int orders = NumberUtils.toInt(request.getParameter("orders"));
                int property = NumberUtils.toInt(request.getParameter("property"));
                int status = NumberUtils.toInt(request.getParameter("status"));

                Category category = new Category();
                category.setId(id);
                category.setCateName(cateName);
                category.setParentId(idParent);
                category.setCateNameSlug(cateNameSlug);
                category.setOrders(orders);
                category.setProperty(property);
                category.setStatus(status);


                Category categoryByID = CategoryModel.INSTANCE.getCategoryByID(id);

                if (categoryByID.getId() == 0) {
                    result.setErrorCode(-1);
                    result.setMessage("Thất bại!");
                    return;
                }

                boolean existCateNameSlug = CategoryModel.INSTANCE.isExistCateNameSlug(id, cateNameSlug);
                if (existCateNameSlug == true) {
                    result.setErrorCode(-4);
                    result.setMessage("Category Name Slug đã tồn tại");
                } else {
                    int editCategory = CategoryModel.INSTANCE.editCategory(category);
                    if (editCategory >= 0) {
                        result.setErrorCode(0);
                        result.setMessage("Sửa category thành công!");
                    } else {
                        result.setErrorCode(-1);
                        result.setMessage("Sửa category thất bại!");
                    }
                }

                break;
            }

            case "delete": {
                int id = NumberUtils.toInt(request.getParameter("id"));
                int deleteCategory = CategoryModel.INSTANCE.deleteCategory(id);
                if (deleteCategory >= 0) {
                    result.setErrorCode(0);
                    result.setMessage("Xóa category thành công!");
                } else {
                    result.setErrorCode(-2);
                    result.setMessage("Xóa category thất bại!");
                }
                break;
            }
            default:
                throw new AssertionError();
        }

        ServletUtil.printJson(request, response, gson.toJson(result));

    }
}
