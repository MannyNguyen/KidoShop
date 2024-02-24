package vn.kido.shop.Bean;

import com.google.gson.annotations.SerializedName;

public class BeanProgram {
    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String title;
    @SerializedName("image")
    private String image;
    @SerializedName("description")
    private String description;
    @SerializedName("start_date")
    private long startDate;
    @SerializedName("end_date")
    private long endDate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }
}
