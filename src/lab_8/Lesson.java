package lab_8;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Lesson class tracks lesson content, resources, quiz, and per-student data.
 */
public class Lesson {
    private String lessonId;
    private String title;
    private String content;
    private List<String> resources;
    private boolean completed;
    private Quiz quiz;

    // Per-student tracking
    private List<String> studentIds;
    private List<Double> studentScores;
    private List<Boolean> studentCompletion;

    public Lesson(String lessonId, String title, String content, List<String> resources) {
        this.lessonId = lessonId;
        this.title = title;
        this.content = content;
        this.resources = resources != null ? resources : new ArrayList<>();
        this.completed = false;
        this.quiz = null;

        this.studentIds = new ArrayList<>();
        this.studentScores = new ArrayList<>();
        this.studentCompletion = new ArrayList<>();
    }

    // Getters and setters
    public String getLessonId() { return lessonId; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public List<String> getResources() { return resources; }
    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }
    public Quiz getQuiz() { return quiz; }
    public void setQuiz(Quiz quiz) { this.quiz = quiz; }

    // -------- JSON Serialization --------
    public JSONObject toJson() {
        JSONObject obj = new JSONObject();
        obj.put("lessonId", lessonId);
        obj.put("title", title);
        obj.put("content", content);
        obj.put("completed", completed);

        // Resources
        JSONArray resArr = new JSONArray(resources);
        obj.put("resources", resArr);

        // Quiz
        obj.put("quiz", quiz != null ? quiz.toJson() : JSONObject.NULL);

        // Student data
        obj.put("studentIds", new JSONArray(studentIds));
        obj.put("studentScores", new JSONArray(studentScores));
        obj.put("studentCompletion", new JSONArray(studentCompletion));

        return obj;
    }

    // -------- JSON Deserialization --------
    public static Lesson fromJson(JSONObject obj) {
        List<String> resList = new ArrayList<>();
        JSONArray resArr = obj.optJSONArray("resources");
        if (resArr != null) {
            for (int i = 0; i < resArr.length(); i++) {
                resList.add(resArr.getString(i));
            }
        }

        Lesson lesson = new Lesson(
                obj.getString("lessonId"),
                obj.getString("title"),
                obj.getString("content"),
                resList
        );

        lesson.setCompleted(obj.optBoolean("completed", false));

        // Quiz
        if (obj.has("quiz") && !obj.isNull("quiz")) {
            lesson.setQuiz(Quiz.fromJson(obj.getJSONObject("quiz")));
        }

        // Student data
        JSONArray idsArr = obj.optJSONArray("studentIds");
        JSONArray scoresArr = obj.optJSONArray("studentScores");
        JSONArray compArr = obj.optJSONArray("studentCompletion");

        if (idsArr != null && scoresArr != null && compArr != null) {
            for (int i = 0; i < idsArr.length(); i++) {
                lesson.studentIds.add(idsArr.getString(i));
                lesson.studentScores.add(scoresArr.getDouble(i));
                lesson.studentCompletion.add(compArr.getBoolean(i));
            }
        }

        return lesson;
    }

    // -------- Per-student management --------
    public void addStudent(String studentId) {
        if (!studentIds.contains(studentId)) {
            studentIds.add(studentId);
            studentScores.add(0.0);
            studentCompletion.add(false);
        }
    }

    public void setStudentScore(String studentId, double score) {
        int idx = studentIds.indexOf(studentId);
        if (idx != -1) studentScores.set(idx, score);
    }

    public void setStudentCompletion(String studentId, boolean completed) {
        int idx = studentIds.indexOf(studentId);
        if (idx != -1) studentCompletion.set(idx, completed);
    }

    public double getStudentScore(String studentId) {
        int idx = studentIds.indexOf(studentId);
        return idx != -1 ? studentScores.get(idx) : 0.0;
    }

    public boolean isStudentCompleted(String studentId) {
        int idx = studentIds.indexOf(studentId);
        return idx != -1 && studentCompletion.get(idx);
    }

    // -------- Aggregated stats --------
    public double getAverageScore() {
        if (studentScores.isEmpty()) return 0;
        double sum = 0;
        for (double s : studentScores) sum += s;
        return sum / studentScores.size();
    }

    public double getCompletionPercentage() {
        if (studentCompletion.isEmpty()) return 0;
        int completedCount = 0;
        for (boolean b : studentCompletion) if (b) completedCount++;
        return (completedCount * 100.0) / studentCompletion.size();
    }

    @Override
    public String toString() {
        return title + (completed ? " ✅" : "");
    }
}
