/* USER CLASS
 * CREATED BY CONNOR MEADS
 *
 * CLASS FLOWS AS FOLLOWS:
 *
 * User() //default constructor defaulting all class variables to empty strings
 * User(String l_firstName, String l_lastName, String l_username, String l_password) //overloaded constructor initializing class variables
 * private String hashPassword(String l_plainTextPassword) //hashes a password to write out to the file & to compare hashes with
 *
 * getters & setters
 *
 * END OF CLASS FILE
 */

package com.company;

import java.util.ArrayList;

public class User
{
    private String firstName,
                   lastName,
                   username,
                   encryptedPassword,
                   email;
    private boolean isMetricUser;
    private ArrayList<String> categories = new ArrayList<>();

    User() //default User
    {
        this.firstName = "";
        this.lastName = "";
        this.username = "";
        this.encryptedPassword = "";
        this.email = "";
        this.isMetricUser = true;
    }

    User(String l_firstName, String l_lastName, String l_username, String l_password, String l_email) //overloaded Users constructor
    {
        this.firstName = l_firstName;
        this.lastName = l_lastName;
        this.username = l_username;
        this.encryptedPassword = hash(l_password);
        this.email = l_email;
    }

    public String hash(String l_plainTextPassword) //function to hash a password
    {
        //local variables
        String encryptedPassword = "";

        for(int i = 0; i < l_plainTextPassword.length(); i++) //walks through each letter of the password, hashing as it goes
        {
            if((int)l_plainTextPassword.charAt(i) >= 64 && (int)l_plainTextPassword.charAt(i) < 127)
            {
                encryptedPassword = encryptedPassword.concat(Character.toString((char)(((int)l_plainTextPassword.charAt(i) % 32) + 32)));
            }
            else if((int)l_plainTextPassword.charAt(i) < 64 && (int)l_plainTextPassword.charAt(i) >= 32)
            {
                encryptedPassword = encryptedPassword.concat(Character.toString((char)(((int)l_plainTextPassword.charAt(i) % 32) + 64)));
            }
            else
            {
                return "Error: Invalid Characters Used";
            }
        }

        return encryptedPassword;
    }

    //Getters & Setters
    public String getFirstName() { return this.firstName; }
    public void setFirstName(String l_firstName){this.firstName = l_firstName;}

    public String getLastName() { return this.lastName; }
    public void setLastName(String l_lastName) {this.lastName = l_lastName;}

    public String getEmail() { return this.email; }
    public void setEmail(String l_email) { this.email = l_email; }

    public String getUsername() { return this.username; }
    public void setUsername(String l_username) {this.username = l_username;}

    public String getEncryptedPassword() { return this.encryptedPassword; }
    public void setEncryptedPassword(String l_encryptedPassword) { this.encryptedPassword = l_encryptedPassword; }

    public boolean getIsMetricUser() { return this.isMetricUser; }
    public void setIsMetricUser(boolean l_isMetricUser) { this.isMetricUser = l_isMetricUser; }

    public ArrayList<String> getCategories() { return this.categories; }
    public void setCategories(ArrayList<String> l_newCategories) { this.categories = l_newCategories; }
    public String getCategory(int l_index) { return this.categories.get(l_index); }
    public void setCategory(String l_newCategory, int l_index) { this.categories.set(l_index, l_newCategory); }
    public void addCategory(String l_newCategory) { this.categories.add(l_newCategory); }
    public void removeCategory(int l_index) { this.categories.remove(l_index); }
}
