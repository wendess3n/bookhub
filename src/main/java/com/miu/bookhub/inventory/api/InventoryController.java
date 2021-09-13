package com.miu.bookhub.inventory.api;

import com.miu.bookhub.global.utils.SecurityUtils;
import com.miu.bookhub.inventory.api.domain.BookItemRequest;
import com.miu.bookhub.inventory.api.domain.BookItemResponse;
import com.miu.bookhub.inventory.repository.entity.BookItem;
import com.miu.bookhub.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/book-items")
@RestController
public class InventoryController {

    private final ModelMapper modelMapper;
    private final InventoryService inventoryService;

    @PostMapping
    public BookItemResponse postBookItem(@RequestBody BookItemRequest request) {

        BookItem bookItem = inventoryService.saveBookItem(SecurityUtils.getCurrentUserId(),
                request.getIsbn(), request.getCondition(), request.getQuantity(), request.getUnitPrice());

        return buildBookItemResponse(bookItem);
    }

    @GetMapping("/{bookItemId}")
    public BookItemResponse getBookItemById(@PathVariable Long bookItemId) {

        return inventoryService.getBookItem(bookItemId)
                .map(this::buildBookItemResponse)
                .orElse(null);
    }

    @PostMapping("/{bookItemId}/stock")
    public BookItemResponse stockBookItem(@PathVariable long bookItemId, @RequestBody int quantity) {

        BookItem bookItem = inventoryService.stockBookItem(bookItemId, quantity);
        return buildBookItemResponse(bookItem);
    }

    @PostMapping("/{bookItemId}/destock")
    public BookItemResponse deStockBookItem(@PathVariable Long bookItemId, @RequestBody int quantity) {

        BookItem bookItem = inventoryService.deStockBookItem(bookItemId, quantity);
        return buildBookItemResponse(bookItem);
    }

    private BookItemResponse buildBookItemResponse(BookItem bookItem) {

        var bookItemResponse = modelMapper.map(bookItem, BookItemResponse.class);
        modelMapper.map(bookItem.getBook(), bookItemResponse);

        bookItemResponse.setBookItemId(bookItem.getId());
        bookItemResponse.setBookId(bookItem.getBook().getId());

        return bookItemResponse;
    }
}
