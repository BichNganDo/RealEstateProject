package entity.category;

public class FilterCategory {

    private int offset;
    private int limit;
    private String searchQuery;
    private int searchProperty;
    private int searchCategory;
    private int status;

    public FilterCategory() {
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public String getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    public int getSearchProperty() {
        return searchProperty;
    }

    public void setSearchProperty(int searchProperty) {
        this.searchProperty = searchProperty;
    }

    public int getSearchCategory() {
        return searchCategory;
    }

    public void setSearchCategory(int searchCategory) {
        this.searchCategory = searchCategory;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

}
