package com.miu.bookhub.inventory.service.integration.openlib;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "bookhub.integration.open-lib")
public class OpenLibProperties {

    private String baseUri;
    private String isbnPath;
    private String authorsPath;
}
