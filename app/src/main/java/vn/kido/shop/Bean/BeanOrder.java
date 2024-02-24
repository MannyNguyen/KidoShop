package vn.kido.shop.Bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BeanOrder extends BeanBase {
    @SerializedName("id")
    private int id;

    @SerializedName("order_code")
    private String orderCode;


    @SerializedName("product_count")
    private int productCount;

    @SerializedName("gift_count")
    private int giftCount;

    @SerializedName("delivery_time")
    private long deliveryTime;

    @SerializedName("create_date")
    private long createDate;

    @SerializedName("order_child")
    private List<BeanOrder> orderChilds;

    @SerializedName("npp_name")
    private String nppName;

    @SerializedName("suggest_time")
    private long suggestTime;

    @SerializedName("tong_km_dong")
    private int totalSaleType;
    @SerializedName("tong_km_don_hang")
    private int totalSaleOrder;
    @SerializedName("total_point")
    private int totalPoint;
    @SerializedName("total_money")
    private int totalMoney;
    @SerializedName("total")
    private int total;

    @SerializedName("tong_thanh_toan")
    private int totalPay;

    @SerializedName("verify_code")
    private String verifyCode;

    public int getTotalPay() {
        return totalPay;
    }

    public void setTotalPay(int totalPay) {
        this.totalPay = totalPay;
    }

    public List<BeanItemOrder> getItems() {
        return items;
    }

    @SerializedName("items")
    private List<BeanItemOrder> items;

    @SerializedName("gifts")
    private List<BeanProduct> gifts;

    public String getCompanyName() {
        return companyName;
    }

    @SerializedName("company_name")
    private String companyName;

    @SerializedName("tong_thung")
    private int thung;

    @SerializedName("tong_le")
    private int le;

    @SerializedName("tong_loc")
    private int loc;

    @SerializedName("type")
    private int type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getSuggestTime() {
        return suggestTime;
    }

    public void setSuggestTime(long suggestTime) {
        this.suggestTime = suggestTime;
    }

    public String getNppName() {
        return nppName;
    }

    public void setNppName(String nppName) {
        this.nppName = nppName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public int getTotalSaleType() {
        return totalSaleType;
    }

    public int getTotalSaleOrder() {
        return totalSaleOrder;
    }

    public int getTotalPoint() {
        return totalPoint;
    }

    public int getTotalMoney() {
        return totalMoney;
    }

    public int getTotal() {
        return total;
    }

    public int getProductCount() {
        return productCount;
    }

    public void setProductCount(int productCount) {
        this.productCount = productCount;
    }

    public long getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(long deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }

    public List<BeanOrder> getOrderChilds() {
        return orderChilds;
    }

    public void setOrderChilds(List<BeanOrder> orderChilds) {
        this.orderChilds = orderChilds;
    }

    public void setTotalSaleType(int totalSaleType) {
        this.totalSaleType = totalSaleType;
    }

    public void setTotalSaleOrder(int totalSaleOrder) {
        this.totalSaleOrder = totalSaleOrder;
    }

    public void setTotalPoint(int totalPoint) {
        this.totalPoint = totalPoint;
    }

    public void setTotalMoney(int totalMoney) {
        this.totalMoney = totalMoney;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public void setItems(List<BeanItemOrder> items) {
        this.items = items;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public int getThung() {
        return thung;
    }

    public void setThung(int thung) {
        this.thung = thung;
    }

    public int getLe() {
        return le;
    }

    public void setLe(int le) {
        this.le = le;
    }

    public int getLoc() {
        return loc;
    }

    public void setLoc(int loc) {
        this.loc = loc;
    }

    public List<BeanProduct> getGifts() {
        return gifts;
    }

    public void setGifts(List<BeanProduct> gifts) {
        this.gifts = gifts;
    }

    public int getGiftCount() {
        return giftCount;
    }

    public void setGiftCount(int giftCount) {
        this.giftCount = giftCount;
    }

    public String getVerifyCode() {
        return verifyCode;
    }

    public void setVerifyCode(String verifyCode) {
        this.verifyCode = verifyCode;
    }
}
