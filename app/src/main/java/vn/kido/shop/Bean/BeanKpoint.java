package vn.kido.shop.Bean;

import com.google.gson.annotations.SerializedName;

public class BeanKpoint {
    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String name;
    @SerializedName("attribute")
    private String attribute;
    @SerializedName("price")
    private int price;
    @SerializedName("image")
    private String image;
    @SerializedName("require")
    private int require;
    @SerializedName("is_receive")
    private int isReceive;
    @SerializedName("description")
    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getIsReceive() {
        return isReceive;
    }

    public void setIsReceive(int isReceive) {
        this.isReceive = isReceive;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getRequire() {
        return require;
    }

    public void setRequire(int require) {
        this.require = require;
    }
}
