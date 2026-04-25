package com.exam.application.dto;

import com.exam.domain.model.Question;
import com.exam.domain.vo.ValueObjects.StudentId;
import java.util.List;

/**
 * Data Transfer Objects para comunicar la capa de presentación con la
 * aplicación.
 */
public class DTOs {

    public record ExamAttemptDTO(StudentId studentId, List<Question> questions, java.util.Map<com.exam.domain.vo.ValueObjects.QuestionId, com.exam.domain.vo.ValueObjects.AnswerText> answers) {
    }

    public record CalificacionDTO(int puntaje, int total) {
    }
}