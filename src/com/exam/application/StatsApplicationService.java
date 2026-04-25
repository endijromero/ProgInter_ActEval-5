package com.exam.application;

import com.exam.domain.model.ExamAttempt;
import com.exam.domain.repository.Repositories.ExamAttemptRepository;
import com.exam.domain.service.GradingService;
import com.exam.domain.vo.ValueObjects.Calificacion;
import java.util.List;

/**
 * Servicio de Aplicación para obtener estadísticas del sistema (HU14).
 */
public class StatsApplicationService {
    private final ExamAttemptRepository attemptRepository;
    private final GradingService gradingService;

    public StatsApplicationService(ExamAttemptRepository attemptRepository, GradingService gradingService) {
        this.attemptRepository = attemptRepository;
        this.gradingService = gradingService;
    }

    public String getGlobalStatistics() {
        List<ExamAttempt> attempts = attemptRepository.findAll();
        int total = attempts.size();
        int abiertos = 0;
        int guardados = 0;
        int cerrados = 0;
        double sumPercentages = 0.0;

        for (ExamAttempt attempt : attempts) {
            if (attempt.estaFinalizado()) {
                cerrados++;
                Calificacion cal = gradingService.calificar(attempt);
                sumPercentages += ((double) cal.puntaje() / cal.total()) * 100;
            } else if (attempt.isPaused()) {
                guardados++;
            } else {
                abiertos++;
            }
        }

        double average = cerrados > 0 ? sumPercentages / cerrados : 0.0;

        StringBuilder sb = new StringBuilder();
        sb.append("--- ESTADÍSTICAS GLOBALES ---\n");
        sb.append("Total de intentos registrados: ").append(total).append("\n");
        sb.append(" - Abiertos (en curso): ").append(abiertos).append("\n");
        sb.append(" - Guardados (pausados): ").append(guardados).append("\n");
        sb.append(" - Cerrados (finalizados): ").append(cerrados).append("\n");
        sb.append(String.format("Grado de asertividad promedio (Cerrados): %.2f%%\n", average));
        return sb.toString();
    }
}
