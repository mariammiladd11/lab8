/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lab_8;

import java.util.List;
import javax.swing.JOptionPane;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.chart.ChartFrame;

/**
 *
 * @author sarahkhaled
 */
public class ChartUtils {
     public static void showStudentPerformanceChart(String courseId) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        List<Lesson> lessons = JsonDatabaseManager.getLessons(courseId);

        for (Lesson lesson : lessons) {
            List<String> studentIds = lesson.getStudentIds();
            List<Double> scores = lesson.getStudentScores();

            for (int i=0;i<studentIds.size(); i++) {
                dataset.addValue(scores.get(i),studentIds.get(i), lesson.getTitle());
            }
        }

        JFreeChart chart=ChartFactory.createBarChart(
                "Student Performance",
                "Lesson",
                "Score",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        ChartFrame frame = new ChartFrame("Student Performance - " + courseId, chart);
        frame.setSize(800, 500);
        frame.setVisible(true);
    }
     public static void showStudentLineChart(String courseId, String studentId) {
    DefaultCategoryDataset dataset = new DefaultCategoryDataset();
    List<Lesson> lessons = JsonDatabaseManager.getLessons(courseId);

    boolean studentFound= false;
    for (Lesson lesson :lessons) {
        List<String> ids= lesson.getStudentIds();
        List<Double> scores= lesson.getStudentScores();
        int index = ids.indexOf(studentId);
        if(index !=-1) {
            dataset.addValue(scores.get(index), studentId, lesson.getTitle());
            studentFound = true;
        }
    }

    if(!studentFound) {
        JOptionPane.showMessageDialog(null, "No data found for this student.");
        return;
    }

    JFreeChart chart = ChartFactory.createLineChart(
            "Performance of " + studentId,
            "Lesson",
            "Score",
            dataset,
            PlotOrientation.VERTICAL,
            true,
            true,
            false
    );

    ChartFrame frame = new ChartFrame("Student Performance - " + studentId, chart);
    frame.setSize(800, 500);
    frame.setVisible(true);
}


    public static void showQuizAveragesChart(String courseId) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        List<Lesson> lessons = JsonDatabaseManager.getLessons(courseId);

        for (Lesson lesson :lessons) {
            dataset.addValue(lesson.getAverageScore(), "Average Score", lesson.getTitle());
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Quiz Averages per Lesson",
                "Lesson",
                "Average Score",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        ChartFrame frame =new ChartFrame("Quiz Averages - " + courseId, chart);
        frame.setSize(800, 500);
        frame.setVisible(true);
    }

   public static void showLessonCompletionChart(String courseId) {
        List<Lesson> lessons=JsonDatabaseManager.getLessons(courseId);

        if (lessons.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No lessons available for this course.");
            return;
        }

        String[] lessonTitles = lessons.stream()
                .map(Lesson::getTitle)
                .toArray(String[]::new);

        String selectedLessonTitle = (String) JOptionPane.showInputDialog(
                null,
                "Select a lesson to view completion:",
                "Lesson Selection",
                JOptionPane.QUESTION_MESSAGE,
                null,
                lessonTitles,
                lessonTitles[0]
        );

        if (selectedLessonTitle == null) return;

        Lesson selectedLesson = lessons.stream()
                .filter(l -> l.getTitle().equals(selectedLessonTitle))
                .findFirst()
                .orElse(null);

        if (selectedLesson == null) return;
        int totalStudents = selectedLesson.getStudentIds().size();
        if (totalStudents == 0) {
            JOptionPane.showMessageDialog(null, "No students enrolled in this lesson.");
            return;
        }

        int completedCount = 0;
        for (boolean b : selectedLesson.getStudentCompletion()) {
            if (b) completedCount++;
        }
        int notCompletedCount = totalStudents - completedCount;

        DefaultPieDataset dataset = new DefaultPieDataset();
        dataset.setValue("Students Completed", completedCount);
        dataset.setValue("Students Not Completed", notCompletedCount);

        ChartFrame frame = new ChartFrame(
                "Lesson Completion - " + selectedLessonTitle,
                ChartFactory.createPieChart(
                        "Lesson Completion",
                        dataset,
                        true,
                        true,
                        false
                )
        );
        frame.setSize(700, 500);
        frame.setVisible(true);
    }

}
