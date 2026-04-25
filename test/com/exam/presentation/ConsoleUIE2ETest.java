package com.exam.presentation;

import com.exam.application.ExamApplicationService;
import com.exam.application.StatsApplicationService;
import com.exam.domain.model.ExamAttempt;
import com.exam.domain.model.Question;
import com.exam.domain.model.QuestionTypes;
import com.exam.domain.repository.Repositories;
import com.exam.domain.service.AttemptManager;
import com.exam.domain.service.ExportService;
import com.exam.domain.service.GradingService;
import com.exam.domain.vo.ValueObjects;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConsoleUIE2ETest {

    private final InputStream originalIn = System.in;
    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream outContent;

    private ExamApplicationService appService;
    private StatsApplicationService statsService;
    private StubQuestionBankRepository questionRepo;
    private StubExamAttemptRepository attemptRepo;

    @BeforeEach
    void setUp() {
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        questionRepo = new StubQuestionBankRepository();
        attemptRepo = new StubExamAttemptRepository();
        AttemptManager attemptManager = new AttemptManager(attemptRepo);
        GradingService gradingService = new GradingService();
        ExportService exportService = (studentId, calificacion) -> {
            System.out.println("Exportando a calificaciones.txt: " + studentId.value());
        };

        appService = new ExamApplicationService(questionRepo, attemptRepo, attemptManager, gradingService, exportService);
        statsService = new StatsApplicationService(attemptRepo, gradingService);
    }

    @AfterEach
    void tearDown() {
        System.setIn(originalIn);
        System.setOut(originalOut);
    }

    @Test
    void testStudentFlow_Aprobado_And_TimePrinted() {
        // Configuramos 1 pregunta
        List<Question> questions = new ArrayList<>();
        questions.add(new QuestionTypes.TrueFalseQuestion(
                new ValueObjects.QuestionId("Q1"), "Test Q", new ValueObjects.AnswerText("V")));
        questionRepo.setQuestions(questions);

        // Simulamos entrada: 1 (Estudiante) -> S1 (ID) -> V (Respuesta correcta)
        String simulatedInput = "1\nS1\nV\n";
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

        ConsoleUI consoleUI = new ConsoleUI(appService, statsService);
        consoleUI.start();

        String output = outContent.toString();
        
        // Verificaciones (HU02)
        assertTrue(output.contains("Estado: APROBADO"));
        // Verificaciones (HU10)
        assertTrue(output.contains("Tiempo total del intento"));
        // Verificaciones (HU12 Simulada mediante el sysout en el exportService del test)
        assertTrue(output.contains("Exportando a calificaciones.txt: S1"));
    }

    @Test
    void testTeacherFlow_CorrectPin_ShowsStats() {
        // Simulamos entrada: 2 (Docente) -> 1234 (PIN)
        String simulatedInput = "2\n1234\n";
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

        ConsoleUI consoleUI = new ConsoleUI(appService, statsService);
        consoleUI.start();

        String output = outContent.toString();
        // Verificaciones (HU14)
        assertTrue(output.contains("Acceso concedido."));
        assertTrue(output.contains("--- ESTADÍSTICAS GLOBALES ---"));
    }

    @Test
    void testStudentFlow_Pausar_AndResume() {
        // Configuramos 1 pregunta
        List<Question> questions = new ArrayList<>();
        questions.add(new QuestionTypes.TrueFalseQuestion(
                new ValueObjects.QuestionId("Q1"), "Test Q", new ValueObjects.AnswerText("V")));
        questionRepo.setQuestions(questions);

        // Simulamos entrada: 1 (Estudiante) -> S2 (ID) -> PAUSAR
        String simulatedInput = "1\nS2\nPAUSAR\n";
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

        ConsoleUI consoleUI = new ConsoleUI(appService, statsService);
        consoleUI.start();

        String output = outContent.toString();
        // Verificaciones (HU13)
        assertTrue(output.contains("Examen pausado con éxito"));
    }

    @Test
    void testInvalidOptionsAndInvalidAnswer() {
        // Configuramos 1 pregunta
        List<Question> questions = new ArrayList<>();
        questions.add(new QuestionTypes.TrueFalseQuestion(
                new ValueObjects.QuestionId("Q1"), "Test Q", new ValueObjects.AnswerText("V")));
        questionRepo.setQuestions(questions);

        // Entrada: 3 (Inválida) -> 1 (Estudiante) -> S3 (ID) -> \n (Inválida) -> V (Correcta)
        String simulatedInput = "3\n1\nS3\n\nV\n";
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

        ConsoleUI consoleUI = new ConsoleUI(appService, statsService);
        consoleUI.start();

        String output = outContent.toString();
        assertTrue(output.contains("Opción inválida. Intente nuevamente."));
        assertTrue(output.contains("La respuesta no puede estar vacía"));
        assertTrue(output.contains("Estado: APROBADO"));
    }

    @Test
    void testTeacherFlow_InvalidPin_ShowsError() {
        String simulatedInput = "2\n9999\n";
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

        ConsoleUI consoleUI = new ConsoleUI(appService, statsService);
        consoleUI.start();

        String output = outContent.toString();
        assertTrue(output.contains("PIN incorrecto. Acceso denegado."));
    }

    @Test
    void testStudentFlow_ExceptionHandled() {
        questionRepo.setQuestions(new ArrayList<>()); // Vacío lanzará IllegalStateException
        
        String simulatedInput = "1\nS_Err\n";
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

        ConsoleUI consoleUI = new ConsoleUI(appService, statsService);
        consoleUI.start();

        String output = outContent.toString();
        assertTrue(output.contains("[ERROR]: El banco de preguntas está vacío."));
    }

    // --- STUBS ---
    static class StubQuestionBankRepository implements Repositories.QuestionBankRepository {
        private List<Question> questions = new ArrayList<>();
        public void setQuestions(List<Question> q) { this.questions = q; }
        @Override public List<Question> findAll() { return questions; }
    }

    static class StubExamAttemptRepository implements Repositories.ExamAttemptRepository {
        private ExamAttempt savedAttempt = null;
        private List<ExamAttempt> all = new ArrayList<>();

        @Override public void save(ExamAttempt attempt) { 
            savedAttempt = attempt; 
            if(!all.contains(attempt)) all.add(attempt);
        }
        @Override public Optional<ExamAttempt> findActiveByStudent(ValueObjects.StudentId studentId) {
            if (savedAttempt != null && savedAttempt.getStudentId().equals(studentId) && !savedAttempt.estaFinalizado()) {
                return Optional.of(savedAttempt);
            }
            return Optional.empty();
        }
        @Override public List<ExamAttempt> findAll() {
            return all;
        }
    }
}
