package vn.kido.shop.Bean;

import com.google.gson.annotations.SerializedName;

public class BeanNews extends BeanBase {
    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String title;
    @SerializedName("image")
    private String image;
    @SerializedName("rate")
    private int rate;
    @SerializedName("news_link")
    private String news_link;
    @SerializedName("create_date")
    private long create_date;
    @SerializedName("is_saved")
    private int isSaved;

    public int getIsSaved() {
        return isSaved;
    }

    public void setIsSaved(int isSaved) {
        this.isSaved = isSaved;
    }

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

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public String getNews_link() {
        return news_link;
    }

    public void setNews_link(String news_link) {
        this.news_link = news_link;
    }

    public long getCreate_date() {
        return create_date;
    }

    public void setCreate_date(long create_date) {
        this.create_date = create_date;
    }
}
