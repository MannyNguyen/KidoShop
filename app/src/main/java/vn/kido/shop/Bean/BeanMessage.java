package vn.kido.shop.Bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BeanMessage extends BeanBase {
    public static final int TEXT = 0;
    public static final int DEFAULT = 1;
    public static final int IMAGE = 2;
    public static final int TIME = 3;
    public static final int ISME = 1;
    public static final int NOTME = 0;
    @SerializedName("message")
    private String message;
    @SerializedName("type")
    private int type;
    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String name;
    @SerializedName("timestamp")
    private long timestamp;

    @SerializedName("child")
    private List<BeanChildMessage> child;

    @SerializedName("isUser")
    private int isMe;


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
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

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public List<BeanChildMessage> getChild() {
        return child;
    }

    public void setChild(List<BeanChildMessage> child) {
        this.child = child;
    }

    public int isMe() {
        return isMe;
    }

    public void setMe(int me) {
        isMe = me;
    }


    public class BeanChildMessage{
        @SerializedName("id")
        private int id;
        @SerializedName("name")
        private String name;

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
    }
}
