package com.exam.infrastructure;

import com.exam.domain.model.Question;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CsvQuestionBankRepositoryTest {

    private File tempCsvFile;
    private CsvQuestionBankRepository repository;

    @BeforeEach
    void setUp() throws IOException {
        tempCsvFile = File.createTempFile("test_questions", ".csv");
        repository = new CsvQuestionBankRepository(tempCsvFile.getAbsolutePath());
    }

    @AfterEach
    void tearDown() {
        if (tempCsvFile.exists()) {
            tempCsvFile.delete();
        }
    }

    @Test
    void findAll_SkipsMalformedAndEmptyLines_ReturnsValidQuestions() throws IOException {
        try (FileWriter writer = new FileWriter(tempCsvFile)) {
            writer.write("Tipo;Enunciado;Opciones;RespuestaCorrecta\n");
            writer.write("SC;¿Capital de Francia?;Paris,Roma,Madrid;Paris\n");
            writer.write("\n"); // Línea vacía
            writer.write("   \n"); // Línea con espacios
            writer.write(";;;\n"); // Línea malformada (sin texto)
            writer.write("UNKNOWN;Malformed type;A,B;A\n"); // Tipo desconocido
            writer.write("TF;¿El sol es una estrella?;;V\n");
        }

        List<Question> questions = repository.findAll();

        assertEquals(2, questions.size(), "Debería cargar solo 2 preguntas válidas ignorando las malformadas o vacías");
        assertEquals("¿Capital de Francia?", questions.get(0).getText());
        assertEquals("¿El sol es una estrella?", questions.get(1).getText());
    }

    @Test
    void findAll_NonExistentFile_ReturnsEmptyList() {
        CsvQuestionBankRepository badRepo = new CsvQuestionBankRepository("non_existent_file.csv");
        List<Question> questions = badRepo.findAll();
        assertTrue(questions.isEmpty());
    }
}
