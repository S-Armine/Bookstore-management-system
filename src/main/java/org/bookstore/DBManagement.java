package org.bookstore;

import java.sql.*;
import java.time.LocalDate;
import java.util.Scanner;

public class DBManagement {
    private final String address = "jdbc:postgresql://localhost:5432/bookstoredb";
    private final String username = "root";
    private final String pwd = "passwords";
    private final Scanner scanner = new Scanner(System.in);

    /**
     *manages actions that user can do with database
     */
    public void managementOfDB() {
        try (Connection connection = DriverManager.getConnection(address, username, pwd)) {
            System.out.println("Successfully conected to Bookstore Database");
            actions: while (true) {
                System.out.println("Choose action you want to execute from list bellow.");
                System.out.println("1: Update book details.");
                System.out.println("2: List books by genre or author.");
                System.out.println("3: Update customer information.");
                System.out.println("4: View a customer’s purchase history.");
                System.out.println("5: Calculate total revenue by genre.");
                System.out.println("6: Generate a report of all books sold");
                System.out.println("7: Generate a report of revenue of each genre.");
                System.out.println("0: Exit.");
                switch (scanner.nextLine()) {
                    case "1" -> updateBookDetails(connection);
                    case "2" -> listBooks(connection);
                    case "3" -> updateCustomerInformation(connection);
                    case "4" -> customerHistory(connection);
                    case "5" -> calculateRevenue(connection);
                    case "6" -> getBookReport(connection);
                    case "7" -> getGenreRevenues(connection);
                    case "0" -> {
                        break actions;
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Couldn't perform action." + e.getMessage());
        }
    }

    /**
     * gives functionality for updating book's information by its id in database
     * @param connection The connection to retrieve data from database
     */
    private void updateBookDetails(Connection connection) {
        int id;
        while (true) {
            System.out.println("Enter id of the book you want to update.");
            if (scanner.hasNextInt()) {
                id = scanner.nextInt();
                break;
            }
        }
        String columnName = "";
        String newValue = "";
        boolean isSet = false;
        while (!isSet) {
            System.out.println("Choose column you want to update");
            System.out.println("1: title");
            System.out.println("2: author");
            System.out.println("3: genre");
            System.out.println("4: price");
            System.out.println("5: quantity in stock");
            String choice = scanner.next();
            switch (choice) {
                case "1" -> {
                    columnName = "title";
                    System.out.println("Input new title.");
                    newValue = scanner.next();
                    isSet = true;
                }
                case "2" -> {
                    columnName = "author";
                    System.out.println("Input new author.");
                    newValue = scanner.next();
                    isSet = true;
                }
                case "3" -> {
                    columnName = "genre";
                    System.out.println("Input new genre.");
                    newValue = scanner.next();
                    isSet = true;
                }
                case "4" -> {
                    columnName = "price";
                    while (true) {
                        System.out.println("Input new price.");
                        if (scanner.hasNextFloat()) {
                            newValue = scanner.next();
                            break;
                        }
                        System.out.println("Price should be floating point number.");
                    }
                    isSet = true;
                }
                case "5" -> {
                    columnName = "quantityinstock";
                    while (true) {
                        System.out.println("Input quantity in stock.");
                        if (scanner.hasNextInt()) {
                            newValue = scanner.next();
                            break;
                        }
                        System.out.println("Quantity in stock should be integer number.");
                    }
                    isSet = true;
                }
                default -> System.out.println("Invalid choice. Try again.");
            }
        }
        String statementString = "UPDATE books " +
                "SET " + columnName + " = ? " +
                "WHERE BookID = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(statementString)) {
            preparedStatement.setInt(2, id);
            switch (columnName) {
                case "title":
                case "author":
                case "genre":
                    preparedStatement.setString(1, newValue);
                    break;
                case "price":
                    preparedStatement.setFloat(1, Float.parseFloat(newValue));
                    break;
                case "quantityinstock":
                    preparedStatement.setInt(1, Integer.parseInt(newValue));
            }
            preparedStatement.execute();
        } catch (SQLException e) {
            System.out.println("Couldn't perform action." + e.getMessage());
        }
    }

    /**
     * lists book information based on given genre or author
     * @param connection The connection to retrieve data from database
     */
    private void listBooks(Connection connection){
        String columnName = "";
        System.out.println("Do you want to list books by ");
        System.out.println("1: genre.");
        System.out.println("2: author");
        boolean isNameSet = false;
        while (!isNameSet) {
            switch (scanner.nextLine()) {
                case "1" -> {
                    columnName = "genre";
                    isNameSet = true;
                }
                case "2" -> {
                    columnName = "author";
                    isNameSet = true;
                }
                default -> System.out.println("Invalid input. Try again.");
            }
        }
        String listingStatement = "SELECT * FROM books\n" +
                "WHERE " + columnName + " = ?";
        System.out.println("Input name of " + columnName);
        try (PreparedStatement preparedStatement = connection.prepareStatement(listingStatement)) {
           preparedStatement.setString(1, scanner.nextLine());
           try(ResultSet resultSet = preparedStatement.executeQuery())
           {
               System.out.printf("%-30s | %-25s | %-20s | %-10s | %-15s%n",
                       "Book Title", "Author", "Genre", "Price", "Quantity In Stock");
               System.out.println("-------------------------------------------------------------------------------------------------------------------------------------");

               while (resultSet.next()) {
                   String bookTitle = resultSet.getString("title");
                   String author = resultSet.getString("author");
                   String genre = resultSet.getString("genre");
                   double price = resultSet.getDouble("price");
                   int quantityInStock = resultSet.getInt("quantityinstock");

                   System.out.printf("%-30s | %-25s | %-20s | %-10.2f | %-15d%n",
                           bookTitle, author, genre, price, quantityInStock);
               }
           }
        } catch (SQLException e) {
            System.out.println("Couldn't perform action." + e.getMessage());
        }
    }

    /**
     * gives functionality for updating customers information by its id in database
     * @param connection The connection to retrieve data from database
     */
    private void updateCustomerInformation(Connection connection) {
        int id;
        while (true) {
            System.out.println("Enter id of the customer you want to update.");
            if (scanner.hasNextInt()) {
                id = scanner.nextInt();
                break;
            }
        }
        String columnName = "";
        String newValue = "";
        boolean isSet = false;
        while (!isSet) {
            System.out.println("Choose column you want to update");
            System.out.println("1: name");
            System.out.println("2: email");
            System.out.println("3: phone");
            String choice = scanner.next();
            switch (choice) {
                case "1" -> {
                    columnName = "name";
                    System.out.println("Input new name.");
                    newValue = scanner.next();
                    isSet = true;
                }
                case "2" -> {
                    columnName = "email";
                    System.out.println("Input new email.");
                    newValue = scanner.next();
                    isSet = true;
                }
                case "3" -> {
                    columnName = "phone";
                    System.out.println("Input new phone number.");
                    newValue = scanner.next();
                    isSet = true;
                }
                default -> System.out.println("Invalid choice. Try again.");
            }
        }
        String statementString = "UPDATE customers " +
                "SET " + columnName + " = ? " +
                "WHERE CustomerID = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(statementString)) {
            preparedStatement.setInt(2, id);
            preparedStatement.setString(1, newValue);
            preparedStatement.execute();
        } catch (SQLException e) {
            System.out.println("Couldn't perform action." + e.getMessage());
        }
    }

    /**
     * prints customer's purchase history by its id in database
     * @param connection The connection to retrieve data from database
     */
    private void customerHistory(Connection connection) {
        int id;
        while (true) {
            System.out.println("Enter id of the customer you want to update.");
            if (scanner.hasNextInt()) {
                id = scanner.nextInt();
                break;
            }
        }
        String historyQuery = "SELECT sales.dateofsale AS DateOfSale, sales.quantitysold as QuantitySold, " +
                "books.title AS BookTitle, books.price AS BookPrice\n" +
                "FROM sales\n" +
                "JOIN books ON books.BookID = sales.BookID\n" +
                "WHERE sales.CustomerID = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(historyQuery)) {
            preparedStatement.setInt(1, id);
            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    String bookTitle = resultSet.getString("BookTitle");
                    LocalDate date = resultSet.getDate("DateOfSale").toLocalDate();
                    float price = resultSet.getFloat("BookPrice");
                    int quantity = resultSet.getInt("QuantitySold");
                    System.out.printf("Title: %-20s, Date: %-10s, Price: %-10.2f, Quantity: %d%n", bookTitle, date, price, quantity);
                }
            }
        } catch (SQLException e) {
            System.out.println("Couldn't perform action." + e.getMessage());
        }
    }

    /**
     * calculates revenue of certain genre
     * @param connection The connection to retrieve data from database
     */
    private void calculateRevenue(Connection connection) {
        System.out.println("Input the genre you want to calculate revenue for.");
        String genre = scanner.nextLine();
        String revenueQuery = "SELECT SUM(sales.totalprice) AS Revenue " +
                "FROM sales " +
                "JOIN books ON sales.BookID = books.BookID " +
                "WHERE books.genre = ? ";
        try (PreparedStatement preparedStatement = connection.prepareStatement(revenueQuery)) {
            preparedStatement.setString(1, genre);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    System.out.println("Genre: " + genre + "  Revenue: " + resultSet.getDouble("Revenue"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Couldn't perform action." + e.getMessage());
        }
    }

    /**
     * gives information about all books that were sold
     * @param connection The connection to retrieve data from database
     */
    private void getBookReport(Connection connection) {
        String reportQuery = "SELECT books.title AS BookTitle, customers.name AS CustomerName, sales.dateofsale AS DateOfSale \n" +
                "FROM sales\n" +
                "JOIN books ON sales.BookID = books.BookID\n" +
                "JOIN customers ON sales.customerid = customers.customerid";
        try (PreparedStatement preparedStatement = connection.prepareStatement(reportQuery);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            System.out.printf("%-30s | %-30s | %-10s%n", "Book Title", "Customer Name", "Date of Sale");
            System.out.println("------------------------------------------------------------------------");
            while (resultSet.next()) {
                String bookTitle = resultSet.getString("BookTitle");
                String customerName = resultSet.getString("CustomerName");
                Date date = resultSet.getDate("DateOfSale");
                System.out.printf("%-30s | %-30s | %-10s%n", bookTitle, customerName, date);
            }
        } catch (SQLException e) {
            System.out.println("Couldn't perform action." + e.getMessage());
        }
    }

    /**
     * gets revenues of all genres in database
     * @param connection The connection to retrieve data from database
     */
    private void getGenreRevenues(Connection connection) {
        String reportQuery = "SELECT Books.Genre, SUM(Sales.TotalPrice) as Revenue\n" +
        "FROM Sales\n" +
        "JOIN Books ON Sales.BookID = Books.BookID\n" +
        "GROUP BY Books.Genre";
        try (PreparedStatement preparedStatement = connection.prepareStatement(reportQuery);
            ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                System.out.printf("Genre: %-20s, Revenue: %f%n", resultSet.getString("genre"), resultSet.getDouble("Revenue"));
            }
        } catch (SQLException e) {
            System.out.println("Couldn't perform action." + e.getMessage());
        }
    }
}
