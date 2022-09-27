package me.whiteship.refactoring._06_mutable_data._22_combine_functions_into_transform;

public class EnrichReading {

    Reading reading;
    double baseCharge;
    double taxableCharge;

    public Reading getReading() {
        return reading;
    }

    public double getBaseCharge() {
        return baseCharge;
    }

    public double getTaxableCharge() {
        return taxableCharge;
    }

    public EnrichReading(Reading reading, double baseCharge, double taxableCharge) {
        this.reading = reading;
        this.baseCharge = baseCharge;
        this.taxableCharge = taxableCharge;
    }
}
