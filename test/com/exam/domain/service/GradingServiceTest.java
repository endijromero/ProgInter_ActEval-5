package com.exam.domain.service;

import com.exam.domain.model.ExamAttempt;
import com.exam.domain.model.Question;
import com.exam.domain.model.QuestionTypes;
import com.exam.domain.vo.ValueObjects;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GradingServiceTest {

    @Test
    void calificar_CalculatesScoreCorrectly() {
        ValueObjects.StudentId student = new ValueObjects.StudentId("S1");
        List<Question> questions = new ArrayList<>();
        
        ValueObjects.QuestionId q1 = new ValueObjects.QuestionId("Q1");
        ValueObjects.QuestionId q2 = new ValueObjects.QuestionId("Q2");
        
        questions.add(new QuestionTypes.TrueFalseQuestion(q1, "Test 1", new ValueObjects.AnswerText("V")));
        questions.add(new QuestionTypes.TrueFalseQuestion(q2, "Test 2", new ValueObjects.AnswerText("F")));

        ExamAttempt attempt = new ExamAttempt(student, questions);
        attempt.responder(q1, new ValueObjects.AnswerText("V")); // Correcta
        attempt.responder(q2, new ValueObjects.AnswerText("V")); // Incorrecta

        GradingService service = new GradingService();
        ValueObjects.Calificacion result = service.calificar(attempt);

        assertEquals(1, result.puntaje(), "Debería tener 1 respuesta correcta");
        assertEquals(2, result.total(), "El total de preguntas es 2");
    }
}
