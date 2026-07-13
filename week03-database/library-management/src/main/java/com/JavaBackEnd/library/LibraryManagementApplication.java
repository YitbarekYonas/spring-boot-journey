package com.JavaBackEnd.library;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class LibraryManagementApplication {
    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(LibraryManagementApplication.class, args);

        System.out.println("\n✅ Library Management Application Started Successfully!");
        System.out.println("📊 Beans: " + context.getBeanDefinitionCount());
        System.out.println("\n📋 Available Endpoints:");
        System.out.println("  GET  /api/books");
        System.out.println("  GET  /api/books/{id}");
        System.out.println("  POST /api/books?authorId=1");
        System.out.println("  GET  /api/books/search?keyword=clean");
        System.out.println("  GET  /api/books/available");
        System.out.println("  GET  /api/books/genre/{genre}");
        System.out.println("  GET  /api/loans");
        System.out.println("  GET  /api/loans/overdue");
        System.out.println("  GET  /api/loans/member/{memberId}");
        System.out.println("  POST /api/loans/checkout?bookId=1&memberId=1");
        System.out.println("  POST /api/loans/{loanId}/return");
        System.out.println("  POST /api/loans/mark-overdue");
    }
}