package com.su.pojo.view;

import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "aliyvn")
@Data
@ToString
public class Aliyvn {
    private String accesskeyId;
    private String accesskeySecret;
    private String endpoint;
}
