package vn.kido.shop.Bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BeanClaim extends BeanBase {
    @SerializedName("event_reward_id")
    private int id;


    @SerializedName("title")
    private String title;

    @SerializedName("can_claim")
    private boolean canClaim;

    private String claims;
    private boolean isActive;

    public BeanClaim() {
    }

    public BeanClaim(int id, boolean isClaim, String claims) {

        this.setId(id);
        this.canClaim = isClaim;
        this.claims = claims;
    }

    public static BeanClaim getActive(List<BeanClaim> claims) {
        for (BeanClaim beanClaim : claims) {
            if (beanClaim.isActive) {
                return beanClaim;
            }
        }
        return null;
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

    public boolean isCanClaim() {
        return canClaim;
    }

    public void setCanClaim(boolean canClaim) {
        this.canClaim = canClaim;
    }

    public String getClaims() {
        return claims;
    }

    public void setClaims(String claims) {
        this.claims = claims;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
