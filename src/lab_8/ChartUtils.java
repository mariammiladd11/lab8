package lab_8;

import java.util.List;
import javax.swing.JOptionPane;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.chart.ChartFrame;

public class ChartUtils {

    private static StudentService studentService = new StudentService();

    public static void showStudentPerformanceChart(String courseId) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        List<Student> students = JsonDatabaseManager.getStudentsEnrolled(courseId);
        List<Lesson> lessons = JsonDatabaseManager.getLessons(courseId);

        if (students.isEmpty() || lessons.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No students or lessons found.");
            return;
        }

        for (Student student : students) {
            for (Lesson lesson : lessons) {
                LessonProgress lp = studentService.getLessonProgress(student.getUserId(), courseId, lesson.getLessonId());
                if (lp != null) {
                    dataset.addValue(lp.getScore(), student.getUsername(), lesson.getTitle());
                }
            }
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Student Performance - " + courseId,
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
        Student student = studentService.getStudentById(studentId);
        if (student == null) {
            JOptionPane.showMessageDialog(null, "Student not found.");
            return;
        }

        List<Lesson> lessons = JsonDatabaseManager.getLessons(courseId);
        boolean hasData = false;

        for (Lesson lesson : lessons) {
            LessonProgress lp = studentService.getLessonProgress(studentId, courseId, lesson.getLessonId());
            if (lp != null) {
                dataset.addValue(lp.getScore(), student.getUsername(), lesson.getTitle());
                hasData = true;
            }
        }

        if (!hasData) {
            JOptionPane.showMessageDialog(null, "No progress found for this student.");
            return;
        }

        JFreeChart chart = ChartFactory.createLineChart(
                "Performance of " + student.getUsername(),
                "Lesson",
                "Score",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        ChartFrame frame = new ChartFrame("Student Performance - " + student.getUsername(), chart);
        frame.setSize(800, 500);
        frame.setVisible(true);
    }

    public static void showQuizAveragesChart(String courseId) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        List<Student> students = JsonDatabaseManager.getStudentsEnrolled(courseId);
        List<Lesson> lessons = JsonDatabaseManager.getLessons(courseId);

        if (lessons.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No lessons available.");
            return;
        }

        for (Lesson lesson : lessons) {
            double total = 0;
            int count = 0;

            for (Student student : students) {
                LessonProgress lp = studentService.getLessonProgress(student.getUserId(), courseId, lesson.getLessonId());
                if (lp != null) {
                    total += lp.getScore();
                    count++;
                }
            }

            double avg = (count > 0) ? total / count : 0;
            dataset.addValue(avg, "Average Score", lesson.getTitle());
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Quiz Averages - " + courseId,
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

    public static void showLessonCompletionChart(String courseId) {
        List<Student> students = JsonDatabaseManager.getStudentsEnrolled(courseId);
        List<Lesson> lessons = JsonDatabaseManager.getLessons(courseId);

        if (lessons.isEmpty() || students.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No lessons or students available.");
            return;
        }

        String[] lessonTitles = lessons.stream().map(Lesson::getTitle).toArray(String[]::new);

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

        int completedCount = 0;
        for (Student student : students) {
            LessonProgress lp = studentService.getLessonProgress(student.getUserId(), courseId, selectedLesson.getLessonId());
            if (lp != null && lp.isPassed()) completedCount++;
        }

        int notCompletedCount = students.size() - completedCount;

        DefaultPieDataset dataset = new DefaultPieDataset();
        dataset.setValue("Completed", completedCount);
        dataset.setValue("Not Completed", notCompletedCount);

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
