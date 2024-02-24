package vn.kido.shop.Bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BeanProduct extends BeanBase {

    public static final int PRODUCT = 1;
    public static final int COMBO = 2;
    public static final int GIFT = 3;
    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String name;
    @SerializedName("price")
    private long price;
    @SerializedName("old_price")
    private long oldPrice;
    @SerializedName("image")
    private String image;
    @SerializedName("point")
    private int point;
    @SerializedName("attribute")
    private List<BeanAttribute> attributes;

    @SerializedName("attribute_received")
    private List<BeanAttribute> attributes_received;

    @SerializedName("type")
    private int type;

    @SerializedName("quantity")
    private int quantity;

    @SerializedName("event_id")
    private int eventId;

    private boolean inCart;

    @SerializedName("products")
    private List<BeanProduct> products;

    @SerializedName("gifts")
    private List<BeanProduct> gifts;

    @SerializedName("min_unit")
    private String minUnit;

    @SerializedName("max_unit")
    private String maxUnit;

    @SerializedName("kind")
    private String kind;

    @SerializedName("total_price")
    private int totalPrice;

    @SerializedName("total_product_price")
    private int totalProductPrice;

    @SerializedName("discount")
    private double discount;

    @SerializedName("images")
    private List<String> images;

    @SerializedName("description")
    private String description;

    @SerializedName("element")
    private String element;

    @SerializedName("nutrition")
    private String nutrition;

    @SerializedName("nutrition_detail")
    private List<BeanNutritions> nutritions;

    @SerializedName("manual")
    private String manual;

    @SerializedName("uses")
    private String uses;

    @SerializedName("storage_condition")
    private String storageCondition;

    @SerializedName("number_gift_name")
    private String numberGiftName;

    @SerializedName("price_max_unit")
    private int priceMaxUnit;

    @SerializedName("total_percent_discount")
    private double totalPercentDiscount;

    @SerializedName("event_reward_id")
    private int eventRewardId;

    @SerializedName("max_value")
    private int maxValue;


    @SerializedName("create_date")
    private long createDate;

    @SerializedName("reason")
    private String reason;

    @SerializedName("expire_date")
    private long expireDate;

    public long getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(long expireDate) {
        this.expireDate = expireDate;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public int getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }

    public double getTotalPercentDiscount() {
        return totalPercentDiscount;
    }

    public void setTotalPercentDiscount(double totalPercentDiscount) {
        this.totalPercentDiscount = totalPercentDiscount;
    }

    public int getPriceMaxUnit() {
        return priceMaxUnit;
    }

    public void setPriceMaxUnit(int priceMaxUnit) {
        this.priceMaxUnit = priceMaxUnit;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getElement() {
        return element;
    }

    public void setElement(String element) {
        this.element = element;
    }

    public String getNutrition() {
        return nutrition;
    }

    public void setNutrition(String nutrition) {
        this.nutrition = nutrition;
    }

    public String getManual() {
        return manual;
    }

    public void setManual(String manual) {
        this.manual = manual;
    }

    public String getStorageCondition() {
        return storageCondition;
    }

    public void setStorageCondition(String storageCondition) {
        this.storageCondition = storageCondition;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public int getTotalProductPrice() {
        return totalProductPrice;
    }

    public void setTotalProductPrice(int totalProductPrice) {
        this.totalProductPrice = totalProductPrice;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getMinUnit() {
        return minUnit;
    }

    public void setMinUnit(String minUnit) {
        this.minUnit = minUnit;
    }

    public String getMaxUnit() {
        return maxUnit;
    }

    public void setMaxUnit(String maxUnit) {
        this.maxUnit = maxUnit;
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

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public long getOldPrice() {
        return oldPrice;
    }

    public void setOldPrice(long oldPrice) {
        this.oldPrice = oldPrice;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }


    public List<BeanProduct> getProducts() {
        return products;
    }

    public void setProducts(List<BeanProduct> products) {
        this.products = products;
    }

    public List<BeanProduct> getGifts() {
        return gifts;
    }

    public void setGifts(List<BeanProduct> gifts) {
        this.gifts = gifts;
    }

    public boolean isInCart() {
        return inCart;
    }

    public void setInCart(boolean inCart) {
        this.inCart = inCart;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public List<BeanAttribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<BeanAttribute> attributes) {
        this.attributes = attributes;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }

    public List<BeanAttribute> getAttributes_received() {
        return attributes_received;
    }

    public void setAttributes_received(List<BeanAttribute> attributes_received) {
        this.attributes_received = attributes_received;
    }

    public String getNumberGiftName() {
        return numberGiftName;
    }

    public void setNumberGiftName(String numberGiftName) {
        this.numberGiftName = numberGiftName;
    }

    public String getUses() {
        return uses;
    }

    public void setUses(String uses) {
        this.uses = uses;
    }

    public List<BeanNutritions> getNutritions() {
        return nutritions;
    }

    public void setNutritions(List<BeanNutritions> nutritions) {
        this.nutritions = nutritions;
    }

    public long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }


    public class BeanNutritions{
        @SerializedName("left")
        private String left;

        @SerializedName("right")
        private String right;

        public String getLeft() {
            return left;
        }

        public void setLeft(String left) {
            this.left = left;
        }

        public String getRight() {
            return right;
        }

        public void setRight(String right) {
            this.right = right;
        }
    }
}
