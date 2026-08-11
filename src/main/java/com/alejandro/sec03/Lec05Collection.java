package com.alejandro.sec03;

import com.alejandro.models.sec03.Book;
import com.alejandro.models.sec03.Library;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

public class Lec05Collection {

    private static final Logger log = LoggerFactory.getLogger(Lec05Collection.class);

    static void main() {

        var book1 = Book.newBuilder()
                .setTitle("harry potter - part 1")
                .setAuthor("j k rowling")
                .setPublicationYear(1997)
                .build();
        var book2 = Book.newBuilder()
                .setTitle("harry potter - part 2")
                .setAuthor("j k rowling")
                .setPublicationYear(1998)
                .build();
        var book3 = Book.newBuilder()
                .setTitle("harry potter - part 3")
                .setAuthor("j k rowling")
                .setPublicationYear(1999)
                .build();

        var library = Library.newBuilder()
                .setName("fantasy library")
//                .addBooks(book1)
//                .addBooks(book2)
//                .addBooks(book3)
                .addAllBooks(Set.of(book1, book2, book3))
                .build();

        log.info("{}", library.getBooksOrBuilderList());

    }
}
