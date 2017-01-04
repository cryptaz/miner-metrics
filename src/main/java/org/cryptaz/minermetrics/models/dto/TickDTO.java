package org.cryptaz.minermetrics.models.dto;

import org.cryptaz.minermetrics.models.CardTickData;
import org.cryptaz.minermetrics.models.GeneralTickData;
import org.joda.time.DateTime;

import java.util.List;

//Abstract class representing tick info. It may contains time, card sensor data, other information(pool url, for example)
public abstract class TickDTO {
    private DateTime dateTime;
    private List<CardTickData> cardTickDatas;
    private GeneralTickData generalTickData;

    public DateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(DateTime dateTime) {
        this.dateTime = dateTime;
    }

    public List<CardTickData> getCardTickDatas() {
        return cardTickDatas;
    }

    public void setCardTickDatas(List<CardTickData> cardTickDatas) {
        this.cardTickDatas = cardTickDatas;
    }

    public GeneralTickData getGeneralTickData() {
        return generalTickData;
    }

    public void setGeneralTickData(GeneralTickData generalTickData) {
        this.generalTickData = generalTickData;
    }
}
