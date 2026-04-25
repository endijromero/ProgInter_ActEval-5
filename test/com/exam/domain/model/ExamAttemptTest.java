package com.exam.domain.model;

import com.exam.domain.vo.ValueObjects;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ExamAttemptTest {

    @Test
    void isPaused_CanBeToggled() {
        ValueObjects.StudentId student = new ValueObjects.StudentId("S1");
        List<Question> questions = new ArrayList<>();
        questions.add(new QuestionTypes.TrueFalseQuestion(new ValueObjects.QuestionId("Q1"), "Test", new ValueObjects.AnswerText("V")));

        ExamAttempt attempt = new ExamAttempt(student, questions);
        
        assertFalse(attempt.isPaused(), "El intento no debe estar pausado al inicio");
        
        attempt.setPaused(true);
        assertTrue(attempt.isPaused(), "El intento debe estar pausado");

        attempt.setPaused(false);
        assertFalse(attempt.isPaused(), "El intento no debe estar pausado tras reanudar");
    }

    @Test
    void responder_StoresAnswer() {
        ValueObjects.StudentId student = new ValueObjects.StudentId("S1");
        List<Question> questions = new ArrayList<>();
        ValueObjects.QuestionId q1 = new ValueObjects.QuestionId("Q1");
        questions.add(new QuestionTypes.TrueFalseQuestion(q1, "Test", new ValueObjects.AnswerText("V")));

        ExamAttempt attempt = new ExamAttempt(student, questions);
        attempt.responder(q1, new ValueObjects.AnswerText("F"));

        assertEquals(1, attempt.getAnswers().size());
        assertEquals("F", attempt.getAnswers().get(q1).value());
    }
}
