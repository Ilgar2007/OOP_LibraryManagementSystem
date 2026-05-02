package org.example;

import java.util.Date;

public interface Search {
    String searchByTitle(String title);
    String searchByAuthor(String authorName);
    String searchBySubject(String subject);
    Date searchByPubDate(Date pubDate);
}