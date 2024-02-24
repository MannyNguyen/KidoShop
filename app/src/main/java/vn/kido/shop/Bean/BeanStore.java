package vn.kido.shop.Bean;

import com.google.gson.annotations.SerializedName;

public class BeanStore extends BeanBase {
    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String name;

    @SerializedName("image")
    private String image;

    @SerializedName("parent")
    private int parent;

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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getParent() {
        return parent;
    }

    public void setParent(int parent) {
        this.parent = parent;
    }
}
