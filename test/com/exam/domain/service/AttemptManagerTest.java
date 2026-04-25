package com.exam.domain.service;

import com.exam.domain.model.ExamAttempt;
import com.exam.domain.repository.Repositories.ExamAttemptRepository;
import com.exam.domain.vo.ValueObjects;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AttemptManagerTest {

    @Test
    void verificarIntentoActivo_ThrowsExceptionIfActive() {
        ValueObjects.StudentId studentId = new ValueObjects.StudentId("S1");
        ExamAttempt attempt = new ExamAttempt(studentId, new ArrayList<>());
        
        ExamAttemptRepository repo = new ExamAttemptRepository() {
            @Override public void save(ExamAttempt attempt) {}
            @Override public Optional<ExamAttempt> findActiveByStudent(ValueObjects.StudentId id) {
                return Optional.of(attempt);
            }
            @Override public java.util.List<ExamAttempt> findAll() { return new ArrayList<>(); }
        };

        AttemptManager manager = new AttemptManager(repo);

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> manager.verificarIntentoActivo(studentId));
        assertEquals("El estudiante ya posee un intento activo en curso.", ex.getMessage());
    }

    @Test
    void verificarIntentoActivo_DoesNothingIfNoActive() {
        ValueObjects.StudentId studentId = new ValueObjects.StudentId("S1");
        
        ExamAttemptRepository repo = new ExamAttemptRepository() {
            @Override public void save(ExamAttempt attempt) {}
            @Override public Optional<ExamAttempt> findActiveByStudent(ValueObjects.StudentId id) {
                return Optional.empty();
            }
            @Override public java.util.List<ExamAttempt> findAll() { return new ArrayList<>(); }
        };

        AttemptManager manager = new AttemptManager(repo);
        manager.verificarIntentoActivo(studentId); // No debe lanzar error
    }
}
