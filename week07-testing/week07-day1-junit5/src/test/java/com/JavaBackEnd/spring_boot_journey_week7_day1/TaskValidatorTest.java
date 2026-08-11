package com.JavaBackEnd.spring_boot_journey_week7_day1;

import com.JavaBackEnd.spring_boot_journey_week7_day1.model.Task;
import com.JavaBackEnd.spring_boot_journey_week7_day1.service.TaskValidator;
import com.JavaBackEnd.spring_boot_journey_week7_day1.service.TaskValidator.ValidationResult;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TaskValidator")
class TaskValidatorTest {

    private TaskValidator validator;

    @BeforeEach
    void setUp() {
        validator = new TaskValidator();
    }

    // ════════════════════════════════════════════════════════════════════════
    // SECTION 1 — Happy path (valid task passes validation)
    // ════════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Valid task should pass all validation rules")
    void validTaskPassesValidation() {
        Task task = Task.builder()
                .title("Fix login bug")
                .ownerEmail("alice@example.com")
                .priority(Task.Priority.HIGH)
                .type(Task.Type.BUG)
                .dueDate(LocalDate.now().plusDays(3))
                .build();

        ValidationResult result = validator.validate(task);

        // assertTrue / assertFalse — for boolean assertions
        assertTrue(result.isValid());
        assertTrue(result.getErrors().isEmpty());
    }

    // ════════════════════════════════════════════════════════════════════════
    // SECTION 2 — @ValueSource (one param, multiple values)
    // ════════════════════════════════════════════════════════════════════════
    // @ValueSource runs the test once for each value in the array.
    // Perfect for testing boundary values and invalid inputs.

    @ParameterizedTest(name = "Invalid email [{0}] should fail validation")
    @ValueSource(strings = {
        "notanemail",       // no @
        "missing-dot@com",  // no dot after @
        "@nodomain.com",    // no local part
        "spaces @test.com", // space in email
    })
    @DisplayName("Invalid email formats should fail validation")
    void invalidEmailFailsValidation(String badEmail) {
        assertFalse(validator.isValidEmail(badEmail));
    }

    @ParameterizedTest(name = "Valid email [{0}] should pass validation")
    @ValueSource(strings = {
        "alice@example.com",
        "bob.smith@company.org",
        "user+tag@mail.co.uk",
    })
    @DisplayName("Valid email formats should pass validation")
    void validEmailPassesValidation(String goodEmail) {
        assertTrue(validator.isValidEmail(goodEmail));
    }

    // ════════════════════════════════════════════════════════════════════════
    // SECTION 3 — @NullAndEmptySource (tests null AND "" in one annotation)
    // ════════════════════════════════════════════════════════════════════════

    @ParameterizedTest(name = "Blank title [{0}] should fail validation")
    @NullAndEmptySource                  // runs twice: once with null, once with ""
    @ValueSource(strings = {"  ", "\t"}) // also tests whitespace-only strings
    @DisplayName("Null, empty, and blank titles should fail")
    void blankTitleFailsValidation(String blankTitle) {
        assertFalse(validator.isTitleValid(blankTitle));
    }

    // ════════════════════════════════════════════════════════════════════════
    // SECTION 4 — @Nested to test each validation rule in isolation
    // ════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Title validation rules")
    class TitleValidationTests {

        @Test
        @DisplayName("Title shorter than 3 chars should add error")
        void shortTitleAddsError() {
            Task task = buildValidTask();
            task.setTitle("ab");  // too short

            ValidationResult result = validator.validate(task);

            assertFalse(result.isValid());
            assertThat(result.getErrors())
                    .hasSize(1)
                    .contains("Title must be at least 3 characters");
        }

        @Test
        @DisplayName("Title longer than 100 chars should add error")
        void longTitleAddsError() {
            Task task = buildValidTask();
            task.setTitle("x".repeat(101));  // too long

            ValidationResult result = validator.validate(task);

            assertFalse(result.isValid());
            assertThat(result.getErrors()).contains("Title cannot exceed 100 characters");
        }

        @Test
        @DisplayName("Title exactly 3 chars should pass")
        void titleExactly3CharsIsValid() {
            Task task = buildValidTask();
            task.setTitle("abc");  // exactly at minimum boundary

            assertTrue(validator.validate(task).isValid());
        }

        @Test
        @DisplayName("Title exactly 100 chars should pass")
        void titleExactly100CharsIsValid() {
            Task task = buildValidTask();
            task.setTitle("a".repeat(100));  // exactly at maximum boundary

            assertTrue(validator.validate(task).isValid());
        }
    }

    @Nested
    @DisplayName("Due date validation rules")
    class DueDateValidationTests {

        @Test
        @DisplayName("Past due date should add error")
        void pastDueDateAddsError() {
            Task task = buildValidTask();
            task.setDueDate(LocalDate.now().minusDays(1));  // yesterday

            ValidationResult result = validator.validate(task);

            assertFalse(result.isValid());
            assertThat(result.getErrors()).contains("Due date cannot be in the past");
        }

        @Test
        @DisplayName("Future due date should pass")
        void futureDueDatePasses() {
            Task task = buildValidTask();
            task.setDueDate(LocalDate.now().plusDays(1));  // tomorrow

            assertTrue(validator.validate(task).isValid());
        }

        @Test
        @DisplayName("Null due date should pass — due date is optional")
        void nullDueDateIsAllowed() {
            Task task = buildValidTask();
            task.setDueDate(null);  // optional field

            assertTrue(validator.validate(task).isValid());
        }
    }

    @Nested
    @DisplayName("Multiple validation errors collected at once")
    class MultipleErrorTests {

        @Test
        @DisplayName("Task with multiple invalid fields should collect ALL errors")
        void multipleInvalidFieldsCollectsAllErrors() {
            Task task = Task.builder()
                    .title("ab")                              // too short
                    .ownerEmail("notanemail")                 // bad format
                    .priority(null)                           // missing
                    .type(null)                               // missing
                    .dueDate(LocalDate.now().minusDays(5))    // past
                    .build();

            ValidationResult result = validator.validate(task);

            // assertFalse — task is invalid
            assertFalse(result.isValid());

            // AssertJ — assert on the list contents
            assertThat(result.getErrors())
                    .hasSize(5)
                    .contains(
                        "Title must be at least 3 characters",
                        "Owner email must be valid",
                        "Priority is required",
                        "Type is required",
                        "Due date cannot be in the past"
                    );
        }

        @Test
        @DisplayName("Null task should return single error immediately")
        void nullTaskReturnsSingleError() {
            ValidationResult result = validator.validate(null);

            assertFalse(result.isValid());
            assertThat(result.getErrors())
                    .hasSize(1)
                    .containsExactly("Task cannot be null");
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    // SECTION 5 — AssertJ advanced — assertThatThrownBy
    // ════════════════════════════════════════════════════════════════════════
    // Alternative to assertThrows — more fluent, chains on the exception itself

    @Test
    @DisplayName("AssertJ assertThatThrownBy — alternative to assertThrows")
    void assertThatThrownByExample() {
        // assertThatThrownBy is AssertJ's version of assertThrows
        // Advantage: you can chain assertions on the exception inline
        assertThatThrownBy(() -> validator.isValidEmail(null))
                .doesNotThrowAnyException();  // null is handled gracefully — returns false

        // Contrast with a method that DOES throw:
        // assertThatThrownBy(() -> someMethod())
        //     .isInstanceOf(IllegalArgumentException.class)
        //     .hasMessage("expected message")
        //     .hasMessageContaining("partial");
    }

    // ════════════════════════════════════════════════════════════════════════
    // Helper — builds a task that passes all validations
    // Using a helper method keeps each test focused on the ONE thing it tests
    // ════════════════════════════════════════════════════════════════════════
    private Task buildValidTask() {
        return Task.builder()
                .title("Fix login bug")
                .ownerEmail("alice@example.com")
                .priority(Task.Priority.HIGH)
                .type(Task.Type.BUG)
                .dueDate(LocalDate.now().plusDays(3))
                .build();
    }
}
