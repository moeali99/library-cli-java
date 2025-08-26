package dev.moeali.library.repo;

import dev.moeali.library.db.Database;

import java.time.LocalDate;

import static org.jooq.impl.DSL.*;

public class LoanRepository {
    private final Database db;

    public LoanRepository(Database db) {
        this.db = db;
    }

    public void createLoan(long bookId, String userName) {
        db.dsl().insertInto(table("LOAN"))
                .columns(field("BOOK_ID"), field("USER_NAME"), field("LOAN_DATE"))
                .values(bookId, userName, LocalDate.now().toString())
                .execute();
    }

    public void closeLoan(long bookId) {
        db.dsl().update(table("LOAN"))
                .set(field("RETURN_DATE"), LocalDate.now().toString())
                .where(field("BOOK_ID").eq(bookId).and(field("RETURN_DATE").isNull()))
                .execute();
    }
}


