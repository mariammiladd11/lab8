/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lab_8;

/**
 *
 * @author CYBER-TECH
 */
public class TestSignup {
    public static void main(String[] args) {
        // Run the signup JFrame in the Event Dispatch Thread
        javax.swing.SwingUtilities.invokeLater(() -> {
            signup form = new signup();
            form.setVisible(true);
        });
    }
}
