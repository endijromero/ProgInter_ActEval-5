package com.exam.domain.model;

import com.exam.domain.vo.ValueObjects.AnswerText;
import com.exam.domain.vo.ValueObjects.Calificacion;
import com.exam.domain.vo.ValueObjects.QuestionId;
import com.exam.domain.vo.ValueObjects.StudentId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Aggregate Root que garantiza la consistencia de un intento de examen.
 */
public class ExamAttempt {
  private final StudentId studentId;
  private final List<Question> questions;
  private final Map<QuestionId, AnswerText> answers;
  private boolean isFinished;
  private boolean isPaused;
  private Calificacion result;

  public ExamAttempt(StudentId studentId, List<Question> questions) {
    this.studentId = studentId;
    this.questions = questions;
    this.answers = new HashMap<>();
    this.isFinished = false;
    this.isPaused = false;
  }

  public void responder(QuestionId qId, AnswerText answer) {
    if (!isFinished) {
      answers.put(qId, answer);
    }
  }

  public void finalizar(Calificacion calificacion) {
    this.isFinished = true;
    this.isPaused = false;
    this.result = calificacion;
  }

  public boolean estaFinalizado() {
    return isFinished;
  }

  public void setPaused(boolean paused) {
    this.isPaused = paused;
  }

  public boolean isPaused() {
    return isPaused;
  }

  public StudentId getStudentId() {
    return studentId;
  }

  public List<Question> getQuestions() {
    return questions;
  }

  public Map<QuestionId, AnswerText> getAnswers() {
    return answers;
  }

  public Calificacion getResult() {
    return result;
  }
}