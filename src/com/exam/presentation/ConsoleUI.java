package com.exam.presentation;

import com.exam.application.ExamApplicationService;
import com.exam.application.dto.DTOs.CalificacionDTO;
import com.exam.application.dto.DTOs.ExamAttemptDTO;
import com.exam.domain.model.Question;
import com.exam.domain.vo.ValueObjects.AnswerText;
import com.exam.domain.vo.ValueObjects.StudentId;
import java.util.Scanner;

/**
 * Capa de Presentación: interactúa con el usuario y traslada peticiones al
 * Application Service.
 */
public class ConsoleUI {
  private final ExamApplicationService service;
  private final Scanner scanner;

  public ConsoleUI(ExamApplicationService service) {
    this.service = service;
    this.scanner = new Scanner(System.in);
  }

  public void start() {
    String ansiCyan = "\u001B[36m";
    String ansiGreen = "\u001B[32m";
    String ansiRed = "\u001B[31m";
    String ansiReset = "\u001B[0m";
    String ansiYellow = "\u001B[33m";

    System.out.println(ansiCyan + "╔═══════════════════════════════════════════════════════╗" + ansiReset);
    System.out.println(ansiCyan + "║         UNIVERSIDAD - SISTEMA DE EVALUACIÓN           ║" + ansiReset);
    System.out.println(ansiCyan + "║            (DDD & CLEAN ARCHITECTURE)                 ║" + ansiReset);
    System.out.println(ansiCyan + "╚═══════════════════════════════════════════════════════╝" + ansiReset);
    System.out.println(ansiGreen + "¡Bienvenido a la plataforma de exámenes institucionales!" + ansiReset);

    System.out.print("\nIngrese su ID de estudiante: ");
    StudentId studentId = new StudentId(scanner.nextLine());

    try {
      ExamAttemptDTO attempt = service.iniciarExamen(studentId);
      System.out.println("\n--- EXAMEN INICIADO ---");

      int count = 1;
      for (Question q : attempt.questions()) {
        System.out.println("\nPregunta " + count++ + " de " + attempt.questions().size());
        q.displayFormat();

        AnswerText answer = null;
        while (answer == null) {
          System.out.print("Su respuesta: ");
          try {
            answer = new AnswerText(scanner.nextLine());
          } catch (IllegalArgumentException e) {
            System.out.println(ansiRed + "[ERROR]: " + e.getMessage() + ansiReset);
          }
        }

        service.responderPregunta(studentId, q.getId(), answer);
      }

      CalificacionDTO resultado = service.finalizarExamen(studentId);

      System.out.println("\n=== RESULTADOS ===");
      System.out.println("Puntuación Final: " + resultado.puntaje() + " / " + resultado.total());
      double porcentaje = ((double) resultado.puntaje() / resultado.total()) * 100;
      System.out.println("Porcentaje de acierto: " + porcentaje + "%");

      if (porcentaje >= 60.0) {
        System.out.println(ansiGreen + "Estado: APROBADO" + ansiReset);
      } else {
        System.out.println(ansiRed + "Estado: REPROBADO" + ansiReset);
      }

      System.out.println(ansiYellow + "\n¡Gracias por utilizar nuestra plataforma de evaluación! Le deseamos mucho éxito." + ansiReset);

    } catch (IllegalStateException | IllegalArgumentException e) {
      System.out.println("\n[ERROR]: " + e.getMessage());
    }
  }
}