package com.JavaBackEnd.spring_boot_journey_week4_day5.controller;

import com.JavaBackEnd.spring_boot_journey_week4_day5.entity.BookBranch;
import com.JavaBackEnd.spring_boot_journey_week4_day5.repository.BookBranchRepository;
import com.JavaBackEnd.spring_boot_journey_week4_day5.service.BookTransferService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transfer")
public class BookTransferController {

    private final BookTransferService bookTransferService;
    private final BookBranchRepository bookBranchRepository;

    public BookTransferController(BookTransferService bookTransferService,
                                  BookBranchRepository bookBranchRepository) {
        this.bookTransferService = bookTransferService;
        this.bookBranchRepository = bookBranchRepository;
    }

    @GetMapping("/branches")
    public ResponseEntity<List<BookBranch>> getAllBranches() {
        return ResponseEntity.ok(bookBranchRepository.findAll());
    }

    @PostMapping("/with-transaction")
    public ResponseEntity<String> transferWithTransaction(
            @RequestParam Long from,
            @RequestParam Long to,
            @RequestParam int copies) {

        try {
            String result = bookTransferService.transferBookWithTransaction(from, to, copies);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.ok("❌ FAILED: " + e.getMessage() + " - Transaction rolled back");
        }
    }

    @PostMapping("/without-transaction")
    public ResponseEntity<String> transferWithoutTransaction(
            @RequestParam Long from,
            @RequestParam Long to,
            @RequestParam int copies) {

        try {
            String result = bookTransferService.transferBookWithoutTransaction(from, to, copies);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.ok("⚠️ FAILED: " + e.getMessage() + " - Partial commit occurred!");
        }
    }
}