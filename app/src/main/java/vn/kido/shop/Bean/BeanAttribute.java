package vn.kido.shop.Bean;

import com.google.gson.annotations.SerializedName;

public class BeanAttribute {

    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("value")
    private int value;

    @SerializedName("status")
    private int status;

    @SerializedName("money")
    private int money;

    @SerializedName("quantity")
    private int quantity;

    @SerializedName("name_per_unit")
    private String namePerUnit;

    @SerializedName("quantity_per_unit")
    private String quantityUnit;

    public String getNamePerUnit() {
        return namePerUnit;
    }

    public void setNamePerUnit(String namePerUnit) {
        this.namePerUnit = namePerUnit;
    }

    private String maxUnit;

    public String getMaxUnit() {
        return maxUnit;
    }

    public void setMaxUnit(String maxUnit) {
        this.maxUnit = maxUnit;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getQuantityUnit() {
        return quantityUnit;
    }

    public void setQuantityUnit(String quantityUnit) {
        this.quantityUnit = quantityUnit;
    }
}
