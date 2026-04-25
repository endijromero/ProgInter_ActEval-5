# HU14: Panel Administrativo Provisional

## Resumen de Cambios

Se ha implementado satisfactoriamente la historia de usuario HU14 "Panel Administrativo Provisional". El flujo de inicio de la aplicación en consola ahora diferencia entre estudiantes y docentes, y estos últimos tienen acceso a un reporte estadístico global de todos los intentos del sistema.

### Nivel de Dominio
- **`ExamAttemptRepository`:** Se expandió la interfaz para incluir un método `List<ExamAttempt> findAll()`, necesario para recolectar las analíticas del repositorio en un momento determinado.

### Nivel de Infraestructura
- **`FileExamAttemptRepository`:** Modificamos el repositorio persistente para incorporar un mapa en memoria activa (`activeMemory`). Al instanciar la aplicación, precargamos en este mapa todos los "intentos_pausados" en disco. Cada vez que se guarda o lee un intento, el mapa en memoria se sincroniza para mantener un registro de **TODOS** los intentos (Abiertos, Guardados y Cerrados) que han interactuado con la plataforma en el ciclo de vida de la ejecución.

### Nivel de Aplicación
- **`StatsApplicationService`:** Se ha introducido este nuevo servicio orquestador, cuya única responsabilidad es recuperar todo el acervo del repositorio e iterar a través de él para producir estadísticas calculadas: la cantidad total de intentos registrados, desglosados en sus tres estados posibles, y el grado promedio de acierto (en porcentaje) derivado exclusivamente de los exámenes cerrados/finalizados. Utiliza internamente el servicio `GradingService`.

### Nivel de Presentación
- **`ConsoleUI`:** Modificamos la interfaz de consola para actuar como un _router_ principal al arrancar. Ahora pregunta qué perfil tiene el usuario.
  - El flujo de estudiante opera sin ningún cambio perceptible.
  - El flujo de docente (que requiere el PIN predeterminado de validación `1234`) imprime por pantalla el informe consolidado provisto por `StatsApplicationService`.

## Verificación

El proyecto ha sido compilado correctamente con el último grupo de cambios (`javac -d bin $(find src -name "*.java")`) y no se han encontrado errores de sintaxis.

El comportamiento ha sido verificado lógicamente para cumplir los criterios de aceptación:
1. Menú 2 opciones: `(1) Soy Estudiante`, `(2) Soy Docente`.
2. Verificación de PIN en caso de perfil docente.
3. Listado de estadísticas de intentos cargados a través del repositorio y cálculo de nota media.
