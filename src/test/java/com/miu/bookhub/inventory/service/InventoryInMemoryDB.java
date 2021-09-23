package com.miu.bookhub.inventory.service;

import com.miu.bookhub.account.repository.entity.Role;
import com.miu.bookhub.account.repository.entity.User;
import com.miu.bookhub.inventory.repository.entity.*;
import com.miu.bookhub.order.repository.entity.Order;
import com.miu.bookhub.order.repository.entity.OrderItem;
import com.miu.bookhub.order.repository.entity.PaymentStatus;
import com.miu.bookhub.order.repository.entity.WishList;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public class InventoryInMemoryDB {

    static final List<Book> books;
    static final List<Author> authors;
    static final List<BookItem> bookItems;
    static final List<Order> orders;

    static {

        User seller1 = User.builder()
                .firstName("Taliyah")
                .lastName("Cornish")
                .emailAddress("taliyah.cornish@email.com")
                .roles(Set.of(Role.CUSTOMER, Role.SELLER))
                .build();

        User seller2 = User.builder()
                .firstName("Clara")
                .lastName("Wynn")
                .emailAddress("clara.wynn@email.com")
                .roles(Set.of(Role.CUSTOMER, Role.SELLER))
                .build();

        User seller3 = User.builder()
                .firstName("Menna")
                .lastName("Byrne")
                .emailAddress("menna.byrne@email.com")
                .roles(Set.of(Role.CUSTOMER, Role.SELLER))
                .build();

        User customer1 = User.builder()
                .firstName("Tegan")
                .lastName("Carrillo")
                .emailAddress("tegan.carrillo@email.com")
                .roles(Set.of(Role.CUSTOMER))
                .build();

        User customer2 = User.builder()
                .firstName("Harry")
                .lastName("Khan")
                .emailAddress("harry.khan@email.com")
                .roles(Set.of(Role.CUSTOMER))
                .build();

        User customer3 = User.builder()
                .firstName("Jay")
                .lastName("Ryder")
                .emailAddress("jay.ryder@email.com")
                .roles(Set.of(Role.CUSTOMER))
                .build();

        Author author1 = Author.builder()
                .name("Rick Riordan")
                .gender(Gender.MALE)
                .build();

        Author author2 = Author.builder()
                .name("Westover, Tara")
                .gender(Gender.FEMALE)
                .build();

        Author author3 = Author.builder()
                .name("Eckhart Tolle")
                .gender(Gender.MALE)
                .build();

        Author author4 = Author.builder()
                .name("Clement C. Moore")
                .gender(Gender.MALE)
                .build();

        Author author5 = Author.builder()
                .name("Morrison, Toni")
                .gender(Gender.FEMALE)
                .build();

        Author author6 = Author.builder()
                .name("Bert Bates")
                .gender(Gender.MALE)
                .build();

        Author author7 = Author.builder()
                .name("Kathy Sierra")
                .gender(Gender.FEMALE)
                .build();

        Author author8 = Author.builder()
                .name("Harper Lee")
                .gender(Gender.FEMALE)
                .build();

        Book book1 = Book.builder()
                .title("Percy Jackson and the Olympians, Book One the Lightning Thief")
                .isbn("0786838655")
                .authors(List.of(author1))
                .publisher("Hyperion Books for Children")
                .publishDate(LocalDate.of(2006, 4, 1))
                .pageCount(416)
                .format(Format.PAPER_BACK)
                .genre(Genre.FANTASY)
                .weight(0.69)
                .ratings(List.of(
                        Rating.builder()
                                .score(3)
                                .rater(customer1)
                                .ratingDate(LocalDate.of(2021, 3, 3))
                                .build(),
                        Rating.builder()
                                .score(4)
                                .rater(customer2)
                                .ratingDate(LocalDate.of(2021, 4, 11))
                                .build(),
                        Rating.builder()
                                .score(3)
                                .rater(customer3)
                                .ratingDate(LocalDate.of(2021, 1, 1))
                                .build()
                ))
                .wishLists(List.of(
                        WishList.builder()
                                .customer(customer1)
                                .wishedDate(LocalDate.of(2021, 1, 4))
                                .build()
                ))
                .build();

        book1.getRatings().forEach(rating -> rating.setBook(book1));
        book1.getWishLists().forEach(wishList -> wishList.setBook(book1));

        Book book2 = Book.builder()
                .title("Educated: A Memoir")
                .isbn("0399590501")
                .authors(List.of(author2, author3))
                .publisher("Random House Publishing Group")
                .publishDate(LocalDate.of(2018, 2, 1))
                .pageCount(352)
                .format(Format.HARD_COVER)
                .genre(Genre.MEMOIR)
                .weight(1.34)
                .ratings(List.of(
                        Rating.builder()
                                .score(5)
                                .rater(customer1)
                                .ratingDate(LocalDate.of(2021, 5, 3))
                                .build(),
                        Rating.builder()
                                .score(5)
                                .rater(customer2)
                                .ratingDate(LocalDate.of(2021, 2, 11))
                                .build(),
                        Rating.builder()
                                .score(4)
                                .rater(customer3)
                                .ratingDate(LocalDate.of(2021, 1, 1))
                                .build()
                ))
                .wishLists(List.of(
                        WishList.builder()
                                .customer(customer3)
                                .wishedDate(LocalDate.of(2021, 4, 4))
                                .build()
                ))
                .build();
        book2.getRatings().forEach(rating -> rating.setBook(book2));
        book2.getWishLists().forEach(wishList -> wishList.setBook(book2));

        Book book3 = Book.builder()
                .title("A New Earth: Awakening to Your Life's Purpose")
                .isbn("0452289963")
                .authors(List.of(author3))
                .publisher("Penguin")
                .publishDate(LocalDate.of(2018, 2, 1))
                .pageCount(336)
                .format(Format.PAPER_BACK)
                .genre(Genre.SELF_HELP)
                .weight(0.7)
                .ratings(List.of(
                        Rating.builder()
                                .score(1)
                                .rater(customer1)
                                .ratingDate(LocalDate.of(2021, 3, 3))
                                .build(),
                        Rating.builder()
                                .score(4)
                                .rater(customer2)
                                .ratingDate(LocalDate.of(2021, 4, 11))
                                .build()
                ))
                .wishLists(List.of(
                        WishList.builder()
                                .customer(customer3)
                                .wishedDate(LocalDate.of(2021, 7, 4))
                                .build()
                ))
                .build();

        book3.getRatings().forEach(rating -> rating.setBook(book3));
        book3.getWishLists().forEach(wishList -> wishList.setBook(book3));

        Book book4 = Book.builder()
                .title("The Night Before Christmas")
                .isbn("0060757442")
                .authors(List.of(author4))
                .publisher("Penguin")
                .publishDate(LocalDate.of(2008, 9, 1))
                .pageCount(40)
                .format(Format.PAPER_BACK)
                .genre(Genre.SHORT_STORIES)
                .weight(0.45)
                .wishLists(List.of(
                        WishList.builder()
                                .customer(customer3)
                                .wishedDate(LocalDate.of(2020, 1, 1))
                                .build()
                ))
                .build();
        book4.getWishLists().forEach(wishList -> wishList.setBook(book4));

        Book book5 = Book.builder()
                .title("The Bluest Eye")
                .isbn("0812410971")
                .authors(List.of(author5))
                .publisher("Perfection Learning")
                .publishDate(LocalDate.of(1994, 9, 1))
                .format(Format.PAPER_BACK)
                .genre(Genre.LITERARY_FICTION)
                .weight(0.65)
                .ratings(List.of(
                        Rating.builder()
                                .score(4)
                                .rater(customer1)
                                .ratingDate(LocalDate.of(2020, 5, 3))
                                .build(),
                        Rating.builder()
                                .score(3)
                                .rater(customer2)
                                .ratingDate(LocalDate.of(2021, 4, 14))
                                .build(),
                        Rating.builder()
                                .score(3)
                                .rater(customer3)
                                .ratingDate(LocalDate.of(2021, 7, 3))
                                .build()
                ))
                .build();

        book5.getRatings().forEach(rating -> rating.setBook(book5));

        Book book6 = Book.builder()
                .title("Song of Solomon")
                .isbn("0451129334")
                .authors(List.of(author5))
                .publisher("Penguin Publishing Group")
                .publishDate(LocalDate.of(1978, 11, 1))
                .format(Format.PAPER_BACK)
                .genre(Genre.ROMANCE)
                .weight(0.65)
                .build();

        Book book7 = Book.builder()
                .title("Home")
                .isbn("0307740919")
                .authors(List.of(author5))
                .publisher("Knopf Doubleday Publishing Group")
                .publishDate(LocalDate.of(2013, 1, 1))
                .format(Format.PAPER_BACK)
                .genre(Genre.LITERARY_FICTION)
                .weight(0.51)
                .pageCount(160)
                .ratings(List.of(
                        Rating.builder()
                                .score(2)
                                .rater(customer1)
                                .ratingDate(LocalDate.of(2021, 4, 3))
                                .build(),
                        Rating.builder()
                                .score(3)
                                .rater(customer2)
                                .ratingDate(LocalDate.of(2021, 6, 11))
                                .build(),
                        Rating.builder()
                                .score(3)
                                .rater(customer3)
                                .ratingDate(LocalDate.of(2021, 1, 19))
                                .build()
                )).wishLists(List.of(
                        WishList.builder()
                                .customer(customer1)
                                .wishedDate(LocalDate.of(2021, 3, 6))
                                .build()
                ))
                .build();
        book7.getRatings().forEach(rating -> rating.setBook(book7));
        book7.getWishLists().forEach(wishList -> wishList.setBook(book7));

        Book book8 = Book.builder()
                .title("Head First Java")
                .isbn("0596009208")
                .authors(List.of(author6, author7))
                .publisher("O'Reilly Media, Incorporated")
                .publishDate(LocalDate.of(2005, 3, 1))
                .format(Format.PAPER_BACK)
                .genre(Genre.TECHNOLOGY)
                .weight(2.72)
                .pageCount(720)
                .build();

        Book book9 = Book.builder()
                .title("To Kill a Mockingbird")
                .isbn("0060935464")
                .authors(List.of(author8))
                .publisher("Harper Perennial")
                .publishDate(LocalDate.of(2002, 1, 1))
                .format(Format.PAPER_BACK)
                .genre(Genre.FANTASY)
                .weight(0.66)
                .pageCount(336)
                .wishLists(List.of(
                        WishList.builder()
                                .customer(customer1)
                                .wishedDate(LocalDate.of(2021, 1, 1))
                                .build(),
                        WishList.builder()
                                .customer(customer3)
                                .wishedDate(LocalDate.of(2021, 4, 5))
                                .build()
                ))
                .build();
        book9.getWishLists().forEach(wishList -> wishList.setBook(book9));

        BookItem bookItem1 = BookItem.builder()
                .book(book1)
                .seller(seller1)
                .condition(Condition.GOOD)
                .quantity(4)
                .unitPrice(12.99)
                .build();

        BookItem bookItem2 = BookItem.builder()
                .book(book1)
                .seller(seller2)
                .condition(Condition.LIKE_NEW)
                .quantity(1)
                .unitPrice(15.99)
                .build();

        BookItem bookItem3 = BookItem.builder()
                .book(book2)
                .seller(seller3)
                .condition(Condition.LIKE_NEW)
                .quantity(4)
                .unitPrice(34.99)
                .build();

        BookItem bookItem4 = BookItem.builder()
                .book(book3)
                .seller(seller2)
                .condition(Condition.GOOD)
                .quantity(4)
                .unitPrice(23.99)
                .build();

        BookItem bookItem5 = BookItem.builder()
                .book(book4)
                .seller(seller1)
                .condition(Condition.LIKE_NEW)
                .quantity(3)
                .unitPrice(16.99)
                .build();

        BookItem bookItem6 = BookItem.builder()
                .book(book4)
                .seller(seller1)
                .condition(Condition.ACCEPTABLE)
                .quantity(1)
                .unitPrice(10.99)
                .build();

        BookItem bookItem7 = BookItem.builder()
                .book(book4)
                .seller(seller2)
                .condition(Condition.NEW)
                .quantity(2)
                .unitPrice(13.99)
                .build();

        BookItem bookItem8 = BookItem.builder()
                .book(book5)
                .seller(seller3)
                .condition(Condition.NEW)
                .quantity(10)
                .unitPrice(19.99)
                .build();

        BookItem bookItem9 = BookItem.builder()
                .book(book6)
                .seller(seller1)
                .condition(Condition.NEW)
                .quantity(1)
                .unitPrice(19.99)
                .build();

        BookItem bookItem10 = BookItem.builder()
                .book(book6)
                .seller(seller2)
                .condition(Condition.LIKE_NEW)
                .quantity(1)
                .unitPrice(14.99)
                .build();

        BookItem bookItem11 = BookItem.builder()
                .book(book7)
                .seller(seller2)
                .condition(Condition.LIKE_NEW)
                .quantity(1)
                .unitPrice(12.99)
                .build();

        BookItem bookItem12 = BookItem.builder()
                .book(book7)
                .seller(seller2)
                .condition(Condition.NEW)
                .quantity(4)
                .unitPrice(23.99)
                .build();

        BookItem bookItem13 = BookItem.builder()
                .book(book8)
                .seller(seller3)
                .condition(Condition.GOOD)
                .quantity(1)
                .unitPrice(40.99)
                .build();

        BookItem bookItem14 = BookItem.builder()
                .book(book8)
                .seller(seller1)
                .condition(Condition.ACCEPTABLE)
                .quantity(1)
                .unitPrice(35.99)
                .build();

        BookItem bookItem15 = BookItem.builder()
                .book(book9)
                .seller(seller1)
                .condition(Condition.ACCEPTABLE)
                .quantity(1)
                .unitPrice(31.99)
                .build();

        Order order1 = Order.builder()
                .paymentStatus(PaymentStatus.APPROVED)
                .orderDate(LocalDateTime.of(2021, 9, 7, 23, 4, 11))
                .orderItems(List.of(
                        OrderItem.builder()
                                .bookItem(bookItem1)
                                .quantity(2)
                                .build(),
                        OrderItem.builder()
                                .bookItem(bookItem2)
                                .quantity(1)
                                .build()
                ))
                .amount(50.37)
                .build();
        order1.getOrderItems().forEach(orderItem -> orderItem.setOrder(order1));

        Order order2 = Order.builder()
                .paymentStatus(PaymentStatus.APPROVED)
                .orderDate(LocalDateTime.of(2021, 4, 5, 13, 4, 11))
                .orderItems(List.of(
                        OrderItem.builder()
                                .bookItem(bookItem1)
                                .quantity(1)
                                .build(),
                        OrderItem.builder()
                                .bookItem(bookItem3)
                                .quantity(1)
                                .build(),
                        OrderItem.builder()
                                .bookItem(bookItem4)
                                .quantity(1)
                                .build()
                ))
                .amount(86.35)
                .build();
        order2.getOrderItems().forEach(orderItem -> orderItem.setOrder(order2));

        Order order3 = Order.builder()
                .paymentStatus(PaymentStatus.APPROVED)
                .orderDate(LocalDateTime.of(2021, 5, 5, 13, 4, 11))
                .orderItems(List.of(
                        OrderItem.builder()
                                .bookItem(bookItem5)
                                .quantity(1)
                                .build()
                ))
                .amount(20.39)
                .build();
        order3.getOrderItems().forEach(orderItem -> orderItem.setOrder(order3));

        Order order4 = Order.builder()
                .paymentStatus(PaymentStatus.APPROVED)
                .orderDate(LocalDateTime.of(2020, 4, 7, 13, 4, 11))
                .orderItems(List.of(
                        OrderItem.builder()
                                .bookItem(bookItem1)
                                .quantity(2)
                                .build(),
                        OrderItem.builder()
                                .bookItem(bookItem6)
                                .quantity(1)
                                .build()
                ))
                .amount(44.37)
                .build();
        order4.getOrderItems().forEach(orderItem -> orderItem.setOrder(order4));

        Order order5 = Order.builder()
                .paymentStatus(PaymentStatus.REJECTED)
                .orderDate(LocalDateTime.of(2020, 4, 7, 13, 4, 11))
                .orderItems(List.of(
                        OrderItem.builder()
                                .bookItem(bookItem1)
                                .quantity(1)
                                .build()
                ))
                .amount(15.59)
                .build();
        order5.getOrderItems().forEach(orderItem -> orderItem.setOrder(order5));

        Order order6 = Order.builder()
                .paymentStatus(PaymentStatus.APPROVED)
                .orderDate(LocalDateTime.of(2021, 3, 7, 13, 4, 11))
                .orderItems(List.of(
                        OrderItem.builder()
                                .bookItem(bookItem6)
                                .quantity(1)
                                .build(),
                        OrderItem.builder()
                                .bookItem(bookItem7)
                                .quantity(2)
                                .build(),
                        OrderItem.builder()
                                .bookItem(bookItem8)
                                .quantity(1)
                                .build()
                ))
                .amount(70.73)
                .build();
        order6.getOrderItems().forEach(orderItem -> orderItem.setOrder(order6));

        Order order7 = Order.builder()
                .paymentStatus(PaymentStatus.APPROVED)
                .orderDate(LocalDateTime.of(2021, 6, 3, 13, 4, 11))
                .orderItems(List.of(
                        OrderItem.builder()
                                .bookItem(bookItem1)
                                .quantity(1)
                                .build(),
                        OrderItem.builder()
                                .bookItem(bookItem7)
                                .quantity(1)
                                .build(),
                        OrderItem.builder()
                                .bookItem(bookItem9)
                                .quantity(1)
                                .build(),
                        OrderItem.builder()
                                .bookItem(bookItem10)
                                .quantity(1)
                                .build()
                ))
                .amount(74.33)
                .build();
        order7.getOrderItems().forEach(orderItem -> orderItem.setOrder(order7));

        Order order8 = Order.builder()
                .paymentStatus(PaymentStatus.APPROVED)
                .orderDate(LocalDateTime.of(2021, 1, 21, 13, 4, 11))
                .orderItems(List.of(
                        OrderItem.builder()
                                .bookItem(bookItem11)
                                .quantity(1)
                                .build(),
                        OrderItem.builder()
                                .bookItem(bookItem12)
                                .quantity(1)
                                .build()
                ))
                .amount(44.38)
                .build();
        order8.getOrderItems().forEach(orderItem -> orderItem.setOrder(order8));

        Order order9 = Order.builder()
                .paymentStatus(PaymentStatus.APPROVED)
                .orderDate(LocalDateTime.of(2021, 5, 24, 13, 4, 11))
                .orderItems(List.of(
                        OrderItem.builder()
                                .bookItem(bookItem12)
                                .quantity(1)
                                .build(),
                        OrderItem.builder()
                                .bookItem(bookItem13)
                                .quantity(1)
                                .build(),
                        OrderItem.builder()
                                .bookItem(bookItem2)
                                .quantity(1)
                                .build(),
                        OrderItem.builder()
                                .bookItem(bookItem5)
                                .quantity(4)
                                .build()
                ))
                .amount(158.33)
                .build();
        order9.getOrderItems().forEach(orderItem -> orderItem.setOrder(order9));

        Order order10 = Order.builder()
                .paymentStatus(PaymentStatus.APPROVED)
                .orderDate(LocalDateTime.of(2021, 3, 12, 13, 4, 11))
                .orderItems(List.of(
                        OrderItem.builder()
                                .bookItem(bookItem4)
                                .quantity(1)
                                .build(),
                        OrderItem.builder()
                                .bookItem(bookItem5)
                                .quantity(1)
                                .build(),
                        OrderItem.builder()
                                .bookItem(bookItem9)
                                .quantity(100)
                                .build()
                ))
                .amount(73.17)
                .build();
        order10.getOrderItems().forEach(orderItem -> orderItem.setOrder(order10));

        Order order11 = Order.builder()
                .paymentStatus(PaymentStatus.APPROVED)
                .orderDate(LocalDateTime.of(2021, 3, 12, 13, 4, 11))
                .orderItems(List.of(
                        OrderItem.builder()
                                .bookItem(bookItem15)
                                .quantity(1)
                                .build()
                ))
                .amount(73.17)
                .build();
        order11.getOrderItems().forEach(orderItem -> orderItem.setOrder(order11));


        books = List.of(
                book1,
                book2,
                book3,
                book4,
                book5,
                book6,
                book7,
                book8,
                book9
        );

        authors = List.of(
                author1,
                author2,
                author3,
                author4,
                author5,
                author6,
                author7
        );

        bookItems = List.of(
                bookItem1,
                bookItem2,
                bookItem3,
                bookItem4,
                bookItem5,
                bookItem6,
                bookItem7,
                bookItem8,
                bookItem9,
                bookItem10,
                bookItem11,
                bookItem12,
                bookItem13,
                bookItem14,
                bookItem15
        );

        orders = List.of(
                order1,
                order2,
                order3,
                order4,
                order5,
                order6,
                order7,
                order8,
                order9,
                order10,
                order11
        );
    }
}
