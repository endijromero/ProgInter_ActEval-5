package com.exam.application;

import com.exam.application.dto.DTOs.CalificacionDTO;
import com.exam.application.dto.DTOs.ExamAttemptDTO;
import com.exam.domain.model.ExamAttempt;
import com.exam.domain.model.Question;
import com.exam.domain.repository.Repositories.ExamAttemptRepository;
import com.exam.domain.repository.Repositories.QuestionBankRepository;
import com.exam.domain.service.AttemptManager;
import com.exam.domain.service.ExportService;
import com.exam.domain.service.GradingService;
import com.exam.domain.vo.ValueObjects.AnswerText;
import com.exam.domain.vo.ValueObjects.Calificacion;
import com.exam.domain.vo.ValueObjects.QuestionId;
import com.exam.domain.vo.ValueObjects.StudentId;
import java.util.List;

/**
 * Application Service que orquesta los casos de uso.
 */
public class ExamApplicationService {
  private final QuestionBankRepository questionRepo;
  private final ExamAttemptRepository attemptRepo;
  private final AttemptManager attemptManager;
  private final GradingService gradingService;
  private final ExportService exportService;

  public ExamApplicationService(QuestionBankRepository qRepo, ExamAttemptRepository aRepo,
      AttemptManager aManager, GradingService gService, ExportService exportService) {
    this.questionRepo = qRepo;
    this.attemptRepo = aRepo;
    this.attemptManager = aManager;
    this.gradingService = gService;
    this.exportService = exportService;
  }

  public ExamAttemptDTO iniciarExamen(StudentId studentId) {
    attemptManager.verificarIntentoActivo(studentId);

    // Revisar si existe un intento pausado para este estudiante
    java.util.Optional<ExamAttempt> activeOpt = attemptRepo.findActiveByStudent(studentId);
    if (activeOpt.isPresent()) {
      ExamAttempt active = activeOpt.get();
      if (active.isPaused()) {
        active.setPaused(false);
        attemptRepo.save(active);
        return new ExamAttemptDTO(studentId, active.getQuestions(), active.getAnswers());
      }
    }

    List<Question> questions = questionRepo.findAll();
    if (questions.isEmpty()) {
      throw new IllegalStateException("El banco de preguntas está vacío.");
    }
    if (questions.size() > 50) {
      throw new IllegalStateException("El banco de preguntas excede el límite máximo permitido");
    }

    ExamAttempt attempt = new ExamAttempt(studentId, questions);
    attemptRepo.save(attempt); // Persiste el inicio

    return new ExamAttemptDTO(studentId, questions, attempt.getAnswers());
  }

  public void pausarExamen(StudentId studentId) {
    ExamAttempt attempt = attemptRepo.findActiveByStudent(studentId)
        .orElseThrow(() -> new IllegalStateException("No se encontró intento activo."));

    attempt.setPaused(true);
    attemptRepo.save(attempt);
  }

  public void responderPregunta(StudentId studentId, QuestionId qId, AnswerText answer) {
    ExamAttempt attempt = attemptRepo.findActiveByStudent(studentId)
        .orElseThrow(() -> new IllegalStateException("No se encontró intento activo."));

    attempt.responder(qId, answer);
    attemptRepo.save(attempt);
  }

  public CalificacionDTO finalizarExamen(StudentId studentId) {
    ExamAttempt attempt = attemptRepo.findActiveByStudent(studentId)
        .orElseThrow(() -> new IllegalStateException("No se encontró intento activo."));

    Calificacion calificacion = gradingService.calificar(attempt);
    attempt.finalizar(calificacion);
    attemptRepo.save(attempt); // Persiste el resultado

    if (exportService != null) {
      exportService.exportResult(studentId, calificacion);
    }

    return new CalificacionDTO(calificacion.puntaje(), calificacion.total());
  }
}