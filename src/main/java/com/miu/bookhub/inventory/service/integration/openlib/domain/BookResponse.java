package com.miu.bookhub.inventory.service.integration.openlib.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BookResponse {

    @JsonProperty("isbn_10")
    private List<String> isbn;

    private String title;
    private String subtitle;

    @JsonProperty("edition_name")
    private String edition;

    private List<Map<String, String>> authors;
    private List<String> publishers;

    @JsonProperty("publish_date")
    private String publishDate;

    private List<Long> covers;

    @JsonProperty("number_of_pages")
    private Integer pageCount;

    private String weight;

    @JsonProperty("physical_format")
    private String format;

}
