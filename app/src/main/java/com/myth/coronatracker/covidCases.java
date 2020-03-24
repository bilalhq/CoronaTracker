package com.myth.coronatracker;

public class covidCases {
    private String newCases;
    private int active;
    private int critical;
    private int recovered;
    private int total;

    covidCases(String newCases , int active, int critical, int recovered, int total){
        newCases = this.newCases;
        active = this.active;
        critical = this.critical;
        recovered = this.recovered;
        total = this.total;
    }

    public int getRecovered() {
        return recovered;
    }

    public int getActive() {
        return active;
    }

    public int getCritical() {
        return critical;
    }

    public String getNewCases() {
        return newCases;
    }

    public int getTotal() {
        return total;
    }
}
