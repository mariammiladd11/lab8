/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lab_8;

import java.util.HashMap;

public class ProgressDatabase {

    // studentId -> lessonId -> LessonProgress
    private static HashMap<String, HashMap<String, LessonProgress>> progressMap = new HashMap<>();

    public static LessonProgress getProgress(String studentId, String lessonId) {

        if (!progressMap.containsKey(studentId))
            return null;

        return progressMap.get(studentId).get(lessonId);
    }

    public static void saveProgress(String studentId, String lessonId, LessonProgress progress) {

        progressMap.putIfAbsent(studentId, new HashMap<>());
        progressMap.get(studentId).put(lessonId, progress);
    }
}

