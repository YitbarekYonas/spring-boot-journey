package com.JavaBackEnd.spring_boot_journey_week7_day1.service;

import com.JavaBackEnd.spring_boot_journey_week7_day1.model.Task;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

// ── Pure Java — no Spring annotations ────────────────────────────────────
// This is the class we will write unit tests for today.
// It has clear inputs → outputs, multiple branches, and edge cases —
// exactly the kind of class that benefits most from unit testing.
public class TaskPricingCalculator {

    // Base cost per task type (in USD)
    private static final BigDecimal BUG_BASE           = new BigDecimal("50.00");
    private static final BigDecimal FEATURE_BASE       = new BigDecimal("100.00");
    private static final BigDecimal DOCUMENTATION_BASE = new BigDecimal("30.00");
    private static final BigDecimal REFACTOR_BASE      = new BigDecimal("75.00");

    // Priority multipliers
    private static final BigDecimal LOW_MULTIPLIER    = new BigDecimal("1.0");
    private static final BigDecimal MEDIUM_MULTIPLIER = new BigDecimal("1.5");
    private static final BigDecimal HIGH_MULTIPLIER   = new BigDecimal("2.0");

    // Overdue penalty — 20% surcharge
    private static final BigDecimal OVERDUE_SURCHARGE = new BigDecimal("0.20");

    // Bulk discount — 10% off when 5 or more tasks
    private static final int        BULK_THRESHOLD    = 5;
    private static final BigDecimal BULK_DISCOUNT     = new BigDecimal("0.10");

    // ── calculatePrice() ──────────────────────────────────────────────────
    // Calculates cost for a single task.
    // Formula: basePrice × priorityMultiplier [+ 20% if overdue]
    public BigDecimal calculatePrice(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("Task cannot be null");
        }
        if (task.getType() == null) {
            throw new IllegalArgumentException("Task type cannot be null");
        }
        if (task.getPriority() == null) {
            throw new IllegalArgumentException("Task priority cannot be null");
        }

        BigDecimal base       = getBasePrice(task.getType());
        BigDecimal multiplier = getMultiplier(task.getPriority());
        BigDecimal price      = base.multiply(multiplier);

        // Add overdue surcharge
        if (task.isOverdue()) {
            BigDecimal surcharge = price.multiply(OVERDUE_SURCHARGE);
            price = price.add(surcharge);
        }

        return price.setScale(2, RoundingMode.HALF_UP);
    }

    // ── calculateBulkPrice() ──────────────────────────────────────────────
    // Total cost for a list of tasks, with bulk discount applied when >= 5 tasks.
    public BigDecimal calculateBulkPrice(List<Task> tasks) {
        if (tasks == null || tasks.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal total = tasks.stream()
                .map(this::calculatePrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Apply bulk discount if threshold reached
        if (tasks.size() >= BULK_THRESHOLD) {
            BigDecimal discount = total.multiply(BULK_DISCOUNT);
            total = total.subtract(discount);
        }

        return total.setScale(2, RoundingMode.HALF_UP);
    }

    // ── applyDiscount() ───────────────────────────────────────────────────
    public BigDecimal applyDiscount(BigDecimal price, int discountPercent) {
        if (price == null || price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Price must be non-negative");
        }
        if (discountPercent < 0 || discountPercent > 100) {
            throw new IllegalArgumentException("Discount must be between 0 and 100");
        }

        BigDecimal discountFraction = new BigDecimal(discountPercent)
                .divide(new BigDecimal("100"));
        BigDecimal discountAmount = price.multiply(discountFraction);
        return price.subtract(discountAmount).setScale(2, RoundingMode.HALF_UP);
    }

    // ── Helpers ───────────────────────────────────────────────────────────
    private BigDecimal getBasePrice(Task.Type type) {
        return switch (type) {
            case BUG           -> BUG_BASE;
            case FEATURE       -> FEATURE_BASE;
            case DOCUMENTATION -> DOCUMENTATION_BASE;
            case REFACTOR      -> REFACTOR_BASE;
        };
    }

    private BigDecimal getMultiplier(Task.Priority priority) {
        return switch (priority) {
            case LOW    -> LOW_MULTIPLIER;
            case MEDIUM -> MEDIUM_MULTIPLIER;
            case HIGH   -> HIGH_MULTIPLIER;
        };
    }
}
