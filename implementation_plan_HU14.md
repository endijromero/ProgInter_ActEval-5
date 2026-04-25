# Goal Description

Implement user story HU14 (Administrative Panel), providing a parallel menu for teachers to view statistics about student exam attempts (open, paused/saved, and closed) stored in active memory.

## User Review Required

- The requirement specifies that the teacher menu should require a hardcoded PIN ("1234").
- Stats require computing the "sum of the aggregate numerical averages" of all students. I will interpret this as calculating the grade percentage for each completed/scored exam and summing them up, or showing the average of the averages.
- The repository must return all attempts "stored in active memory". `FileExamAttemptRepository` will be updated to maintain an in-memory collection of all attempts. On application startup, it will also preload paused attempts from the file system into this active memory.

## Proposed Changes

### Domain Layer (Repositories & Services)

#### [MODIFY] `src/com/exam/domain/repository/ExamAttemptRepository.java`
- Add `List<ExamAttempt> findAll();` to the interface.

#### [MODIFY] `src/com/exam/domain/repository/Repositories.java`
- Update `ExamAttemptRepository` nested interface to include `List<ExamAttempt> findAll();`.

### Infrastructure Layer

#### [MODIFY] `src/com/exam/infrastructure/InMemoryExamAttemptRepository.java`
- Implement `findAll()` returning `new ArrayList<>(db.values())`.

#### [MODIFY] `src/com/exam/infrastructure/FileExamAttemptRepository.java`
- Introduce a `private final Map<StudentId, ExamAttempt> activeMemory = new HashMap<>();`.
- In the constructor, list all files in `intentos_pausados/`, read them (using logic similar to `findActiveByStudent`), and populate `activeMemory` with the paused attempts.
- In `save()`, add the attempt to `activeMemory`.
- Implement `findAll()` returning all values in `activeMemory`.
- In `findActiveByStudent()`, check `activeMemory` first, or continue relying on the file but keeping `activeMemory` updated.

### Application Layer

#### [NEW] `src/com/exam/application/StatsApplicationService.java`
- Create a new service that depends on `ExamAttemptRepository` and `GradingService`.
- Create a method `public String getGlobalStatistics()` that:
  - Calls `repository.findAll()`.
  - Counts the total number of attempts.
  - Groups by state (Open, Paused, Closed).
  - Uses `GradingService` to calculate the percentage scores of all closed exams and sums them up (or calculates the global average).
  - Returns a formatted string with these statistics.

#### [MODIFY] `src/com/exam/Main.java`
- Instantiate `StatsApplicationService`.
- Pass it to `ConsoleUI`.

### Presentation Layer

#### [MODIFY] `src/com/exam/presentation/ConsoleUI.java`
- Update constructor to accept `StatsApplicationService`.
- In `start()`, replace the initial flow with a router:
  - "(1) Soy Estudiante" -> Redirects to the current exam flow.
  - "(2) Soy Docente" -> Prompts for PIN. If "1234", calls `StatsApplicationService.getGlobalStatistics()` and prints it.

## Verification Plan

### Automated Tests
- Run `Main.java` to start the application.

### Manual Verification
- Start the application. Select option (2) Docente, enter incorrect PIN -> Access denied.
- Enter correct PIN "1234" -> Verify stats show 0 attempts.
- Select option (1) Estudiante, start an exam, answer a question, pause it.
- Re-run application, select Docente -> Verify stats show 1 attempt (paused).
- Resume exam as Estudiante, complete it.
- Check Docente stats -> Verify stats show 1 closed attempt and the correct average.
