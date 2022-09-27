package me.whiteship.refactoring._06_mutable_data._22_combine_functions_into_transform;

import java.time.Month;
import java.time.Year;

public class Reading {
    String customer;
    double quantity;
    Month month;
    Year year;

    public String getCustomer() {
        return customer;
    }

    public double getQuantity() {
        return quantity;
    }

    public Month getMonth() {
        return month;
    }

    public Year getYear() {
        return year;
    }

    public Reading(String customer, double quantity, Month month, Year year) {
        this.customer = customer;
        this.quantity = quantity;
        this.month = month;
        this.year = year;
    }
}
