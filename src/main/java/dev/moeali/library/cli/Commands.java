package dev.moeali.library.cli;

import dev.moeali.library.db.Database;
import dev.moeali.library.repo.BookRepository;
import dev.moeali.library.repo.LoanRepository;
import dev.moeali.library.service.LibraryService;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParentCommand;
import picocli.CommandLine;

@Command(name = "library", mixinStandardHelpOptions = true, version = "0.1", description = "Bibliotheks-CLI", subcommands = {
        Commands.AddBook.class,
        Commands.ListBooks.class,
        Commands.Borrow.class,
        Commands.Return.class
})
public class Commands implements Runnable {
    @Option(names = {"-d", "--db"}, defaultValue = "db/library.db")
    String dbPath;

    public void run() { new CommandLine(this).usage(System.out); }

    private LibraryService service() {
        Database db = new Database(dbPath);
        db.connect();
        return new LibraryService(new BookRepository(db), new LoanRepository(db));
    }

    @Command(name = "add-book", description = "Buch hinzufügen")
    static class AddBook implements Runnable {
        @ParentCommand Commands parent;
        @Option(names = "--title", required = true) String title;
        @Option(names = "--author", required = true) String author;
        public void run() {
            long id = parent.service().addBook(title, author);
            System.out.println("Added book id=" + id);
        }
    }

    @Command(name = "list-books", description = "Bücher auflisten")
    static class ListBooks implements Runnable {
        @ParentCommand Commands parent;
        public void run() {
            parent.service().listBooks().forEach(b -> System.out.println(b.id() + ": " + b.title() + " / " + b.author() + (b.available()?" [frei]":" [verliehen]")));
        }
    }

    @Command(name = "borrow", description = "Buch ausleihen")
    static class Borrow implements Runnable {
        @ParentCommand Commands parent;
        @Option(names = "--id", required = true) long id;
        @Option(names = "--user", required = true) String user;
        public void run() {
            System.out.println(parent.service().borrow(id, user) ? "OK" : "Nicht verfügbar");
        }
    }

    @Command(name = "return", description = "Buch zurückgeben")
    static class Return implements Runnable {
        @ParentCommand Commands parent;
        @Option(names = "--id", required = true) long id;
        public void run() {
            System.out.println(parent.service().giveBack(id) ? "OK" : "Nicht gefunden/war nicht verliehen");
        }
    }
}


