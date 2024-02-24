package vn.kido.shop.Bean;

public class BeanRetail extends BeanProduct {
    private boolean isSelected;
    private String note;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getNote() {
        if (note == null) {
            note = "";
        }
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
