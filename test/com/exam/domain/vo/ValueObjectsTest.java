package com.exam.domain.vo;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ValueObjectsTest {

    @Test
    void studentId_ValidValue_CreatesInstance() {
        ValueObjects.StudentId studentId = new ValueObjects.StudentId("12345");
        assertEquals("12345", studentId.value());
        assertEquals("[Estudiante-ID: 12345]", studentId.toString());
    }

    @Test
    void studentId_NullOrBlankValue_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new ValueObjects.StudentId(null));
        assertThrows(IllegalArgumentException.class, () -> new ValueObjects.StudentId(""));
        assertThrows(IllegalArgumentException.class, () -> new ValueObjects.StudentId("   "));
    }

    @Test
    void answerText_ValidValue_CreatesInstance() {
        ValueObjects.AnswerText answerText = new ValueObjects.AnswerText("Valid Answer");
        assertEquals("Valid Answer", answerText.value());
    }

    @Test
    void answerText_NullOrBlankValue_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new ValueObjects.AnswerText(null));
        assertThrows(IllegalArgumentException.class, () -> new ValueObjects.AnswerText(""));
        assertThrows(IllegalArgumentException.class, () -> new ValueObjects.AnswerText("   "));
    }

    @Test
    void questionId_ValidValue_CreatesInstance() {
        ValueObjects.QuestionId questionId = new ValueObjects.QuestionId("Q1");
        assertEquals("Q1", questionId.value());
    }

    @Test
    void calificacion_ValidValues_CreatesInstance() {
        ValueObjects.Calificacion calificacion = new ValueObjects.Calificacion(80, 100);
        assertEquals(80, calificacion.puntaje());
        assertEquals(100, calificacion.total());
    }
}
