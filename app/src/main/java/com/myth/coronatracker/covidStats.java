package com.myth.coronatracker;

public class covidStats {
    private String province;
    private String country;
    private String lastUpdate;
    private int confirmed;
    private int deaths;
    private int recovered;

    covidStats(String province, String country, String lastUpdate, int confirmed, int deaths, int recovered){
        province = this.province;
        country = this.country;
        lastUpdate = this.lastUpdate;
        confirmed = this.confirmed;
        deaths = this.deaths;
        recovered = this.recovered;
    }

    public int getDeaths() {
        return deaths;
    }

    public int getConfirmed() {
        return confirmed;
    }

    public int getRecovered() {
        return recovered;
    }

    public String getLastUpdate() {
        lastUpdate = lastUpdate.replace('T',' ');
        lastUpdate = "Updated on: " + lastUpdate;
        return lastUpdate;
    }
}
