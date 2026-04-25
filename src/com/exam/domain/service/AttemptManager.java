package com.exam.domain.service;

import com.exam.domain.model.ExamAttempt;
import com.exam.domain.repository.Repositories.ExamAttemptRepository;
import com.exam.domain.vo.ValueObjects.StudentId;
import java.util.Optional;

/**
 * Domain Service responsable de la regla de negocio: un solo intento activo.
 */
public class AttemptManager {
    private final ExamAttemptRepository repository;

    public AttemptManager(ExamAttemptRepository repository) {
        this.repository = repository;
    }

    public void verificarIntentoActivo(StudentId studentId) {
        Optional<ExamAttempt> active = repository.findActiveByStudent(studentId);
        if (active.isPresent() && !active.get().isPaused()) {
            throw new IllegalStateException("El estudiante ya posee un intento activo en curso.");
        }
    }
}