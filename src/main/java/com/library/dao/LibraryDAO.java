package com.library.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.sql.Statement;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import java.util.Scanner;

import com.library.util.DBConnection;

public class LibraryDAO {

    private static final double FINE_PER_DAY = 5.0;

    // 1. ADD BOOK
    public void addBook(Scanner sc) {

        System.out.print("Enter book title: ");
        String title = sc.nextLine();

        System.out.print("Enter author name: ");
        String author = sc.nextLine();

        System.out.print("Enter category: ");
        String category = sc.nextLine();

        System.out.print("Enter number of copies: ");
        int copies = Integer.parseInt(sc.nextLine());

        if (copies <= 0) {
            System.out.println("Copies must be greater than zero.");
            return;
        }

        String sql = "INSERT INTO books "
                + "(title, author, category, total_copies, available_copies) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, title);
            ps.setString(2, author);
            ps.setString(3, category);
            ps.setInt(4, copies);
            ps.setInt(5, copies);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("Book added successfully!");
            }

        } catch (SQLException e) {
            System.out.println("Error adding book: " + e.getMessage());
        }
    }


    // 2. VIEW ALL BOOKS
    public void viewBooks() {

        String sql = "SELECT * FROM books ORDER BY book_id";

        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            System.out.println("\n========== BOOK LIST ==========");

            while (rs.next()) {

                System.out.println(
                    "ID: " + rs.getInt("book_id")
                    + " | Title: " + rs.getString("title")
                    + " | Author: " + rs.getString("author")
                    + " | Category: " + rs.getString("category")
                    + " | Total: " + rs.getInt("total_copies")
                    + " | Available: " + rs.getInt("available_copies")
                );
            }

        } catch (SQLException e) {
            System.out.println("Error displaying books: " + e.getMessage());
        }
    }


    // 3. SEARCH BOOK
    public void searchBook(Scanner sc) {

        System.out.print("Enter book title or author to search: ");
        String keyword = sc.nextLine();

        String sql = "SELECT * FROM books "
                + "WHERE title LIKE ? OR author LIKE ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");

            try (ResultSet rs = ps.executeQuery()) {

                boolean found = false;

                while (rs.next()) {

                    found = true;

                    System.out.println(
                        "ID: " + rs.getInt("book_id")
                        + " | Title: " + rs.getString("title")
                        + " | Author: " + rs.getString("author")
                        + " | Available copies: "
                        + rs.getInt("available_copies")
                    );
                }

                if (!found) {
                    System.out.println("No matching books found.");
                }
            }

        } catch (SQLException e) {
            System.out.println("Error searching books: " + e.getMessage());
        }
    }


    // 4. REGISTER MEMBER
    public void registerMember(Scanner sc) {

        System.out.print("Enter member name: ");
        String name = sc.nextLine();

        System.out.print("Enter email: ");
        String email = sc.nextLine();

        System.out.print("Enter phone number: ");
        String phone = sc.nextLine();

        String sql = "INSERT INTO members "
                + "(member_name, email, phone) VALUES (?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, phone);

            ps.executeUpdate();

            System.out.println("Member registered successfully!");

        } catch (SQLException e) {
            System.out.println("Error registering member: " + e.getMessage());
        }
    }


    // 5. VIEW MEMBERS
    public void viewMembers() {

        String sql = "SELECT * FROM members ORDER BY member_id";

        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            System.out.println("\n========== MEMBER LIST ==========");

            while (rs.next()) {

                System.out.println(
                    "Member ID: " + rs.getInt("member_id")
                    + " | Name: " + rs.getString("member_name")
                    + " | Email: " + rs.getString("email")
                    + " | Phone: " + rs.getString("phone")
                );
            }

        } catch (SQLException e) {
            System.out.println("Error displaying members: " + e.getMessage());
        }
    }


    // 6. ISSUE BOOK
    public void issueBook(Scanner sc) {

        System.out.print("Enter book ID: ");
        int bookId = Integer.parseInt(sc.nextLine());

        System.out.print("Enter member ID: ");
        int memberId = Integer.parseInt(sc.nextLine());

        String checkBook = "SELECT available_copies FROM books "
                + "WHERE book_id = ? FOR UPDATE";

        String checkMember = "SELECT member_id FROM members "
                + "WHERE member_id = ?";

        String issueSql = "INSERT INTO book_issues "
                + "(book_id, member_id, issue_date, due_date, status, fine) "
                + "VALUES (?, ?, ?, ?, 'ISSUED', 0)";

        String updateBook = "UPDATE books "
                + "SET available_copies = available_copies - 1 "
                + "WHERE book_id = ? AND available_copies > 0";

        try (Connection con = DBConnection.getConnection()) {

            con.setAutoCommit(false);

            try {

                // Check book availability and lock its row.
                try (PreparedStatement ps =
                             con.prepareStatement(checkBook)) {

                    ps.setInt(1, bookId);

                    try (ResultSet rs = ps.executeQuery()) {

                        if (!rs.next()) {
                            throw new SQLException("Book ID not found.");
                        }

                        if (rs.getInt("available_copies") <= 0) {
                            throw new SQLException(
                                    "No copies available for this book.");
                        }
                    }
                }

                // Check that the member exists.
                try (PreparedStatement ps =
                             con.prepareStatement(checkMember)) {

                    ps.setInt(1, memberId);

                    try (ResultSet rs = ps.executeQuery()) {

                        if (!rs.next()) {
                            throw new SQLException("Member ID not found.");
                        }
                    }
                }

                LocalDate issueDate = LocalDate.now();
                LocalDate dueDate = issueDate.plusDays(14);

                // Insert issue record.
                try (PreparedStatement ps =
                             con.prepareStatement(issueSql)) {

                    ps.setInt(1, bookId);
                    ps.setInt(2, memberId);
                    ps.setDate(3, Date.valueOf(issueDate));
                    ps.setDate(4, Date.valueOf(dueDate));

                    ps.executeUpdate();
                }

                // Update book availability.
                try (PreparedStatement ps =
                             con.prepareStatement(updateBook)) {

                    ps.setInt(1, bookId);

                    if (ps.executeUpdate() != 1) {
                        throw new SQLException(
                                "Could not update book availability.");
                    }
                }

                con.commit();

                System.out.println("Book issued successfully!");
                System.out.println("Issue date: " + issueDate);
                System.out.println("Due date: " + dueDate);

            } catch (Exception e) {

                con.rollback();

                System.out.println(
                        "Issue failed. Transaction rolled back: "
                        + e.getMessage());
            }

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }


    // 7. RETURN BOOK AND CALCULATE FINE
    public void returnBook(Scanner sc) {

        System.out.print("Enter issue ID: ");
        int issueId = Integer.parseInt(sc.nextLine());

        String findIssue = "SELECT book_id, due_date, status "
                + "FROM book_issues WHERE issue_id = ? FOR UPDATE";

        String returnSql = "UPDATE book_issues "
                + "SET return_date = ?, fine = ?, status = 'RETURNED' "
                + "WHERE issue_id = ? AND status = 'ISSUED'";

        String updateBook = "UPDATE books "
                + "SET available_copies = available_copies + 1 "
                + "WHERE book_id = ?";

        try (Connection con = DBConnection.getConnection()) {

            con.setAutoCommit(false);

            try {

                int bookId;
                LocalDate dueDate;

                // Find and lock the issue record.
                try (PreparedStatement ps =
                             con.prepareStatement(findIssue)) {

                    ps.setInt(1, issueId);

                    try (ResultSet rs = ps.executeQuery()) {

                        if (!rs.next()) {
                            throw new SQLException("Issue ID not found.");
                        }

                        if (!"ISSUED".equals(rs.getString("status"))) {
                            throw new SQLException(
                                    "This book has already been returned.");
                        }

                        bookId = rs.getInt("book_id");
                        dueDate = rs.getDate("due_date").toLocalDate();
                    }
                }

                LocalDate returnDate = LocalDate.now();

                long lateDays = Math.max(
                        0,
                        ChronoUnit.DAYS.between(dueDate, returnDate)
                );

                double fine = lateDays * FINE_PER_DAY;

                // Mark the issue as returned.
                try (PreparedStatement ps =
                             con.prepareStatement(returnSql)) {

                    ps.setDate(1, Date.valueOf(returnDate));
                    ps.setDouble(2, fine);
                    ps.setInt(3, issueId);

                    if (ps.executeUpdate() != 1) {
                        throw new SQLException(
                                "Could not update return record.");
                    }
                }

                // Increase the available book count.
                try (PreparedStatement ps =
                             con.prepareStatement(updateBook)) {

                    ps.setInt(1, bookId);

                    if (ps.executeUpdate() != 1) {
                        throw new SQLException(
                                "Could not update book availability.");
                    }
                }

                con.commit();

                System.out.println("Book returned successfully!");
                System.out.println("Return date: " + returnDate);
                System.out.println("Late days: " + lateDays);
                System.out.println("Fine amount: Rs. " + fine);

            } catch (Exception e) {

                con.rollback();

                System.out.println(
                        "Return failed. Transaction rolled back: "
                        + e.getMessage());
            }

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }


    // 8. VIEW ISSUE HISTORY USING JOIN
    public void viewIssueHistory() {

        String sql = "SELECT bi.issue_id, b.title, m.member_name, "
                + "bi.issue_date, bi.due_date, bi.return_date, "
                + "bi.fine, bi.status "
                + "FROM book_issues bi "
                + "JOIN books b ON bi.book_id = b.book_id "
                + "JOIN members m ON bi.member_id = m.member_id "
                + "ORDER BY bi.issue_id DESC";

        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            System.out.println("\n========== ISSUE HISTORY ==========");

            while (rs.next()) {

                System.out.println(
                    "Issue ID: " + rs.getInt("issue_id")
                    + " | Book: " + rs.getString("title")
                    + " | Member: " + rs.getString("member_name")
                    + " | Issue date: " + rs.getDate("issue_date")
                    + " | Due date: " + rs.getDate("due_date")
                    + " | Return date: " + rs.getDate("return_date")
                    + " | Fine: Rs. " + rs.getDouble("fine")
                    + " | Status: " + rs.getString("status")
                );
            }

        } catch (SQLException e) {
            System.out.println("Error displaying history: " + e.getMessage());
        }
    }
}