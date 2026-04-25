package com.exam.presentation;

import com.exam.application.ExamApplicationService;
import com.exam.application.dto.DTOs.CalificacionDTO;
import com.exam.application.dto.DTOs.ExamAttemptDTO;
import com.exam.domain.model.Question;
import com.exam.domain.model.QuestionTypes;
import com.exam.domain.vo.ValueObjects.AnswerText;
import com.exam.domain.vo.ValueObjects.StudentId;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Capa de Presentación Gráfica (Swing).
 * Implementa un diseño desacoplado donde la vista actúa como un componente
 * pasivo
 * que solo interactúa con los Casos de Uso (Application Service) (Fowler,
 * 2006).
 */
public class SwingUI extends JFrame {
  private final ExamApplicationService service;
  private StudentId currentStudentId;
  private ExamAttemptDTO currentAttempt;
  private int currentQuestionIndex;
  private QuestionPanel currentQuestionPanel;

  private JPanel mainContainer;
  private CardLayout cardLayout;

  public SwingUI(ExamApplicationService service) {
    this.service = service;
    configurarVentana();
    inicializarComponentes();
  }

  private void configurarVentana() {
    setTitle("Sistema de Evaluación (DDD & SOLID)");
    setSize(600, 400);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLocationRelativeTo(null);

    cardLayout = new CardLayout();
    mainContainer = new JPanel(cardLayout);
    add(mainContainer);
  }

  private void inicializarComponentes() {
    // Panel de Inicio (Login)
    JPanel loginPanel = new JPanel(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(10, 10, 10, 10);
    gbc.gridx = 0;
    gbc.gridy = 0;

    JLabel titleLabel = new JLabel("Bienvenido al Sistema de Exámenes");
    titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
    loginPanel.add(titleLabel, gbc);

    gbc.gridy = 1;
    JPanel inputPanel = new JPanel(new FlowLayout());
    inputPanel.add(new JLabel("ID de Estudiante:"));
    JTextField txtStudentId = new JTextField(15);
    inputPanel.add(txtStudentId);
    loginPanel.add(inputPanel, gbc);

    gbc.gridy = 2;
    JButton btnStart = new JButton("Iniciar Examen");
    btnStart.addActionListener(e -> iniciarExamen(txtStudentId.getText()));
    loginPanel.add(btnStart, gbc);

    mainContainer.add(loginPanel, "LOGIN");
  }

  public void start() {
    setVisible(true);
    cardLayout.show(mainContainer, "LOGIN");
  }

  private void iniciarExamen(String studentIdStr) {
    if (studentIdStr == null || studentIdStr.trim().isEmpty()) {
      mostrarErrorGrafico("El ID no puede estar vacío.");
      return;
    }

    try {
      currentStudentId = new StudentId(studentIdStr.trim());
      currentAttempt = service.iniciarExamen(currentStudentId);
      currentQuestionIndex = 0;
      mostrarPreguntaActual();
    } catch (IllegalStateException | IllegalArgumentException ex) {
      mostrarErrorGrafico(ex.getMessage());
    }
  }

  private void mostrarPreguntaActual() {
    if (currentQuestionIndex >= currentAttempt.questions().size()) {
      finalizarExamen();
      return;
    }

    Question q = currentAttempt.questions().get(currentQuestionIndex);

    JPanel examPanel = new JPanel(new BorderLayout(10, 10));
    examPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    // Cabecera
    JLabel lblHeader = new JLabel(
        "Pregunta " + (currentQuestionIndex + 1) + " de " + currentAttempt.questions().size());
    lblHeader.setFont(new Font("Arial", Font.BOLD, 14));
    examPanel.add(lblHeader, BorderLayout.NORTH);

    // Renderizado dinámico según el tipo de pregunta (Polimorfismo en UI)
    currentQuestionPanel = QuestionRendererFactory.crearPanelPregunta(q);
    examPanel.add(currentQuestionPanel.getPanel(), BorderLayout.CENTER);

    // Botonera
    JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    JButton btnNext = new JButton(
        currentQuestionIndex == currentAttempt.questions().size() - 1 ? "Finalizar" : "Siguiente");

    btnNext.addActionListener(e -> {
      AnswerText answer = currentQuestionPanel.getAnswer();
      if (answer.value().trim().isEmpty()) {
        int confirm = JOptionPane.showConfirmDialog(this, "No ha ingresado respuesta. ¿Desea continuar?", "Advertencia",
            JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION)
          return;
      }
      service.responderPregunta(currentStudentId, q.getId(), answer);
      currentQuestionIndex++;
      mostrarPreguntaActual();
    });

    bottomPanel.add(btnNext);
    examPanel.add(bottomPanel, BorderLayout.SOUTH);

    // Actualizar la vista
    String viewName = "QUESTION_" + currentQuestionIndex;
    mainContainer.add(examPanel, viewName);
    cardLayout.show(mainContainer, viewName);
  }

  private void finalizarExamen() {
    try {
      CalificacionDTO resultado = service.finalizarExamen(currentStudentId);
      double porcentaje = ((double) resultado.puntaje() / resultado.total()) * 100;

      String mensaje = String.format("Examen finalizado.\n\nPuntuación: %d / %d\nPorcentaje de acierto: %.2f%%",
          resultado.puntaje(), resultado.total(), porcentaje);

      JOptionPane.showMessageDialog(this, mensaje, "Resultados", JOptionPane.INFORMATION_MESSAGE);

      // Reiniciar flujo
      currentStudentId = null;
      currentAttempt = null;
      cardLayout.show(mainContainer, "LOGIN");

    } catch (Exception ex) {
      mostrarErrorGrafico("Error al finalizar el examen: " + ex.getMessage());
    }
  }

  private void mostrarErrorGrafico(String msg) {
    JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
  }

  // ==========================================
  // RENDERIZADOR POLIMÓRFICO DE PREGUNTAS
  // ==========================================

  /**
   * Interfaz para extraer la respuesta introducida por el usuario de cualquier
   * panel gráfico.
   */
  interface QuestionPanel {
    JPanel getPanel();

    AnswerText getAnswer();
  }

  /**
   * Patrón Factory que aísla la lógica de presentación de la lógica de dominio.
   */
  static class QuestionRendererFactory {
    public static QuestionPanel crearPanelPregunta(Question q) {
      JPanel container = new JPanel(new BorderLayout(0, 15));
      JTextArea txtEnunciado = new JTextArea(q.getText());
      txtEnunciado.setEditable(false);
      txtEnunciado.setLineWrap(true);
      txtEnunciado.setWrapStyleWord(true);
      txtEnunciado.setBackground(new Color(240, 240, 240));
      txtEnunciado.setFont(new Font("Arial", Font.PLAIN, 14));
      container.add(txtEnunciado, BorderLayout.NORTH);

      JPanel optionsPanel = new JPanel();
      optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
      container.add(optionsPanel, BorderLayout.CENTER);

      // Uso de abstracción para determinar la UI sin romper el dominio
      if (q instanceof QuestionTypes.TrueFalseQuestion) {
        return new TrueFalsePanel(container, optionsPanel);
      } else if (q instanceof QuestionTypes.FillBlankQuestion) {
        return new FillBlankPanel(container, optionsPanel);
      } else if (q instanceof QuestionTypes.SingleChoiceQuestion) {
        // Requiere exponer un getter para options en SingleChoiceQuestion en un
        // escenario real,
        // O usar reflection si no se desea modificar el dominio.
        // Para este ejemplo, se asume que se extrae del texto base formateado.
        return new SingleChoicePanel(container, optionsPanel, extractOptionsFromDomain(q));
      } else if (q instanceof QuestionTypes.MultipleChoiceQuestion) {
        return new MultipleChoicePanel(container, optionsPanel, extractOptionsFromDomain(q));
      }

      return new FillBlankPanel(container, optionsPanel); // Fallback
    }

    // Método auxiliar simulado para extraer opciones (en una implementación pura,
    // el DTO pasaría las opciones)
    private static List<String> extractOptionsFromDomain(Question q) {
      List<String> mockOptions = new ArrayList<>();
      // Nota: En un sistema totalmente integrado, la entidad Question debería proveer
      // un método abstracto
      // List<String> getOptions() o los DTOs deberían transportar estas opciones
      // estructuradas.
      mockOptions.add("Opción A (Simulada)");
      mockOptions.add("Opción B (Simulada)");
      return mockOptions;
    }
  }

  static class TrueFalsePanel implements QuestionPanel {
    private JPanel panel;
    private JRadioButton rbTrue, rbFalse;

    public TrueFalsePanel(JPanel basePanel, JPanel optionsPanel) {
      this.panel = basePanel;
      rbTrue = new JRadioButton("Verdadero (V)");
      rbFalse = new JRadioButton("Falso (F)");
      ButtonGroup group = new ButtonGroup();
      group.add(rbTrue);
      group.add(rbFalse);
      optionsPanel.add(rbTrue);
      optionsPanel.add(rbFalse);
    }

    @Override
    public JPanel getPanel() {
      return panel;
    }

    @Override
    public AnswerText getAnswer() {
      if (rbTrue.isSelected())
        return new AnswerText("V");
      if (rbFalse.isSelected())
        return new AnswerText("F");
      return new AnswerText("");
    }
  }

  static class FillBlankPanel implements QuestionPanel {
    private JPanel panel;
    private JTextField txtAnswer;

    public FillBlankPanel(JPanel basePanel, JPanel optionsPanel) {
      this.panel = basePanel;
      txtAnswer = new JTextField(20);
      optionsPanel.add(new JLabel("Escriba la palabra faltante:"));
      optionsPanel.add(txtAnswer);
    }

    @Override
    public JPanel getPanel() {
      return panel;
    }

    @Override
    public AnswerText getAnswer() {
      return new AnswerText(txtAnswer.getText());
    }
  }

  static class SingleChoicePanel implements QuestionPanel {
    private JPanel panel;
    private List<JRadioButton> buttons = new ArrayList<>();

    public SingleChoicePanel(JPanel basePanel, JPanel optionsPanel, List<String> options) {
      this.panel = basePanel;
      ButtonGroup group = new ButtonGroup();
      for (String opt : options) {
        JRadioButton rb = new JRadioButton(opt);
        buttons.add(rb);
        group.add(rb);
        optionsPanel.add(rb);
      }
    }

    @Override
    public JPanel getPanel() {
      return panel;
    }

    @Override
    public AnswerText getAnswer() {
      for (JRadioButton rb : buttons) {
        if (rb.isSelected())
          return new AnswerText(rb.getText());
      }
      return new AnswerText("");
    }
  }

  static class MultipleChoicePanel implements QuestionPanel {
    private JPanel panel;
    private List<JCheckBox> checkBoxes = new ArrayList<>();

    public MultipleChoicePanel(JPanel basePanel, JPanel optionsPanel, List<String> options) {
      this.panel = basePanel;
      for (String opt : options) {
        JCheckBox cb = new JCheckBox(opt);
        checkBoxes.add(cb);
        optionsPanel.add(cb);
      }
    }

    @Override
    public JPanel getPanel() {
      return panel;
    }

    @Override
    public AnswerText getAnswer() {
      List<String> answers = new ArrayList<>();
      for (JCheckBox cb : checkBoxes) {
        if (cb.isSelected())
          answers.add(cb.getText());
      }
      return new AnswerText(String.join(",", answers));
    }
  }
}