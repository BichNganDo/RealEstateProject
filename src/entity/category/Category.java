package entity.category;

public class Category {

    private int id;
    private String cateName;
    private String cateNameSlug;
    private int parentId;
    private String parentName;
    private int orders;
    private int status;
    private String createdDate;
    private String updatedDate;
    private Property property;

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

    public Category() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCateName() {
        return cateName;
    }

    public void setCateName(String cateName) {
        this.cateName = cateName;
    }

    public String getCateNameSlug() {
        return cateNameSlug;
    }

    public void setCateNameSlug(String cateNameSlug) {
        this.cateNameSlug = cateNameSlug;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
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

    public Property getProperty() {
        return property;
    }

    public void setProperty(Property property) {
        this.property = property;
    }

    public void setProperty(int numberProperty) {
        this.property = new Property(numberProperty);
    }

}
