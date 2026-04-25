package com.exam.presentation;

import com.exam.application.ExamApplicationService;
import com.exam.application.StatsApplicationService;
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
  private final StatsApplicationService statsService;
  private final Scanner scanner;

  public ConsoleUI(ExamApplicationService service, StatsApplicationService statsService) {
    this.service = service;
    this.statsService = statsService;
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

    while (true) {
      System.out.println("\nSeleccione su perfil:");
      System.out.println("(1) Soy Estudiante");
      System.out.println("(2) Soy Docente");
      System.out.print("Opción: ");
      String opcion = scanner.nextLine().trim();

      if ("1".equals(opcion)) {
        runStudentFlow(ansiCyan, ansiGreen, ansiRed, ansiReset, ansiYellow);
        break;
      } else if ("2".equals(opcion)) {
        runTeacherFlow(ansiRed, ansiYellow, ansiGreen, ansiReset);
        break;
      } else {
        System.out.println(ansiRed + "Opción inválida. Intente nuevamente." + ansiReset);
      }
    }
  }

  private void runTeacherFlow(String ansiRed, String ansiYellow, String ansiGreen, String ansiReset) {
    System.out.print("\nIngrese el PIN de Docente: ");
    String pin = scanner.nextLine().trim();
    if ("1234".equals(pin)) {
        System.out.println(ansiGreen + "\nAcceso concedido." + ansiReset);
        System.out.println(statsService.getGlobalStatistics());
    } else {
        System.out.println(ansiRed + "\n[ERROR]: PIN incorrecto. Acceso denegado." + ansiReset);
    }
  }

  private void runStudentFlow(String ansiCyan, String ansiGreen, String ansiRed, String ansiReset, String ansiYellow) {
    System.out.print("\nIngrese su ID de estudiante: ");
    StudentId studentId = new StudentId(scanner.nextLine());

    try {
      ExamAttemptDTO attempt = service.iniciarExamen(studentId);
      long startTime = System.currentTimeMillis();
      System.out.println("\n--- EXAMEN INICIADO ---");

      int count = 1;
      for (Question q : attempt.questions()) {
        if (attempt.answers() != null && attempt.answers().containsKey(q.getId())) {
          count++;
          continue; // Saltar pregunta ya respondida
        }

        System.out.println("\nPregunta " + count++ + " de " + attempt.questions().size());
        q.displayFormat();

        AnswerText answer = null;
        while (answer == null) {
          System.out.print("Su respuesta (escriba PAUSAR para detener): ");
          String input = scanner.nextLine().trim();

          if ("PAUSAR".equalsIgnoreCase(input)) {
            service.pausarExamen(studentId);
            System.out.println(ansiYellow + "\nExamen pausado con éxito. Puede retomar su intento ingresando de nuevo su ID de estudiante." + ansiReset);
            return; // Termina la ejecución
          }

          try {
            answer = new AnswerText(input);
          } catch (IllegalArgumentException e) {
            System.out.println(ansiRed + "[ERROR]: " + e.getMessage() + ansiReset);
          }
        }

        service.responderPregunta(studentId, q.getId(), answer);
      }

      CalificacionDTO resultado = service.finalizarExamen(studentId);
      long endTime = System.currentTimeMillis();
      long durationInSeconds = (endTime - startTime) / 1000;

      System.out.println("\n=== RESULTADOS ===");
      System.out.println("Puntuación Final: " + resultado.puntaje() + " / " + resultado.total());
      System.out.println("Tiempo total del intento: " + durationInSeconds + " segundos");
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