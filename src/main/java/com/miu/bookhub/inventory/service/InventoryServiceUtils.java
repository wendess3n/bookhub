package com.miu.bookhub.inventory.service;

import com.miu.bookhub.inventory.repository.entity.*;
import com.miu.bookhub.order.repository.entity.Order;
import com.miu.bookhub.order.repository.entity.OrderItem;
import com.miu.bookhub.order.repository.entity.PaymentStatus;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Collectors;

public interface InventoryServiceUtils {

    /**
     * << Helper Method >>
     * Given a book and a year, computes the average rating of the book
     */
    BiFunction<Book, Integer, Double> getAverageRatingOfBook =
            (book, year) -> Optional.ofNullable(book)
                    .flatMap(bk -> Optional.ofNullable(bk.getRatings()))
                    .orElse(List.of()).stream()
                    .mapToInt(Rating::getScore)
                    .average()
                    .orElse(0);
    /**
     * << Helper Method >>
     * Given an order, checks if it was purchased in a given year
     */
    BiPredicate<Order, Integer> isOrderPurchased =
            (order, year) -> order.getPaymentStatus() == PaymentStatus.APPROVED && order.getOrderDate().getYear() == year;

    /**
     * << Helper Method >>
     * Given a book, counts the number of wishlists a book have for a year
     */
    BiFunction<Book, Integer, Long> countWishListOfBook =
            (book, year) -> Optional.ofNullable(book.getWishLists()).orElse(List.of()).stream()
                    .filter(wishList -> wishList.getWishedDate().getYear() == year)
                    .count();

    /**
     * << Helper Method >>
     * Given a list of orders and a book, counts the number of purchases of the book in a given year
     */
    TriFunction<List<Order>, Book, Integer, Long> countPurchasesOfABook =
            (orders, book, year) -> Optional.ofNullable(orders).orElse(List.of()).stream()
                    .filter(order -> isOrderPurchased.test(order, year))
                    .flatMap(order -> order.getOrderItems().stream())
                    .filter(item -> item.getBookItem().getBook().equals(book))
                    .mapToLong(OrderItem::getQuantity)
                    .sum();


    /**
     * Given a list of orders, finds the most sold books in a given year order by their selling count in reverse order
     */
    TriFunction<List<Order>, Integer, Integer, List<String>> findMostPurchasedBooks =

            (orders, year, limit) -> Optional.ofNullable(orders).orElse(List.of()).stream()
                    .filter(order -> isOrderPurchased.test(order, year))
                    .flatMap(order -> order.getOrderItems().stream())
                    .map(item -> new Tuple<>(item.getBookItem().getBook(), item.getQuantity()))
                    .collect(Collectors.groupingBy(Tuple::getLeft, Collectors.summingInt(Tuple::getRight)))
                    .entrySet().stream()
                    .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                    .limit(limit)
                    .map(e -> e.getKey().getTitle())
                    .collect(Collectors.toList());


    /**
     * Given a list of books, finds names of female authors of top-rated books of a given year, sorted by their name
     */
    TriFunction<List<Book>, Integer, Integer, List<String>> findFemaleAuthorsOfTopRatedBooks =

            (books, year, limit) -> Optional.ofNullable(books).orElse(List.of()).stream()
                    .filter(book -> book.getAuthors().stream().anyMatch(author -> author.getGender() == Gender.FEMALE))
                    .map(book -> new Tuple<>(book, getAverageRatingOfBook.apply(book, year)))
                    .sorted(Comparator.comparing(Tuple::getRight, Comparator.reverseOrder()))
                    .flatMap(tuple -> tuple.getLeft().getAuthors().stream())
                    .filter(author -> author.getGender() == Gender.FEMALE)
                    .limit(limit)
                    .map(Author::getName)
                    .sorted()
                    .collect(Collectors.toList());

    /**
     * Given a list of orders, finds titles of the most wish listed but least bought books of a given year filtered by book genre
     * Note: The least bought books should at least be purchased once to qualify on the list
     */
    QuadFunction<List<Order>, Genre, Integer, Integer, List<String>> findMostWishedButLeastBoughtBooks =

            (orders, genre, year, limit) -> Optional.ofNullable(orders).orElse(List.of()).stream()
                    .flatMap(order -> order.getOrderItems().stream())
                    .filter(item -> item.getBookItem().getBook().getGenre() == genre)
                    .collect(Collectors.groupingBy(item -> item.getBookItem().getBook(), Collectors.summingInt(item -> item.getBookItem().getBook().getWishLists().size())))
                    .keySet().stream()
                    .map(book -> new Triple<>(book, countPurchasesOfABook.apply(orders, book, year), countWishListOfBook.apply(book, year)))
                    .sorted(Comparator.comparing((Function<Triple<Book, Long, Long>, Long>) Triple::getRight, Comparator.reverseOrder())
                            .thenComparingLong(Triple::getMiddle))
                    .limit(limit)
                    .map(t -> t.getLeft().getTitle())
                    .collect(Collectors.toList());
}
