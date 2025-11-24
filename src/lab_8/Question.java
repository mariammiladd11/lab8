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

    
    public JSONObject toJson() {
        JSONObject obj = new JSONObject();
        obj.put("question", questionText);

        JSONArray arr = new JSONArray();
        for (String opt : options) arr.put(opt);
        obj.put("options", arr);

        obj.put("correctIndex", correctIndex);

        return obj;
    }

    
    public static Question fromJson(JSONObject obj) {
        if (obj == null) return null;

        String questionText = obj.optString("question", null);
        JSONArray optionsArray = obj.optJSONArray("options");
        int correctIndex = obj.optInt("correctIndex", -1);

        
        if (questionText == null || optionsArray == null || optionsArray.length() == 0 || correctIndex < 0 || correctIndex >= optionsArray.length()) {
            System.out.println("Warning: Skipping invalid question JSON: " + obj);
            return null;
        }

        List<String> options = new ArrayList<>();
        for (int i = 0; i < optionsArray.length(); i++) {
            options.add(optionsArray.getString(i));
        }

        return new Question(questionText, options, correctIndex);
    }
}
