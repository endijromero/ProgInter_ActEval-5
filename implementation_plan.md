# Implementación de Pausa de Exámen (HU13)

La Historia de Usuario 13 requiere permitir al estudiante ingresar la palabra "PAUSAR" durante el examen para abortar el proceso momentáneamente. El estado del examen debe persistirse en el disco (archivo temporal) para que al volver a ingresar con su ID de estudiante, el examen se reanude omitiendo las preguntas que ya había contestado.

## User Review Required

Se implementará un archivo de guardado local llamado `pausa_<student_id>.txt` o un archivo general para todos usando un formato simple para evitar añadir librerías externas como Gson/Jackson (ya que el proyecto usa Java nativo sin Maven/Gradle).

## Open Questions

Ninguna por el momento. La estructura actual permite modificar la infraestructura y el dominio de manera aislada siguiendo Clean Architecture.

## Proposed Changes

---

### Domain Model

Se modificará el modelo de `ExamAttempt` para soportar el estado "pausado" y para poder recuperar las respuestas.

#### [MODIFY] `src/com/exam/domain/model/ExamAttempt.java`
- Añadir campo `private boolean isPaused = false;`
- Añadir setter `public void setPaused(boolean paused)`
- Añadir getter `public boolean isPaused()`
- Asegurar que al finalizar el examen `isPaused` pase a ser `false`.

#### [MODIFY] `src/com/exam/domain/service/AttemptManager.java`
- Modificar `verificarIntentoActivo` para permitir iniciar/continuar un intento si se encuentra activo **pero pausado**.

---

### Infrastructure Layer

Reemplazaremos `InMemoryExamAttemptRepository` por una implementación que persiste los datos en un archivo para cumplir el requerimiento de guardar el estado al salir del software.

#### [NEW] `src/com/exam/infrastructure/FileExamAttemptRepository.java`
- Clase que implemente `ExamAttemptRepository`.
- Al guardar (`save`), serializará el ID del estudiante, su estado de finalizado/pausado y las respuestas contestadas en un archivo de texto simple (`intentos_pausados.csv` o similar).
- Al buscar (`findActiveByStudent`), deserializará este archivo leyendo las respuestas previamente seleccionadas.
- Requerirá de un `QuestionBankRepository` para rehidratar la lista original de preguntas al reconstruir el `ExamAttempt`.

---

### Application Layer

Los DTOs y el Application Service necesitan ser adaptados para transferir el estado de las preguntas contestadas.

#### [MODIFY] `src/com/exam/application/dto/DTOs.java`
- Modificar `ExamAttemptDTO` para incluir las respuestas ya registradas:
  `public record ExamAttemptDTO(StudentId studentId, List<Question> questions, java.util.Map<QuestionId, AnswerText> answers) {}`

#### [MODIFY] `src/com/exam/application/ExamApplicationService.java`
- Modificar `iniciarExamen`: Si hay un intento pausado, debe reactivarlo (`setPaused(false)`) y retornarlo.
- Agregar un método `pausarExamen(StudentId studentId)`: Busca el intento activo, cambia su estado a pausado, y guarda en el repositorio.

---

### Presentation Layer

El menú de consola necesita interceptar la entrada de usuario para procesar el comando "PAUSAR" y omitir las preguntas que el DTO indique que ya están respondidas.

#### [MODIFY] `src/com/exam/presentation/ConsoleUI.java`
- Al recibir la respuesta a una pregunta, verificar si la entrada (ignorando mayúsculas/minúsculas) es igual a `"PAUSAR"`.
- Si lo es, invocar `service.pausarExamen(...)`, mostrar un mensaje de despedida y romper el ciclo del examen usando un `return` temprano o un `break`.
- Antes de mostrar una pregunta, revisar si ya existe en el mapa de respuestas del `ExamAttemptDTO`. Si existe, omitirla y sumar al contador.

#### [MODIFY] `src/com/exam/Main.java`
- Actualizar la inyección de dependencias para usar el nuevo `FileExamAttemptRepository(questionRepo)` en lugar de `InMemoryExamAttemptRepository`.

## Verification Plan

### Automated Tests
- Compilación del proyecto (`javac -d bin src/com/exam/**/*.java src/com/exam/*.java`).

### Manual Verification
- Iniciar la aplicación.
- Ingresar ID de estudiante (ej. `E001`).
- Contestar 1 o 2 preguntas y luego ingresar `PAUSAR`.
- La aplicación se debe cerrar graciosamente sin generar nota final.
- Verificar que se cree un archivo persistente en disco (ej. `intentos_pausados.csv`).
- Reiniciar la aplicación.
- Ingresar el mismo ID `E001`.
- Verificar que el examen inicie saltándose las primeras preguntas ya contestadas, sin error.
- Terminar el examen normalmente y verificar que la calificación tome en cuenta todo el set de respuestas y muestre el mensaje de "APROBADO/REPROBADO".
