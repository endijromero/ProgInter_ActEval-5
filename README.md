# Sistema de Gestión de Exámenes (Actividad Evaluable 4)

Este proyecto es una aplicación de consola desarrollada en **Java** que implementa un sistema para la carga, realización y calificación automática de exámenes. El desarrollo se ha realizado siguiendo los principios **SOLID**, el diseño guiado por el dominio (**DDD**) y los pilares de la programación orientada a objetos (**POO**).

---

## 🎯 Objetivo
El objetivo es construir una solución mantenible, extensible y desacoplada que permita a docentes cargar bancos de preguntas desde archivos CSV y a estudiantes realizar exámenes con diferentes tipos de preguntas, garantizando la consistencia del negocio mediante reglas de dominio claras.

## 🚀 Guía de Uso

### Requisitos
- **Java JDK 17** o superior.
- Banco de preguntas en formato CSV (la aplicación genera uno de prueba automáticamente al iniciar).

### Compilación y Ejecución
Desde la raíz del proyecto, ejecute los siguientes comandos:

```bash
# Compilar el proyecto
find . -name "*.java" > sources.txt
javac -d bin @sources.txt

# Ejecutar la aplicación (Modo Consola)
java -cp bin com.exam.Main
```

### Ejecución con Interfaz Gráfica (Swing)
Para ejecutar la versión que posee botones y ventanas gráficas (Swing), existen dos alternativas:

**Opción 1: Mediante comando (Recomendado)**
Puede ejecutar directamente la clase `MainSwing` ya configurada para este fin:
```bash
java -cp bin com.exam.MainSwing
```

**Opción 2: Cambios en el código (`Main.java`)**
En caso de querer iniciar desde la clase principal `Main.java`, localice y reemplace el bloque de instanciación de la presentación en dicho archivo de la siguiente manera:

```java
    // Comente o elimine estas líneas referentes a la consola:
    // ConsoleUI ui = new ConsoleUI(appService);
    // ui.start();

    // Descomente o agregue las líneas referentes a Swing:
    com.exam.presentation.SwingUI ui = new com.exam.presentation.SwingUI(appService);
    ui.start();
```

---

## 🏗️ Arquitectura del Software (DDD)

La solución se ha organizado en cuatro capas principales para asegurar el desacoplamiento:

1.  **Capa de Dominio (`com.exam.domain`)**:
    *   **Modelos**: Entidades y Raíces de Agregado como `Question`, `ExamAttempt` y su jerarquía.
    *   **Objetos de Valor (VO)**: Tipos inmutables como `QuestionId`, `StudentId`, y `AnswerText` que encapsulan lógica de validación.
    *   **Servicios de Dominio**: Lógica compleja que involucra múltiples entidades, como `GradingService` (calificación) y `AttemptManager` (gestión de concurrencia de intentos).
    *   **Repositorios (Interfaces)**: Definición de contratos para la persistencia de datos.

2.  **Capa de Aplicación (`com.exam.application`)**:
    *   Actúa como orquestador de los casos de uso (`Iniciar examen`, `Responder pregunta`, `Finalizar examen`).
    *   Utiliza **DTOs** (Data Transfer Objects) para comunicar la aplicación con la interfaz de usuario sin exponer el modelo de dominio interno.

3.  **Capa de Infraestructura (`com.exam.infrastructure`)**:
    *   Implementaciones concretas de la persistencia: `CsvQuestionBankRepository` y `InMemoryExamAttemptRepository`.
    *   Manejo de archivos y parsing de datos externos.

4.  **Capa de Presentación (`com.exam.presentation`)**:
    *   Interfaz de consola interactiva (`ConsoleUI`) que gestiona la interacción con el usuario y captura entradas.

---

## ⚖️ Aplicación de Principios SOLID

1.  **Single Responsibility Principle (SRP)**: Cada componente tiene una única razón para cambiar. Los repositorios solo manejan datos, `GradingService` solo califica y `ConsoleUI` solo gestiona E/S.
2.  **Open/Closed Principle (OCP)**: El sistema es abierto a la extensión pero cerrado a la modificación. Se pueden añadir nuevos tipos de preguntas (ej. Ordenamiento) simplemente extendiendo la clase base `Question` sin tocar la lógica de los servicios.
3.  **Liskov Substitution Principle (LSP)**: Cualquier subtipo de `Question` (Única respuesta, Múltiple respuesta, F/V) puede sustituir a su clase padre sin alterar el comportamiento correcto del programa.
4.  **Interface Segregation Principle (ISP)**: Se utilizan interfaces específicas para los repositorios (`QuestionBankRepository`, `ExamAttemptRepository`), evitando que implementaciones concretas dependan de métodos que no necesitan.
5.  **Dependency Inversion Principle (DIP)**: La capa de aplicación y el dominio dependen de abstracciones (interfaces de repositorio), no de implementaciones de bajo nivel. La inyección de dependencias se realiza en el `Main`.

---

## 📋 Historias de Usuario Soportadas
1. **Carga de preguntas**: Importación desde CSV con manejo de errores de formato.
2. **Tipos de preguntas**: Única respuesta, opción múltiple, verdadero/falso y completar frases.
3. **Validación de intentos**: Prevención de múltiples intentos simultáneos para un mismo estudiante.
4. **Calificación Automática**: Evaluación inmediata al finalizar el examen.
5. **Interfaz de Usuario**: Menús claros y flujos de navegación lógicos.

---

## 🛠️ Patrones de Diseño Utilizados

*   **Factory Pattern**: Se utiliza una `QuestionFactory` (en la capa de infraestructura) para crear instancias dinámicas de los subtipos de `Question` basándose en el tipo definido en el CSV.
*   **Repository Pattern**: Desacopla la lógica de negocio de los detalles de almacenamiento.
*   **Dependency Injection**: Facilita el testeo y el cumplimiento del DIP, inyectantdo las dependencias necesarias desde el `Main`.
*   **Template Method / Polimorfismo**: El método `esCorrecta()` en `Question` permite que cada tipo de pregunta defina su propia lógica de validación.

## 🛡️ Manejo de Errores y Validaciones

La aplicación implementa un sistema de gestión de excepciones robusto para:
*   **Errores de Formato CSV**: Captura líneas mal estructuradas o tipos de preguntas no soportados, informando al usuario.
*   **Reglas de Negocio**: Lanza excepciones personalizadas cuando un estudiante intenta iniciar un examen teniendo uno ya en curso.
*   **Validación de Entradas**: Los Objetos de Valor (VO) validan que las respuestas y los IDs no sean nulos o vacíos.

---

## 📂 Estructura de Archivos
```text
src/
└── com/exam/
    ├── Main.java                # Punto de entrada e Inyección de Dependencias
    ├── application/             # Capa de Aplicación (Servicios, DTOs)
    ├── domain/                  # Corazón del negocio
    │   ├── model/               # Entidades (Question, ExamAttempt, etc.)
    │   ├── service/             # Servicios (AttemptManager, GradingService)
    │   ├── repository/          # Interfaces de Repositorio
    │   └── vo/                  # Objetos de Valor (QuestionId, StudentId, etc.)
    ├── infrastructure/          # Detalles técnicos (Persistencia, CSV Parser)
    └── presentation/            # Interfaces de Usuario (Consola)
```

---
*Desarrollado para la asignatura Programación Intermedia - Ingeniería de Sistemas.*
