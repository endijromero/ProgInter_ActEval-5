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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Repositorio de intentos que guarda el estado en un archivo temporal
 * para poder recuperar exámenes pausados (HU13) y en memoria (HU14).
 */
public class FileExamAttemptRepository implements ExamAttemptRepository {

    private final String persistenceDir = "intentos_pausados";
    private final QuestionBankRepository questionRepo;
    private final Map<StudentId, ExamAttempt> activeMemory = new HashMap<>();

    public FileExamAttemptRepository(QuestionBankRepository questionRepo) {
        this.questionRepo = questionRepo;
        File dir = new File(persistenceDir);
        if (!dir.exists()) {
            dir.mkdirs();
        } else {
            preloadPausedAttempts(dir);
        }
    }

    private void preloadPausedAttempts(File dir) {
        File[] files = dir.listFiles((d, name) -> name.startsWith("pausa_") && name.endsWith(".txt"));
        if (files != null) {
            for (File file : files) {
                String name = file.getName();
                String id = name.substring("pausa_".length(), name.length() - 4);
                StudentId studentId = new StudentId(id);
                loadFromFile(file, studentId).ifPresent(attempt -> activeMemory.put(studentId, attempt));
            }
        }
    }

    private File getFileForStudent(StudentId studentId) {
        return new File(persistenceDir, "pausa_" + studentId.value() + ".txt");
    }

    private Optional<ExamAttempt> loadFromFile(File file, StudentId studentId) {
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
    public Optional<ExamAttempt> findActiveByStudent(StudentId studentId) {
        ExamAttempt memoryAttempt = activeMemory.get(studentId);
        if (memoryAttempt != null && !memoryAttempt.estaFinalizado()) {
            return Optional.of(memoryAttempt);
        }

        Optional<ExamAttempt> fileAttempt = loadFromFile(getFileForStudent(studentId), studentId);
        fileAttempt.ifPresent(attempt -> activeMemory.put(studentId, attempt));
        return fileAttempt;
    }

    @Override
    public void save(ExamAttempt attempt) {
        activeMemory.put(attempt.getStudentId(), attempt);
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

    @Override
    public List<ExamAttempt> findAll() {
        return new ArrayList<>(activeMemory.values());
    }
}
