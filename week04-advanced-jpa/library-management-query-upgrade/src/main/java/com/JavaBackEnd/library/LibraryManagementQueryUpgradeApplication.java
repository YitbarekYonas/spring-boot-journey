package com.JavaBackEnd.library;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
// ❌ Remove this import
// import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
// ❌ Remove this annotation
// @EnableJpaAuditing
public class LibraryManagementQueryUpgradeApplication {
    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(LibraryManagementQueryUpgradeApplication.class, args);

        System.out.println("\n✅ Library Management Query Layer Upgrade Started!");
        System.out.println("📊 Beans: " + context.getBeanDefinitionCount());
        System.out.println("\n📋 Paginated Endpoints:");
        System.out.println("  GET  /api/books?page=0&size=10&sort=title,asc");
        System.out.println("  GET  /api/books/search?keyword=clean&page=0&size=5");
        System.out.println("  GET  /api/books/genre/Software%20Engineering?page=0&size=5");
        System.out.println("  GET  /api/books/available");
        System.out.println("  GET  /api/loans?status=ACTIVE&page=0&size=10");
        System.out.println("  GET  /api/loans/overdue");
        System.out.println("  GET  /api/loans/member/1");
        System.out.println("  GET  /api/loans/stats/books");
        System.out.println("  POST /api/loans/checkout?bookId=1&memberId=1");
        System.out.println("  POST /api/loans/1/return");
    }
}