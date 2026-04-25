#!/bin/bash
set -e

# Asegurar directorios
mkdir -p lib
mkdir -p bin
mkdir -p bin-test
mkdir -p coverage-report
mkdir -p test

# 1. Descargar dependencias si no existen
if [ ! -f "lib/junit-platform-console-standalone.jar" ]; then
    echo "Descargando JUnit 5 Standalone..."
    curl -sL -o lib/junit-platform-console-standalone.jar "https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/1.10.2/junit-platform-console-standalone-1.10.2.jar"
fi

if [ ! -f "lib/jacocoagent.jar" ] || [ ! -f "lib/jacococli.jar" ]; then
    echo "Descargando JaCoCo 0.8.12..."
    curl -sL -o lib/jacoco-0.8.12.zip "https://repo1.maven.org/maven2/org/jacoco/jacoco/0.8.12/jacoco-0.8.12.zip"
    # Extraer sin rutas de directorio (-j) solo los jars
    unzip -q -j lib/jacoco-0.8.12.zip lib/jacocoagent.jar lib/jacococli.jar -d lib/
fi

# 2. Compilar el código principal
echo "Compilando código fuente principal..."
find src -name "*.java" > sources.txt
javac -d bin @sources.txt

# 3. Compilar los tests (si existen)
if [ -z "$(find test -name '*.java')" ]; then
    echo "No se encontraron archivos de prueba en la carpeta test/. Crea tus pruebas primero."
    exit 0
fi

echo "Compilando código de pruebas..."
find test -name "*.java" > test-sources.txt
javac -d bin-test -cp "bin:lib/junit-platform-console-standalone.jar" @test-sources.txt

# 4. Ejecutar pruebas con agente JaCoCo
echo "Ejecutando pruebas y recolectando datos de cobertura..."
rm -f jacoco.exec
java -javaagent:lib/jacocoagent.jar=destfile=jacoco.exec,excludes=com.exam.Main*:com.exam.presentation.SwingUI* \
     -cp "bin:bin-test:lib/junit-platform-console-standalone.jar" \
     org.junit.platform.console.ConsoleLauncher --scan-class-path

# 5. Generar reporte HTML
if [ -f "jacoco.exec" ]; then
    echo "Preparando binarios para reporte..."
    rm -rf bin-report
    cp -r bin bin-report
    rm -f bin-report/com/exam/Main*.class
    rm -rf bin-report/com/exam/presentation/SwingUI*.class
    
    echo "Generando reporte de JaCoCo en coverage-report/"
    java -jar lib/jacococli.jar report jacoco.exec --classfiles bin-report --sourcefiles src --html coverage-report --csv coverage-report/jacoco.csv
    echo "¡Reporte generado! Puedes verlo abriendo coverage-report/index.html en un navegador."
else
    echo "Error: No se generó el archivo jacoco.exec."
fi
