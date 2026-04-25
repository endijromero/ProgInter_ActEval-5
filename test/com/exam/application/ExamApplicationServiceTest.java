package com.exam.application;

import com.exam.domain.model.ExamAttempt;
import com.exam.domain.model.Question;
import com.exam.domain.model.QuestionTypes;
import com.exam.domain.repository.Repositories;
import com.exam.domain.service.AttemptManager;
import com.exam.domain.service.ExportService;
import com.exam.domain.service.GradingService;
import com.exam.domain.vo.ValueObjects;
import com.exam.application.dto.DTOs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class ExamApplicationServiceTest {

    private ExamApplicationService service;
    private StubQuestionBankRepository questionRepo;
    private StubExamAttemptRepository attemptRepo;

    @BeforeEach
    void setUp() {
        questionRepo = new StubQuestionBankRepository();
        attemptRepo = new StubExamAttemptRepository();
        AttemptManager attemptManager = new AttemptManager(attemptRepo);
        GradingService gradingService = new GradingService();
        ExportService exportService = (studentId, calificacion) -> {};

        service = new ExamApplicationService(questionRepo, attemptRepo, attemptManager, gradingService, exportService);
    }

    @Test
    void iniciarExamen_WithMoreThan50Questions_ThrowsException() {
        ValueObjects.StudentId student = new ValueObjects.StudentId("S1");
        List<Question> manyQuestions = new ArrayList<>();
        for (int i = 0; i < 51; i++) {
            manyQuestions.add(new QuestionTypes.TrueFalseQuestion(
                    new ValueObjects.QuestionId("Q" + i),
                    "Text " + i,
                    new ValueObjects.AnswerText("V")
            ));
        }
        questionRepo.setQuestions(manyQuestions);

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> service.iniciarExamen(student));
        assertEquals("El banco de preguntas excede el límite máximo permitido", ex.getMessage());
    }

    @Test
    void iniciarExamen_WithEmptyBank_ThrowsException() {
        ValueObjects.StudentId student = new ValueObjects.StudentId("S1");
        questionRepo.setQuestions(new ArrayList<>()); // Vacío

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> service.iniciarExamen(student));
        assertEquals("El banco de preguntas está vacío.", ex.getMessage());
    }

    @Test
    void pausarExamen_SetsPausedFlag() {
        ValueObjects.StudentId student = new ValueObjects.StudentId("S1");
        ExamAttempt attempt = new ExamAttempt(student, new ArrayList<>());
        attemptRepo.save(attempt);

        service.pausarExamen(student);

        assertTrue(attempt.isPaused());
    }

    @Test
    void responderPregunta_AddsAnswerToAttempt() {
        ValueObjects.StudentId student = new ValueObjects.StudentId("S1");
        ExamAttempt attempt = new ExamAttempt(student, new ArrayList<>());
        attemptRepo.save(attempt);

        ValueObjects.QuestionId qId = new ValueObjects.QuestionId("Q1");
        ValueObjects.AnswerText ans = new ValueObjects.AnswerText("V");
        
        service.responderPregunta(student, qId, ans);

        assertEquals(ans, attempt.getAnswers().get(qId));
    }

    @Test
    void finalizarExamen_CalculatesAndSavesScore() {
        ValueObjects.StudentId student = new ValueObjects.StudentId("S1");
        ExamAttempt attempt = new ExamAttempt(student, new ArrayList<>());
        attemptRepo.save(attempt);

        DTOs.CalificacionDTO res = service.finalizarExamen(student);

        assertNotNull(attempt.getResult());
        assertEquals(0, res.total()); // Sin preguntas
    }

    // --- STUBS ---
    static class StubQuestionBankRepository implements Repositories.QuestionBankRepository {
        private List<Question> questions = new ArrayList<>();
        public void setQuestions(List<Question> q) { this.questions = q; }
        @Override public List<Question> findAll() { return questions; }
    }

    static class StubExamAttemptRepository implements Repositories.ExamAttemptRepository {
        private ExamAttempt savedAttempt = null;
        @Override public void save(ExamAttempt attempt) { savedAttempt = attempt; }
        @Override public Optional<ExamAttempt> findActiveByStudent(ValueObjects.StudentId studentId) {
            if (savedAttempt != null && savedAttempt.getStudentId().equals(studentId) && !savedAttempt.estaFinalizado()) {
                return Optional.of(savedAttempt);
            }
            return Optional.empty();
        }
        @Override public List<ExamAttempt> findAll() { return new ArrayList<>(); }
    }
}
