/* PERSON CLASS
 * CREATED BY CONNOR MEADS
 *
 * CLASS FLOWS AS FOLLOWS:
 *
 * Person() //default constructor defaulting all class variables to empty strings
 * @Override public String toString() //overrides the toString method  **RETURNS THE FULL NAME IN A STRING**
 *
 * getters & setters
 *
 * END OF CLASS FILE
 */

package com.company;

import javafx.scene.image.Image;

import java.time.LocalDate;
import java.util.ArrayList;

public class Person
{
    Person()
    {
        this.firstName = "empty";
        this.middleName = "empty";
        this.lastName = "empty";
        this.sex = "empty";
        this.weight = "empty";
        this.height = "empty";
        this.eyeColor = "empty";
        this.hairColor = "empty";
        this.comment = "empty";
        contactPhoto = new Image("https://sdmg.com/wp-content/uploads/2017/04/picture-not-available.jpg?x67906");
    }

    private String firstName, middleName, lastName;
    private String sex, weight, height, eyeColor, hairColor, comment; //weight & height are always saved in the metric system... because it's just better
    private Image contactPhoto;
    private LocalDate dateOfBirth;
    private ArrayList<PersonAddress> addresses = new ArrayList<>();
    private ArrayList<PersonPhoneNumber> phoneNumbers = new ArrayList<>();
    private ArrayList<PersonEmail> emails = new ArrayList<>();
    private ArrayList<String> categories = new ArrayList<>();

    //toString override
    @Override
    public String toString()
    {
        return this.lastName + ", " + this.firstName + " " + this.middleName;
    }

    //getters & setters
    public String getFirstName(){return this.firstName;}
    public void setFirstName (String l_firstName){this.firstName = l_firstName;}

    public String getMiddleName(){return this.middleName;}
    public void setMiddleName(String l_middleName){this.middleName = l_middleName;}

    public String getLastName(){return this.lastName;}
    public void setLastName(String l_lastName){this.lastName = l_lastName;}

    public LocalDate getDateOfBirth(){return this.dateOfBirth;}
    public void setDateOfBirth(LocalDate l_dateOfBirth){this.dateOfBirth = l_dateOfBirth;}

    public String getSex() { return this.sex; }
    public void setSex(String l_sex) { this.sex = l_sex; }

    public String getWeight() { return this.weight; }
    public void setWeight(String l_weight) { this.weight = l_weight;}

    public String getHeight() { return this.height; }
    public void setHeight(String l_height) { this.height = l_height; }

    public String getEyeColor() { return this.eyeColor; }
    public void setEyeColor(String l_eyeColor) { this.eyeColor = l_eyeColor; }

    public String getHairColor() { return this.hairColor; }
    public void setHairColor(String l_hairColor) { this.hairColor = l_hairColor; }

    public Image getContactPhoto() { return this.contactPhoto; }
    public void setContactPhoto(Image l_contactPhoto) { this.contactPhoto = l_contactPhoto; }

    public PersonAddress getAddress(int l_index){return this.addresses.get(l_index);} //to get a specific address
    public ArrayList<PersonAddress> getAddresses() {return this.addresses;} //to get the array of addresses
    public void setAddress(PersonAddress l_newAddress, int l_index)
    {
        this.addresses.get(l_index).setPersonAddress(l_newAddress);
    }
    public void setAddresses(ArrayList<PersonAddress> l_newAddresses)
    {
        this.addresses = l_newAddresses;
    }
    public void addAddress(PersonAddress l_newAddress){ this.addresses.add(l_newAddress); }
    public void removeAddress(int l_index) { this.addresses.remove(l_index); }

    public PersonPhoneNumber getPhoneNumber(int l_index) { return this.phoneNumbers.get(l_index); }
    public ArrayList<PersonPhoneNumber> getPhoneNumbers() { return this.phoneNumbers; }
    public void setPhoneNumber(String l_phoneNumber, String l_type, int l_index)
    {
        PersonPhoneNumber newPhoneNumber = new PersonPhoneNumber(l_phoneNumber, l_type);
        phoneNumbers.get(l_index).setPersonPhoneNumber(newPhoneNumber);
    }
    public void setPhoneNumbers(ArrayList<PersonPhoneNumber> l_newPhoneNumbers)
    {
        this.phoneNumbers = l_newPhoneNumbers;
    }
    public void addPhoneNumber(PersonPhoneNumber l_newPhoneNumber) { this.phoneNumbers.add(l_newPhoneNumber); }
    public void removePhoneNumber(int l_index) { this.phoneNumbers.remove(l_index); }

    public PersonEmail getEmail(int l_index) { return this.emails.get(l_index); }
    public ArrayList<PersonEmail> getEmails() { return this.emails; }
    public void setEmail(String l_emailAddress, String l_type, int l_index)
    {
        PersonEmail newEmail = new PersonEmail(l_emailAddress, l_type);
        emails.get(l_index).setPersonEmail(newEmail);
    }
    public void setEmails(ArrayList<PersonEmail> l_newEmails)
    {
        this.emails = l_newEmails;
    }
    public void addEmail(PersonEmail l_newEmail) { this.emails.add(l_newEmail); }
    public void removeEmail(int l_index) { this.emails.remove(l_index); }

    public ArrayList<String> getCategories() { return this.categories; }
    public String getCategory(int l_index) { return this.categories.get(l_index); }
    public void setCategories(ArrayList<String> l_newCategories) { this.categories = l_newCategories; }
    public void setCategory(String l_newCategory, int l_index) { this.categories.set(l_index, l_newCategory); }
    public void addCategory(String l_newCategory) { this.categories.add(l_newCategory); }
    public void removeCategory(int l_index) { this.categories.remove(l_index); }

    public String getComment() { return this.comment; }
    public void setComment(String l_comment) { this.comment = l_comment; }
}
