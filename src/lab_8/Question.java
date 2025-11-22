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

public class Question {
     private String questionText;
    private List<String> options;
    private int correctIndex;

    public Question(String questionText, List<String> options, int correctIndex) {
        this.questionText = questionText;
        this.options = options;
        this.correctIndex = correctIndex;
    }

  
    public String getQuestionText() { return questionText; }
    public List<String> getOptions() { return options; }
    public int getCorrectIndex() { return correctIndex; }

    // ---------- JSON Serialization ----------
    public JSONObject toJson() {
        JSONObject obj = new JSONObject();
        obj.put("question", questionText);

        JSONArray arr = new JSONArray();
        for (String opt : options) arr.put(opt);
        obj.put("options", arr);

        obj.put("correctIndex", correctIndex);

        return obj;
    }

    // ---------- JSON Deserialization ----------
    public static Question fromJson(JSONObject obj) {
        String text = obj.getString("question");

        List<String> optList = new ArrayList<>();
        JSONArray arr = obj.getJSONArray("options");
        for (int i = 0; i < arr.length(); i++) {
            optList.add(arr.getString(i));
        }

        int correct = obj.getInt("correctIndex");

        return new Question(text, optList, correct);
    }
}
