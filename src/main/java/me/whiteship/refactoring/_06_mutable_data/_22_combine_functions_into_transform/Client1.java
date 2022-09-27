package me.whiteship.refactoring._06_mutable_data._22_combine_functions_into_transform;

public class Client1 extends ReadingClient{

    double baseCharge;

    public Client1(Reading reading) {
//        this.baseCharge = baseRate(reading.getMonth(), reading.getYear()) * reading.getQuantity();
        this.baseCharge = enrichReading(reading).getBaseCharge();
    }

    // Client1, Client2, Client3 공통된 기능은 ReadingClient(부모클래스)로 만들어서 구현
//    private double baseRate(Month month, Year year) {
//        return 10;
//    }

    public double getBaseCharge() {
        return baseCharge;
    }
}
