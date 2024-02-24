package vn.kido.shop.Bean;

import com.google.gson.annotations.SerializedName;

public class BeanItemOrder {
    @SerializedName("name")
    private String name;

    @SerializedName("total_product_price")
    private int totalProductPrice;

    @SerializedName("total_money_discount")
    private int totalMoneyDiscount;


    @SerializedName("total_price")
    private int totalPrice;

    @SerializedName("total_percent_discount")
    private double totalPercentDiscount;

    @SerializedName("thung")
    private int thung;

    @SerializedName("le")
    private int le;

    @SerializedName("loc")
    private int loc;

    @SerializedName("price")
    private int price;

    @SerializedName("don_gia")
    private int unitPrice;



    public int getPrice() {
        return price;
    }

    public int getThung() {
        return thung;
    }

    public int getLe() {
        return le;
    }

    public int getLoc() {
        return loc;
    }


    public int getTotalMoneyDiscount() {
        return totalMoneyDiscount;
    }

    public String getName() {
        return name;
    }

    public int getTotalProductPrice() {
        return totalProductPrice;
    }


    public int getTotalPrice() {
        return totalPrice;
    }

    public double getTotalPercentDiscount() {
        return totalPercentDiscount;
    }


    public int getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(int unitPrice) {
        this.unitPrice = unitPrice;
    }
}
