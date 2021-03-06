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

                boolean existCateNameNews = CategoryNewsModel.INSTANCE.isExistCateNameNews(0, cateNameNews);
                if (existCateNameNews == true) {
                    result.setErrorCode(-4);
                    result.setMessage("Category Name News ???? t???n t???i");
                } else {
                    int addCategoryNews = CategoryNewsModel.INSTANCE.addCategoryNews(categoryNews);
                    if (addCategoryNews >= 0) {
                        result.setErrorCode(0);
                        result.setMessage("Th??m Category News th??nh c??ng!");
                    } else {
                        result.setErrorCode(-1);
                        result.setMessage("Th??m Category News th???t b???i!");
                    }
                }

                break;
            }

            case "edit": {
                int id = NumberUtils.toInt(request.getParameter("id"));
                String cateNameNews = request.getParameter("cate_name_news");
                int orders = NumberUtils.toInt(request.getParameter("orders"));
                int property = NumberUtils.toInt(request.getParameter("property"));
                int status = NumberUtils.toInt(request.getParameter("status"));

                CategoryNews categoryNews = new CategoryNews();
                categoryNews.setId(id);
                categoryNews.setCategoryNameNews(cateNameNews);
                categoryNews.setCategoryNameNewsSlug(ServletUtil.toSlug(cateNameNews));
                categoryNews.setOrders(orders);
                categoryNews.setProperty(property);
                categoryNews.setStatus(status);

                CategoryNews categoryNewsByID = CategoryNewsModel.INSTANCE.getCategoryNewsByID(id);
                if (categoryNewsByID.getId() == 0) {
                    result.setErrorCode(-1);
                    result.setMessage("Th???t b???i!");
                    return;
                }
                boolean existCateNameNews = CategoryNewsModel.INSTANCE.isExistCateNameNews(id, cateNameNews);
                if (existCateNameNews == true) {
                    result.setErrorCode(-4);
                    result.setMessage("Category News Name ???? t???n t???i");
                } else {
                    int editCategoryNews = CategoryNewsModel.INSTANCE.editCategoryNews(categoryNews);
                    if (editCategoryNews >= 0) {
                        result.setErrorCode(0);
                        result.setMessage("S???a category news th??nh c??ng!");
                    } else {
                        result.setErrorCode(-1);
                        result.setMessage("S???a category news th???t b???i!");
                    }
                }
                break;
            }

            case "delete": {
                int id = NumberUtils.toInt(request.getParameter("id"));
                int deleteCategoryNews = CategoryNewsModel.INSTANCE.deleteCategoryNews(id);
                if (deleteCategoryNews >= 0) {
                    result.setErrorCode(0);
                    result.setMessage("X??a category news th??nh c??ng!");
                } else {
                    result.setErrorCode(-2);
                    result.setMessage("X??a category news th???t b???i!");
                }
                break;
            }
            default:
                throw new AssertionError();
        }

        ServletUtil.printJson(request, response, gson.toJson(result));

    }
}
