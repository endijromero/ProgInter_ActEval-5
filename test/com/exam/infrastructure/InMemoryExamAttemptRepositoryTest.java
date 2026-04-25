package com.exam.infrastructure;

import com.exam.domain.model.ExamAttempt;
import com.exam.domain.model.Question;
import com.exam.domain.model.QuestionTypes;
import com.exam.domain.vo.ValueObjects;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryExamAttemptRepositoryTest {

    @Test
    void saveAndFind_WorksCorrectly() {
        InMemoryExamAttemptRepository repo = new InMemoryExamAttemptRepository();
        
        ValueObjects.StudentId studentId = new ValueObjects.StudentId("123");
        List<Question> questions = new ArrayList<>();
        questions.add(new QuestionTypes.TrueFalseQuestion(
            new ValueObjects.QuestionId("Q1"), "Test", new ValueObjects.AnswerText("V")));

        ExamAttempt attempt = new ExamAttempt(studentId, questions);
        
        repo.save(attempt);
        
        Optional<ExamAttempt> found = repo.findActiveByStudent(studentId);
        assertTrue(found.isPresent());
        assertEquals(studentId, found.get().getStudentId());

        List<ExamAttempt> all = repo.findAll();
        assertEquals(1, all.size());

        // Finalizar y verificar que ya no está activo
        attempt.finalizar(new ValueObjects.Calificacion(1, 1));
        repo.save(attempt);
        
        Optional<ExamAttempt> foundActiveAgain = repo.findActiveByStudent(studentId);
        assertFalse(foundActiveAgain.isPresent(), "No debería haber intentos activos si se finalizó");
    }
}
