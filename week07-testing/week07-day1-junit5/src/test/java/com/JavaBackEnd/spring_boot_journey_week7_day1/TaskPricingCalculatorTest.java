package com.JavaBackEnd.spring_boot_journey_week7_day1;

import com.JavaBackEnd.spring_boot_journey_week7_day1.model.Task;
import com.JavaBackEnd.spring_boot_journey_week7_day1.service.TaskPricingCalculator;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

// ── No @SpringBootTest here ────────────────────────────────────────────────
// Pure unit test — no Spring context loaded.
// This means tests start in milliseconds, not 3-5 seconds.
// Rule: if your test doesn't need HTTP, DB, or Spring beans → don't load Spring.
@DisplayName("TaskPricingCalculator")   // shown in test report instead of class name
class TaskPricingCalculatorTest {

    // ── Test Lifecycle ─────────────────────────────────────────────────────
    //
    // @BeforeAll  — runs ONCE before ALL tests in this class (must be static)
    // @BeforeEach — runs before EACH individual test method
    // @AfterEach  — runs after EACH individual test method
    // @AfterAll   — runs ONCE after ALL tests in this class (must be static)
    //
    // Use @BeforeEach to set up fresh objects for each test.
    // Never share mutable state between tests — tests must be independent.

    private TaskPricingCalculator calculator;   // created fresh for each test

    @BeforeAll
    static void initAll() {
        // Runs once — good for expensive setup (DB connections, static config).
        // For simple unit tests you won't use this much, but know it exists.
        System.out.println("▶ Starting TaskPricingCalculatorTest suite");
    }

    @BeforeEach
    void setUp() {
        // Runs before each @Test — gives every test a fresh calculator.
        // If one test breaks the calculator state, it doesn't affect others.
        calculator = new TaskPricingCalculator();
    }

    @AfterEach
    void tearDown() {
        // Runs after each @Test — good for releasing resources, clearing files, etc.
        // For this simple class there's nothing to clean up, but the pattern matters.
        calculator = null;
    }

    @AfterAll
    static void tearDownAll() {
        System.out.println("✅ TaskPricingCalculatorTest suite complete");
    }

    // ════════════════════════════════════════════════════════════════════════
    // SECTION 1 — Basic @Test with JUnit Assertions
    // ════════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("LOW priority BUG task should cost $50.00")
    void lowPriorityBugCosts50() {
        Task task = Task.builder()
                .type(Task.Type.BUG)
                .priority(Task.Priority.LOW)
                .build();

        BigDecimal price = calculator.calculatePrice(task);

        // assertEquals(expected, actual) — most common JUnit assertion
        assertEquals(new BigDecimal("50.00"), price);
    }

    @Test
    @DisplayName("HIGH priority FEATURE task should cost $200.00")
    void highPriorityFeatureCosts200() {
        Task task = Task.builder()
                .type(Task.Type.FEATURE)
                .priority(Task.Priority.HIGH)
                .build();

        BigDecimal price = calculator.calculatePrice(task);

        assertEquals(new BigDecimal("200.00"), price);
        // FEATURE base=$100, HIGH multiplier=2.0 → $200.00
    }

    @Test
    @DisplayName("MEDIUM priority REFACTOR task should cost $112.50")
    void mediumPriorityRefactorCosts112_50() {
        Task task = Task.builder()
                .type(Task.Type.REFACTOR)
                .priority(Task.Priority.MEDIUM)
                .build();

        BigDecimal price = calculator.calculatePrice(task);

        assertEquals(new BigDecimal("112.50"), price);
        // REFACTOR base=$75, MEDIUM multiplier=1.5 → $112.50
    }

    @Test
    @DisplayName("Overdue task should have 20% surcharge applied")
    void overdueTaskHas20PercentSurcharge() {
        Task task = Task.builder()
                .type(Task.Type.BUG)
                .priority(Task.Priority.LOW)
                .dueDate(LocalDate.now().minusDays(1))  // yesterday → overdue
                .completed(false)
                .build();

        BigDecimal price = calculator.calculatePrice(task);

        // BUG LOW = $50.00, + 20% = $60.00
        assertEquals(new BigDecimal("60.00"), price);
    }

    @Test
    @DisplayName("Completed overdue task should NOT have surcharge")
    void completedOverdueTaskHasNoSurcharge() {
        Task task = Task.builder()
                .type(Task.Type.BUG)
                .priority(Task.Priority.LOW)
                .dueDate(LocalDate.now().minusDays(1))
                .completed(true)    // completed → isOverdue() returns false
                .build();

        BigDecimal price = calculator.calculatePrice(task);

        assertEquals(new BigDecimal("50.00"), price);  // no surcharge
    }

    // ════════════════════════════════════════════════════════════════════════
    // SECTION 2 — assertThrows (testing exception cases)
    // ════════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Null task should throw IllegalArgumentException")
    void nullTaskThrowsException() {
        // assertThrows(ExpectedException.class, () -> code that should throw)
        // Returns the exception so you can assert on its message too
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> calculator.calculatePrice(null)
        );

        assertEquals("Task cannot be null", ex.getMessage());
    }

    @Test
    @DisplayName("Task with null type should throw IllegalArgumentException")
    void nullTypeThrowsException() {
        Task task = Task.builder()
                .priority(Task.Priority.HIGH)
                .type(null)    // missing type
                .build();

        assertThrows(IllegalArgumentException.class,
                () -> calculator.calculatePrice(task));
    }

    @Test
    @DisplayName("Discount above 100 should throw IllegalArgumentException")
    void discountAbove100Throws() {
        assertThrows(IllegalArgumentException.class,
                () -> calculator.applyDiscount(new BigDecimal("100"), 101));
    }

    @Test
    @DisplayName("Negative discount should throw IllegalArgumentException")
    void negativeDiscountThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> calculator.applyDiscount(new BigDecimal("100"), -1));
    }

    // ════════════════════════════════════════════════════════════════════════
    // SECTION 3 — AssertJ fluent assertions (more readable than JUnit's)
    // ════════════════════════════════════════════════════════════════════════
    //   JUnit:   assertEquals(new BigDecimal("200.00"), price)
    //   AssertJ: assertThat(price).isEqualByComparingTo(new BigDecimal("200.00"))
    //
    // AssertJ reads like English and gives better failure messages.

    @Test
    @DisplayName("AssertJ: bulk price with discount applied for 5+ tasks")
    void bulkPriceAppliesDiscountForFiveOrMoreTasks() {
        Task bugHigh  = Task.builder().type(Task.Type.BUG).priority(Task.Priority.HIGH).build();
        Task featMed  = Task.builder().type(Task.Type.FEATURE).priority(Task.Priority.MEDIUM).build();
        Task docLow   = Task.builder().type(Task.Type.DOCUMENTATION).priority(Task.Priority.LOW).build();
        Task refMed   = Task.builder().type(Task.Type.REFACTOR).priority(Task.Priority.MEDIUM).build();
        Task bugLow   = Task.builder().type(Task.Type.BUG).priority(Task.Priority.LOW).build();

        List<Task> tasks = List.of(bugHigh, featMed, docLow, refMed, bugLow);
        // BUG HIGH=$100, FEAT MED=$150, DOC LOW=$30, REF MED=$112.50, BUG LOW=$50
        // Total = $442.50, 10% off = $398.25

        BigDecimal bulk = calculator.calculateBulkPrice(tasks);

        // AssertJ — chaining multiple assertions on the same subject
        assertThat(bulk)
                .isNotNull()
                .isGreaterThan(BigDecimal.ZERO)
                .isLessThan(new BigDecimal("442.50"))       // confirms discount was applied
                .isEqualByComparingTo(new BigDecimal("398.25"));
    }

    @Test
    @DisplayName("AssertJ: empty task list returns zero")
    void emptyListReturnsZero() {
        BigDecimal result = calculator.calculateBulkPrice(List.of());

        assertThat(result)
                .isNotNull()
                .isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("AssertJ: null list returns zero")
    void nullListReturnsZero() {
        BigDecimal result = calculator.calculateBulkPrice(null);

        assertThat(result).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("AssertJ: less than 5 tasks have no bulk discount")
    void fourTasksHaveNoBulkDiscount() {
        Task t1 = Task.builder().type(Task.Type.BUG).priority(Task.Priority.LOW).build();  // $50
        Task t2 = Task.builder().type(Task.Type.BUG).priority(Task.Priority.LOW).build();  // $50
        Task t3 = Task.builder().type(Task.Type.BUG).priority(Task.Priority.LOW).build();  // $50
        Task t4 = Task.builder().type(Task.Type.BUG).priority(Task.Priority.LOW).build();  // $50

        BigDecimal total = calculator.calculateBulkPrice(List.of(t1, t2, t3, t4));

        assertThat(total).isEqualByComparingTo(new BigDecimal("200.00")); // no discount
    }

    // ════════════════════════════════════════════════════════════════════════
    // SECTION 4 — assertAll() groups multiple assertions
    // ════════════════════════════════════════════════════════════════════════
    // Normal behavior: first failed assertion STOPS the test.
    // assertAll: ALL assertions run, ALL failures reported at once.
    // Use when you want to see the full picture of what failed.

    @Test
    @DisplayName("assertAll: verify discount applied correctly")
    void assertAllDiscount() {
        BigDecimal price = new BigDecimal("200.00");

        assertAll("discount checks",
                () -> assertEquals(new BigDecimal("180.00"), calculator.applyDiscount(price, 10)),
                () -> assertEquals(new BigDecimal("150.00"), calculator.applyDiscount(price, 25)),
                () -> assertEquals(new BigDecimal("100.00"), calculator.applyDiscount(price, 50)),
                () -> assertEquals(new BigDecimal("200.00"), calculator.applyDiscount(price, 0)),
                () -> assertEquals(new BigDecimal("0.00"),   calculator.applyDiscount(price, 100))
        );
        // If two of these fail, assertAll reports BOTH failures.
        // Without assertAll, the second failure would be hidden by the first.
    }

    // ════════════════════════════════════════════════════════════════════════
    // SECTION 5 — @ParameterizedTest with @CsvSource
    // ════════════════════════════════════════════════════════════════════════
    // One test method, multiple input/output rows.
    // Eliminates copy-paste tests that test the same logic with different values.

    @ParameterizedTest(name = "Type={0}, Priority={1} → expected=${2}")
    @CsvSource({
        // type,          priority, expectedPrice
        "BUG,           LOW,    50.00",
        "BUG,           MEDIUM, 75.00",
        "BUG,           HIGH,   100.00",
        "FEATURE,       LOW,    100.00",
        "FEATURE,       MEDIUM, 150.00",
        "FEATURE,       HIGH,   200.00",
        "DOCUMENTATION, LOW,    30.00",
        "DOCUMENTATION, HIGH,   60.00",
        "REFACTOR,      LOW,    75.00",
        "REFACTOR,      HIGH,   150.00",
    })
    @DisplayName("Price matrix — all type × priority combinations")
    void priceMatrix(String type, String priority, String expectedPrice) {
        Task task = Task.builder()
                .type(Task.Type.valueOf(type.trim()))
                .priority(Task.Priority.valueOf(priority.trim()))
                .build();

        BigDecimal actual = calculator.calculatePrice(task);

        assertThat(actual).isEqualByComparingTo(new BigDecimal(expectedPrice.trim()));
    }

    // ════════════════════════════════════════════════════════════════════════
    // SECTION 6 — @ParameterizedTest with @EnumSource
    // ════════════════════════════════════════════════════════════════════════
    // Runs the test once for each value of the enum.

    @ParameterizedTest(name = "Priority={0} should return a positive price")
    @EnumSource(Task.Priority.class)   // runs for LOW, MEDIUM, HIGH
    @DisplayName("All priorities return a positive price for FEATURE tasks")
    void allPrioritiesReturnPositivePrice(Task.Priority priority) {
        Task task = Task.builder()
                .type(Task.Type.FEATURE)
                .priority(priority)
                .build();

        BigDecimal price = calculator.calculatePrice(task);

        assertThat(price).isGreaterThan(BigDecimal.ZERO);
    }

    // ════════════════════════════════════════════════════════════════════════
    // SECTION 7 — @Nested for grouping related tests
    // ════════════════════════════════════════════════════════════════════════
    // @Nested groups tests with a shared @DisplayName context.
    // Makes the test report read like a spec document.

    @Nested
    @DisplayName("applyDiscount()")
    class ApplyDiscountTests {

        @Test
        @DisplayName("0% discount returns original price")
        void zeroDiscountReturnsOriginal() {
            BigDecimal result = calculator.applyDiscount(new BigDecimal("100.00"), 0);
            assertThat(result).isEqualByComparingTo(new BigDecimal("100.00"));
        }

        @Test
        @DisplayName("100% discount returns zero")
        void fullDiscountReturnsZero() {
            BigDecimal result = calculator.applyDiscount(new BigDecimal("100.00"), 100);
            assertThat(result).isEqualByComparingTo(BigDecimal.ZERO);
        }

        @Test
        @DisplayName("Negative price throws exception")
        void negativePriceThrows() {
            assertThrows(IllegalArgumentException.class,
                    () -> calculator.applyDiscount(new BigDecimal("-1"), 10));
        }
    }
}
