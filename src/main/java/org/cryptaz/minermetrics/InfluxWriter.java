package org.cryptaz.minermetrics;

import org.apache.log4j.Logger;
import org.cryptaz.minermetrics.models.CardTickData;
import org.cryptaz.minermetrics.models.GeneralTickData;
import org.cryptaz.minermetrics.models.dto.ClaymoreTickDTO;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;

import java.util.Properties;
import java.util.concurrent.TimeUnit;


public class InfluxWriter {

    private final static Logger log = Logger.getLogger(InfluxWriter.class);

    private String INFLUXDB_HOST = null;
    private String INFLUXDB_PASS = null;
    private String INFLUXDB_USER = null;
    private String INFLUXDB_DB = null;

    private InfluxDB influxDB;
    private Properties properties;

    public InfluxWriter(Properties properties) {
        this.properties = properties;
        INFLUXDB_HOST = properties.getProperty("influxdb_host");
        INFLUXDB_USER = properties.getProperty("influxdb_user");
        INFLUXDB_PASS = properties.getProperty("influxdb_pass");
        INFLUXDB_DB = properties.getProperty("influxdb_db");
        this.influxDB = InfluxDBFactory.connect(INFLUXDB_HOST, INFLUXDB_USER, INFLUXDB_PASS);
    }

    public InfluxWriter() {
        throw new IllegalArgumentException("Do not call without properties!");
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
                .addField("miner_url", String.valueOf(generalTickData.getMinerUrl()))
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
                    .addField("miner_url", String.valueOf(generalTickData.getMinerUrl()))
                    .addField("card_id", cardTickData.getId())
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
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
