package com.example.quoteservice.task;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

@Slf4j
@RequiredArgsConstructor
public class PingTask implements Task{

    private final String urlForPing;

    @Scheduled(fixedRateString = "${pingtask.period}")
    public void pingMe() {
        if (urlForPing != null) {
            try {
                URL url = new URL(urlForPing);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                log.info("Ping {}, OK: response code {}",
                        url.getHost(),
                        connection.getResponseCode());
                connection.disconnect();
            } catch (IOException e) {
                log.error("Ping job exited with error");
            }
        }
    }
}
