package lab_8;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

public class Lesson {
    private String lessonId;
    private String title;
    private String content;
    private List<String> resources;
    private boolean completed;
    private Quiz quiz;
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

    // ---------- Getters and Setters ----------
    public String getLessonId() { return lessonId; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public List<String> getResources() { return resources; }
    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }
    public Quiz getQuiz() { return quiz; }
    public void setQuiz(Quiz quiz) { this.quiz = quiz; }
    public List<String> getStudentIds() { return studentIds; }
    public List<Double> getStudentScores() { return studentScores; }
    public List<Boolean> getStudentCompletion() { return studentCompletion; }

    @Override
    public String toString() {
        return title;
    }

    // ---------- JSON Serialization ----------
    public JSONObject toJson() {
        JSONObject obj = new JSONObject();
        obj.put("lessonId", lessonId);
        obj.put("title", title);
        obj.put("content", content);

        JSONArray resArr = new JSONArray(resources);
        obj.put("resources", resArr);

        obj.put("completed", completed);

        JSONArray idArr = new JSONArray(studentIds);
        obj.put("studentIds", idArr);

        JSONArray scoresArr = new JSONArray(studentScores);
        obj.put("studentScores", scoresArr);

        JSONArray completedArr = new JSONArray(studentCompletion);
        obj.put("studentCompletion", completedArr);

        if (quiz != null) obj.put("quiz", quiz.toJson());
        else obj.put("quiz", JSONObject.NULL);

        return obj;
    }

    public static Lesson fromJson(JSONObject obj) {
        List<String> resList = new ArrayList<>();
        JSONArray resArr = obj.optJSONArray("resources");
        if (resArr != null) {
            for (int i = 0; i < resArr.length(); i++) resList.add(resArr.getString(i));
        }

        Lesson lesson = new Lesson(
            obj.getString("lessonId"),
            obj.getString("title"),
            obj.getString("content"),
            resList
        );
        lesson.setCompleted(obj.optBoolean("completed", false));

        // Load studentIds, scores, and completion
        JSONArray idsArr = obj.optJSONArray("studentIds");
        if (idsArr != null) {
            for (int i = 0; i < idsArr.length(); i++) lesson.studentIds.add(idsArr.getString(i));
        }

        JSONArray scoresArr = obj.optJSONArray("studentScores");
        if (scoresArr != null) {
            for (int i = 0; i < scoresArr.length(); i++) lesson.studentScores.add(scoresArr.getDouble(i));
        }

        JSONArray completedArr = obj.optJSONArray("studentCompletion");
        if (completedArr != null) {
            for (int i = 0; i < completedArr.length(); i++) lesson.studentCompletion.add(completedArr.getBoolean(i));
        }

        if (obj.has("quiz") && !obj.isNull("quiz")) lesson.setQuiz(Quiz.fromJson(obj.getJSONObject("quiz")));

        return lesson;
    }

    // ---------- Student Progress Methods ----------
    public void recordStudentProgress(String studentId, double score, boolean completed) {
        int index = studentIds.indexOf(studentId);
        if (index == -1) {
            // New student for this lesson
            studentIds.add(studentId);
            studentScores.add(score);
            studentCompletion.add(completed);
        } else {
            // Update existing student
            studentScores.set(index, score);
            studentCompletion.set(index, completed);
        }
    }

    public double getAverageScore() {
        if (studentScores.isEmpty()) return 0;
        double sum = 0;
        for (double s : studentScores) sum += s;
        return sum / studentScores.size();
    }

    public double getCompletionPercentage() {
        if (studentCompletion.isEmpty()) return 0;
        int count = 0;
        for (boolean b : studentCompletion) if (b) count++;
        return (count * 100.0) / studentCompletion.size();
    }

}
