package com.exam;

import com.exam.application.ExamApplicationService;
import com.exam.domain.repository.Repositories.ExamAttemptRepository;
import com.exam.domain.repository.Repositories.QuestionBankRepository;
import com.exam.domain.service.AttemptManager;
import com.exam.domain.service.GradingService;
import com.exam.infrastructure.CsvQuestionBankRepository;
import com.exam.infrastructure.FileExamAttemptRepository;
import com.exam.presentation.ConsoleUI;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Configuración de inyección de dependencias y punto de entrada principal
 * (Martin, 2017).
 */
public class Main {

  public static void main(String[] args) {
    String csvPath = "banco_preguntas_test.csv";
    generarCsvDePrueba(csvPath);

    // 1. Instanciación de Infraestructura y Repositorios
    QuestionBankRepository questionRepo = new CsvQuestionBankRepository(csvPath);
    ExamAttemptRepository attemptRepo = new FileExamAttemptRepository(questionRepo);

    // 2. Instanciación de Servicios de Dominio
    AttemptManager attemptManager = new AttemptManager(attemptRepo);
    GradingService gradingService = new GradingService();
    com.exam.domain.service.ExportService exportService = new com.exam.infrastructure.TxtExportService("calificaciones.txt");

    // 3. Instanciación de la Capa de Aplicación
    ExamApplicationService appService = new ExamApplicationService(
        questionRepo, attemptRepo, attemptManager, gradingService, exportService);

    // 4. Instanciación e inicio de la Presentación
    ConsoleUI ui = new ConsoleUI(appService);
    ui.start();
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
      writer.println("INVALIDO;Esta fila debe fallar para probar el manejo de errores;;");
    } catch (IOException e) {
      System.err.println("Error al crear el CSV de prueba: " + e.getMessage());
    }
  }
}