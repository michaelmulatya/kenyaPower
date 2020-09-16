package com.bluefox.kenyapowerbill;

public class Data {
    private String charge;
    private String amount;

    public Data(String charge, String amount) {
        this.charge = charge;
        this.amount = amount;
    }

    public String getCharge() {
        return charge;
    }

    public void setCharge(String charge) {
        this.charge = charge;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
