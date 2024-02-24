package vn.kido.shop.Bean;

import com.google.gson.annotations.SerializedName;

public class BeanUserLevel extends BeanBase{
    @SerializedName("level_name")
    private String levelName;
    @SerializedName("id_level")
    private int idLevel;
    @SerializedName("description")
    private String description;
    @SerializedName("value")
    private int value;

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getLevelName() {
        return levelName;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    public int getIdLevel() {
        return idLevel;
    }

    public void setIdLevel(int idLevel) {
        this.idLevel = idLevel;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
