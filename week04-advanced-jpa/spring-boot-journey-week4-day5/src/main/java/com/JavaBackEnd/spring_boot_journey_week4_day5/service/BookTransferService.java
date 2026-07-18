package com.JavaBackEnd.spring_boot_journey_week4_day5.service;

public interface BookTransferService {

    // WITH Transaction - should rollback on failure
    String transferBookWithTransaction(Long fromBranchId, Long toBranchId, int copies);

    // WITHOUT Transaction - will commit partial state on failure
    String transferBookWithoutTransaction(Long fromBranchId, Long toBranchId, int copies);
}