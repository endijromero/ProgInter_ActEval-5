package com.exam.infrastructure;

import com.exam.domain.model.ExamAttempt;
import com.exam.domain.repository.Repositories.ExamAttemptRepository;
import com.exam.domain.vo.ValueObjects.StudentId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Adaptador de infraestructura en memoria para la gestión de intentos (Fase 3).
 */
public class InMemoryExamAttemptRepository implements ExamAttemptRepository {
  private final Map<StudentId, ExamAttempt> db = new HashMap<>();

  @Override
  public Optional<ExamAttempt> findActiveByStudent(StudentId studentId) {
    ExamAttempt attempt = db.get(studentId);
    if (attempt != null && !attempt.estaFinalizado()) {
      return Optional.of(attempt);
    }
    return Optional.empty();
  }

  @Override
  public void save(ExamAttempt attempt) {
    db.put(attempt.getStudentId(), attempt);
  }

  @Override
  public List<ExamAttempt> findAll() {
    return new ArrayList<>(db.values());
  }
}