/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lab_8;

/**
 *
 * @author Linae
 */
public class LessonProgress {
     private int attempts;   
    private int score;     
    private boolean passed; 

    public LessonProgress() {
        this.attempts = 0;
        this.score = 0;
        this.passed = false;
    }

    // Getters and setters
    public int getAttempts() { return attempts; }
    public void incrementAttempts() { this.attempts++; } 
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; } 
    public boolean isPassed() { return passed; }
    public void setPassed(boolean passed) { this.passed = passed; } 
    
}
