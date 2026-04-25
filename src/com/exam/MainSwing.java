package com.exam;

import com.exam.application.ExamApplicationService;
import com.exam.domain.repository.Repositories.ExamAttemptRepository;
import com.exam.domain.repository.Repositories.QuestionBankRepository;
import com.exam.domain.service.AttemptManager;
import com.exam.domain.service.GradingService;
import com.exam.infrastructure.CsvQuestionBankRepository;
import com.exam.infrastructure.InMemoryExamAttemptRepository;
import com.exam.presentation.SwingUI;

import javax.swing.SwingUtilities;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Nueva clase Main configurada para ejecutar la interfaz gráfica basada en
 * Swing.
 * Realiza la inyección manual de dependencias y carga el caso de uso central.
 */
public class MainSwing {

  public static void main(String[] args) {
    String csvPath = "banco_preguntas_test.csv";
    generarCsvDePrueba(csvPath);

    // 1. Inyección de Dependencias (Capa de Infraestructura)
    QuestionBankRepository questionRepo = new CsvQuestionBankRepository(csvPath);
    ExamAttemptRepository attemptRepo = new InMemoryExamAttemptRepository();

    // 2. Servicios de Dominio
    AttemptManager attemptManager = new AttemptManager(attemptRepo);
    GradingService gradingService = new GradingService();
    com.exam.domain.service.ExportService exportService = new com.exam.infrastructure.TxtExportService("calificaciones.txt");

    // 3. Casos de Uso (Application Service)
    ExamApplicationService appService = new ExamApplicationService(
        questionRepo, attemptRepo, attemptManager, gradingService, exportService);

    // 4. Lanzamiento asíncrono seguro de la interfaz gráfica Swing
    SwingUtilities.invokeLater(() -> {
      try {
        // Para darle un aspecto moderno nativo al sistema operativo
        javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
      } catch (Exception e) {
        e.printStackTrace();
      }

      SwingUI ui = new SwingUI(appService);
      ui.start();
    });
  }

  private static void generarCsvDePrueba(String path) {
    try (PrintWriter writer = new PrintWriter(new FileWriter(path))) {
      writer.println("TIPO;ENUNCIADO;OPCIONES;RESPUESTAS_CORRECTAS");
      writer.println("SC;¿Cuál es la capital de Colombia?;Bogotá,Lima,Quito;Bogotá");
      writer.println("TF;Java es un lenguaje de programación orientado a objetos;;V");
      writer.println(
          "FB;El principio SOLID que dicta que una clase debe tener una sola razón para cambiar es el principio de _______;;responsabilidad unica");
      writer.println(
          "MC;¿Cuáles son lenguajes de programación tipados estáticamente?;Java,Python,C++,JavaScript;Java,C++");
    } catch (IOException e) {
      System.err.println("Error al crear el CSV de prueba: " + e.getMessage());
    }
  }
}