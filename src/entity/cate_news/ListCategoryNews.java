package entity.cate_news;

import java.util.List;

public class ListCategoryNews {

    private int total;
    private List<CategoryNews> listCategoryNews;
    private int itemPerPage;

    public ListCategoryNews() {
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<CategoryNews> getListCategoryNews() {
        return listCategoryNews;
    }

    public void setListCategoryNews(List<CategoryNews> listCategoryNews) {
        this.listCategoryNews = listCategoryNews;
    }

    public int getItemPerPage() {
        return itemPerPage;
    }

    public void setItemPerPage(int itemPerPage) {
        this.itemPerPage = itemPerPage;
    }

}
