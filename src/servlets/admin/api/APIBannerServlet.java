package servlets.admin.api;

import com.google.gson.Gson;
import common.APIResult;
import entity.banner.Banner;
import entity.banner.FilterBanner;
import entity.banner.ListBanner;
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
import model.BannerModel;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class APIBannerServlet extends HttpServlet {

    final static Logger logger = Logger.getLogger(APIBannerServlet.class);

    public APIBannerServlet() {
        PropertyConfigurator.configure("log4j.properties");
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        APIResult result = new APIResult(0, "Success");

        String action = request.getParameter("action");
        switch (action) {
            case "getbanner": {
                int pageIndex = NumberUtils.toInt(request.getParameter("page_index"));
                int limit = NumberUtils.toInt(request.getParameter("limit"), 10);
                String searchQuery = request.getParameter("search_query");
                int searchStatus = NumberUtils.toInt(request.getParameter("search_status"));
                int offset = (pageIndex - 1) * limit;

                FilterBanner filterBanner = new FilterBanner();
                filterBanner.setLimit(limit);
                filterBanner.setOffset(offset);
                filterBanner.setSearchQuery(searchQuery);
                filterBanner.setStatus(searchStatus);
                List<Banner> sliceBanner = BannerModel.INSTANCE.getSliceBanner(filterBanner);
                int totalBanner = BannerModel.INSTANCE.getTotalBanner(filterBanner);

                ListBanner listBanners = new ListBanner();
                listBanners.setTotal(totalBanner);
                listBanners.setListBanners(sliceBanner);
                listBanners.setItemPerPage(10);

                if (sliceBanner.size() >= 0) {
                    result.setErrorCode(0);
                    result.setMessage("Success");
                    result.setData(listBanners);
                } else {
                    result.setErrorCode(-1);
                    result.setMessage("Fail");
                }
                break;
            }
            case "getBannerById": {
                int idBanner = NumberUtils.toInt(request.getParameter("id_banner"));
                Banner bannerByID = BannerModel.INSTANCE.getBannerByID(idBanner);

                if (bannerByID.getId() > 0) {
                    result.setErrorCode(0);
                    result.setMessage("Success");
                    result.setData(bannerByID);
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
                        Banner banner = new Banner();
                        ServletFileUpload upload = new ServletFileUpload(new DiskFileItemFactory());
                        upload.setHeaderEncoding("UTF-8");
                        List<FileItem> items = upload.parseRequest(request);
                        for (FileItem item : items) {
                            if (item.isFormField()) {
                                // Process regular form field (input type="text|radio|checkbox|etc", select, etc).
                                String fieldname = item.getFieldName();
                                String fieldvalue = item.getString("UTF-8");

                                switch (fieldname) {
                                    case "banner_name": {
                                        banner.setBannerName(fieldvalue);
                                        break;
                                    }
                                    case "action": {
                                        banner.setAction(fieldvalue);
                                        break;
                                    }
                                    case "position": {
                                        banner.setPosition(NumberUtils.toInt(fieldvalue));
                                        break;
                                    }
                                    case "status": {
                                        banner.setStatus(NumberUtils.toInt(fieldvalue));
                                        break;
                                    }

                                }

                            } else {
                                // Process form file field (input type="file").
                                String filename = FilenameUtils.getName(item.getName());
                                InputStream a = item.getInputStream();
                                Path uploadDir = Paths.get("upload/banner/" + filename);
                                Files.copy(a, uploadDir, StandardCopyOption.REPLACE_EXISTING);
                                banner.setImage("upload/banner/" + filename);
                            }
                        }
                        boolean existBannerName = BannerModel.INSTANCE.isExistBannerName(0, banner.getBannerName());
                        if (existBannerName == true) {
                            result.setErrorCode(-4);
                            result.setMessage("Banner Name đã tồn tại");
                        } else {
                            int addBanner = BannerModel.INSTANCE.addBanner(banner);
                            if (addBanner >= 0) {
                                result.setErrorCode(0);
                                result.setMessage("Thêm banner thành công!");
                            } else {
                                result.setErrorCode(-1);
                                result.setMessage("Thêm banner thất bại!");
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
//                        Film film = new Film();
//                        String oldImagePoster = "";
//                        String newImagePoster = "";
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
//                                        film.setId(NumberUtils.toInt(fieldvalue));
//                                        break;
//                                    }
//                                    case "category": {
//                                        film.setIdCate(NumberUtils.toInt(fieldvalue));
//                                        break;
//                                    }
//                                    case "title": {
//                                        film.setTitle(fieldvalue);
//                                        break;
//                                    }
//                                    case "content": {
//                                        film.setContent(fieldvalue);
//                                        break;
//                                    }
//                                    case "duration": {
//                                        film.setDuration(fieldvalue);
//                                        break;
//                                    }
//                                    case "opening_day": {
//                                        film.setOpenDay(fieldvalue);
//                                        break;
//                                    }
//                                    case "trailer": {
//                                        film.setTrailer(fieldvalue);
//                                        break;
//                                    }
//                                    case "status": {
//                                        film.setStatus(NumberUtils.toInt(fieldvalue));
//                                        break;
//                                    }
//                                    case "property": {
//                                        film.setProperty(NumberUtils.toInt(fieldvalue));
//                                        break;
//                                    }
//                                    case "poster": {
//                                        oldImagePoster = fieldvalue;
//                                        break;
//                                    }
//
//                                }
//
//                            } else {
//                                // Process form file field (input type="file").
//                                String filename = FilenameUtils.getName(item.getName());
//                                InputStream a = item.getInputStream();
//                                Path uploadDir = Paths.get("upload/poster_film/" + filename);
//                                Files.copy(a, uploadDir, StandardCopyOption.REPLACE_EXISTING);
//                                newImagePoster = "upload/poster_film/" + filename;
//                            }
//                        }
//
//                        if (StringUtils.isNotEmpty(newImagePoster)) {
//                            film.setPoster(newImagePoster);
//                        } else {
//                            film.setPoster(oldImagePoster);
//                        }
//
//                        Film filmById = FilmModel.INSTANCE.getFilmByID(film.getId());
//                        if (filmById.getId() == 0) {
//                            result.setErrorCode(-1);
//                            result.setMessage("Thất bại");
//                            return;
//                        }
//
//                        int editFilm = FilmModel.INSTANCE.editFilm(film);
//
//                        if (editFilm >= 0) {
//                            result.setErrorCode(0);
//                            result.setMessage("Sua film thanh cong!");
//                        } else {
//                            result.setErrorCode(-1);
//                            result.setMessage("Sua film that bai");
//                        }
//                    } else {
//                        result.setErrorCode(-4);
//                        result.setMessage("Có lỗi");
//                    }
//
////
//                } catch (Exception e) {
//                    System.out.println(e.getMessage());
//                }
//                break;
//            }
//            case "delete": {
//                int id = NumberUtils.toInt(request.getParameter("id"));
//                int deleteFilm = FilmModel.INSTANCE.deleteFilm(id);
//                if (deleteFilm >= 0) {
//                    result.setErrorCode(0);
//                    result.setMessage("Xóa Film thành công1");
//                } else {
//                    result.setErrorCode(-2);
//                    result.setMessage("Xóa Film thất bại!");
//                }
//                break;
//            }
            default:
                throw new AssertionError();
        }

        ServletUtil.printJson(request, response, gson.toJson(result));
    }
}
