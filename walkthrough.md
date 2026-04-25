# Implementación Completada: Pausa de Examen (HU13)

He completado la implementación de la Historia de Usuario 13, que permite a los estudiantes pausar su examen y retomarlo posteriormente.

## Cambios Realizados

### 1. Modelo de Dominio Actualizado
- Se añadió el estado `isPaused` en el aggregate root `ExamAttempt.java`.
- Se flexibilizó la regla de negocio en `AttemptManager.java`, permitiendo iniciar un examen si existe un intento en curso **pero está pausado**.

### 2. Persistencia en Disco
- Se reemplazó el repositorio en memoria por `FileExamAttemptRepository.java` en la capa de infraestructura.
- Este nuevo repositorio guarda los intentos pausados en la carpeta `intentos_pausados/` usando un formato simple de texto plano (`pausa_<ID>.txt`), en el que se persiste el estado de pausa y cada pregunta respondida hasta el momento.
- Se configuró la inyección de dependencias en `Main.java` para utilizar este nuevo repositorio.

### 3. Lógica de Aplicación
- `ExamApplicationService.java` ahora cuenta con el método `pausarExamen`.
- El método `iniciarExamen` ahora verifica si el intento está pausado. Si es así, lo reanuda (`setPaused(false)`) y retorna el progreso cargando las respuestas previas en el `ExamAttemptDTO`.

### 4. Interfaz de Usuario
- El ciclo principal de `ConsoleUI.java` ahora comprueba si una pregunta ya fue contestada omitiéndola automáticamente.
- Se agregó una verificación al leer la consola que intercepta la entrada `"PAUSAR"`, disparando la lógica de guardado y saliendo graciosamente de la aplicación.

## Resultados de Validación
- ✔️ Compilación exitosa sin errores y sin requerir dependencias externas de librerías JSON, manteniendo todo puramente nativo.
- ✔️ Las respuestas se reanudan exitosamente sin romper el flujo del examen.
- ✔️ Las excepciones relacionadas al formato de entrada siguen funcionales para el resto del examen.
