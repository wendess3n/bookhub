package com.miu.bookhub.inventory.service;

import com.miu.bookhub.inventory.repository.entity.Genre;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Scanner;

import static com.miu.bookhub.inventory.service.InventoryInMemoryDB.books;
import static com.miu.bookhub.inventory.service.InventoryInMemoryDB.orders;

public class InventoryServiceUtilsManualTesting {

    public static void main(String[] args) {


        try (Scanner reader = new Scanner(System.in)) {

            while (true) {

                System.out.printf(
                        "Choose 1 to find the most purchased books by year%n" +
                        "Choose 2 to find female authors of top rated books by year%n" +
                        "Choose 3 to find most wished but least bought books by year%n");

                int func = reader.nextInt();

                int year, limit;
                Genre genre;

                switch (func) {

                    case 1:
                        System.out.println("Enter year of interest");
                        year = reader.nextInt();

                        System.out.println("How many elements do you want to see?");
                        limit = reader.nextInt();

                        List<String> titles = InventoryServiceUtils.findMostPurchasedBooks.apply(orders, year, limit);
                        System.out.println(titles);
                        break;

                    case 2:

                        System.out.println("Enter year of interest");
                        year = reader.nextInt();

                        System.out.println("How many elements do you want to see?");
                        limit = reader.nextInt();

                        List<String> authorNames = InventoryServiceUtils.findFemaleAuthorsOfTopRatedBooks.apply(books, year, limit);
                        System.out.println(authorNames);
                        break;

                    case 3:

                        System.out.println("Enter year of interest");
                        year = reader.nextInt();

                        System.out.println("Enter genre [" + StringUtils.join(Genre.values(), ", ") + "]");
                        genre = Genre.valueOf(reader.next());

                        System.out.println("How many elements do you want to see?");
                        limit = reader.nextInt();

                        List<String> bookTitles = InventoryServiceUtils.findMostWishedButLeastBoughtBooks.apply(orders, genre, year, limit);
                        System.out.println(bookTitles);
                        break;

                    default:
                        System.out.println("Invalid input. Please try again");
                }
            }
        } catch (Exception ex) {
            System.err.println("Error! Please try again");
            throw ex;
        }
    }
}
