package com.JavaBackEnd.spring_boot_journey_week4_day2.projection;

public interface MemberLoanStats {
    Long getMemberId();
    String getMemberName();
    String getMemberEmail();
    Long getTotalLoans();
    Long getActiveLoans();
    Long getOverdueLoans();
    Long getReturnedLoans();
}