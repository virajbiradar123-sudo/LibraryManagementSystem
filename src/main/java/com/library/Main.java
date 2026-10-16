package com.library;

import java.util.Scanner;

import com.library.dao.LibraryDAO;

public class Main {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        LibraryDAO dao = new LibraryDAO();

        int choice = 0;

        System.out.println("====================================");
        System.out.println("   LIBRARY MANAGEMENT SYSTEM");
        System.out.println("====================================");

        do {

            System.out.println("\n========== MAIN MENU ==========");
            System.out.println("1. Add Book");
            System.out.println("2. View All Books");
            System.out.println("3. Search Book");
            System.out.println("4. Register Member");
            System.out.println("5. View Members");
            System.out.println("6. Issue Book");
            System.out.println("7. Return Book");
            System.out.println("8. View Issue History");
            System.out.println("9. Exit");

            System.out.print("Enter your choice: ");

            try {

                choice = Integer.parseInt(sc.nextLine());

                switch (choice) {

                    case 1:
                        dao.addBook(sc);
                        break;

                    case 2:
                        dao.viewBooks();
                        break;

                    case 3:
                        dao.searchBook(sc);
                        break;

                    case 4:
                        dao.registerMember(sc);
                        break;

                    case 5:
                        dao.viewMembers();
                        break;

                    case 6:
                        dao.issueBook(sc);
                        break;

                    case 7:
                        dao.returnBook(sc);
                        break;

                    case 8:
                        dao.viewIssueHistory();
                        break;

                    case 9:
                        System.out.println(
                                "Thank you for using Library Management System!");
                        break;

                    default:
                        System.out.println(
                                "Invalid choice. Please enter 1 to 9.");
                }

            } catch (NumberFormatException e) {

                System.out.println(
                        "Invalid input. Please enter a number.");

            }

        } while (choice != 9);

        sc.close();
    }
}