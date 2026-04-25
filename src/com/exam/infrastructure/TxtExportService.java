package com.exam.infrastructure;

import com.exam.domain.service.ExportService;
import com.exam.domain.vo.ValueObjects.Calificacion;
import com.exam.domain.vo.ValueObjects.StudentId;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Implementación de infraestructura para exportar calificaciones a un archivo de texto plano.
 */
public class TxtExportService implements ExportService {
    private final Path filePath;

    public TxtExportService(String fileName) {
        this.filePath = Paths.get(fileName);
    }

    @Override
    public void exportResult(StudentId studentId, Calificacion calificacion) {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        String line = String.format("%s | ID: %s | %d/%d%n",
                date,
                studentId.value(),
                calificacion.puntaje(),
                calificacion.total());

        try {
            Files.writeString(filePath, line, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.err.println("Error al exportar calificaciones: " + e.getMessage());
        }
    }
}
