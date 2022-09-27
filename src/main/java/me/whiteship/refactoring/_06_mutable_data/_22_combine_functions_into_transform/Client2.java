package me.whiteship.refactoring._06_mutable_data._22_combine_functions_into_transform;

public class Client2 extends ReadingClient{

    private double base;
    private double taxableCharge;

    public Client2(Reading reading) {
//        this.base = baseRate(reading.getMonth(), reading.getYear()) * reading.getQuantity();
//        this.taxableCharge = Math.max(0, this.base - taxThreshold(reading.getYear()));
        EnrichReading enrichReading = enrichReading(reading);
        this.base = enrichReading.getBaseCharge();
        this.taxableCharge = enrichReading.getTaxableCharge();
    }

    // Client1, Client2, Client3 공통된 기능은 ReadingClient(부모클래스)로 만들어서 구현
//    private double taxThreshold(Year year) {
//        return 5;
//    }
//
//    private double baseRate(Month month, Year year) {
//        return 10;
//    }

    public double getBase() {
        return base;
    }

    public double getTaxableCharge() {
        return taxableCharge;
    }
}
