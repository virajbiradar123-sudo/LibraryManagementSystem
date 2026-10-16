CREATE DATABASE library_db;

USE library_db;

-- 1. Books table
CREATE TABLE books (
    book_id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(100) NOT NULL,
    author VARCHAR(100) NOT NULL,
    category VARCHAR(50),
    total_copies INT NOT NULL,
    available_copies INT NOT NULL
);

-- 2. Members table
CREATE TABLE members (
    member_id INT PRIMARY KEY AUTO_INCREMENT,
    member_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(15)
);

-- 3. Book issues table
CREATE TABLE book_issues (
    issue_id INT PRIMARY KEY AUTO_INCREMENT,
    book_id INT NOT NULL,
    member_id INT NOT NULL,
    issue_date DATE NOT NULL,
    due_date DATE NOT NULL,
    return_date DATE,
    fine DECIMAL(10,2) DEFAULT 0,
    status VARCHAR(20) DEFAULT 'ISSUED',
    FOREIGN KEY (book_id) REFERENCES books(book_id),
    FOREIGN KEY (member_id) REFERENCES members(member_id)
);

-- Sample books
INSERT INTO books
(title, author, category, total_copies, available_copies)
VALUES
('Java Basics', 'James Gosling', 'Programming', 5, 5),
('SQL Fundamentals', 'John Smith', 'Database', 3, 3),
('Spring Boot Intro', 'Mark Heckler', 'Programming', 2, 2);

-- Sample members
INSERT INTO members (member_name, email, phone)
VALUES
('Asha', 'asha@example.com', '9876543210'),
('Rahul', 'rahul@example.com', '9876543211'),
('Priya', 'priya@example.com', '9876543212');

-- Verify the data
SELECT * FROM books;
SELECT * FROM members;
SELECT * FROM book_issues;

USE library_db;

SHOW TABLES;

SELECT * FROM books;

SELECT * FROM members;

SELECT * FROM book_issues;
USE library_db;

SELECT * FROM books;

SELECT * FROM members;

SELECT * FROM book_issues;