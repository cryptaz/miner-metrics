package org.cryptaz.minermetrics;

import org.cryptaz.minermetrics.models.CardTickData;
import org.cryptaz.minermetrics.models.GeneralTickData;
import org.cryptaz.minermetrics.models.dto.ClaymoreTickDTO;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;


public class InfluxWriter {

    private static Logger log = LoggerFactory.getLogger(InfluxWriter.class);

    private final String INFLUXDB_HOST = "http://127.0.0.1:8086";
    private final String INFLUXDB_PASS = "root";
    private final String INFLUXDB_USER = "root";
    private final String INFLUXDB_DB = "miner";

    private InfluxDB influxDB;

    public InfluxWriter() {
        this.influxDB = InfluxDBFactory.connect(INFLUXDB_HOST, INFLUXDB_USER, INFLUXDB_PASS);
    }

    public boolean writeClaymoreTick(ClaymoreTickDTO tickDTO) {

        //Preparing
        BatchPoints batchPoints =
                BatchPoints
                        .database(INFLUXDB_DB)
                        .consistency(InfluxDB.ConsistencyLevel.ALL)
                        .build();


        //General tick
        GeneralTickData generalTickData = tickDTO.getGeneralTickData();
        Point generalPoint = Point
                .measurement("general")
                .time(tickDTO.getDateTime().getMillis(), TimeUnit.MILLISECONDS)
                .addField("version", generalTickData.getVersion())
                .addField("uptime", generalTickData.getUptime().getMinutes())
                .addField("accepted_shares", generalTickData.getAcceptedShares())
                .addField("rejected_shares", generalTickData.getRejectedShares())
                .addField("pool_url", generalTickData.getPoolUrl())
                .addField("overall_hashrate", generalTickData.getOverallHashrate())
                .build();

        for (CardTickData cardTickData : tickDTO.getCardTickDatas()) {
            Point point = Point.measurement("card")
                    .time(tickDTO.getDateTime().getMillis(), TimeUnit.MILLISECONDS)
                    .tag("id", String.valueOf(cardTickData.getId()))
                    .addField("hashrate", cardTickData.getHashrate())
                    .addField("temp", cardTickData.getTemperature())
                    .addField("fan", cardTickData.getFan())
                    .build();
            batchPoints.point(point);
        }
        batchPoints.point(generalPoint);

        try {
            influxDB.write(batchPoints);
        } catch (Exception e) {
            log.info("Could not write to InfluxDB. Possibly database is not accessible!");
            if (log.isDebugEnabled()) {
                e.printStackTrace();
            }
            return false;
        }
        return true;
    }
}
