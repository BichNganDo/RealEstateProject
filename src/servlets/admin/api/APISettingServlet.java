package servlets.admin.api;

import com.google.gson.Gson;
import common.APIResult;
import entity.category.Category;
import entity.category.FilterCategory;
import entity.category.ListCategory;
import entity.setting.FilterSetting;
import entity.setting.ListSetting;
import entity.setting.Setting;
import helper.ServletUtil;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.CategoryModel;
import model.SettingModel;
import org.apache.commons.lang3.math.NumberUtils;

public class APISettingServlet extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        APIResult result = new APIResult(0, "Success");

        String action = request.getParameter("action");
        switch (action) {
            case "getsetting": {
                int pageIndex = NumberUtils.toInt(request.getParameter("page_index"));
                int limit = NumberUtils.toInt(request.getParameter("limit"), 10);
                String searchQuery = request.getParameter("search_query");
                int searchStatus = NumberUtils.toInt(request.getParameter("search_status"));
                int offset = (pageIndex - 1) * limit;

                FilterSetting filterSetting = new FilterSetting();
                filterSetting.setLimit(limit);
                filterSetting.setOffset(offset);
                filterSetting.setSearchQuery(searchQuery);
                filterSetting.setSearchStatus(searchStatus);

                List<Setting> sliceSetting = SettingModel.INSTANCE.getSliceSetting(filterSetting);
                int totalSetting = SettingModel.INSTANCE.getTotalSetting(filterSetting);

                ListSetting listSetting = new ListSetting();
                listSetting.setTotal(totalSetting);
                listSetting.setListSettings(sliceSetting);
                listSetting.setItemPerPage(10);

                if (sliceSetting.size() > 0) {
                    result.setErrorCode(0);
                    result.setMessage("Success");
                    result.setData(listSetting);
                } else {
                    result.setErrorCode(-1);
                    result.setMessage("Fail");
                }
                break;
            }
            case "getSettingById": {
                int idSetting = NumberUtils.toInt(request.getParameter("id_setting"));
                Setting settingByID = SettingModel.INSTANCE.getSettingByID(idSetting);

                if (settingByID.getId() > 0) {
                    result.setErrorCode(0);
                    result.setMessage("Success");
                    result.setData(settingByID);
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
                String key = request.getParameter("key");
                String value = request.getParameter("value");
                int status = NumberUtils.toInt(request.getParameter("status"));
                String group = request.getParameter("group");

                Setting setting = new Setting();
                setting.setKey(key);
                setting.setValue(value);
                setting.setStatus(status);
                setting.setGroup(group);

                boolean existKey = SettingModel.INSTANCE.isExistKey(status, key);
                if (existKey == true) {
                    result.setErrorCode(-4);
                    result.setMessage("Key đã tồn tại");
                } else {
                    int addSetting = SettingModel.INSTANCE.addSetting(setting);
                    if (addSetting >= 0) {
                        result.setErrorCode(0);
                        result.setMessage("Thêm setting thành công!");
                    } else {
                        result.setErrorCode(-1);
                        result.setMessage("Thêm setting thất bại!");
                    }
                }
                break;
            }
            case "edit": {
                int id = NumberUtils.toInt(request.getParameter("id"));
                String key = request.getParameter("key");
                String value = request.getParameter("value");
                int status = NumberUtils.toInt(request.getParameter("status"));
                String group = request.getParameter("group");

                Setting setting = new Setting();
                setting.setId(id);
                setting.setKey(key);
                setting.setValue(value);
                setting.setGroup(group);
                setting.setStatus(status);

                Setting settingByID = SettingModel.INSTANCE.getSettingByID(id);
                if (settingByID.getId() == 0) {
                    result.setErrorCode(-1);
                    result.setMessage("Thất bại!");
                    return;
                }

                boolean existKey = SettingModel.INSTANCE.isExistKey(id, key);
                if (existKey == true) {
                    result.setErrorCode(-4);
                    result.setMessage("Key đã tồn tại");
                } else {
                    int editSetting = SettingModel.INSTANCE.editSetting(setting);
                    if (editSetting >= 0) {
                        result.setErrorCode(0);
                        result.setMessage("Sửa setting thành công!");
                    } else {
                        result.setErrorCode(-1);
                        result.setMessage("Sửa setting thất bại!");
                    }
                }

                break;
            }

            case "delete": {
                int id = NumberUtils.toInt(request.getParameter("id"));
                int deleteSetting = SettingModel.INSTANCE.deleteSetting(id);
                if (deleteSetting >= 0) {
                    result.setErrorCode(0);
                    result.setMessage("Xóa setting thành công!");
                } else {
                    result.setErrorCode(-2);
                    result.setMessage("Xóa setting thất bại!");
                }
                break;
            }
            default:
                throw new AssertionError();
        }

        ServletUtil.printJson(request, response, gson.toJson(result));

    }
}
