package org.cryptaz.minermetrics.api.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.cryptaz.minermetrics.TemplateConfigurator;
import org.cryptaz.minermetrics.api.MinerAPI;
import org.cryptaz.minermetrics.models.CardTickData;
import org.cryptaz.minermetrics.models.GeneralTickData;
import org.cryptaz.minermetrics.models.dto.ClaymoreRawDTO;
import org.cryptaz.minermetrics.models.dto.ClaymoreTickDTO;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClaymoreAPI implements MinerAPI {
    private HttpClient httpClient;
    private Properties properties;
    private String claymoreUrl;
    private boolean lastTrySuccessful;
    private boolean connected;
    private Logger logger = LoggerFactory.getLogger(ClaymoreAPI.class);
    private TemplateConfigurator templateConfigurator;

    public ClaymoreAPI(String claymoreUrl) {
        this.httpClient = HttpClientBuilder.create().build();
        //this.properties = properties;
        //this.claymoreUrl = properties.getProperty("apiURL");
        this.claymoreUrl = claymoreUrl;
        this.lastTrySuccessful = true;
        this.connected = false;
        this.templateConfigurator = new TemplateConfigurator();
        if (claymoreUrl == null) {
            logger.error("No apiURL set!");
        }
    }

    public ClaymoreAPI() throws Exception {
        throw new Exception("No claymore url!");
    }

    @Override
    public ClaymoreTickDTO tick() {
        ClaymoreTickDTO tickDTO = getData();
        if (tickDTO == null) {
            return null;
        }
        tickDTO.setDateTime(new DateTime());
        return tickDTO;
    }

    @Override
    public String getUrl() {
        return claymoreUrl;
    }

    private ClaymoreTickDTO getData() {
        HttpGet request = new HttpGet(claymoreUrl);
        HttpResponse response = null;
        try {
            boolean updateTemplate = false;
            if (lastTrySuccessful && !connected) {
                logger.info("Daemon started. Going to start collecting data from external APIs.");
                updateTemplate = true;
            }
            response = httpClient.execute(request);
            int responseCode = response.getStatusLine().getStatusCode();

            assert (responseCode == 200);
            if (!connected) {
                logger.info("Successfully connected to Claymore miner API");
            }
            connected = true;
            lastTrySuccessful = true;
            BufferedReader rd = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent()));
            StringBuffer html = new StringBuffer();
            String line = "";
            while ((line = rd.readLine()) != null) {
                html.append(line);
            }
            String stringPattern = "\\{.*\\}";
            Pattern pattern = Pattern.compile(stringPattern);
            Matcher matcher = pattern.matcher(html);
            String json = null;
            if (matcher.find()) {
                json = matcher.group(0);
            }
            if (json == null) {
                logger.error("Json wasn't found in html");
                lastTrySuccessful = false;
                return null;
            }
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JodaModule());
            ClaymoreRawDTO claymoreRawDTO = null;
            try {
                claymoreRawDTO = objectMapper.readValue(json, ClaymoreRawDTO.class);
            } catch (IOException e) {
                logger.error("Could not parse json!");
                e.printStackTrace();
                lastTrySuccessful = false;
                return null;
            }

            ClaymoreTickDTO claymoreTickDTO = convert(claymoreRawDTO);
            if(updateTemplate){
                try {
                    saveTemplate(templateConfigurator.template(claymoreTickDTO.getGeneralTickData().getCardCount()));
                }
                catch (Exception e){
                    logger.error("Could not make template! Probably IO error.");
                    e.printStackTrace();
                }
            }
            return claymoreTickDTO;
        } catch (IOException e) {
            //e.printStackTrace();
            logger.error("Could not get api data for miner (Rejecting sending)");
            lastTrySuccessful = false;
            connected = false;
            return null;
        }
    }

    private ClaymoreTickDTO convert(ClaymoreRawDTO claymoreRawDTO) {
        ClaymoreTickDTO tickDTO = new ClaymoreTickDTO();

        GeneralTickData generalTickData = new GeneralTickData();
        List<CardTickData> cardTickDatas = new ArrayList<CardTickData>();

        String versionBuffer = claymoreRawDTO.getResult().get(0);
        String onlineTimeInMinutes = claymoreRawDTO.getResult().get(1);
        //overall_hashrate;accepted_shares;rejected_shares
        String[] statBuffer = claymoreRawDTO.getResult().get(2).split(";");
        //hashrate for each card with ; delimiter
        String hashrateBuffer = claymoreRawDTO.getResult().get(3);
        //String unknownStats = claymoreRawDTO.getResult().get(4);
        String stringTemperaturesAndFanBuffer = claymoreRawDTO.getResult().get(6);
        String[] tempBuffer = stringTemperaturesAndFanBuffer.split(";");
        String poolUrl = claymoreRawDTO.getResult().get(7);
        //String unknownStats1 = claymoreRawDTO.getResult().get(8);

        int cardCount = claymoreRawDTO.getResult().get(3).split(";").length;
        generalTickData.setCardCount(cardCount);
        generalTickData.setVersion(versionBuffer);
        Period period = new Period();
        period.plusMinutes(Integer.parseInt(onlineTimeInMinutes));
        generalTickData.setUptime(period);
        generalTickData.setOverallHashrate(Integer.parseInt(statBuffer[0]));
        generalTickData.setAcceptedShares(Long.parseLong(statBuffer[1]));
        generalTickData.setRejectedShares(Long.parseLong(statBuffer[2]));
        generalTickData.setPoolUrl(poolUrl);

        for (int i = 0; i < cardCount; i++) {
            CardTickData cardTickData = new CardTickData();
            cardTickData.setId(i);
            cardTickData.setHashrate(Integer.parseInt(hashrateBuffer.split(";")[i]));
            cardTickData.setTemperature(Integer.parseInt(tempBuffer[i + i]));
            cardTickData.setFan(Integer.parseInt(tempBuffer[i + i + 1]));
            cardTickDatas.add(cardTickData);
        }
        tickDTO.setGeneralTickData(generalTickData);
        tickDTO.setCardTickDatas(cardTickDatas);
        return tickDTO;
    }

    private void saveTemplate(String json) throws IOException {
        File file = new File("default_dashboards.json");
        if(file.delete()){
            logger.debug("Template deleted");
        }else{
            logger.debug("Delete template operation is failed.");
        }

        logger.debug("Saving template to file");
        FileUtils.writeStringToFile(new File("default_dashboards.json"), json);
    }
}
