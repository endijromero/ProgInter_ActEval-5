package com.exam.domain.model;

import com.exam.domain.vo.ValueObjects;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class QuestionTypesTest {

    @Test
    void singleChoiceQuestion_isCorrect_Works() {
        QuestionTypes.SingleChoiceQuestion q = new QuestionTypes.SingleChoiceQuestion(
            new ValueObjects.QuestionId("Q1"),
            "Test",
            Arrays.asList("A", "B", "C"),
            new ValueObjects.AnswerText("B")
        );

        assertTrue(q.isCorrect(new ValueObjects.AnswerText("B")));
        assertFalse(q.isCorrect(new ValueObjects.AnswerText("A")));
    }

    @Test
    void multipleChoiceQuestion_isCorrect_Works() {
        QuestionTypes.MultipleChoiceQuestion q = new QuestionTypes.MultipleChoiceQuestion(
            new ValueObjects.QuestionId("Q1"),
            "Test",
            Arrays.asList("A", "B", "C"),
            new ValueObjects.AnswerText("A,C")
        );

        assertTrue(q.isCorrect(new ValueObjects.AnswerText("A,C")));
        assertTrue(q.isCorrect(new ValueObjects.AnswerText("C,A"))); // Debería funcionar independientemente del orden
        assertFalse(q.isCorrect(new ValueObjects.AnswerText("A")));
        assertFalse(q.isCorrect(new ValueObjects.AnswerText("B,C")));
    }

    @Test
    void trueFalseQuestion_isCorrect_Works() {
        QuestionTypes.TrueFalseQuestion q = new QuestionTypes.TrueFalseQuestion(
            new ValueObjects.QuestionId("Q1"),
            "Test",
            new ValueObjects.AnswerText("V")
        );

        assertTrue(q.isCorrect(new ValueObjects.AnswerText("V")));
        assertTrue(q.isCorrect(new ValueObjects.AnswerText("v"))); // case insensitive
        assertFalse(q.isCorrect(new ValueObjects.AnswerText("F")));
    }

    @Test
    void fillBlankQuestion_isCorrect_Works() {
        QuestionTypes.FillBlankQuestion q = new QuestionTypes.FillBlankQuestion(
            new ValueObjects.QuestionId("Q1"),
            "Test ___",
            new ValueObjects.AnswerText("Word")
        );

        assertTrue(q.isCorrect(new ValueObjects.AnswerText("Word")));
        assertTrue(q.isCorrect(new ValueObjects.AnswerText("word"))); // case insensitive
        assertFalse(q.isCorrect(new ValueObjects.AnswerText("wrong")));
    }

    @Test
    void questionBaseClassGetters_Work() {
        QuestionTypes.TrueFalseQuestion q = new QuestionTypes.TrueFalseQuestion(
            new ValueObjects.QuestionId("Q1"),
            "Test",
            new ValueObjects.AnswerText("V")
        );
        assertEquals("Q1", q.getId().value());
        assertEquals("Test", q.getText());
    }
}
