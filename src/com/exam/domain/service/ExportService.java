package com.exam.domain.service;

import com.exam.domain.vo.ValueObjects.Calificacion;
import com.exam.domain.vo.ValueObjects.StudentId;

/**
 * Contrato de servicio de dominio para exportar los resultados de los exámenes finalizados.
 */
public interface ExportService {
    /**
     * Exporta el resultado de un examen finalizado.
     *
     * @param studentId    ID del estudiante.
     * @param calificacion Calificación obtenida.
     */
    void exportResult(StudentId studentId, Calificacion calificacion);
}
