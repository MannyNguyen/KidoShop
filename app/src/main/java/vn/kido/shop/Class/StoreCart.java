package vn.kido.shop.Class;

import java.util.ArrayList;
import java.util.List;

public class StoreCart {
    private static List<Integer> products;
    private static List<Integer> combos;

    public static List<Integer> getProducts() {
        if (products == null) {
            products = new ArrayList<>();
        }
        return products;
    }

    public static void setProducts(List<Integer> products) {
        StoreCart.products = products;
    }

    public static List<Integer> getCombos() {
        if (combos == null) {
            combos = new ArrayList<>();
        }
        return combos;
    }

    public static void setCombos(List<Integer> combos) {
        StoreCart.combos = combos;
    }


}
