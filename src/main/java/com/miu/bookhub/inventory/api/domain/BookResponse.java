package com.miu.bookhub.inventory.api.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.miu.bookhub.inventory.api.domain.model.Author;
import com.miu.bookhub.inventory.repository.entity.Format;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class BookResponse {

    private Long bookId;
    private String isbn;
    private String title;
    private String edition;
    private String publisher;
    private String coverUri;

    @JsonFormat(pattern = "yyyy-MM")
    private LocalDate publishDate;
    private Format format;
    private Integer pageCount;
    private Double weight;
    private List<Author> authors;
}
