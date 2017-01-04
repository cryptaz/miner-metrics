package org.cryptaz.minermetrics.models;

public class CardTickData extends TickData{
    //card index (as it appears in claymore)
    private int id;
    private int hashrate;
    private int temperature;
    private int fan;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getHashrate() {
        return hashrate;
    }

    public void setHashrate(int hashrate) {
        this.hashrate = hashrate;
    }

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    public int getFan() {
        return fan;
    }

    public void setFan(int fan) {
        this.fan = fan;
    }
}
