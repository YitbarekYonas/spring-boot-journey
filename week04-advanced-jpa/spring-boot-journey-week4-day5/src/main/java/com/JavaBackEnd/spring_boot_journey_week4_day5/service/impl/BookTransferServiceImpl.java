package com.JavaBackEnd.spring_boot_journey_week4_day5.service.impl;

import com.JavaBackEnd.spring_boot_journey_week4_day5.entity.BookBranch;
import com.JavaBackEnd.spring_boot_journey_week4_day5.repository.BookBranchRepository;
import com.JavaBackEnd.spring_boot_journey_week4_day5.service.BookTransferService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookTransferServiceImpl implements BookTransferService {

    private final BookBranchRepository bookBranchRepository;

    public BookTransferServiceImpl(BookBranchRepository bookBranchRepository) {
        this.bookBranchRepository = bookBranchRepository;
    }

    // ✅ WITH Transaction - Atomic: ALL or NOTHING
    @Override
    @Transactional
    public String transferBookWithTransaction(Long fromBranchId, Long toBranchId, int copies) {

        System.out.println("\n=== TRANSFER WITH TRANSACTION (Atomic) ===");

        // Step 1: Decrement from branch
        BookBranch fromBranch = bookBranchRepository.findById(fromBranchId)
                .orElseThrow(() -> new RuntimeException("From branch not found"));

        System.out.println("Before decrement - From: " + fromBranch.getCopies());

        if (fromBranch.getCopies() < copies) {
            throw new RuntimeException("Not enough copies at source branch");
        }

        fromBranch.setCopies(fromBranch.getCopies() - copies);
        bookBranchRepository.save(fromBranch);
        System.out.println("After decrement - From: " + fromBranch.getCopies());

        // Step 2: Increment to branch
        BookBranch toBranch = bookBranchRepository.findById(toBranchId)
                .orElseThrow(() -> new RuntimeException("To branch not found"));

        System.out.println("Before increment - To: " + toBranch.getCopies());
        toBranch.setCopies(toBranch.getCopies() + copies);
        bookBranchRepository.save(toBranch);
        System.out.println("After increment - To: " + toBranch.getCopies());

        // Step 3: 🚨 DELIBERATE FAILURE - throws RuntimeException
        System.out.println("💥 Deliberately throwing exception to test rollback...");
        throw new RuntimeException("Simulated failure after increment!");

        // Transaction rolls back - BOTH changes are undone
    }

    // ❌ WITHOUT Transaction - Partial commit possible
    @Override
    public String transferBookWithoutTransaction(Long fromBranchId, Long toBranchId, int copies) {

        System.out.println("\n=== TRANSFER WITHOUT TRANSACTION (Partial) ===");

        // Step 1: Decrement from branch
        BookBranch fromBranch = bookBranchRepository.findById(fromBranchId)
                .orElseThrow(() -> new RuntimeException("From branch not found"));

        System.out.println("Before decrement - From: " + fromBranch.getCopies());

        if (fromBranch.getCopies() < copies) {
            throw new RuntimeException("Not enough copies at source branch");
        }

        fromBranch.setCopies(fromBranch.getCopies() - copies);
        bookBranchRepository.save(fromBranch);
        System.out.println("After decrement - From: " + fromBranch.getCopies());

        // Step 2: Increment to branch
        BookBranch toBranch = bookBranchRepository.findById(toBranchId)
                .orElseThrow(() -> new RuntimeException("To branch not found"));

        System.out.println("Before increment - To: " + toBranch.getCopies());
        toBranch.setCopies(toBranch.getCopies() + copies);
        bookBranchRepository.save(toBranch);
        System.out.println("After increment - To: " + toBranch.getCopies());

        // Step 3: 🚨 DELIBERATE FAILURE
        System.out.println("💥 Deliberately throwing exception...");
        throw new RuntimeException("Simulated failure!");

        // ⚠️ NO TRANSACTION - First change is COMMITTED, second is NOT
        // Database is in inconsistent state!
    }
}