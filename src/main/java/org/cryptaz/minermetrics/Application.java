package org.cryptaz.minermetrics;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Logger;
import org.cryptaz.minermetrics.async.AsyncTicker;
import org.cryptaz.minermetrics.models.Configuration;
import org.cryptaz.minermetrics.models.dto.ConfigurationDTO;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;

import static spark.Spark.get;
import static spark.Spark.post;

public class Application {

    private final static Logger log = Logger.getLogger(Application.class);

    public static void main(String[] args) throws IOException, InterruptedException {
        // Set up a simple configuration that logs on the console.
        log.info("MinerMetrics SNAPSHOT 1.0 started!");
        log.info("Loading configuration");
        final Configuration configuration = new Configuration();
        try {
            configuration.loadFromFile(new File("config.json"));
        } catch (IOException e) {
            log.info("Could not load config. Creating new with default");
            try {
                configuration.initDefault();
            } catch (IOException e1) {
                log.info("Could not save default config. Maybe permissions? It was IOException");
            }
        }

        //Starting web-server. This is done for sending command to application
        Spark.setPort(9090);
        Spark.setIpAddress("127.0.0.1");

        Route getConfigurationEndpoint = new Route("/configuration") {
            @Override
            public String handle(Request request, Response response) {
                log.debug("Request for configuration");
                ConfigurationDTO configurationDTO = Configuration.convertToDTO(configuration);
                String json = null;
                try {
                    json = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(configurationDTO);
                } catch (JsonProcessingException e) {
                    log.info("Could not parse config");
                    response.status(500);
                    return "Error during config loading";
                }
                return json;
            }
        };
        get(getConfigurationEndpoint);

        Route testClaymore = new Route("/miner/claymore/test") {
            @Override
            public Object handle(Request request, Response response) {
                if (!Objects.equals(request.body(), "")) {
                    try {
                        HttpClient httpClient = HttpClientBuilder.create().build();
                        HttpGet httpRequest = new HttpGet(request.body());
                        HttpResponse httpResponse = null;
                        httpResponse = httpClient.execute(httpRequest);
                        int responseCode = httpResponse.getStatusLine().getStatusCode();
                        BufferedReader rd = new BufferedReader(
                                new InputStreamReader(httpResponse.getEntity().getContent()));
                        StringBuffer html = new StringBuffer();
                        String line = "";
                        while ((line = rd.readLine()) != null) {
                            html.append(line);
                        }
                        if(Objects.equals(html.toString(), "")) {
                            //useless condition, but still
                            throw new IOException();
                        }
                        response.status(200);
                        return "";
                    } catch (IOException e) {
                        response.status(404);
                        return "";
                    }

                } else {
                    response.status(400);
                    return "";
                }
            }
        };
        post(testClaymore);

        log.info("Basic initializing passed, staring async worker.");

        AsyncTicker asyncTicker = new AsyncTicker(configuration);
        Thread thread = new Thread(asyncTicker);
        thread.start();
    }
}
