package com.miu.bookhub.inventory.service.integration.openlib.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthorResponse {

    private String name;

    @JsonProperty("personal_name")
    private String personalName;

    private List<String> photos;

    @JsonProperty("remote_ids")
    private Map<String, String> remoteIds;
}
