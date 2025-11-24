package lab_8;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import org.json.JSONArray;
import org.json.JSONObject;

public class QuizCreatorDialog extends JDialog {

    private String courseId;
    private String lessonId;

    private JTextField txtQuestion;
    private JTextField txtOpt1;
    private JTextField txtOpt2;
    private JTextField txtOpt3;
    private JComboBox<String> correctAnswerBox;

    private DefaultListModel<JSONObject> questionListModel;

    public QuizCreatorDialog(JFrame parent, String courseId, String lessonId) {
        super(parent, "Create Quiz", true);
        this.courseId = courseId;
        this.lessonId = lessonId;

        initUI();
        setSize(600, 450);
        setLocationRelativeTo(parent);
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // Form panel
        JPanel form = new JPanel(new GridLayout(6, 2, 5, 5));
        form.add(new JLabel("Question:"));
        txtQuestion = new JTextField();
        form.add(txtQuestion);

        form.add(new JLabel("Option 1:"));
        txtOpt1 = new JTextField();
        form.add(txtOpt1);

        form.add(new JLabel("Option 2:"));
        txtOpt2 = new JTextField();
        form.add(txtOpt2);

        form.add(new JLabel("Option 3:"));
        txtOpt3 = new JTextField();
        form.add(txtOpt3);

        form.add(new JLabel("Correct Answer:"));
        correctAnswerBox = new JComboBox<>(new String[]{"1", "2", "3"});
        form.add(correctAnswerBox);

        add(form, BorderLayout.NORTH);

        
        questionListModel = new DefaultListModel<>();
        JList<JSONObject> questionList = new JList<>(questionListModel);
        
        questionList.setCellRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel label = new JLabel(value.getString("question"));
            if (isSelected) {
                label.setBackground(list.getSelectionBackground());
                label.setForeground(list.getSelectionForeground());
                label.setOpaque(true);
            }
            return label;
        });
        add(new JScrollPane(questionList), BorderLayout.CENTER);

        
        JPanel btnPanel = new JPanel();
        JButton addBtn = new JButton("Add Question");
        addBtn.addActionListener(e -> addQuestion());
        JButton saveBtn = new JButton("Save Quiz");
        saveBtn.addActionListener(e -> saveQuiz());
        btnPanel.add(addBtn);
        btnPanel.add(saveBtn);
        add(btnPanel, BorderLayout.SOUTH);
    }

    private void addQuestion() {
        String q = txtQuestion.getText().trim();
        String o1 = txtOpt1.getText().trim();
        String o2 = txtOpt2.getText().trim();
        String o3 = txtOpt3.getText().trim();

        if (q.isEmpty() || o1.isEmpty() || o2.isEmpty() || o3.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Fill all fields!");
            return;
        }

        int correctIndex = correctAnswerBox.getSelectedIndex();

        JSONObject qObj = new JSONObject();
        qObj.put("question", q);
        qObj.put("options", new JSONArray(Arrays.asList(o1, o2, o3)));
        qObj.put("correctIndex", correctIndex);

        
        questionListModel.addElement(qObj);

        
        txtQuestion.setText("");
        txtOpt1.setText("");
        txtOpt2.setText("");
        txtOpt3.setText("");
        correctAnswerBox.setSelectedIndex(0);

        JOptionPane.showMessageDialog(this, "Question Added!");
    }

    private void saveQuiz() {
        JSONArray courses = JsonDatabaseManager.loadCourses();
        boolean lessonFound = false;

        for (int i = 0; i < courses.length(); i++) {
            JSONObject c = courses.getJSONObject(i);
            if (!c.getString("courseId").equals(courseId)) continue;

            JSONArray lessons = c.optJSONArray("lessons");
            if (lessons == null) continue;

            for (int j = 0; j < lessons.length(); j++) {
                JSONObject l = lessons.getJSONObject(j);
                if (!l.getString("lessonId").equals(lessonId)) continue;

                lessonFound = true;

                // Ensure lesson has a quiz object
                JSONObject quiz = l.optJSONObject("quiz");
                if (quiz == null) {
                    quiz = new JSONObject();
                    quiz.put("questions", new JSONArray());
                    l.put("quiz", quiz);
                }

                JSONArray qArr = quiz.optJSONArray("questions");
                if (qArr == null) {
                    qArr = new JSONArray();
                    quiz.put("questions", qArr);
                } else {
                    qArr.clear(); 
                }

                
                for (int k = 0; k < questionListModel.size(); k++) {
                    JSONObject qObj = questionListModel.get(k);
                    qArr.put(qObj);
                }

                JsonDatabaseManager.saveCourses(courses);
                JOptionPane.showMessageDialog(this, "Quiz Saved Successfully!");
                dispose();
                return;
            }
        }

        if (!lessonFound) {
            JOptionPane.showMessageDialog(this, "Error: Lesson not found!");
        }
    }
}

