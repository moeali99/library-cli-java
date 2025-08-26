package dev.moeali.library.service;

import dev.moeali.library.model.Book;
import dev.moeali.library.repo.BookRepository;
import dev.moeali.library.repo.LoanRepository;

import java.util.List;

public class LibraryService {
    private final BookRepository books;
    private final LoanRepository loans;

    public LibraryService(BookRepository books, LoanRepository loans) {
        this.books = books;
        this.loans = loans;
    }

    public long addBook(String title, String author) {
        return books.add(title, author);
    }

    public List<Book> listBooks() { return books.listAll(); }

    public boolean borrow(long bookId, String user) {
        for (Book b : books.listAll()) {
            if (b.id() == bookId && b.available()) {
                books.setAvailability(bookId, false);
                loans.createLoan(bookId, user);
                return true;
            }
        }
        return false;
    }

    public boolean giveBack(long bookId) {
        for (Book b : books.listAll()) {
            if (b.id() == bookId && !b.available()) {
                books.setAvailability(bookId, true);
                loans.closeLoan(bookId);
                return true;
            }
        }
        return false;
    }
}


