/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lab_8;

/**
 *
 * @author CYBER-TECH
 */
public class LogoutService {
    private static User current = null;
    public static void setCurrent(User u){ 
        current = u; }
    public static User getCurrent(){
        return current; }
    public static void logout(){ 
        current = null; }
}
