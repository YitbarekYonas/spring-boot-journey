package com.JavaBackEnd.spring_boot_journey_week7_day3.service;

import com.JavaBackEnd.spring_boot_journey_week7_day3.dto.request.CreateTaskRequest;
import com.JavaBackEnd.spring_boot_journey_week7_day3.exception.GlobalExceptionHandler;
import com.JavaBackEnd.spring_boot_journey_week7_day3.model.Task;

import java.util.List;

// ── Why this is an interface ───────────────────────────────────────────────
// @WebMvcTest only loads the web layer (controllers, filters, exception handlers).
// The service is NOT loaded — it's replaced by a @MockBean in the test.
// Using an interface makes this explicit: the controller depends on the
// abstraction, not the implementation. The mock implements this interface.
public interface TaskService {

    List<Task> getAll();

    Task getById(Long id);

    Task create(CreateTaskRequest request);

    Task updateStatus(Long id, Task.Status newStatus);

    void delete(Long id);
}
