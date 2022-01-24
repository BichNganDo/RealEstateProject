package entity.setting;

import java.util.List;

public class ListSetting {

    private int total;
    private List<Setting> listSettings;
    private int itemPerPage;

    public ListSetting() {
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<Setting> getListSettings() {
        return listSettings;
    }

    public void setListSettings(List<Setting> listSettings) {
        this.listSettings = listSettings;
    }

    public int getItemPerPage() {
        return itemPerPage;
    }

    public void setItemPerPage(int itemPerPage) {
        this.itemPerPage = itemPerPage;
    }

}
