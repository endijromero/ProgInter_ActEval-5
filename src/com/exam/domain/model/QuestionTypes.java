package com.exam.domain.model;

import com.exam.domain.vo.ValueObjects.AnswerText;
import com.exam.domain.vo.ValueObjects.QuestionId;
import java.util.*;

/**
 * Implementaciones concretas de los tipos de preguntas.
 */
public class QuestionTypes {

  public static class SingleChoiceQuestion extends Question {
    private final List<String> options;

    public SingleChoiceQuestion(QuestionId id, String text, List<String> options, AnswerText correct) {
      super(id, text, correct);
      this.options = options;
    }

    @Override
    public boolean isCorrect(AnswerText studentAnswer) {
      return correctAnswer.value().trim().equalsIgnoreCase(studentAnswer.value().trim());
    }

    @Override
    public void displayFormat() {
      System.out.println("[Única Respuesta] " + text);
      options.forEach(o -> System.out.println("  ( ) " + o));
    }
  }

  public static class TrueFalseQuestion extends Question {
    public TrueFalseQuestion(QuestionId id, String text, AnswerText correct) {
      super(id, text, correct);
    }

    @Override
    public boolean isCorrect(AnswerText studentAnswer) {
      return correctAnswer.value().trim().equalsIgnoreCase(studentAnswer.value().trim());
    }

    @Override
    public void displayFormat() {
      System.out.println("[Verdadero / Falso] (Escriba V o F)");
      System.out.println(text + "\n  ( ) V\n  ( ) F");
    }
  }

  public static class FillBlankQuestion extends Question {
    public FillBlankQuestion(QuestionId id, String text, AnswerText correct) {
      super(id, text, correct);
    }

    @Override
    public boolean isCorrect(AnswerText studentAnswer) {
      String normCorrect = correctAnswer.value().trim().replaceAll("\\s+", " ").toLowerCase();
      String normStudent = studentAnswer.value().trim().replaceAll("\\s+", " ").toLowerCase();
      return normCorrect.equals(normStudent);
    }

    @Override
    public void displayFormat() {
      System.out.println("[Completar Frase] " + text);
      System.out.println("  [________________________]");
    }
  }

  public static class MultipleChoiceQuestion extends Question {
    private final List<String> options;

    public MultipleChoiceQuestion(QuestionId id, String text, List<String> options, AnswerText correct) {
      super(id, text, correct);
      this.options = options;
    }

    @Override
    public boolean isCorrect(AnswerText studentAnswer) {
      Set<String> correctSet = new HashSet<>(Arrays.asList(correctAnswer.value().split(",")));
      Set<String> studentSet = new HashSet<>(Arrays.asList(studentAnswer.value().split(",")));
      return correctSet.equals(studentSet);
    }

    @Override
    public void displayFormat() {
      System.out.println("[Múltiple Respuesta] (Separar por comas, ej: Java,C++) " + text);
      options.forEach(o -> System.out.println("  * " + o));
    }
  }
}