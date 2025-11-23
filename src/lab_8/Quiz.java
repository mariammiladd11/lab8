/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lab_8;

/**
 *
 * @author Linae
 */
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class Quiz {
    private List<Question> questions;

    public Quiz(List<Question> questions) {
        this.questions = questions;
    }

    public List<Question> getQuestions() { return questions; }

    // ---------- Evaluate Answers ----------
    public int calculateScore(List<Integer> studentChoices) {
         int score = 0;
    if (questions == null || questions.isEmpty() || studentChoices == null) return score;

    int n = Math.min(questions.size(), studentChoices.size()); // only loop through available answers
    for (int i = 0; i < n; i++) {
        Question q = questions.get(i);
        if (q == null) continue; // skip invalid questions
        Integer choice = studentChoices.get(i);
        if (choice != null && choice == q.getCorrectIndex()) {
            score++;
        }
    }
    return score;
    }

    // ---------- JSON Serialization ----------
    public JSONObject toJson() {
        JSONArray arr = new JSONArray();
        for (Question q : questions) {
            arr.put(q.toJson());
        }

        JSONObject obj = new JSONObject();
        obj.put("questions", arr);
        return obj;
    }

    // ---------- JSON Deserialization ----------
   public static Quiz fromJson(JSONObject obj) {
    List<Question> list = new ArrayList<>();
    if (obj == null) return new Quiz(list);

    JSONArray arr = obj.optJSONArray("questions"); // safer
    if (arr == null) return new Quiz(list);

    for (int i = 0; i < arr.length(); i++) {
        JSONObject qObj = arr.optJSONObject(i);
        if (qObj == null) continue;

        Question q = Question.fromJson(qObj);
        if (q != null) list.add(q); // only add valid questions
    }

    return new Quiz(list);
}
}
