package com.miu.bookhub.inventory.api;

import com.miu.bookhub.inventory.api.domain.BookResponse;
import com.miu.bookhub.inventory.repository.entity.Book;
import com.miu.bookhub.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RequestMapping("/books")
@RequiredArgsConstructor
@RestController
public class BookController {

    private final ModelMapper modelMapper;
    private final InventoryService inventoryService;

    @GetMapping(params = "isbn")
    public BookResponse findBookByIsbn(@RequestParam String isbn) {

        return inventoryService.findBookByIsbn(isbn)
                .map(this::buildBookResponse)
                .orElse(null);
    }

    @GetMapping(params = "title")
    public List<BookResponse> searchBooksByTitle(@RequestParam String title, Pageable pageable) {

        return inventoryService.searchBooksByTitle(title, pageable).stream()
                .map(this::buildBookResponse)
                .collect(Collectors.toList());
    }

    @GetMapping(params = "author")
    public List<BookResponse> searchBooksByAuthor(@RequestParam String author, Pageable pageable) {

        return inventoryService.searchBooksByAuthor(author, pageable).stream()
                .map(this::buildBookResponse)
                .collect(Collectors.toList());
    }

    private BookResponse buildBookResponse(Book book) {

        var bookResponse = modelMapper.map(book, BookResponse.class);
        bookResponse.setBookId(book.getId());
        return bookResponse;
    }
}
