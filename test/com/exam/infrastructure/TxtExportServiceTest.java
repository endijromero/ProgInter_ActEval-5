package com.exam.infrastructure;

import com.exam.domain.vo.ValueObjects;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TxtExportServiceTest {

    private File tempFile;
    private TxtExportService service;

    @BeforeEach
    void setUp() throws Exception {
        tempFile = File.createTempFile("test_calificaciones", ".txt");
        service = new TxtExportService(tempFile.getAbsolutePath());
    }

    @AfterEach
    void tearDown() {
        if (tempFile.exists()) {
            tempFile.delete();
        }
    }

    @Test
    void exportResult_AppendsToTxtFile() throws Exception {
        ValueObjects.StudentId studentId = new ValueObjects.StudentId("S1");
        ValueObjects.Calificacion calificacion = new ValueObjects.Calificacion(8, 10);

        service.exportResult(studentId, calificacion);

        String content = Files.readString(tempFile.toPath());
        assertTrue(content.contains("ID: S1"));
        assertTrue(content.contains("8/10"));
    }
}
