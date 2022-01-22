package entity.news;

public class FilterNews {

    private int limit;
    private int offset;
    private String searchQuery;
    private int searchCategoryNews;
    private int searchProperty;
    private int status;

    public FilterNews() {
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public String getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    public int getSearchCategoryNews() {
        return searchCategoryNews;
    }

    public void setSearchCategoryNews(int searchCategoryNews) {
        this.searchCategoryNews = searchCategoryNews;
    }

    public int getSearchProperty() {
        return searchProperty;
    }

    public void setSearchProperty(int searchProperty) {
        this.searchProperty = searchProperty;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

}
