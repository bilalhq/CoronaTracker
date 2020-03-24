package com.myth.coronatracker;

public class covidDeaths {
    String newDeaths;
    int total;

    covidDeaths(String newDeaths,int total){
        newDeaths = this.newDeaths;
        total = this.total;
    }

    public int getTotal() {
        return total;
    }

    public String getNewDeaths() {
        return newDeaths;
    }
}
