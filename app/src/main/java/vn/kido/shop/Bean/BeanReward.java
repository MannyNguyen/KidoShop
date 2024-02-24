package vn.kido.shop.Bean;

public class BeanReward {
    private int id;
    private boolean isSuccess;

    public BeanReward(){

    }

    public BeanReward(int id, boolean isSuccess) {
        this.id = id;
        this.isSuccess = isSuccess;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }
}
