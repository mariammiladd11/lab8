/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lab_8;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

    private DefaultListModel<String> questionListModel;

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

        // list
        questionListModel = new DefaultListModel<>();
        JList<String> questionList = new JList<>(questionListModel);
        add(new JScrollPane(questionList), BorderLayout.CENTER);

        // buttons
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

        String correct = correctAnswerBox.getSelectedItem().toString();

        // Add to list (UI only now)
        questionListModel.addElement(q);

        // Store in component client properties (temp)
        JSONObject qObj = new JSONObject();
        qObj.put("question", q);
        qObj.put("options", new JSONArray(Arrays.asList(o1, o2, o3)));
        qObj.put("correctIndex", Integer.parseInt(correct) - 1);

        // Attach the JSON object to JList element
        questionListModel.addElement(q);
        questionListModel.set(questionListModel.size() - 1, qObj.toString());

        txtQuestion.setText("");
        txtOpt1.setText("");
        txtOpt2.setText("");
        txtOpt3.setText("");

        JOptionPane.showMessageDialog(this, "Question Added!");
    }

    private void saveQuiz() {

        JSONArray courses = JsonDatabaseManager.loadCourses();

        for (int i = 0; i < courses.length(); i++) {
            JSONObject c = courses.getJSONObject(i);

            if (c.getString("courseId").equals(courseId)) {

                JSONArray lessons = c.getJSONArray("lessons");

                for (int j = 0; j < lessons.length(); j++) {
                    JSONObject l = lessons.getJSONObject(j);

                    if (l.getString("lessonId").equals(lessonId)) {

                        // quiz must already exist because you create it before opening dialog
                        JSONObject quiz = l.getJSONObject("quiz");
                        JSONArray qArr = quiz.getJSONArray("questions");

                        // clear old questions
                        qArr.clear();

                        // add all new questions
                        for (int k = 0; k < questionListModel.size(); k++) {
                            JSONObject qObj = new JSONObject(questionListModel.get(k));
                            qArr.put(qObj);
                        }

                        JsonDatabaseManager.saveCourses(courses);
                        JOptionPane.showMessageDialog(this, "Quiz Saved Successfully!");
                        dispose();
                        return;
                    }
                }
            }
        }

        JOptionPane.showMessageDialog(this, "Error: Lesson not found!");
    }
}
