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
        for (int i = 0; i < questions.size(); i++) {
            if (studentChoices.get(i) == questions.get(i).getCorrectIndex()) {
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
        JSONArray arr = obj.getJSONArray("questions");

        for (int i = 0; i < arr.length(); i++) {
            list.add(Question.fromJson(arr.getJSONObject(i)));
        }

        return new Quiz(list);
    }
}
