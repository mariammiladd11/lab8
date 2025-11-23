/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lab_8;

import java.util.List;
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

            for (int i = 0; i < studentIds.size(); i++) {
                dataset.addValue(scores.get(i), studentIds.get(i), lesson.getTitle());
            }
        }

        JFreeChart chart = ChartFactory.createBarChart(
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

    // ---------------- 2. Quiz Averages (Bar Chart) ----------------
    public static void showQuizAveragesChart(String courseId) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        List<Lesson> lessons = JsonDatabaseManager.getLessons(courseId);

        for (Lesson lesson : lessons) {
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

        ChartFrame frame = new ChartFrame("Quiz Averages - " + courseId, chart);
        frame.setSize(800, 500);
        frame.setVisible(true);
    }

    // ---------------- 3. Completion Percentages (Pie Chart) ----------------
    public static void showCompletionChart(String courseId) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        List<Lesson> lessons = JsonDatabaseManager.getLessons(courseId);

        for (Lesson lesson : lessons) {
            dataset.setValue(lesson.getTitle(), lesson.getCompletionPercentage());
        }

        JFreeChart chart = ChartFactory.createPieChart(
                "Lesson Completion %",
                dataset,
                true,
                true,
                false
        );

        ChartFrame frame = new ChartFrame("Lesson Completion - " + courseId, chart);
        frame.setSize(700, 500);
        frame.setVisible(true);
    }
}
