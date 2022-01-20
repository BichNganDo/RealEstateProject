package servlets.admin.api;

import com.google.gson.Gson;
import common.APIResult;
import entity.cate_news.CategoryNews;
import entity.cate_news.FilterCategoryNews;
import entity.cate_news.ListCategoryNews;
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
import model.CategoryNewsModel;
import org.apache.commons.lang3.math.NumberUtils;

public class APICategoryNewsServlet extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        APIResult result = new APIResult(0, "Success");

        String action = request.getParameter("action");
        switch (action) {
            case "getcategorynews": {
                int pageIndex = NumberUtils.toInt(request.getParameter("page_index"));
                int limit = NumberUtils.toInt(request.getParameter("limit"), 10);
                String searchQuery = request.getParameter("search_query");
                int searchStatus = NumberUtils.toInt(request.getParameter("search_status"));
                int searchProperty = NumberUtils.toInt(request.getParameter("search_property"));
                int offset = (pageIndex - 1) * limit;

                FilterCategoryNews filterCategoryNews = new FilterCategoryNews();
                filterCategoryNews.setLimit(limit);
                filterCategoryNews.setOffset(offset);
                filterCategoryNews.setSearchProperty(searchProperty);
                filterCategoryNews.setSearchQuery(searchQuery);
                filterCategoryNews.setStatus(searchStatus);

                List<CategoryNews> sliceCategoryNews = CategoryNewsModel.INSTANCE.getSliceCategoryNews(filterCategoryNews);
                int totalCategoryNews = CategoryNewsModel.INSTANCE.getTotalCategoryNews(filterCategoryNews);

                ListCategoryNews listCategoryNews = new ListCategoryNews();
                listCategoryNews.setTotal(totalCategoryNews);
                listCategoryNews.setListCategoryNews(sliceCategoryNews);
                listCategoryNews.setItemPerPage(10);

                if (sliceCategoryNews.size() > 0) {
                    result.setErrorCode(0);
                    result.setMessage("Success");
                    result.setData(listCategoryNews);
                } else {
                    result.setErrorCode(-1);
                    result.setMessage("Fail");
                }
                break;
            }
            case "getCategoryNewsById": {
                int idCategoryNews = NumberUtils.toInt(request.getParameter("id_category_news"));
                CategoryNews categoryNewsByID = CategoryNewsModel.INSTANCE.getCategoryNewsByID(idCategoryNews);

                if (categoryNewsByID.getId() > 0) {
                    result.setErrorCode(0);
                    result.setMessage("Success");
                    result.setData(categoryNewsByID);
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
                String cateNameNews = request.getParameter("cate_name_news");
                int orders = NumberUtils.toInt(request.getParameter("orders"));
                int property = NumberUtils.toInt(request.getParameter("property"));
                int status = NumberUtils.toInt(request.getParameter("status"));

                CategoryNews categoryNews = new CategoryNews();
                categoryNews.setCategoryNameNews(cateNameNews);
                categoryNews.setOrders(orders);
                categoryNews.setProperty(property);
                categoryNews.setStatus(status);

                boolean existCateNameNews = CategoryNewsModel.INSTANCE.isExistCateNameNews(cateNameNews);
                if (existCateNameNews == true) {
                    result.setErrorCode(-4);
                    result.setMessage("Category Name News đã tồn tại");
                } else {
                    int addCategoryNews = CategoryNewsModel.INSTANCE.addCategoryNews(categoryNews);
                    if (addCategoryNews >= 0) {
                        result.setErrorCode(0);
                        result.setMessage("Thêm Category News thành công!");
                    } else {
                        result.setErrorCode(-1);
                        result.setMessage("Thêm Category News thất bại!");
                    }
                }

                break;
            }
//            case "edit": {
//                int id = NumberUtils.toInt(request.getParameter("id"));
//                String cateName = request.getParameter("cate_name");
//                String cateNameSlug = ServletUtil.toSlug(request.getParameter("cate_name_slug"));
//                int idParent = NumberUtils.toInt(request.getParameter("id_parent"));
//                int orders = NumberUtils.toInt(request.getParameter("orders"));
//                int property = NumberUtils.toInt(request.getParameter("property"));
//                int status = NumberUtils.toInt(request.getParameter("status"));
//
//                Category category = new Category();
//                category.setId(id);
//                category.setCateName(cateName);
//                category.setParentId(idParent);
//                category.setCateNameSlug(cateNameSlug);
//                category.setOrders(orders);
//                category.setProperty(property);
//                category.setStatus(status);
//
//
//                Category categoryByID = CategoryModel.INSTANCE.getCategoryByID(id);
//
//                if (categoryByID.getId() == 0) {
//                    result.setErrorCode(-1);
//                    result.setMessage("Thất bại!");
//                    return;
//                }
//
//                boolean existCateNameSlug = CategoryModel.INSTANCE.isExistCateNameSlug(id, cateNameSlug);
//                if (existCateNameSlug == true) {
//                    result.setErrorCode(-4);
//                    result.setMessage("Category Name Slug đã tồn tại");
//                } else {
//                    int editCategory = CategoryModel.INSTANCE.editCategory(category);
//                    if (editCategory >= 0) {
//                        result.setErrorCode(0);
//                        result.setMessage("Sửa category thành công!");
//                    } else {
//                        result.setErrorCode(-1);
//                        result.setMessage("Sửa category thất bại!");
//                    }
//                }
//
//                break;
//            }

//            case "delete": {
//                int id = NumberUtils.toInt(request.getParameter("id"));
//                int deleteCategory = CategoryModel.INSTANCE.deleteCategory(id);
//                if (deleteCategory >= 0) {
//                    result.setErrorCode(0);
//                    result.setMessage("Xóa category thành công!");
//                } else {
//                    result.setErrorCode(-2);
//                    result.setMessage("Xóa category thất bại!");
//                }
//                break;
//            }
            default:
                throw new AssertionError();
        }

        ServletUtil.printJson(request, response, gson.toJson(result));

    }
}
