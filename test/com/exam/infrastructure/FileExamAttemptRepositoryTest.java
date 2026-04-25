package com.exam.infrastructure;

import com.exam.domain.model.ExamAttempt;
import com.exam.domain.model.Question;
import com.exam.domain.model.QuestionTypes;
import com.exam.domain.vo.ValueObjects;
import com.exam.domain.repository.Repositories;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileExamAttemptRepositoryTest {

    private File tempDir;
    private FileExamAttemptRepository repo;

    @BeforeEach
    void setUp() throws Exception {
        tempDir = new File("intentos_pausados");
        if (!tempDir.exists()) tempDir.mkdirs();
        
        Repositories.QuestionBankRepository qRepo = new Repositories.QuestionBankRepository() {
            @Override public List<Question> findAll() { return new ArrayList<>(); }
        };
        repo = new FileExamAttemptRepository(qRepo);
    }

    @AfterEach
    void tearDown() {
        for (File f : tempDir.listFiles()) {
            f.delete();
        }
        tempDir.delete();
    }

    @Test
    void saveAndFindActiveByStudent_Works() {
        ValueObjects.StudentId studentId = new ValueObjects.StudentId("S1");
        List<Question> questions = new ArrayList<>();
        questions.add(new QuestionTypes.TrueFalseQuestion(
            new ValueObjects.QuestionId("Q1"), "Test", new ValueObjects.AnswerText("V")));

        ExamAttempt attempt = new ExamAttempt(studentId, questions);
        attempt.responder(new ValueObjects.QuestionId("Q1"), new ValueObjects.AnswerText("V"));
        
        repo.save(attempt);

        Optional<ExamAttempt> loadedOpt = repo.findActiveByStudent(studentId);
        assertTrue(loadedOpt.isPresent());
        ExamAttempt loaded = loadedOpt.get();
        assertEquals(studentId, loaded.getStudentId());
        assertEquals(1, loaded.getAnswers().size());
    }

    @Test
    void findActiveByStudent_NonExistent_ReturnsEmpty() {
        ValueObjects.StudentId studentId = new ValueObjects.StudentId("NotExists");
        Optional<ExamAttempt> attempt = repo.findActiveByStudent(studentId);
        assertTrue(attempt.isEmpty());
    }

    @Test
    void loadFromFile_CorruptedFile_ReturnsEmpty() throws Exception {
        ValueObjects.StudentId studentId = new ValueObjects.StudentId("Corrupt");
        File corrupt = new File(tempDir, "pausa_Corrupt.txt");
        java.nio.file.Files.writeString(corrupt.toPath(), "true\nbad_line\n");
        
        Optional<ExamAttempt> attempt = repo.findActiveByStudent(studentId);
        assertTrue(attempt.isEmpty() || attempt.get().getAnswers().isEmpty());
    }
}
