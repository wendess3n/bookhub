package com.miu.bookhub.inventory.service;

import com.miu.bookhub.inventory.repository.entity.Genre;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.miu.bookhub.inventory.service.InventoryInMemoryDB.books;
import static com.miu.bookhub.inventory.service.InventoryInMemoryDB.orders;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class InventoryServiceUtilsTest {

    @Test
    void shouldFindMostPurchasedBooks() {

        List<String> titles = InventoryServiceUtils.findMostPurchasedBooks.apply(orders, 2021, 2);

        assertThat(titles)
                .containsExactly("Song of Solomon","The Night Before Christmas")
                .hasSize(2);
    }

    @Test
    void shouldFindFemaleAuthorsOfTopRatedBooks() {

        List<String> authorNames = InventoryServiceUtils.findFemaleAuthorsOfTopRatedBooks.apply(books, 2021, 1);

        assertThat(authorNames)
                .containsExactly("Westover, Tara")
                .hasSize(1);

        authorNames = InventoryServiceUtils.findFemaleAuthorsOfTopRatedBooks.apply(books, 2021, 2);

        assertThat(authorNames)
                .containsExactly("Morrison, Toni", "Westover, Tara")
                .hasSize(2);
    }

    @Test
    void shouldFindMostWishedButLeastBoughtBooks() {

        List<String> titles = InventoryServiceUtils.findMostWishedButLeastBoughtBooks.apply(orders, Genre.FANTASY, 2021, 1);

        assertThat(titles)
                .containsExactly("To Kill a Mockingbird")
                .hasSize(1);
    }
}
