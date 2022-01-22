package entity.news;

import common.Config;
import entity.category.Category;

public class News {

    private int id;
    private int idCategoryNews;
    private String categoryNewsName;
    private String title;
    private String description;
    private String content;
    private String image;
    private int orders;
    private int status;
    private Property property;
    private String createdDate;
    private String updatedDate;

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

    public News() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdCategoryNews() {
        return idCategoryNews;
    }

    public void setIdCategoryNews(int idCategoryNews) {
        this.idCategoryNews = idCategoryNews;
    }

    public String getCategoryNewsName() {
        return categoryNewsName;
    }

    public void setCategoryNewsName(String categoryNewsName) {
        this.categoryNewsName = categoryNewsName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setImageUrlWithBaseDomain(String image) { // set de hien thi
        if (image.startsWith("http")) {
            this.image = image;
        } else {
            this.image = Config.APP_DOMAIN + "/" + image;
        }
    }

    public int getOrders() {
        return orders;
    }

    public void setOrders(int orders) {
        this.orders = orders;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
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
