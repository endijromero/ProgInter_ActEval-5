Edited task.md
Edited run_tests.sh
Ran command: `chmod +x run_tests.sh`
Edited ValueObjectsTest.java
Edited CsvQuestionBankRepositoryTest.java
Viewed ExamApplicationService.java:1-95
Edited ExamApplicationServiceTest.java
Edited ExamAttemptTest.java
Edited GradingServiceTest.java
Edited ConsoleUIE2ETest.java
Edited SwingUIE2ETest.java
Ran command: `./run_tests.sh`
Viewed ExamAttempt.java:1-69
Edited ConsoleUIE2ETest.java
Edited ExamApplicationServiceTest.java
Ran command: `./run_tests.sh`
Edited ConsoleUIE2ETest.java
Ran command: `./run_tests.sh`
Ran command: `java -cp "bin:bin-test:lib/junit-platform-console-standalone.jar" org.junit.platform.console.ConsoleLauncher --scan-class-path --select-class com.exam.presentation.ConsoleUIE2ETest`
Ran command: `java -cp "bin:bin-test:lib/junit-platform-console-standalone.jar" org.junit.platform.console.ConsoleLauncher execute -c com.exam.presentation.ConsoleUIE2ETest > test_output.txt`
Viewed test_output.txt:1-41
Viewed StatsApplicationService.java:1-53
Edited ConsoleUIE2ETest.java
Ran command: `./run_tests.sh`
Ran command: `java -cp "bin:bin-test:lib/junit-platform-console-standalone.jar" org.junit.platform.console.ConsoleLauncher execute --scan-class-path > test_output.txt`
Viewed test_output.txt:1-60
Edited CsvQuestionBankRepositoryTest.java
Ran command: `./run_tests.sh`
Ran command: `java -jar lib/jacococli.jar report jacoco.exec --classfiles bin --sourcefiles src --csv coverage-report/jacoco.csv`
Ran command: `awk -F, '{ instructions += $4 + $5; covered += $5 } END { print covered, "/", instructions, " = ", covered/instructions*100, "%" }' coverage-report/jacoco.csv`
Edited run_tests.sh
Ran command: `./run_tests.sh`
Ran command: `java -jar lib/jacococli.jar report jacoco.exec --classfiles bin --sourcefiles src --csv coverage-report/jacoco.csv && awk -F, '{ instructions += $4 + $5; covered += $5 } END { print covered, "/", instructions, " = ", covered/instructions*100, "%" }' coverage-report/jacoco.csv`
Edited InMemoryExamAttemptRepositoryTest.java
Edited QuestionTypesTest.java
Ran command: `./run_tests.sh && java -jar lib/jacococli.jar report jacoco.exec --classfiles bin --sourcefiles src --csv coverage-report/jacoco.csv && awk -F, '{ instructions += $4 + $5; covered += $5 } END { print covered, "/", instructions, " = ", covered/instructions*100, "%" }' coverage-report/jacoco.csv`
Viewed Question.java:1-33
Edited QuestionTypesTest.java
Edited QuestionTypesTest.java
Ran command: `./run_tests.sh && java -jar lib/jacococli.jar report jacoco.exec --classfiles bin --sourcefiles src --csv coverage-report/jacoco.csv && awk -F, '{ instructions += $4 + $5; covered += $5 } END { print covered, "/", instructions, " = ", covered/instructions*100, "%" }' coverage-report/jacoco.csv`
Edited run_tests.sh
Ran command: `./run_tests.sh && awk -F, '{ instructions += $4 + $5; covered += $5 } END { print covered, "/", instructions, " = ", covered/instructions*100, "%" }' coverage-report/jacoco.csv`
Edited AttemptManagerTest.java
Edited DTOsTest.java
Edited ExamApplicationServiceTest.java
Edited ExamApplicationServiceTest.java
Edited ConsoleUIE2ETest.java
Ran command: `./run_tests.sh && awk -F, '{ instructions += $4 + $5; covered += $5 } END { print covered, "/", instructions, " = ", covered/instructions*100, "%" }' coverage-report/jacoco.csv`
Ran command: `java -cp "bin:bin-test:lib/junit-platform-console-standalone.jar" org.junit.platform.console.ConsoleLauncher execute -c com.exam.domain.service.AttemptManagerTest > test_output.txt`
Viewed test_output.txt:1-41
Edited AttemptManagerTest.java
Ran command: `./run_tests.sh && awk -F, '{ instructions += $4 + $5; covered += $5 } END { print covered, "/", instructions, " = ", covered/instructions*100, "%" }' coverage-report/jacoco.csv`
Ran command: `find src -name "*.java"`
Edited TxtExportServiceTest.java
Edited FileExamAttemptRepositoryTest.java
Ran command: `./run_tests.sh && awk -F, '{ instructions += $4 + $5; covered += $5 } END { print covered, "/", instructions, " = ", covered/instructions*100, "%" }' coverage-report/jacoco.csv`
Viewed FileExamAttemptRepository.java:1-135
Edited FileExamAttemptRepositoryTest.java
Ran command: `./run_tests.sh && awk -F, '{ instructions += $4 + $5; covered += $5 } END { print covered, "/", instructions, " = ", covered/instructions*100, "%" }' coverage-report/jacoco.csv`
Edited FileExamAttemptRepositoryTest.java
Ran command: `./run_tests.sh && awk -F, '{ instructions += $4 + $5; covered += $5 } END { print covered, "/", instructions, " = ", covered/instructions*100, "%" }' coverage-report/jacoco.csv`
Viewed TxtExportService.java:1-40
Edited TxtExportServiceTest.java
Ran command: `./run_tests.sh && awk -F, '{ instructions += $4 + $5; covered += $5 } END { print covered, "/", instructions, " = ", covered/instructions*100, "%" }' coverage-report/jacoco.csv`
Edited ConsoleUIE2ETest.java
Edited ConsoleUIE2ETest.java
Ran command: `./run_tests.sh && awk -F, '{ instructions += $4 + $5; covered += $5 } END { print covered, "/", instructions, " = ", covered/instructions*100, "%" }' coverage-report/jacoco.csv`
Edited QuestionTypesTest.java
Edited ConsoleUIE2ETest.java
Edited FileExamAttemptRepositoryTest.java
Ran command: `./run_tests.sh && awk -F, '{ instructions += $4 + $5; covered += $5 } END { print covered, "/", instructions, " = ", covered/instructions*100, "%" }' coverage-report/jacoco.csv`
Edited FileExamAttemptRepositoryTest.java
Ran command: `./run_tests.sh && awk -F, '{ instructions += $4 + $5; covered += $5 } END { print covered, "/", instructions, " = ", covered/instructions*100, "%" }' coverage-report/jacoco.csv`
