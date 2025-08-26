package dev.moeali.library.repo;

import dev.moeali.library.db.Database;
import dev.moeali.library.model.Book;
import org.jooq.Record;
import org.jooq.Result;

import java.util.ArrayList;
import java.util.List;

import static org.jooq.impl.DSL.*;

public class BookRepository {
    private final Database db;

    public BookRepository(Database db) {
        this.db = db;
    }

    public long add(String title, String author) {
        var id = db.dsl().insertInto(table("BOOK"))
                .columns(field("TITLE"), field("AUTHOR"), field("AVAILABLE"))
                .values(title, author, 1)
                .returningResult(field("ID"))
                .fetchOne(0, Long.class);
        return id == null ? -1L : id;
    }

    public List<Book> listAll() {
        Result<Record> r = db.dsl().select().from("BOOK").fetch();
        List<Book> out = new ArrayList<>();
        for (Record rec : r) {
            out.add(new Book(
                    rec.get("ID", Long.class),
                    rec.get("TITLE", String.class),
                    rec.get("AUTHOR", String.class),
                    rec.get("AVAILABLE", Integer.class) == 1
            ));
        }
        return out;
    }

    public void setAvailability(long bookId, boolean available) {
        db.dsl().update(table("BOOK"))
                .set(field("AVAILABLE"), available ? 1 : 0)
                .where(field("ID").eq(bookId))
                .execute();
    }
}


