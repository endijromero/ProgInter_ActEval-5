# Implementar Pausa de Examen (HU13)

- [x] Modificar modelo de dominio
  - [x] Añadir estado pausado a `ExamAttempt.java`
  - [x] Modificar regla en `AttemptManager.java` para permitir reanudar
- [/] Implementar nuevo repositorio de infraestructura
  - [ ] Crear `FileExamAttemptRepository.java` (serializa/deserializa desde CSV/TXT local)
- [x] Modificar lógica de aplicación
  - [x] Actualizar `DTOs.java` para incluir respuestas contestadas
  - [x] Modificar `ExamApplicationService.java` (métodos `iniciarExamen` y nuevo `pausarExamen`)
- [x] Modificar Interfaz de Consola y Configuración
  - [x] Modificar `ConsoleUI.java` (capturar "PAUSAR", omitir preguntas respondidas, detener ejecución)
  - [x] Modificar `Main.java` (reemplazar repositorio en memoria por persistente de archivo)
- [x] Validar y Testear
  - [x] Compilar y verificar funcionamiento de pausado y reanudado
