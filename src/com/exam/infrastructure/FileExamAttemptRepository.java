package com.exam.infrastructure;

import com.exam.domain.model.ExamAttempt;
import com.exam.domain.model.Question;
import com.exam.domain.repository.Repositories.ExamAttemptRepository;
import com.exam.domain.repository.Repositories.QuestionBankRepository;
import com.exam.domain.vo.ValueObjects.AnswerText;
import com.exam.domain.vo.ValueObjects.QuestionId;
import com.exam.domain.vo.ValueObjects.StudentId;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Repositorio de intentos que guarda el estado en un archivo temporal
 * para poder recuperar exámenes pausados (HU13).
 */
public class FileExamAttemptRepository implements ExamAttemptRepository {

    private final String persistenceDir = "intentos_pausados";
    private final QuestionBankRepository questionRepo;

    public FileExamAttemptRepository(QuestionBankRepository questionRepo) {
        this.questionRepo = questionRepo;
        File dir = new File(persistenceDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    private File getFileForStudent(StudentId studentId) {
        return new File(persistenceDir, "pausa_" + studentId.value() + ".txt");
    }

    @Override
    public Optional<ExamAttempt> findActiveByStudent(StudentId studentId) {
        File file = getFileForStudent(studentId);
        if (!file.exists()) {
            return Optional.empty();
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String isPausedStr = reader.readLine();
            if (isPausedStr == null) return Optional.empty();
            
            boolean isPaused = Boolean.parseBoolean(isPausedStr);

            List<Question> questions = questionRepo.findAll();
            ExamAttempt attempt = new ExamAttempt(studentId, questions);
            
            if (isPaused) {
                attempt.setPaused(true);
            } else {
                // Si existe el archivo pero no está pausado, podría estar finalizado
                // pero este archivo solo guarda pausados. Lo consideramos pausado por defecto si existe.
            }

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split("\\|", 2);
                if (parts.length == 2) {
                    QuestionId qId = new QuestionId(parts[0]);
                    AnswerText answer = new AnswerText(parts[1]);
                    attempt.responder(qId, answer);
                }
            }
            
            return Optional.of(attempt);
        } catch (IOException e) {
            System.err.println("Error leyendo intento pausado: " + e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public void save(ExamAttempt attempt) {
        File file = getFileForStudent(attempt.getStudentId());
        
        // Si el intento finalizó, ya no necesitamos guardarlo como pausado, borramos el archivo
        if (attempt.estaFinalizado()) {
            if (file.exists()) {
                file.delete();
            }
            return;
        }
        
        // Si no está finalizado (activo o pausado), guardamos/actualizamos el archivo
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            writer.println(attempt.isPaused());
            for (Map.Entry<QuestionId, AnswerText> entry : attempt.getAnswers().entrySet()) {
                writer.println(entry.getKey().value() + "|" + entry.getValue().value());
            }
        } catch (IOException e) {
            System.err.println("Error guardando intento pausado: " + e.getMessage());
        }
    }
}
