package me.whiteship.refactoring._07_divergent_change._24_split_phase;

public class PriceData {

    double basePrice;
    double discount;
    int quantity;

    public PriceData(double basePrice, double discount, int quantity) {
        this.basePrice = basePrice;
        this.discount = discount;
        this.quantity = quantity;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public double getDiscount() {
        return discount;
    }

    public int getQuantity() {
        return quantity;
    }
}
