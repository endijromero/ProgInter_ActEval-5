# Implementación de Estrategia de Pruebas Integral

Este documento describe el plan para implementar una estrategia de pruebas unitarias y End-to-End (E2E) para alcanzar más del 80% de cobertura y validar las Historias de Usuario desarrolladas en el proyecto. 

## > [!IMPORTANT] User Review Required

Tras verificar el entorno, se detectó que herramientas de construcción estándar como **Maven** o **Gradle** no están instaladas en el sistema. 

Para poder ejecutar pruebas unitarias (JUnit 5) y generar reportes de cobertura (JaCoCo) sin modificar los requerimientos actuales del sistema, propongo la creación de un script de Bash (`run_tests.sh`) que:
1. Descargue automáticamente los archivos `.jar` necesarios de JUnit 5 Standalone y JaCoCo mediante `curl` en una carpeta `lib/`.
2. Compile el código fuente y las clases de prueba.
3. Ejecute las pruebas utilizando el agente de JaCoCo.
4. Genere el reporte HTML de cobertura.

**¿Está de acuerdo con este enfoque mediante script, o prefiere que el proyecto se configure y migre exclusivamente con un `pom.xml` (requeriría que usted instale Maven localmente)?** Por defecto, avanzaré con la solución del script para asegurar que las pruebas corran inmediatamente en este entorno.

## Proposed Changes

---

### Tests Unitarios y Cobertura (Code Coverage)

Se creará una nueva carpeta `test/` (siguiendo la misma estructura de paquetes `com/exam/...`) en la raíz del proyecto para alojar las pruebas unitarias.

#### [NEW] test/com/exam/domain/vo/ValueObjectsTest.java
Pruebas para `StudentId` y `AnswerText` (validación de vacíos para HU03 y HU07).
#### [NEW] test/com/exam/infrastructure/CsvQuestionBankRepositoryTest.java
Pruebas de tolerancia a fallos en lectura de CSV (HU08) utilizando un archivo CSV simulado.
#### [NEW] test/com/exam/application/ExamApplicationServiceTest.java
Pruebas de la lógica de aplicación, límite de 50 preguntas (HU09).
#### [NEW] test/com/exam/domain/model/ExamAttemptTest.java
Pruebas de estado de pausa y continuación (HU13).
#### [NEW] test/com/exam/domain/service/GradingServiceTest.java
Pruebas de calificación.

### Tests E2E (Requirement Coverage)

Las validaciones funcionales se lograrán interceptando y simulando la Entrada/Salida estándar (`System.in`, `System.out`).

#### [NEW] test/com/exam/presentation/ConsoleUIE2ETest.java
Simulará el flujo completo de la aplicación de consola.
- **HU02**: Verificará si se imprime `"Estado: APROBADO"` o `"Estado: REPROBADO"` analizando el texto impreso.
- **HU10**: Validará que se imprima el texto del tiempo ("Tiempo total del intento").
- **HU12**: Verificará la creación y el contenido de `calificaciones.txt` tras finalizar la prueba.
- **HU14**: Verificará el inicio de sesión de Docente con el PIN "1234" y la impresión de estadísticas.

#### [NEW] test/com/exam/presentation/SwingUIE2ETest.java
- **HU15**: Simulará un intento fallido de inicio de sesión gráfico (en un entorno sin cabeza `Headless` controlado) para asegurar que la clase Swing maneja las excepciones correctamente sin colapsar.

### Scripts de Ejecución

#### [NEW] run_tests.sh
Script orquestador de pruebas en la raíz del proyecto. Descargará dependencias (`junit-platform-console-standalone.jar`, `jacocoagent.jar`, `jacococli.jar`), compilará los tests en un directorio `bin-test/`, los ejecutará y generará el reporte en `coverage-report/index.html`.

## Verification Plan

### Automated Tests
- Ejecutar `./run_tests.sh` en la consola.
- El script deberá imprimir el resultado exitoso de JUnit 5 (alrededor de 15-20 pruebas pasadas).
- El reporte de JaCoCo generado en `coverage-report/index.html` indicará una cobertura global superior al 80%.

### Manual Verification
- Puede abrir el archivo `coverage-report/index.html` generado localmente en un navegador para observar las ramas de código analizadas visualmente.
