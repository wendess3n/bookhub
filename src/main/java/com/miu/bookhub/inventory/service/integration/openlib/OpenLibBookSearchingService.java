package com.miu.bookhub.inventory.service.integration.openlib;

import com.miu.bookhub.inventory.repository.entity.Author;
import com.miu.bookhub.inventory.repository.entity.Book;
import com.miu.bookhub.inventory.repository.entity.Format;
import com.miu.bookhub.inventory.service.integration.BookSearchIntegrationService;
import com.miu.bookhub.inventory.service.integration.openlib.domain.AuthorResponse;
import com.miu.bookhub.inventory.service.integration.openlib.domain.BookResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service("openLibBookSearchingService")
public class OpenLibBookSearchingService implements BookSearchIntegrationService {

    private static final String URL_SUFFIX = ".json";
    private static final String BOOK_COVER_URI_TEMPLATE = "https://covers.openlibrary.org/b/id/%s-M.jpg";
    private static final String AUTHOR_PHOTO_URI_TEMPLATE = "https://covers.openlibrary.org/a/id/%s-M.jpg";

    private WebClient webClient;
    private final OpenLibProperties openLibProperties;

    @PostConstruct
    public void init() {
        webClient = WebClient.builder()
                .baseUrl(openLibProperties.getBaseUri())
                .clientConnector(new ReactorClientHttpConnector(
                        HttpClient.create().followRedirect(true)
                ))
                .build();
    }

    @Override
    public Optional<Book> searchBookByIsbn(String isbn) {

        try {

            BookResponse bookResponse = webClient.get()
                    .uri(openLibProperties.getIsbnPath() + isbn + URL_SUFFIX)
                    .exchangeToMono(response -> {

                        if (response.statusCode().is2xxSuccessful())
                            return response.bodyToMono(BookResponse.class);

                        return Mono.empty();
                    }).block();

            if (bookResponse == null) return Optional.empty();

            Book book = extractBookDetails(bookResponse);
            List<Author> authors = extractAuthorsDetails(bookResponse);
            book.setAuthors(authors);

            return Optional.of(book);

        } catch (Exception ex) {
            log.error("Failed to fetch book details", ex);
            return Optional.empty();
        }
    }

    public Optional<Author> searchBookAuthors(String authorId) {

        try {

            Optional<AuthorResponse> authorResponse = webClient.get()
                    .uri(authorId + URL_SUFFIX)
                    .accept(MediaType.APPLICATION_JSON)
                    .exchangeToMono(response -> {

                        Mono<Optional<AuthorResponse>> result;

                        if (response.statusCode().is2xxSuccessful()) {
                            result = response.bodyToMono(AuthorResponse.class)
                                    .map(Optional::of);
                        } else {
                            result = Mono.just(Optional.empty());
                        }
                        return result;
                    })
                    .block();

            return authorResponse
                    .map(author -> {

                        String isni = extractIsni(author);
                        String photoUri = extractPhotoUri(author);

                        return Author.builder()
                                .name(author.getName())
                                .personalName(author.getPersonalName())
                                .isni(isni)
                                .photoUri(photoUri)
                                .build();
                    });

        } catch (Exception ex) {
            log.error("Failed to fetch authors' details", ex);
            return Optional.empty();
        }
    }

    private String extractPhotoUri(AuthorResponse author) {

        return Optional.ofNullable(author.getPhotos()).orElse(List.of()).stream()
                .filter(photoId -> !StringUtils.isNumeric(photoId) || Integer.parseInt(photoId) > 0)
                .findFirst()
                .map(photoId -> String.format(AUTHOR_PHOTO_URI_TEMPLATE, photoId))
                .orElse(null);
    }

    private String extractIsni(AuthorResponse author) {

        return Optional.ofNullable(author.getRemoteIds())
                .filter(ids -> ids.containsKey("isni"))
                .map(ids -> ids.get("isni"))
                .orElse(null);
    }

    private List<Author> extractAuthorsDetails(BookResponse bookResponse) {

        return Optional.ofNullable(bookResponse.getAuthors()).orElse(List.of()).stream()
                .filter(author -> author.containsKey("key"))
                .map(author -> searchBookAuthors(author.get("key")).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private Book extractBookDetails(BookResponse bookResponse) {

        String publishers = StringUtils.join(Optional.ofNullable(bookResponse.getPublishers()).orElse(List.of()), ", ");

        Double weight = Optional.ofNullable(bookResponse.getWeight())
                .map(str -> str.replaceAll("[^0-9\\\\.]", "").trim())
                .map(Double::parseDouble)
                .orElse(null);

        Format format = Optional.ofNullable(bookResponse.getFormat())
                .map(String::toLowerCase)
                .filter(ft -> StringUtils.startsWithAny(ft, "mass", "paper", "hard"))
                .map(ft -> {
                    if (ft.startsWith("Mass")) return Format.MASS_MARKET_PAPER_BACK;
                    if (ft.startsWith("Paper")) return Format.PAPER_BACK;
                    return Format.HARD_COVER;
                })
                .orElse(null);

        String coverUri = Optional.ofNullable(bookResponse.getCovers()).orElse(List.of()).stream()
                .filter(coverId -> coverId > 0)
                .findFirst()
                .map(coverId -> String.format(BOOK_COVER_URI_TEMPLATE, coverId))
                .orElse(null);

        LocalDate publishDate = null;

        try {
            publishDate = Optional.ofNullable(bookResponse.getPublishDate())
                    .map(LocalDate::parse)
                    .orElse(null);
        } catch (Exception ignore) {}

        return Book.builder()
                .isbn(bookResponse.getIsbn().get(0))
                .title(bookResponse.getTitle())
                .publisher(publishers)
                .publishDate(publishDate)
                .coverUri(coverUri)
                .pageCount(bookResponse.getPageCount())
                .weight(weight)
                .format(format)
                .edition(bookResponse.getEdition())
                .build();
    }
}
