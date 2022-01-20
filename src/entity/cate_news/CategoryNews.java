package entity.cate_news;

import entity.category.Category;

public class CategoryNews {

    private int id;
    private String categoryNameNews;
    private String categoryNameNewsSlug;
    private int status;
    private int orders;
    private Property property;
    private String createdDate;
    private String updatedDate;

    public CategoryNews() {
    }

    public class Property {

        private boolean cateHot;
        private boolean cateNew;

        public Property(int numberProperty) {
            if ((numberProperty & 1) > 0) {
                cateHot = true;
            }
            if ((numberProperty & 2) > 0) {
                cateNew = true;
            }

        }

        public Property(boolean cateHot, boolean cateNew) {
            this.cateHot = cateHot;
            this.cateNew = cateNew;
        }

        public int getValue() {
            int property = 0;
            if (this.cateHot) {
                property = property + 1;
            }
            if (this.cateNew) {
                property = property + 2;
            }
            return property;
        }

        public boolean isCateHot() {
            return cateHot;
        }

        public void setCateHot(boolean cateHot) {
            this.cateHot = cateHot;
        }

        public boolean isCateNew() {
            return cateNew;
        }

        public void setCateNew(boolean cateNew) {
            this.cateNew = cateNew;
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCategoryNameNews() {
        return categoryNameNews;
    }

    public void setCategoryNameNews(String categoryNameNews) {
        this.categoryNameNews = categoryNameNews;
    }

    public String getCategoryNameNewsSlug() {
        return categoryNameNewsSlug;
    }

    public void setCategoryNameNewsSlug(String categoryNameNewsSlug) {
        this.categoryNameNewsSlug = categoryNameNewsSlug;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getOrders() {
        return orders;
    }

    public void setOrders(int orders) {
        this.orders = orders;
    }

    public Property getProperty() {
        return property;
    }

    public void setProperty(Property property) {
        this.property = property;
    }

    public void setProperty(int numberProperty) {
        this.property = new Property(numberProperty);
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(String updatedDate) {
        this.updatedDate = updatedDate;
    }


}
