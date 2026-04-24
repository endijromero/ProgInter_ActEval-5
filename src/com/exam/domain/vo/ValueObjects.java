package com.exam.domain.vo;

/**
 * Agrupa los Value Objects (Objetos de Valor) del dominio.
 * Utiliza records de Java para garantizar la inmutabilidad (Evans, 2003).
 */
public class ValueObjects {

    public record StudentId(String value) {
        public StudentId {
            if (value == null || value.isBlank()) {
                throw new IllegalArgumentException("El ID del estudiante no puede estar vacío.");
            }
        }

        @Override
        public String toString() {
            return "[Estudiante-ID: " + value + "]";
        }
    }

    public record QuestionId(String value) {
    }

    public record AnswerText(String value) {
    }

    public record Calificacion(int puntaje, int total) {
    }
}