package com.exam.domain.repository;

import com.exam.domain.model.ExamAttempt;
import com.exam.domain.model.Question;
import com.exam.domain.vo.ValueObjects.StudentId;
import java.util.List;
import java.util.Optional;

/**
 * Interfaces de dominio (Puertos en Arquitectura Hexagonal/Clean).
 * Aplica el Principio de Segregación de Interfaces (ISP).
 */
public class Repositories {

  public interface ExamAttemptRepository {
    Optional<ExamAttempt> findActiveByStudent(StudentId studentId);

    void save(ExamAttempt attempt);

    List<ExamAttempt> findAll();
  }

  public interface QuestionBankRepository {
    List<Question> findAll();
  }
}