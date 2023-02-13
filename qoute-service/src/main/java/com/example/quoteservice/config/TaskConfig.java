package com.example.quoteservice.config;

import com.example.quoteservice.task.PingTask;
import com.example.quoteservice.task.Task;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "pingtask.mode",havingValue = "active")
public class TaskConfig {

    @Value("${SEVER_PORT}")
    private String pingTaskUrl;
    @Bean
    public Task pingTask(){
        return new PingTask(pingTaskUrl+"/task");
    }
}
