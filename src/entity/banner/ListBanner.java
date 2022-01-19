package entity.banner;

import java.util.List;

public class ListBanner {

    private int total;
    private List<Banner> listBanners;
    private int itemPerPage;

    public ListBanner() {
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<Banner> getListBanners() {
        return listBanners;
    }

    public void setListBanners(List<Banner> listBanners) {
        this.listBanners = listBanners;
    }

    public int getItemPerPage() {
        return itemPerPage;
    }

    public void setItemPerPage(int itemPerPage) {
        this.itemPerPage = itemPerPage;
    }

}
