package entity.setting;

public class FilterSetting {

    private int offset;
    private int limit;
    private String searchQuery;
    private int searchStatus;

    public FilterSetting() {
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

    public int getSearchStatus() {
        return searchStatus;
    }

    public void setSearchStatus(int searchStatus) {
        this.searchStatus = searchStatus;
    }


}
