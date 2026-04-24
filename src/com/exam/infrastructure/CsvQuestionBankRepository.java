package com.exam.infrastructure;

import com.exam.domain.model.Question;
import com.exam.domain.model.QuestionTypes;
import com.exam.domain.repository.Repositories.QuestionBankRepository;
import com.exam.domain.vo.ValueObjects.AnswerText;
import com.exam.domain.vo.ValueObjects.QuestionId;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Adaptador de infraestructura para cargar las preguntas desde un archivo CSV.
 */
public class CsvQuestionBankRepository implements QuestionBankRepository {
  private final String filePath;

  public CsvQuestionBankRepository(String filePath) {
    this.filePath = filePath;
  }

  @Override
  public List<Question> findAll() {
    List<Question> questions = new ArrayList<>();

    try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
      String line;
      int lineNum = 0;
      while ((line = br.readLine()) != null) {
        lineNum++;
        if (lineNum == 1)
          continue; // Saltar cabecera

        String trimmedLine = line.trim();
        if (trimmedLine.isEmpty() || trimmedLine.replace(";", "").replace(",", "").trim().isEmpty()) {
          continue; // Ignorar líneas vacías o sin texto útil
        }

        try {
          questions.add(parseQuestion(line, lineNum));
        } catch (Exception e) {
          System.err.println("Advertencia - Línea " + lineNum + " omitida: " + e.getMessage());
        }
      }
    } catch (IOException e) {
      System.err.println("Error crítico de lectura del CSV: " + e.getMessage());
    }

    return questions;
  }

  private Question parseQuestion(String line, int idStr) {
    String[] parts = line.split(";");
    if (parts.length < 4 && !parts[0].equals("TF") && !parts[0].equals("FB")) {
      throw new IllegalArgumentException("Datos insuficientes en la fila.");
    }

    QuestionId id = new QuestionId(String.valueOf(idStr));
    String type = parts[0];
    String text = parts[1];
    String options = parts.length > 2 ? parts[2] : "";
    AnswerText correct = new AnswerText(parts.length > 3 ? parts[3] : "");

    return switch (type) {
      case "SC" -> new QuestionTypes.SingleChoiceQuestion(id, text, Arrays.asList(options.split(",")), correct);
      case "MC" -> new QuestionTypes.MultipleChoiceQuestion(id, text, Arrays.asList(options.split(",")), correct);
      case "TF" -> new QuestionTypes.TrueFalseQuestion(id, text, correct);
      case "FB" -> new QuestionTypes.FillBlankQuestion(id, text, correct);
      default -> throw new IllegalArgumentException("Tipo de pregunta desconocido: " + type);
    };
  }
}