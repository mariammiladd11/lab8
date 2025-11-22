package lab_8;


import java.util.ArrayList;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author sarahkhaled
 */
public class Course {
    public enum CourseStatus {
        PENDING,
        APPROVED,
        REJECTED
    }

    private String courseId;
    private String title;
    private String description;
    private String instructorId;

    private CourseStatus status = CourseStatus.PENDING;

    private ArrayList<Lesson> lessons = new ArrayList<>();
    private ArrayList<String> students = new ArrayList<>();

    public Course(String courseId, String title, String description, String instructorId) {
        this.courseId = courseId;
        this.title = title;
        this.description = description;
        this.instructorId = instructorId;
        this.status = CourseStatus.PENDING; 
    }

   
    public String getCourseId() { return courseId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getInstructorId() { return instructorId; }
    public CourseStatus getStatus() { return status; }
    public ArrayList<Lesson> getLessons() { return lessons; }
    public ArrayList<String> getStudents() { return students; }

    public void setStatus(CourseStatus status) {
        this.status = status;
    }

    public void addLesson(Lesson lesson) {
        lessons.add(lesson);
    }

    public void addStudent(String studentId) {
        students.add(studentId);
    }

   
    public JSONObject toJson() {
        JSONObject obj = new JSONObject();
        obj.put("courseId", courseId);
        obj.put("title", title);
        obj.put("description", description);
        obj.put("instructorId", instructorId);
        obj.put("status", status.toString());

      
        JSONArray lessonArray = new JSONArray();
        for (Lesson lesson : lessons) {
            lessonArray.put(lesson.toJson());
        }
        obj.put("lessons", lessonArray);

        // Students
        JSONArray studentArray = new JSONArray(students);
        obj.put("students", studentArray);

        return obj;
    }

    public static Course fromJson(JSONObject obj) {
        Course c = new Course(
                obj.getString("courseId"),
                obj.getString("title"),
                obj.getString("description"),
                obj.getString("instructorId")
        );

      
        if (obj.has("status")) {
            c.status = CourseStatus.valueOf(obj.getString("status"));
        } else {
            c.status = CourseStatus.PENDING; 
        }

     
        if (obj.has("lessons")) {
            JSONArray arr = obj.getJSONArray("lessons");
            for (int i = 0; i < arr.length(); i++) {
                c.lessons.add(Lesson.fromJson(arr.getJSONObject(i)));
            }
        }

        
        if (obj.has("students")) {
            JSONArray arr = obj.getJSONArray("students");
            for (int i = 0; i < arr.length(); i++) {
                c.students.add(arr.getString(i));
            }
        }

        return c;
    }
}