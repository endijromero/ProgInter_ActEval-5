package com.exam.application.dto;

// import com.exam.domain.model.Question;
import com.exam.domain.vo.ValueObjects;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DTOsTest {

    @Test
    void examAttemptDTO_CreatesCorrectly() {
        ValueObjects.StudentId id = new ValueObjects.StudentId("S1");
        DTOs.ExamAttemptDTO dto = new DTOs.ExamAttemptDTO(id, new ArrayList<>(), new HashMap<>());

        assertEquals(id, dto.studentId());
        assertNotNull(dto.questions());
        assertNotNull(dto.answers());
    }

    @Test
    void calificacionDTO_CreatesCorrectly() {
        DTOs.CalificacionDTO dto = new DTOs.CalificacionDTO(5, 10);

        assertEquals(5, dto.puntaje());
        assertEquals(10, dto.total());
    }
}
