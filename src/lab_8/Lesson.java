package lab_8;

import java.util.ArrayList;
import java.util.List;


/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author sarahkhaled
 */
public class Lesson {
    private String lessonId;
    private String title;
    private String content;
     private List<String> resources; // optional resources
    private boolean completed; // track if lesson is completed

    public Lesson(String lessonId, String title, String content, List<String> resources) {
        this.lessonId = lessonId;
        this.title = title;
        this.content = content;
        this.resources = resources;
        this.completed = false;
    }

    // Getters and setters
    public String getLessonId() { return lessonId; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public List<String> getResources() { return resources; }
    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    @Override
    public String toString() {
        return title + (completed ? " ✅" : "");
    }
    public JSONObject toJson() {
    JSONObject obj = new JSONObject();
    obj.put("lessonId", lessonId);
    obj.put("title", title);
    obj.put("content", content);

    JSONArray arr = new JSONArray();
    if (resources != null) {
        for (String r : resources) arr.put(r);
    }
    obj.put("resources", arr);

    obj.put("completed", completed);

    return obj;
}

public static Lesson fromJson(JSONObject obj) {
    String id = obj.getString("lessonId");
    String title = obj.getString("title");
    String content = obj.getString("content");

    List<String> resList = new ArrayList<>();
    JSONArray arr = obj.optJSONArray("resources");
    if (arr != null) {
        for (int i = 0; i < arr.length(); i++) {
            resList.add(arr.getString(i));
        }
    }

    Lesson lesson = new Lesson(id, title, content, resList);
    lesson.setCompleted(obj.optBoolean("completed", false));
    return lesson;
}
    
    
}
