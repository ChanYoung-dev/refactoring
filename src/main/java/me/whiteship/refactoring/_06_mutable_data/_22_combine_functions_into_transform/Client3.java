package me.whiteship.refactoring._06_mutable_data._22_combine_functions_into_transform;

public class Client3 extends ReadingClient{

    private double basicChargeAmount;

    public Client3(Reading reading) {
//        this.basicChargeAmount = calculateBaseCharge(reading);
        this.basicChargeAmount = enrichReading(reading).getBaseCharge();
    }

    private double calculateBaseCharge(Reading reading) {
        return baseRate(reading.getMonth(), reading.getYear()) * reading.getQuantity();
    }

    // Client1, Client2, Client3 공통된 기능은 ReadingClient(부모클래스)로 만들어서 구현
//    private double baseRate(Month month, Year year) {
//        return 10;
//    }

    public double getBasicChargeAmount() {
        return basicChargeAmount;
    }
}
