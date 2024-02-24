package vn.kido.shop.Bean;

import java.util.List;

public class BeanMenu extends BeanBase {
    public static final String EVENT_PROGRAM = "EVENT_PROGRAM";
    public static final String HISTORY_ORDER = "HISTORY_ORDER";
    public static final String FOLLOW_ORDER = "FOLLOW_ORDER";
    public static final String NEWS = "NEWS";
    public static final String FEEDBACK = "FEEDBACK";
    public static final String CHANGE_PAY = "CHANGE_PAY";
    public static final String SETTING = "SETTING";

    private int icon;
    private boolean isShowNumber;
    private String title;
    private String id;

    public BeanMenu(String id, String title, int icon) {
        this.id = id;
        this.title = title;
        this.icon = icon;
    }

    public BeanMenu(String id, String title) {
        this.id = id;
        this.title = title;
    }

    public static BeanMenu getById(String id, List<BeanMenu> list) {
        for (BeanMenu menu : list) {
            if (menu.getId().equals(id)) {
                return menu;
            }
        }
        return null;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public boolean isShowNumber() {
        return isShowNumber;
    }

    public void setShowNumber(boolean showNumber) {
        isShowNumber = showNumber;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
