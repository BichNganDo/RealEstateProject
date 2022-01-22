package servlets.admin.api;

import com.google.gson.Gson;
import common.APIResult;
import entity.news.FilterNews;
import entity.news.ListNews;
import entity.news.News;
import helper.ServletUtil;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.NewsModel;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class APINewsServlet extends HttpServlet {

    final static Logger logger = Logger.getLogger(APINewsServlet.class);

    public APINewsServlet() {
        PropertyConfigurator.configure("log4j.properties");
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        APIResult result = new APIResult(0, "Success");

        String action = request.getParameter("action");
        switch (action) {
            case "getnews": {
                int pageIndex = NumberUtils.toInt(request.getParameter("page_index"));
                int limit = NumberUtils.toInt(request.getParameter("limit"), 10);
                String searchQuery = request.getParameter("search_query");
                int searchStatus = NumberUtils.toInt(request.getParameter("search_status"));
                int searchProperty = NumberUtils.toInt(request.getParameter("search_property"));
                int searchCategoryNews = NumberUtils.toInt(request.getParameter("search_category_news"));
                int offset = (pageIndex - 1) * limit;

                FilterNews filterNews = new FilterNews();
                filterNews.setLimit(limit);
                filterNews.setOffset(offset);
                filterNews.setSearchQuery(searchQuery);
                filterNews.setStatus(searchStatus);
                filterNews.setSearchProperty(searchStatus);
                filterNews.setSearchCategoryNews(searchCategoryNews);

                List<News> sliceNews = NewsModel.INSTANCE.getSliceNews(filterNews);
                int totalNews = NewsModel.INSTANCE.getTotalNews(filterNews);

                ListNews listNews = new ListNews();
                listNews.setTotal(totalNews);
                listNews.setListNews(sliceNews);
                listNews.setItemPerPage(10);

                if (sliceNews.size() >= 0) {
                    result.setErrorCode(0);
                    result.setMessage("Success");
                    result.setData(listNews);
                } else {
                    result.setErrorCode(-1);
                    result.setMessage("Fail");
                }
                break;
            }
            case "getNewsById": {
                int idNews = NumberUtils.toInt(request.getParameter("id_news"));
                News newsByID = NewsModel.INSTANCE.getNewsByID(idNews);

                if (newsByID.getId() > 0) {
                    result.setErrorCode(0);
                    result.setMessage("Success");
                    result.setData(newsByID);
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
                try {
                    boolean isMultipart = ServletFileUpload.isMultipartContent(request);
                    if (isMultipart) {
                        News news = new News();
                        ServletFileUpload upload = new ServletFileUpload(new DiskFileItemFactory());
                        upload.setHeaderEncoding("UTF-8");
                        List<FileItem> items = upload.parseRequest(request);
                        for (FileItem item : items) {
                            if (item.isFormField()) {
                                // Process regular form field (input type="text|radio|checkbox|etc", select, etc).
                                String fieldname = item.getFieldName();
                                String fieldvalue = item.getString("UTF-8");

                                switch (fieldname) {
                                    case "category_news": {
                                        news.setIdCategoryNews(NumberUtils.toInt(fieldvalue));
                                        break;
                                    }
                                    case "title": {
                                        news.setTitle(fieldvalue);
                                        break;
                                    }
                                    case "description": {
                                        news.setDescription(fieldvalue);
                                        break;
                                    }
                                    case "content": {
                                        news.setContent(fieldvalue);
                                        break;
                                    }
                                    case "status": {
                                        news.setStatus(NumberUtils.toInt(fieldvalue));
                                        break;
                                    }
                                    case "orders": {
                                        news.setOrders(NumberUtils.toInt(fieldvalue));
                                        break;
                                    }
                                    case "property": {
                                        news.setProperty(NumberUtils.toInt(fieldvalue));
                                        break;
                                    }
                                }

                            } else {
                                // Process form file field (input type="file").
                                String filename = FilenameUtils.getName(item.getName());
                                InputStream a = item.getInputStream();
                                Path uploadDir = Paths.get("upload/news/" + filename);
                                Files.copy(a, uploadDir, StandardCopyOption.REPLACE_EXISTING);
                                news.setImage("upload/news/" + filename);
                            }
                        }
                        boolean existTitle = NewsModel.INSTANCE.isExistTitle(0, news.getTitle());
                        if (existTitle == true) {
                            result.setErrorCode(-4);
                            result.setMessage("Title đã tồn tại");
                        } else {
                            int addNews = NewsModel.INSTANCE.addNews(news);
                            if (addNews >= 0) {
                                result.setErrorCode(0);
                                result.setMessage("Thêm news thành công!");
                            } else {
                                result.setErrorCode(-1);
                                result.setMessage("Thêm news thất bại!");
                            }
                        }

                    } else {
                        result.setErrorCode(-5);
                        result.setMessage("Có lỗi");
                    }
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }
                break;
            }

//            case "edit": {
//                try {
//                    boolean isMultipart = ServletFileUpload.isMultipartContent(request);
//                    if (isMultipart) {
//                        Banner banner = new Banner();
//                        String oldImage = "";
//                        String newImage = "";
//                        ServletFileUpload upload = new ServletFileUpload(new DiskFileItemFactory());
//                        upload.setHeaderEncoding("UTF-8");
//
//                        List<FileItem> items = upload.parseRequest(request);
//                        for (FileItem item : items) {
//                            if (item.isFormField()) {
//                                // Process regular form field (input type="text|radio|checkbox|etc", select, etc).
//                                String fieldname = item.getFieldName();
//                                String fieldvalue = item.getString("UTF-8");
//
//                                switch (fieldname) {
//                                    case "id": {
//                                        banner.setId(NumberUtils.toInt(fieldvalue));
//                                        break;
//                                    }
//                                    case "banner_name": {
//                                        banner.setBannerName(fieldvalue);
//                                        break;
//                                    }
//                                    case "action": {
//                                        banner.setAction(fieldvalue);
//                                        break;
//                                    }
//                                    case "position": {
//                                        banner.setPosition(NumberUtils.toInt(fieldvalue));
//                                        break;
//                                    }
//                                    case "status": {
//                                        banner.setStatus(NumberUtils.toInt(fieldvalue));
//                                        break;
//                                    }
//                                    case "orders": {
//                                        banner.setOrders(NumberUtils.toInt(fieldvalue));
//                                        break;
//                                    }
//                                    case "old_image": {
//                                        oldImage = fieldvalue;
//                                        break;
//                                    }
//
//                                }
//
//                            } else {
//                                // Process form file field (input type="file").
//                                String filename = FilenameUtils.getName(item.getName());
//                                InputStream a = item.getInputStream();
//                                Path uploadDir = Paths.get("upload/banner/" + filename);
//                                Files.copy(a, uploadDir, StandardCopyOption.REPLACE_EXISTING);
//                                newImage = "upload/banner/" + filename;
//                            }
//                        }
//
//                        if (StringUtils.isNotEmpty(newImage)) {
//                            banner.setImage(newImage);
//                        } else {
//                            banner.setImage(oldImage);
//                        }
//                        Banner bannerByID = BannerModel.INSTANCE.getBannerByID(banner.getId());
//                        if (bannerByID.getId() == 0) {
//                            result.setErrorCode(-1);
//                            result.setMessage("Thất bại");
//                            return;
//                        }
//                        boolean existBannerName = BannerModel.INSTANCE.isExistBannerName(banner.getId(), banner.getBannerName());
//                        if (existBannerName == true) {
//                            result.setErrorCode(-4);
//                            result.setMessage("Banner Name đã tồn tại");
//                        } else {
//                            int editBanner = BannerModel.INSTANCE.editBanner(banner);
//                            if (editBanner >= 0) {
//                                result.setErrorCode(0);
//                                result.setMessage("Sửa banner thành công!");
//                            } else {
//                                result.setErrorCode(-1);
//                                result.setMessage("Sửa banner thất bại!");
//                            }
//                        }
//                    } else {
//                        result.setErrorCode(-4);
//                        result.setMessage("Có lỗi");
//                    }
//
//                } catch (Exception e) {
//                    System.out.println(e.getMessage());
//                }
//                break;
//            }

//            case "delete": {
//                int id = NumberUtils.toInt(request.getParameter("id"));
//                int deleteBanner = BannerModel.INSTANCE.deleteBanner(id);
//                if (deleteBanner >= 0) {
//                    result.setErrorCode(0);
//                    result.setMessage("Xóa banner thành công1");
//                } else {
//                    result.setErrorCode(-2);
//                    result.setMessage("Xóa banner thất bại!");
//                }
//                break;
//            }
            default:
                throw new AssertionError();
        }

        ServletUtil.printJson(request, response, gson.toJson(result));
    }

}
