/*CREEPER
 *CREATED BY CONNOR MEADS
 *
 * PROGRAM FLOWS AS FOLLOWS:
 * MAIN.JAVA
 * --------------
 * static void main() //no description
 * public void start() //launches the screen where the user logs in with a set username & password or can create a new account
 * private void makeAccountScreen() //a window to add a user creating folders & .txt files to write out to
 * private void createDirectories(String l_firstName, String l_lastName, String l_username, String l_password) //backend functionality to make directories for a new user
 * private void mainScreen() //the main screen of the program where the user looks up or adds contacts
 * private void settingsScreen() //a screen to adjust user settings
 * private Person searchPersonWindow() //a screen to search through the current list of contacts
 * private Person addPersonWindow() //a screen to add a person to contacts.  Also writes it out to the file
 * private void popUpErrorWindow(String l_error) //anytime there's a try{...}catch{...}, this function is accessed to make a pop up displaying the error
 */

package com.company;

import com.sun.javafx.tk.Toolkit;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.control.TabPane.TabClosingPolicy;

import java.io.*;
import java.lang.reflect.Array;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Main extends Application
{
    private ObservableList<Person> contacts = FXCollections.observableArrayList();
    private ObservableList<User> users = FXCollections.observableArrayList();
    private User currentUser = new User();
    private Text helpText = new Text();
    private boolean showingMore = false;
    public static void main(String[] args)
    {
        Application.launch(args);
    }

    @Override
    public void start(Stage tempStage)
    {
        //TEST
        //settingsScreen();
        //ENDTEST
        tempStage.setTitle("Creeper");tempStage.setWidth(800);tempStage.setHeight(400);tempStage.setResizable(false);
        Pane tempPane = new Pane();tempPane.setStyle("-fx-background-color: white");

        try //set the icon image
        {
            tempStage.getIcons().add(new Image("https://i.pinimg.com/originals/7e/67/79/7e6779bf6d689ef9d288052bdbfdcf41.jpg"));
        }catch(Exception e)
        {
            popUpErrorWindow("ERROR: " + e + " (not able to load icon from internet)");
        }

        //Text Objects in the log in screen
        Text logInWelcomeText = new Text("Log In");logInWelcomeText.setX((tempStage.getWidth() / 2) - logInWelcomeText.getLayoutBounds().getWidth());logInWelcomeText.setY(tempStage.getHeight() / 4);logInWelcomeText.setFont(Font.font("Times New Roman", FontWeight.BOLD, 26));
        Text noAccountFlavorText = new Text("Don't have an account?");noAccountFlavorText.setX(((tempStage.getWidth() / 2) - noAccountFlavorText.getLayoutBounds().getWidth() / 2) - 25);noAccountFlavorText.setY(tempStage.getHeight() - 70);noAccountFlavorText.setFont(Font.font("Times New Roman", 13));
        this.helpText.setY((tempStage.getHeight() / 3) + 160);this.helpText.setX((tempStage.getWidth() / 2) - (this.helpText.getLayoutBounds().getWidth() / 2));
        Hyperlink makeAccountText = new Hyperlink("Click Here");makeAccountText.setLayoutX(noAccountFlavorText.getX() + noAccountFlavorText.getLayoutBounds().getWidth());makeAccountText.setLayoutY(noAccountFlavorText.getY() - noAccountFlavorText.getLayoutBounds().getHeight() - 2);makeAccountText.setFont(Font.font("Times New Roman", FontWeight.THIN, 13));
        makeAccountText.setOnAction(action ->
        {
            makeAccountScreen();
            tempStage.close();
        });

        //TextField's
        TextField userNameTextField = new TextField();userNameTextField.setPromptText("Username");userNameTextField.setLayoutX((tempStage.getWidth() / 2) - 70);userNameTextField.setLayoutY(tempStage.getHeight() / 3);
        PasswordField passwordTextField = new PasswordField();passwordTextField.setPromptText("Password");passwordTextField.setLayoutX((tempStage.getWidth() / 2) - 70);passwordTextField.setLayoutY(userNameTextField.getLayoutY() + 50);

        //Buttons in log in screen
        Button verifyButton = new Button("Verify");verifyButton.setMinWidth(70);verifyButton.setMinHeight(30);verifyButton.setDefaultButton(true);verifyButton.setLayoutX((tempStage.getWidth() / 2) - 30);verifyButton.setLayoutY(passwordTextField.getLayoutY() + 50);

        //Button Functionality
        //Verify button
        verifyButton.setOnAction(action ->
        {
            if(verifyAccount(userNameTextField.getText(), passwordTextField.getText())) //successful login.  Launches the main screen of the program
            {
                tempStage.close();
                currentUser.setEncryptedPassword(currentUser.hash(passwordTextField.getText()));
                mainScreen();
            }
            else //username or password was incorrect.  displays helpful message in red
            {
                this.helpText.setText("Username or Password incorrect");helpText.setX((tempStage.getWidth() / 2) - (helpText.getLayoutBounds().getWidth() / 2));helpText.setFill(Color.RED);
            }
        });

        //This checks to see if there is a file already created.  Depending on the answer, it adjusts the helpText accordingly
        File checkFolder = new File("C:\\Creeper");
        if(!checkFolder.exists())
        {
            this.helpText.setText("It seems like this is your first time here.  Click the link below to get started!");helpText.setX((tempStage.getWidth() / 2) - (helpText.getLayoutBounds().getWidth() / 2));
        }
        else
        {
            this.helpText.setText("Welcome Back! Log in above or create a new account below!");helpText.setX((tempStage.getWidth() / 2) - (helpText.getLayoutBounds().getWidth() / 2));
        }

        tempPane.getChildren().addAll(logInWelcomeText, helpText, userNameTextField, passwordTextField, verifyButton, noAccountFlavorText, makeAccountText);
        Scene tempScene = new Scene(tempPane);
        tempStage.setScene(tempScene);
        tempStage.show();
    }

    private void makeAccountScreen() //opens a window allowing user to create an account with a fresh set of contacts
    {
        //make and launch new stage
        Stage tempStage = new Stage();tempStage.setWidth(500);tempStage.setHeight(300);tempStage.initModality(Modality.APPLICATION_MODAL);tempStage.setTitle("Account Creation");//tempStage.setResizable(false);
        Pane tempPane = new Pane();tempPane.setStyle("-fx-background-color: white");
        String emailRegex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$"; //a regex to check for valid emails
        User newUser = new User(); //creating a new User to work with

        try //set the icon image
        {
            tempStage.getIcons().add(new Image("https://i.pinimg.com/originals/7e/67/79/7e6779bf6d689ef9d288052bdbfdcf41.jpg"));
        }catch(Exception e)
        {
            popUpErrorWindow("ERROR: " + e);
        }

        //Text Objects
        Text informationalText = new Text("A Few Things About You...");informationalText.setX(tempStage.getWidth() - (tempStage.getWidth() * 0.98));informationalText.setY(tempStage.getHeight() - 280);informationalText.setFont(Font.font("Times New Roman", FontWeight.BOLD, 18));
        Text helpText = new Text();helpText.setX(informationalText.getX());helpText.setY(informationalText.getY() + 20);helpText.setFont(Font.font("Verdana", 12));helpText.setVisible(false);helpText.setFill(Color.RED);
        Text textPromptUL= new Text("First Name");textPromptUL.setX(tempStage.getWidth() - (tempStage.getWidth() * 0.98));textPromptUL.setY(informationalText.getY() + 60);textPromptUL.setFont(Font.font("Times New Roman", 14));
        Text textPromptUR = new Text("Last Name");textPromptUR.setX(textPromptUL.getX() + 170);textPromptUR.setY(textPromptUL.getY());textPromptUR.setFont(Font.font("Times New Roman", 14));
        Text textPromptLL = new Text("Email Address");textPromptLL.setX(textPromptUL.getX());textPromptLL.setY(textPromptUL.getY() + 60);textPromptLL.setFont(Font.font("Times New Roman", 14));
        Text textPromptLR = new Text("Measurement Preference");textPromptLR.setX(textPromptUR.getX());textPromptLR.setY(textPromptLL.getY());textPromptLR.setFont(Font.font("Times New Roman",14));

        //TextField Objects
        TextField textFieldUL = new TextField();textFieldUL.setLayoutX(textPromptUL.getX());textFieldUL.setLayoutY(textPromptUL.getY() + 10);textFieldUL.setVisible(true);
        TextField textFieldUR = new TextField();textFieldUR.setLayoutX(textPromptUR.getX());textFieldUR.setLayoutY(textFieldUL.getLayoutY());textFieldUR.setVisible(true);
        TextField textFieldLL = new TextField();textFieldLL.setLayoutX(textPromptLL.getX());textFieldLL.setLayoutY(textPromptLL.getY() + 10);textFieldLL.setVisible(true);

        //passwordField Object(s)
        PasswordField passwordTextField = new PasswordField();passwordTextField.setLayoutX(textFieldUR.getLayoutX());passwordTextField.setLayoutY(textFieldUR.getLayoutY());passwordTextField.setPromptText("Password");passwordTextField.setVisible(false);

        //Radio Buttons
        ToggleGroup preference = new ToggleGroup();
        RadioButton metricPreferenceRB = new RadioButton("Metric");metricPreferenceRB.setLayoutX(textPromptLR.getX());metricPreferenceRB.setLayoutY(textPromptLR.getY() + 15);metricPreferenceRB.setSelected(true);
        RadioButton imperialPreferenceRB = new RadioButton("Imperial");imperialPreferenceRB.setLayoutX(metricPreferenceRB.getLayoutX() + 75);imperialPreferenceRB.setLayoutY(metricPreferenceRB.getLayoutY());

        metricPreferenceRB.setToggleGroup(preference);
        imperialPreferenceRB.setToggleGroup(preference);

        //Images
        Image arrowImage = new Image("https://cdn3.iconfinder.com/data/icons/ui-elements-light/100/UI_Light_chevron_right-512.png");
        ImageView arrowImageView = new ImageView(arrowImage);arrowImageView.setFitHeight(100);arrowImageView.setFitWidth(40);

        //Buttons
        Button continueButton = new Button("", arrowImageView);continueButton.setPrefSize(40, 262.3);continueButton.setLayoutX(tempStage.getWidth() - 70);continueButton.setStyle("-fx-background-color: white;");continueButton.setStyle("-fx-border-color: black;");
        Button backButton = new Button("<");backButton.setLayoutX(informationalText.getX());backButton.setLayoutY(tempStage.getHeight() - 115);backButton.setPrefSize(35, 20);backButton.setVisible(false);
        Button finishButton = new Button("Finish");finishButton.setLayoutX(informationalText.getX());finishButton.setLayoutY(tempStage.getHeight() - 80);finishButton.setPrefSize(70, 30);finishButton.setVisible(false);

        //button action
        continueButton.setOnAction(action ->
        {
            /* GUIDE TO THE continueButton
             * Step 1: Check to make sure the text fields aren't blank & the email entered looks like a valid email
             * Step 2: Save the information to the correct variables
             * Step 3: Adjust the visibilities as needed
             */
            //STEP 1
            if(!(textFieldUL.getText().length() == 0) && !(textFieldUR.getText().length() == 0) && !(textFieldLL.getText().length() == 0) && (textFieldLL.getText().matches(emailRegex)))
            {
                //STEP 2
                newUser.setFirstName(textFieldUL.getText());
                newUser.setLastName(textFieldUR.getText());
                newUser.setEmail(textFieldLL.getText());

                if(metricPreferenceRB.isSelected())
                {
                    newUser.setIsMetricUser(true);
                }
                else
                {
                    newUser.setIsMetricUser(false);
                }

                //STEP 3
                //Visibility
                helpText.setVisible(false);
                continueButton.setVisible(false);
                backButton.setVisible(true);
                finishButton.setVisible(true);
                passwordTextField.setVisible(true);
                textFieldLL.setVisible(false);
                textFieldUR.setVisible(false);
                textPromptLL.setVisible(false);
                textPromptLR.setVisible(false);
                metricPreferenceRB.setVisible(false);
                imperialPreferenceRB.setVisible(false);

                //readjust prompts
                informationalText.setText("A Few Things About You...");
                textPromptUL.setText("Username");
                textPromptUR.setText("Password");
                textFieldUL.setText(newUser.getUsername());
            }
            else
            {
                helpText.setVisible(true);
                helpText.setText("Make sure the first & last name text fields aren't blank\nand you have a valid email address before continuing!");
            }
        });
        backButton.setOnAction(action ->
        {
            /* GUIDE TO backButton
             * Step 1: Adjust visibility to nodes
             * Step 2: clear any progress on username & password nodes & readjust prompts
             */
            //Step 1
            passwordTextField.setVisible(false);
            continueButton.setVisible(true);
            backButton.setVisible(false);
            finishButton.setVisible(false);
            textFieldLL.setVisible(true);
            textFieldUR.setVisible(true);
            textPromptLL.setVisible(true);
            textPromptLR.setVisible(true);
            metricPreferenceRB.setVisible(true);
            imperialPreferenceRB.setVisible(true);

            //Step 2
            newUser.setUsername(textFieldUL.getText());
            passwordTextField.clear();
            informationalText.setText("Account Information");
            textPromptUL.setText("First Name");
            textPromptUR.setText("Last Name");
            textFieldUL.setText(newUser.getFirstName());
        });
        finishButton.setOnAction(action ->
        {
            /* GUIDE TO finishButton
             * Step 1: Save everything to the newUser variable and pass it along to the createDirectories function
             * Step 2: Close the stage & log you in automatically
             */

            //Step 1
            newUser.setUsername(textFieldUL.getText());
            newUser.setEncryptedPassword(newUser.hash(passwordTextField.getText()));
            createDirectories(newUser);

            //Step 2
            if(verifyAccount(newUser.getUsername(), passwordTextField.getText()))
            {
                mainScreen();
                tempStage.close();
            }
            else
            {
                popUpErrorWindow("Go get Connor,\ncan't validate a brand new user");
            }
        });

        tempPane.getChildren().addAll(informationalText, helpText, textPromptUL, textPromptUR, textPromptLL, textPromptLR, textFieldUL, textFieldUR, textFieldLL, passwordTextField, metricPreferenceRB, imperialPreferenceRB, finishButton, backButton, continueButton);
        Scene tempScene = new Scene(tempPane);
        tempStage.setScene(tempScene);
        tempStage.show();
    }

    private void createDirectories(User l_newUser) //makes folders & .txt folders for new user accounts as well as adds that user to the users ObservableArrayList
    {
        //local variables
        boolean noErrors = true;
        String errorMsg = "";

        //Make folders
        File folders = new File("C:\\Creeper\\" + l_newUser.getUsername());
        try
        {
            if(!folders.exists())
            {
                if(!folders.mkdirs())
                {
                    noErrors = false;
                    errorMsg = errorMsg + "Failed to make Directory: 'Creeper'\n";
                }
            }
            else
            {
                noErrors = false;
                errorMsg = errorMsg + "Directory already exists\n";
            }
        }catch(Exception e)
        {
            popUpErrorWindow("ERROR: " + e.toString());
        }

        //make .txt files
        File accountInfoTextFile = new File("C:\\Creeper\\" + l_newUser.getUsername() + "\\accountInfo.txt");
        File contactsInfoTextFile = new File("C:\\Creeper\\" + l_newUser.getUsername() + "\\contacts.txt");

        try
        {
            if(!(accountInfoTextFile.createNewFile() && contactsInfoTextFile.createNewFile()))
            {
                noErrors = false;
                errorMsg = errorMsg + "Failed to create text files\n";
            }
        }catch (Exception e)
        {
            popUpErrorWindow("ERROR: " + e.toString());
        }

        //write out the account info to accountInfo.txt
        try
        {
            PrintWriter writer = new PrintWriter("C:\\Creeper\\" + l_newUser.getUsername() + "\\accountInfo.txt");
            writer.println(l_newUser.getFirstName() + ">~>" + l_newUser.getLastName()); //Write out to the .txt file "John Doe"
            writer.println(l_newUser.getUsername()); //on a new line, write out the username
            writer.println(l_newUser.getEncryptedPassword()); //on a new line, write out the encrypted password
            writer.println("Unassigned>~>Friends>~>Family>~>Co-Workers");
            if(l_newUser.getIsMetricUser())
            {
                writer.println("T");
            }
            else
            {
                writer.println("F");
            }
            writer.close();
            l_newUser.addCategory("Unassigned");
            l_newUser.addCategory("Friends");
            l_newUser.addCategory("Family");
            l_newUser.addCategory("Co-Workers");
        }catch (Exception e)
        {
            popUpErrorWindow("Error: " + e.toString());
        }

        if(noErrors)
        {
            helpText.setText("Account Creation Successful!");helpText.setX(400 - (helpText.getLayoutBounds().getWidth() / 2));helpText.setFill(Color.GREEN);
        }
        else
        {
            helpText.setText(errorMsg);
            helpText.setFill(Color.RED);
        }
    }

    private boolean verifyAccount(String l_username, String l_passwordAttempt) //verifies username & password as well as reads in all account information and contacts information
    {
        //local variables
        User newUser;//a User object to create a new User
        Person newPerson;//a Person object to represent the current Person being read in and saved to the global contacts ObservableList
        File readFile = new File("C:\\Creeper\\" + l_username); //File data type to store the file path to the pertinent information
        BufferedReader br;//the BufferedReader used to read in the .txt files
        ArrayList<String> fileContents = new ArrayList<>(); //to store the fileContents of both accountInfo.txt & contacts.txt at different times
        String[] splitFullName;//get the first & last name by splitting the full Name up using a white space
        String firstName, lastName, fullName; //Strings to store the individual first, last, and full name of that found in accountInfo.txt
        String username, encryptedPasswordReadIn, encryptedPasswordInput; //Strings to store the username and password read in as found in accountInfo.txt.  Also contains the encryptedPassword that the user types in and is verified through encryption
        String categoryReadIn; //The concatenated String of categories
        String[] userCategories; //Array of Strings representing all the categories the User has previously entered
        String measurementData;
        String str;//a placeholder string to read in the files and save them to the ArrayList 'fileContents'

        try
        {
            if(readFile.exists())
            {
                readFile = new File("C:\\Creeper\\" + l_username + "\\accountInfo.txt");
                br = new BufferedReader(new FileReader(readFile));

                while((str = br.readLine()) != null) //save the file contents to a ArrayList
                {
                    fileContents.add(str);
                }

                fullName = fileContents.get(0);splitFullName = fullName.split(">~>");firstName = splitFullName[0];lastName = splitFullName[1]; //split the name String
                username = fileContents.get(1);
                encryptedPasswordReadIn = fileContents.get(2);
                categoryReadIn = fileContents.get(3);
                measurementData = fileContents.get(4);
                userCategories = categoryReadIn.split(">~>", -1);
                newUser = new User();
                newUser.setFirstName(firstName);
                newUser.setLastName(lastName);
                newUser.setUsername(username);
                encryptedPasswordInput = newUser.hash(l_passwordAttempt);

                if(measurementData.equals("T")) //User prefers the metric system
                {
                    newUser.setIsMetricUser(true);
                }
                else //tsk tsk
                {
                    newUser.setIsMetricUser(false);
                }

                for(String st : userCategories)
                {
                    newUser.addCategory(st);
                }

                this.users.add(newUser); //add the user to the global ArrayList
                this.currentUser = newUser; //make the current user equal to who they logged in to

                br.close();

                readFile = new File("C:\\Creeper\\" + l_username + "\\contacts.txt");
                br = new BufferedReader(new FileReader(readFile));

                fileContents.clear();
                while((str = br.readLine()) != null) //read in the contents of the file & add them to an ArrayList<String> where each item in the list represents a Person object
                {
                    fileContents.add(str);
                }

                for(int i = 0; i < fileContents.size(); i++) //goes through each line of contacts.txt and splits them apart via regex
                {
                    //loop variables
                    newPerson = new Person();
                    String[] personInfo;
                    String[] nameAndDoB;
                    String[] addressArray;
                    String[] addressInfo;
                    String[] phoneNumberArray;
                    String[] phoneNumberInfo;
                    String[] emailArray;
                    String[] emailInfo;
                    String[] categories;
                    LocalDate tempDoB;
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

                    //split the current line into 5 parts to be further regex'd
                    personInfo = fileContents.get(i).split("<<!>>", -1);

                    //split & save the contacts name & date of birth
                    nameAndDoB = personInfo[0].split(">~>", -1);
                    newPerson.setLastName(nameAndDoB[0]);
                    newPerson.setFirstName(nameAndDoB[1]);
                    newPerson.setMiddleName(nameAndDoB[2]);
                    tempDoB = LocalDate.parse(nameAndDoB[3], formatter);
                    newPerson.setDateOfBirth(tempDoB);
                    newPerson.setSex(nameAndDoB[4]);
                    newPerson.setWeight(nameAndDoB[5]);
                    newPerson.setHeight(nameAndDoB[6]);
                    newPerson.setEyeColor(nameAndDoB[7]);
                    newPerson.setHairColor(nameAndDoB[8]);
                    newPerson.setComment(nameAndDoB[9].replaceAll("<'r'>", "\n")); //replaces the placeholder for a carriage return, with a carriage return

                    //split & save the contacts addresses
                    addressArray = personInfo[1].split("/&/", -1);

                    for(int j = 0; j < addressArray.length; j++)
                    {
                        addressInfo = addressArray[j].split(">~>", -1);
                        PersonAddress newPersonAddress = new PersonAddress(addressInfo[0], addressInfo[1], addressInfo[2], addressInfo[3], addressInfo[4]);

                        newPerson.addAddress(newPersonAddress);
                    }

                    //split & save the contacts Phone Numbers
                    phoneNumberArray = personInfo[2].split("/&/", -1);

                    for(int j = 0; j < phoneNumberArray.length; j++)
                    {
                        phoneNumberInfo = phoneNumberArray[j].split(">~>", -1);
                        PersonPhoneNumber newPhoneNumber = new PersonPhoneNumber(phoneNumberInfo[0], phoneNumberInfo[1]);

                        newPerson.addPhoneNumber(newPhoneNumber);
                    }

                    //split & save the contacts emails
                    emailArray = personInfo[3].split("/&/", -1);

                    for(int j = 0; j < emailArray.length; j++)
                    {
                        emailInfo = emailArray[j].split(">~>", -1);
                        PersonEmail newEmail = new PersonEmail(emailInfo[0], emailInfo[1]);

                        newPerson.addEmail(newEmail);
                    }

                    //split & save the categories
                    categories = personInfo[4].split(">~>", -1);

                    for(int j = 0; j < categories.length; j++)
                    {
                        newPerson.addCategory(categories[j]);
                    }

                    contacts.add(newPerson);
                }

                br.close();
                return encryptedPasswordInput.equals(encryptedPasswordReadIn); //final return if password & username match
            }
            else
            {
                return false;
            }
        } catch (Exception e)
        {
            popUpErrorWindow("ERROR: " + e);
            return false;
        }
    }

    private void mainScreen() //the home page
    {
        //local variables
        ObservableList<Person> recentlySearchedPeople = FXCollections.observableArrayList();
        ArrayList<Text> addresses = new ArrayList<>();
        ArrayList<Text> phoneNumbers = new ArrayList<>();
        ArrayList<Text> emails = new ArrayList<>();
        DateTimeFormatter dtFormatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
        final int xIncrement = 10;
        final int yIncrement = 10;

        //set all the defaults for the stage
        Stage primaryStage = new Stage();primaryStage.setTitle("Creeper");primaryStage.setWidth(900);primaryStage.setHeight(650);//primaryStage.setResizable(false);

        //declare panes
        BorderPane root = new BorderPane();
        Pane topPane = new Pane();
        Pane bottomLeftPane = new Pane();
        ScrollPane sp = new ScrollPane();
        AnchorPane ap = new AnchorPane();
        VBox bottomRightPane = new VBox();

        try //set the icon image
        {
            primaryStage.getIcons().add(new Image("https://i.pinimg.com/originals/7e/67/79/7e6779bf6d689ef9d288052bdbfdcf41.jpg"));
        }catch(Exception e)
        {
            popUpErrorWindow("ERROR: " + e.toString());
        }

        //declare nodes on screen by pane
        //TOP PANE NODES
        //Images
        Image contactPhoto = new Image("https://sdmg.com/wp-content/uploads/2017/04/picture-not-available.jpg?x67906");
        ImageView imageView = new ImageView(contactPhoto);imageView.setX(primaryStage.getWidth() - 890);imageView.setY(primaryStage.getWidth() - 890);imageView.setFitHeight(170);imageView.setFitWidth(140);imageView.setPreserveRatio(true);

        //Text
        Text nameText = new Text("[Name]");nameText.setX(imageView.getX() + 150);nameText.setY(imageView.getY() + 10);nameText.setFont(Font.font("Times New Roman", FontWeight.BOLD, 22));
        Text DoBText = new Text("[DoB]");DoBText.setX(imageView.getX() + 150);DoBText.setY(primaryStage.getHeight() - 600);DoBText.setFont(Font.font("Times New Roman", 14));
        Text addressText = new Text("[Address]");addressText.setX(imageView.getX() + 150);addressText.setY(primaryStage.getHeight() - 550);addressText.setFont(Font.font("Times New Roman", 18));
        Text phoneNumberText = new Text("[Phone #]");phoneNumberText.setX(imageView.getX() + 150);phoneNumberText.setY(primaryStage.getHeight() - 470);phoneNumberText.setFont(Font.font("Times New Roman", 18));
        Hyperlink moreDetailsText = new Hyperlink("More Details  v");moreDetailsText.setLayoutX(primaryStage.getWidth() - 890);moreDetailsText.setLayoutY(primaryStage.getHeight() - 445);moreDetailsText.setFont(Font.font("Times New Roman", FontWeight.THIN, 13));moreDetailsText.setBorder(Border.EMPTY);

        //buttons
        Button addPersonButton = new Button("Add Person");addPersonButton.setMinHeight(40);addPersonButton.setMinWidth(100);addPersonButton.setLayoutX(primaryStage.getWidth() - 130);addPersonButton.setLayoutY(primaryStage.getHeight() - 630);
        Button searchExistingButton = new Button("Search Existing");searchExistingButton.setMinHeight(40);searchExistingButton.setMinWidth(100);searchExistingButton.setLayoutX(primaryStage.getWidth() - 130);searchExistingButton.setLayoutY(primaryStage.getHeight() - 580);searchExistingButton.setMaxWidth(100);searchExistingButton.setStyle("-fx-font-size:11");
        Button settingsButton = new Button("Settings");settingsButton.setPrefSize(100, 40);settingsButton.setLayoutX(searchExistingButton.getLayoutX());settingsButton.setLayoutY(searchExistingButton.getLayoutY() + 100);settingsButton.setStyle("-fx-font-size:11");

        //Add nodes of top pane to the top pane
        topPane.getChildren().addAll(nameText, DoBText, addressText, phoneNumberText, moreDetailsText, addPersonButton, searchExistingButton, settingsButton, imageView);

        //BOTTOM LEFT PANE NODES
        //listView

        //Text
        Text addressTitleText = new Text("Addresses");addressTitleText.setX(imageView.getX());addressTitleText.setY((primaryStage.getHeight() - 640) + yIncrement * 2);addressTitleText.setFont(Font.font("Times New Roman", FontWeight.EXTRA_BOLD, 22));addressTitleText.setVisible(false);
        Text primaryAddressSubTitleText = new Text("Primary Address");primaryAddressSubTitleText.setX(addressTitleText.getX() + xIncrement);primaryAddressSubTitleText.setY(addressTitleText.getY() + yIncrement * 2);primaryAddressSubTitleText.setUnderline(true);primaryAddressSubTitleText.setFont(Font.font("Times New Roman", FontWeight.LIGHT, 16));primaryAddressSubTitleText.setVisible(false);
        Text secondaryAddressSubTitleText = new Text("Secondary Address");secondaryAddressSubTitleText.setX(primaryAddressSubTitleText.getX());secondaryAddressSubTitleText.setY(primaryAddressSubTitleText.getY() + yIncrement * 6);secondaryAddressSubTitleText.setUnderline(true);secondaryAddressSubTitleText.setFont(Font.font("Times New Roman", FontWeight.LIGHT, 16));secondaryAddressSubTitleText.setVisible(false);

        Text phoneNumberTitleText = new Text("Phone Numbers");phoneNumberTitleText.setX(addressTitleText.getX());phoneNumberTitleText.setY(secondaryAddressSubTitleText.getY() + yIncrement * 8);phoneNumberTitleText.setFont(Font.font("Times New Roman", FontWeight.EXTRA_BOLD, 22));phoneNumberTitleText.setVisible(false);
        Text primaryPhoneNumberSubTitleText = new Text("Primary Phone Number");primaryPhoneNumberSubTitleText.setX(primaryAddressSubTitleText.getX());primaryPhoneNumberSubTitleText.setY(phoneNumberTitleText.getY() + yIncrement * 2);primaryPhoneNumberSubTitleText.setUnderline(true);primaryPhoneNumberSubTitleText.setFont(Font.font("Times New Roman", FontWeight.LIGHT, 16));primaryPhoneNumberSubTitleText.setVisible(false);
        Text secondaryPhoneNumberSubTitleText = new Text("Secondary Phone Numbers");secondaryPhoneNumberSubTitleText.setX(primaryPhoneNumberSubTitleText.getX());secondaryPhoneNumberSubTitleText.setY(primaryPhoneNumberSubTitleText.getY() + yIncrement * 6);secondaryPhoneNumberSubTitleText.setUnderline(true);secondaryPhoneNumberSubTitleText.setFont(Font.font("Times New Roman", FontWeight.LIGHT, 16));secondaryPhoneNumberSubTitleText.setVisible(false);

        Text emailTitleText = new Text("Emails");emailTitleText.setX(phoneNumberTitleText.getX());emailTitleText.setY(secondaryPhoneNumberSubTitleText.getY() + yIncrement * 8);emailTitleText.setFont(Font.font("Times New Roman", FontWeight.EXTRA_BOLD, 22));emailTitleText.setVisible(false);
        Text primaryEmailSubTitleText = new Text("Primary Email");primaryEmailSubTitleText.setX(primaryAddressSubTitleText.getX());primaryEmailSubTitleText.setY(emailTitleText.getY() + yIncrement * 2);primaryEmailSubTitleText.setUnderline(true);primaryEmailSubTitleText.setFont(Font.font("Times New Roman", FontWeight.LIGHT, 16));primaryEmailSubTitleText.setVisible(false);
        Text secondaryEmailSubTitleText = new Text("Secondary Emails");secondaryEmailSubTitleText.setX(primaryEmailSubTitleText.getX());secondaryEmailSubTitleText.setY(primaryEmailSubTitleText.getY() + yIncrement * 6);secondaryEmailSubTitleText.setUnderline(true);secondaryEmailSubTitleText.setFont(Font.font("Times New Roman", FontWeight.LIGHT, 16));secondaryEmailSubTitleText.setVisible(false);

        Text commentTitleText = new Text("Comments");commentTitleText.setX(addressTitleText.getX());commentTitleText.setY(emailTitleText.getY() + yIncrement * 8);commentTitleText.setFont(Font.font("Times New Roman", FontWeight.EXTRA_BOLD, 22));commentTitleText.setVisible(false);
        Text comment = new Text();comment.setX(commentTitleText.getX());comment.setVisible(false);
        //buttons

        //Add nodes of bottom pane to the bottom pane
        bottomLeftPane.getChildren().addAll(addressTitleText, primaryAddressSubTitleText, secondaryAddressSubTitleText, phoneNumberTitleText, primaryPhoneNumberSubTitleText, secondaryPhoneNumberSubTitleText, emailTitleText, primaryEmailSubTitleText, secondaryEmailSubTitleText, commentTitleText);

        //BOTTOM RIGHT PANE NODES
        //buttons
        Button exitButton = new Button("Exit");exitButton.setMinHeight(50);exitButton.setMinWidth(100);

        //listview
        ListView<Person> recentlySearchedListView = new ListView<>();recentlySearchedListView.setPrefSize(170, 280);

        //Add nodes of bottom right pane to the VBox & set settings
        bottomRightPane.setPadding(new Insets(0, 10, 10, 10));
        bottomRightPane.setSpacing(10);
        bottomRightPane.getChildren().addAll(recentlySearchedListView, exitButton);

        //node Functions
        //Hyperlink
        moreDetailsText.setOnAction(action ->
        {
            if(showingMore) //show less
            {
                showingMore = false;
                moreDetailsText.setText("More Details  v");

                //Change the visibility of nodes
                //upper pane
                addressText.setVisible(true);
                phoneNumberText.setVisible(true);

                //lower pane
                addressTitleText.setVisible(false);
                primaryAddressSubTitleText.setVisible(false);
                secondaryAddressSubTitleText.setVisible(false);
                for(Text address : addresses)
                {
                    address.setVisible(false);
                }

                phoneNumberTitleText.setVisible(false);
                primaryPhoneNumberSubTitleText.setVisible(false);
                secondaryPhoneNumberSubTitleText.setVisible(false);
                for(Text phoneNumber : phoneNumbers)
                {
                    phoneNumber.setVisible(false);
                }

                emailTitleText.setVisible(false);
                primaryEmailSubTitleText.setVisible(false);
                secondaryEmailSubTitleText.setVisible(false);
                for(Text email : emails)
                {
                    email.setVisible(false);
                }
                commentTitleText.setVisible(false);
            }
            else //show more
            {
                showingMore = true;
                moreDetailsText.setText("Less Details  ^");

                //change the visibility of nodes
                //upper pane
                addressText.setVisible(false);
                phoneNumberText.setVisible(false);

                //lower pane
                addressTitleText.setVisible(true);
                primaryAddressSubTitleText.setVisible(true);
                secondaryAddressSubTitleText.setVisible(true);
                for(Text address : addresses)
                {
                    address.setVisible(true);
                }

                phoneNumberTitleText.setVisible(true);
                primaryPhoneNumberSubTitleText.setVisible(true);
                secondaryPhoneNumberSubTitleText.setVisible(true);
                for(Text phoneNumber : phoneNumbers)
                {
                    phoneNumber.setVisible(true);
                }

                emailTitleText.setVisible(true);
                primaryEmailSubTitleText.setVisible(true);
                secondaryEmailSubTitleText.setVisible(true);
                for(Text email : emails)
                {
                    email.setVisible(true);
                }

                commentTitleText.setVisible(true);
                comment.setVisible(true);
            }
        });

        //ListView
        recentlySearchedListView.setOnMouseClicked(new EventHandler<MouseEvent>() { //This allows user to select a person from the recently searched listview
            @Override
            public void handle(MouseEvent mouseEvent) {
                if(mouseEvent.getClickCount() == 2)
                {
                    /* Guide to this function
                     * 1) Wipe the panes clear of the last contact
                     * 2) Reset the positions of bottom pane nodes
                     * 3) Actually search for your contacts via the method searchPersonWindow
                     * 4) Set the positions and create the Text objects necessary to properly display the selected contact
                     */

                    //wipe info from labels & clear ArrayLists
                    nameText.setText("");
                    DoBText.setText("");
                    addressText.setText("");
                    phoneNumberText.setText("");

                    for(int i = 0; i < addresses.size(); i++)
                    {
                        bottomLeftPane.getChildren().remove(addresses.get(i));
                    }
                    addresses.clear();
                    for(int i = 0; i < phoneNumbers.size(); i++)
                    {
                        bottomLeftPane.getChildren().remove(phoneNumbers.get(i));
                    }
                    phoneNumbers.clear();
                    for(int i = 0; i < emails.size(); i++)
                    {
                        bottomLeftPane.getChildren().remove(emails.get(i));
                    }
                    phoneNumbers.clear();
                    bottomLeftPane.getChildren().remove(comment);

                    //reset positions of bottom pane nodes
                    addressTitleText.setY(recentlySearchedListView.getLayoutY() + yIncrement * 2);
                    primaryAddressSubTitleText.setY(addressTitleText.getY() + yIncrement * 2);
                    secondaryAddressSubTitleText.setY(primaryAddressSubTitleText.getY() + yIncrement * 6);

                    phoneNumberTitleText.setY(secondaryAddressSubTitleText.getY() + yIncrement * 8);
                    primaryPhoneNumberSubTitleText.setY(phoneNumberTitleText.getY() + yIncrement * 2);
                    secondaryPhoneNumberSubTitleText.setY(primaryPhoneNumberSubTitleText.getY() + yIncrement * 6);

                    emailTitleText.setY(secondaryPhoneNumberSubTitleText.getY() + yIncrement * 8);
                    primaryEmailSubTitleText.setY(emailTitleText.getY() + yIncrement * 2);
                    secondaryEmailSubTitleText.setY(primaryEmailSubTitleText.getY() + yIncrement * 6);

                    commentTitleText.setY(secondaryEmailSubTitleText.getY() + yIncrement * 8);
                    comment.setY(commentTitleText.getY() + yIncrement * 2);

                    Person selectedPerson = recentlySearchedListView.getSelectionModel().getSelectedItem();

                    for(int i = 0; i < selectedPerson.getAddresses().size(); i++) //making the List of address Text objects
                    {
                        Text newAddress = new Text(selectedPerson.getAddresses().get(i).getStreetAddress() + " " + selectedPerson.getAddresses().get(i).getCity() + ", " + selectedPerson.getAddresses().get(i).getState() + " " + selectedPerson.getAddresses().get(i).getZipcode() + "  [" + selectedPerson.getAddresses().get(i).getType() + "]");
                        newAddress.setFont(Font.font("Times New Roman", FontWeight.NORMAL, 16));
                        if(showingMore)
                            newAddress.setVisible(true);
                        else
                            newAddress.setVisible(false);

                        if(i == 0) //Setting the first Address as the primary address
                        {
                            newAddress.setX(primaryAddressSubTitleText.getX());
                            newAddress.setY(primaryAddressSubTitleText.getY() + yIncrement * 3);
                        }
                        else //Setting the rest of the addresses as secondary addresses shown below
                        {
                            newAddress.setX(primaryAddressSubTitleText.getX());
                            newAddress.setY(secondaryAddressSubTitleText.getY() + (yIncrement * 2) + (yIncrement * 2 * i));
                        }
                        bottomLeftPane.getChildren().add(newAddress);

                        //move nodes below this one accordingly
                        if(i >= 2)
                        {
                            phoneNumberTitleText.setY(phoneNumberTitleText.getY() + yIncrement * i);
                            primaryPhoneNumberSubTitleText.setY(primaryPhoneNumberSubTitleText.getY() + yIncrement * i);
                            secondaryPhoneNumberSubTitleText.setY(secondaryPhoneNumberSubTitleText.getY() + yIncrement * i);

                            emailTitleText.setY(emailTitleText.getY() + yIncrement * i);
                            primaryEmailSubTitleText.setY(primaryEmailSubTitleText.getY() + yIncrement * i);
                            secondaryEmailSubTitleText.setY(secondaryEmailSubTitleText.getY() + yIncrement *i);

                            commentTitleText.setY(commentTitleText.getY() + yIncrement * i);
                            comment.setY(comment.getY() + yIncrement * i);
                        }
                        addresses.add(newAddress);
                    }

                    for(int i = 0; i < selectedPerson.getPhoneNumbers().size(); i++) //making the List of phone number Text objects
                    {
                        Text newPhoneNumber = new Text(selectedPerson.getPhoneNumbers().get(i).getPhoneNumber() + "  [" + selectedPerson.getPhoneNumbers().get(i).getType() + "]");
                        newPhoneNumber.setFont(Font.font("Times New Roman", FontWeight.NORMAL, 16));
                        if(showingMore)
                            newPhoneNumber.setVisible(true);
                        else
                            newPhoneNumber.setVisible(false);

                        if(i ==0) //Setting first phone # as primary phone #
                        {
                            newPhoneNumber.setX(primaryPhoneNumberSubTitleText.getX());
                            newPhoneNumber.setY(primaryPhoneNumberSubTitleText.getY() + yIncrement * 3);
                        }
                        else //setting the rest of the phone numbers as secondary phone #
                        {
                            newPhoneNumber.setX(primaryPhoneNumberSubTitleText.getX());
                            newPhoneNumber.setY(secondaryPhoneNumberSubTitleText.getY() + (yIncrement * 2) + (yIncrement * 2 * i));
                        }
                        bottomLeftPane.getChildren().add(newPhoneNumber);

                        //move nodes below this one accordingly
                        if(i >= 2)
                        {
                            emailTitleText.setY(emailTitleText.getY() + yIncrement * i);
                            primaryEmailSubTitleText.setY(primaryEmailSubTitleText.getY() + yIncrement * i);
                            secondaryEmailSubTitleText.setY(secondaryEmailSubTitleText.getY() + yIncrement *i);

                            commentTitleText.setY(commentTitleText.getY() + yIncrement * i);
                            comment.setY(comment.getY() + yIncrement * i);
                        }
                        phoneNumbers.add(newPhoneNumber);
                    }

                    for(int i = 0; i < selectedPerson.getEmails().size(); i++) // making a list of email Text objects
                    {
                        Text newEmail = new Text(selectedPerson.getEmails().get(i).getEmailAddress() + "  [" + selectedPerson.getEmails().get(i).getType() + "]");
                        newEmail.setFont(Font.font("Times New Roman", FontWeight.NORMAL, 16));
                        if(showingMore)
                            newEmail.setVisible(true);
                        else
                            newEmail.setVisible(false);

                        if(i ==0) //Setting first email as primary email
                        {
                            newEmail.setX(primaryEmailSubTitleText.getX());
                            newEmail.setY(primaryEmailSubTitleText.getY() + yIncrement * 3);
                        }
                        else //setting the rest of the emails as secondary emails
                        {
                            newEmail.setX(primaryEmailSubTitleText.getX());
                            newEmail.setY(secondaryEmailSubTitleText.getY() + (yIncrement * 2) + (yIncrement * 3 * i));
                        }
                        bottomLeftPane.getChildren().add(newEmail);

                        //move nodes below accordingly
                        if(i >= 2)
                        {
                            commentTitleText.setY(commentTitleText.getY() + yIncrement * i);
                            comment.setY(comment.getY() + yIncrement * i);
                        }

                        emails.add(newEmail);
                    }

                    bottomLeftPane.getChildren().add(comment);

                    //Set the overhead text to display appropriate data
                    String dateString = dtFormatter.format(selectedPerson.getDateOfBirth());

                    if(!recentlySearchedListView.getItems().contains(selectedPerson)) //if the person isn't added to the list yet, add them at the top
                    {
                        recentlySearchedListView.getItems().add(selectedPerson);
                    }
                    else //if the person is already on the recently searched list, then make the most recent search appear on top
                    {
                        ArrayList<Person> personList = new ArrayList<>(recentlySearchedListView.getItems());
                        recentlySearchedListView.getItems().clear();
                        recentlySearchedListView.getItems().add(selectedPerson);
                        for(Person p : personList)
                        {
                            if(p != selectedPerson)
                            {
                                recentlySearchedListView.getItems().add(p);
                            }
                        }
                    }

                    nameText.setText(selectedPerson.toString());
                    DoBText.setText(dateString);
                    if(addresses.size() > 0)
                        addressText.setText(addresses.get(0).getText());
                    else
                        addressText.setText("No Address Found");
                    if(phoneNumbers.size() > 0)
                        phoneNumberText.setText(phoneNumbers.get(0).getText());
                    else
                        phoneNumberText.setText("No Phone Number Found");
                    comment.setText(selectedPerson.getComment());
                }
            }
        });
        //buttons
        exitButton.setOnAction(action ->
        {
            System.exit(0);
        });

        addPersonButton.setOnAction(action ->
        {
            this.contacts.add(addPersonWindow());
        });

        searchExistingButton.setOnAction(action ->
        {
            /* GUIDE TO THIS BUTTON
             * 1) Wipe the panes clear of the last contact
             * 2) Reset the positions of bottom pane nodes
             * 3) Actually search for your contacts via the method searchPersonWindow
             * 4) Set the positions and create the Text objects necessary to properly display the selected contact
            */

            //wipe info from labels & clear ArrayLists
            nameText.setText("");
            DoBText.setText("");
            addressText.setText("");
            phoneNumberText.setText("");

            for(int i = 0; i < addresses.size(); i++)
            {
                bottomLeftPane.getChildren().remove(addresses.get(i));
            }
            addresses.clear();
            for(int i = 0; i < phoneNumbers.size(); i++)
            {
                bottomLeftPane.getChildren().remove(phoneNumbers.get(i));
            }
            phoneNumbers.clear();
            for(int i = 0; i < emails.size(); i++)
            {
                bottomLeftPane.getChildren().remove(emails.get(i));
            }
            phoneNumbers.clear();
            bottomLeftPane.getChildren().remove(comment);

            //reset positions of bottom pane nodes
            addressTitleText.setY(recentlySearchedListView.getLayoutY() + yIncrement * 2);
            primaryAddressSubTitleText.setY(addressTitleText.getY() + yIncrement * 2);
            secondaryAddressSubTitleText.setY(primaryAddressSubTitleText.getY() + yIncrement * 6);

            phoneNumberTitleText.setY(secondaryAddressSubTitleText.getY() + yIncrement * 8);
            primaryPhoneNumberSubTitleText.setY(phoneNumberTitleText.getY() + yIncrement * 2);
            secondaryPhoneNumberSubTitleText.setY(primaryPhoneNumberSubTitleText.getY() + yIncrement * 6);

            emailTitleText.setY(secondaryPhoneNumberSubTitleText.getY() + yIncrement * 8);
            primaryEmailSubTitleText.setY(emailTitleText.getY() + yIncrement * 2);
            secondaryEmailSubTitleText.setY(primaryEmailSubTitleText.getY() + yIncrement * 6);

            commentTitleText.setY(secondaryEmailSubTitleText.getY() + yIncrement * 8);
            comment.setY(commentTitleText.getY() + yIncrement * 2);

            Person selectedPerson = searchPersonWindow();
            if(selectedPerson == null) //display error message if no contact was selected
            {
                nameText.setText("Error: No Contact was Selected...");
            }
            else
            {
                for(int i = 0; i < selectedPerson.getAddresses().size(); i++) //making the List of address Text objects
                {
                    Text newAddress = new Text(selectedPerson.getAddresses().get(i).getStreetAddress() + " " + selectedPerson.getAddresses().get(i).getCity() + ", " + selectedPerson.getAddresses().get(i).getState() + " " + selectedPerson.getAddresses().get(i).getZipcode() + "  [" + selectedPerson.getAddresses().get(i).getType() + "]");
                    newAddress.setFont(Font.font("Times New Roman", FontWeight.NORMAL, 16));
                    if(showingMore)
                        newAddress.setVisible(true);
                    else
                        newAddress.setVisible(false);

                    if(i == 0) //Setting the first Address as the primary address
                    {
                        newAddress.setX(primaryAddressSubTitleText.getX());
                        newAddress.setY(primaryAddressSubTitleText.getY() + yIncrement * 3);
                    }
                    else //Setting the rest of the addresses as secondary addresses shown below
                    {
                        newAddress.setX(primaryAddressSubTitleText.getX());
                        newAddress.setY(secondaryAddressSubTitleText.getY() + yIncrement *3);
                    }
                    bottomLeftPane.getChildren().add(newAddress);

                    //move nodes below this one accordingly
                    if(i >= 2)
                    {
                        phoneNumberTitleText.setY(phoneNumberTitleText.getY() + yIncrement * i);
                        primaryPhoneNumberSubTitleText.setY(primaryPhoneNumberSubTitleText.getY() + yIncrement * i);
                        secondaryPhoneNumberSubTitleText.setY(secondaryPhoneNumberSubTitleText.getY() + yIncrement * i);

                        emailTitleText.setY(emailTitleText.getY() + yIncrement * i);
                        primaryEmailSubTitleText.setY(primaryEmailSubTitleText.getY() + yIncrement * i);
                        secondaryEmailSubTitleText.setY(secondaryEmailSubTitleText.getY() + yIncrement *i);

                        commentTitleText.setY(commentTitleText.getY() + yIncrement * i);
                        comment.setY(comment.getY() + yIncrement * i);
                    }
                    addresses.add(newAddress);
                }

                for(int i = 0; i < selectedPerson.getPhoneNumbers().size(); i++) //making the List of phone number Text objects
                {
                    Text newPhoneNumber = new Text(selectedPerson.getPhoneNumbers().get(i).getPhoneNumber() + "  [" + selectedPerson.getPhoneNumbers().get(i).getType() + "]");
                    newPhoneNumber.setFont(Font.font("Times New Roman", FontWeight.NORMAL, 16));
                    if(showingMore)
                        newPhoneNumber.setVisible(true);
                    else
                        newPhoneNumber.setVisible(false);

                    if(i ==0) //Setting first phone # as primary phone #
                    {
                        newPhoneNumber.setX(primaryPhoneNumberSubTitleText.getX());
                        newPhoneNumber.setY(primaryPhoneNumberSubTitleText.getY() + yIncrement * 3);
                    }
                    else //setting the rest of the phone numbers as secondary phone #
                    {
                        newPhoneNumber.setX(primaryPhoneNumberSubTitleText.getX());
                        newPhoneNumber.setY(secondaryPhoneNumberSubTitleText.getY() + (yIncrement * 2) + (yIncrement * 2 * i));
                    }
                    bottomLeftPane.getChildren().add(newPhoneNumber);

                    //move nodes below this one accordingly
                    if(i >= 2)
                    {
                        emailTitleText.setY(emailTitleText.getY() + yIncrement * i);
                        primaryEmailSubTitleText.setY(primaryEmailSubTitleText.getY() + yIncrement * i);
                        secondaryEmailSubTitleText.setY(secondaryEmailSubTitleText.getY() + yIncrement *i);

                        commentTitleText.setY(commentTitleText.getY() + yIncrement * i);
                        comment.setY(comment.getY() + yIncrement * i);
                    }
                    phoneNumbers.add(newPhoneNumber);
                }

                for(int i = 0; i < selectedPerson.getEmails().size(); i++) // making a list of email Text objects
                {
                    Text newEmail = new Text(selectedPerson.getEmails().get(i).getEmailAddress() + "  [" + selectedPerson.getEmails().get(i).getType() + "]");
                    newEmail.setFont(Font.font("Times New Roman", FontWeight.NORMAL, 16));
                    if(showingMore)
                        newEmail.setVisible(true);
                    else
                        newEmail.setVisible(false);

                    if(i ==0) //Setting first email as primary email
                    {
                        newEmail.setX(primaryEmailSubTitleText.getX());
                        newEmail.setY(primaryEmailSubTitleText.getY() + yIncrement * 3);
                    }
                    else //setting the rest of the emails as secondary emails
                    {
                        newEmail.setX(primaryEmailSubTitleText.getX());
                        newEmail.setY(secondaryEmailSubTitleText.getY() + (yIncrement * 2) + (yIncrement * 3 * i));
                    }
                    bottomLeftPane.getChildren().add(newEmail);

                    //move nodes below accordingly
                    if(i >= 2)
                    {
                        commentTitleText.setY(commentTitleText.getY() + yIncrement * i);
                        comment.setY(comment.getY() + yIncrement * i);
                    }

                    emails.add(newEmail);
                }

                bottomLeftPane.getChildren().add(comment);

                //Set the overhead text to display appropriate data
                String dateString = dtFormatter.format(selectedPerson.getDateOfBirth());

                //If the listview contains the name, don't add it.  If it does, add it
                if(!recentlySearchedListView.getItems().contains(selectedPerson))
                {
                    ArrayList<Person> personList = new ArrayList<>(recentlySearchedListView.getItems());
                    recentlySearchedListView.getItems().clear();
                    recentlySearchedListView.getItems().add(selectedPerson);

                    for(Person p : personList)
                    {
                        if(p != selectedPerson)
                        {
                            recentlySearchedListView.getItems().add(p);
                        }
                    }
                }
                else //if the person is already on the recently searched list, then make the most recent search appear on top
                {
                    ArrayList<Person> personList = new ArrayList<>(recentlySearchedListView.getItems());
                    recentlySearchedListView.getItems().clear();
                    recentlySearchedListView.getItems().add(selectedPerson);
                    for(Person p : personList)
                    {
                        if(p != selectedPerson)
                        {
                            recentlySearchedListView.getItems().add(p);
                        }
                    }
                }

                //set the appropriate text
                nameText.setText(selectedPerson.toString());
                DoBText.setText(dateString);
                if(addresses.size() > 0)
                    addressText.setText(addresses.get(0).getText());
                else
                    addressText.setText("No Address Found");
                if(phoneNumbers.size() > 0)
                    phoneNumberText.setText(phoneNumbers.get(0).getText());
                else
                    phoneNumberText.setText("No Phone Number Foudn");
                comment.setText(selectedPerson.getComment());
            }
        });

        settingsButton.setOnAction(action ->
        {
            settingsScreen();
        });

        //AnchorPane
        ap.getChildren().addAll(bottomLeftPane, bottomRightPane);
        AnchorPane.setBottomAnchor(bottomRightPane, 8.0);
        AnchorPane.setRightAnchor(bottomRightPane, 5.0);
        AnchorPane.setLeftAnchor(bottomLeftPane, 10.0);

        //Scroll Pane
        sp.setContent(bottomLeftPane);
        sp.setPannable(true);
        sp.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        //Adding the rest of the panes
        root.setTop(topPane);
        root.setCenter(sp);
        root.setRight(bottomRightPane);

        //Scene
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void settingsScreen()
    {
        //Stage Specifications
        Stage tempStage = new Stage();
        tempStage.initModality(Modality.WINDOW_MODAL);
        tempStage.setTitle("Creeper - Settings");
        tempStage.setWidth(600);tempStage.setHeight(800);
        tempStage.setResizable(false);

        try //set the icon image
        {
            tempStage.getIcons().add(new Image("https://i.pinimg.com/originals/7e/67/79/7e6779bf6d689ef9d288052bdbfdcf41.jpg"));
        }catch(Exception e)
        {
            popUpErrorWindow("ERROR: " + e.toString());
        }

        //Panes & Tabs
        //Root
        BorderPane root = new BorderPane();

        //TabPane & Tabs
        TabPane tp = new TabPane();
        tp.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
        Tab tab1 = new Tab("User");
        Tab tab2 = new Tab("Contacts");
        Tab tab3 = new Tab("Search");

        tp.getTabs().add(tab1);
        tp.getTabs().add(tab2);
        tp.getTabs().add(tab3);

        root.setCenter(tp);

        //Panes within Tabs
        Pane userSettingsPane = new Pane();
        tab1.setContent(userSettingsPane);

        Pane contactSettingsPane = new Pane();
        ScrollPane contactSP = new ScrollPane();
        contactSP.setContent(contactSettingsPane);
        contactSP.setPannable(true);
        contactSP.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        tab2.setContent(contactSP);

        Pane searchSettingsPane = new Pane();
        tab3.setContent(searchSettingsPane);

        //Bottom Pane
        HBox bottomPane = new HBox(400);
        root.setBottom(bottomPane);

        //NODES
        //User Settings Nodes
        //Labels / Text
        Label userSettingsLabel = new Label("Edit User Information");userSettingsLabel.setLayoutX(tempStage.getWidth() - 590);userSettingsLabel.setLayoutY(tempStage.getHeight() - 790);userSettingsLabel.setFont(Font.font("Times New Roman", FontWeight.EXTRA_BOLD, 18));
        Label nameTitleLabel = new Label("User's Name");nameTitleLabel.setLayoutX(userSettingsLabel.getLayoutX());nameTitleLabel.setLayoutY(userSettingsLabel.getLayoutY() + 50);nameTitleLabel.setFont(Font.font("Times New Roman", FontWeight.BOLD, 14));
        Label usernameTitleLabel = new Label("Username");usernameTitleLabel.setLayoutX(userSettingsLabel.getLayoutX());usernameTitleLabel.setLayoutY(nameTitleLabel.getLayoutY() + 100);usernameTitleLabel.setFont(Font.font("Times New Roman", FontWeight.BOLD, 14));
        Label passwordTitleLabel = new Label("Password");passwordTitleLabel.setLayoutX(userSettingsLabel.getLayoutX());passwordTitleLabel.setLayoutY(usernameTitleLabel.getLayoutY() + 70);passwordTitleLabel.setFont(Font.font("Times New Roman", FontWeight.BOLD, 14));
        Label measurementTitleLabel = new Label("Measurement Preference");measurementTitleLabel.setLayoutX(userSettingsLabel.getLayoutX());measurementTitleLabel.setLayoutY(passwordTitleLabel.getLayoutY() + 70);measurementTitleLabel.setFont(Font.font("Times New Roman", FontWeight.BOLD, 14));measurementTitleLabel.setUnderline(true);
        Label metricTitleLabel = new Label("Metric");metricTitleLabel.setLayoutX(measurementTitleLabel.getLayoutX() + 20);metricTitleLabel.setLayoutY(measurementTitleLabel.getLayoutY() + 20);
        Label imperialTitleLabel = new Label("Imperial");imperialTitleLabel.setLayoutX(metricTitleLabel.getLayoutX() + 50);imperialTitleLabel.setLayoutY(metricTitleLabel.getLayoutY());
        Label categoriesTitleLabel = new Label("Categories");categoriesTitleLabel.setLayoutX(userSettingsLabel.getLayoutX());categoriesTitleLabel.setLayoutY(measurementTitleLabel.getLayoutY() + 70);categoriesTitleLabel.setFont(Font.font("Times New Roman", FontWeight.BOLD, 15));

        Text firstNameText = new Text(this.currentUser.getFirstName());firstNameText.setX(nameTitleLabel.getLayoutX() + 10);firstNameText.setY(nameTitleLabel.getLayoutY() + 40);
        Text lastNameText = new Text(this.currentUser.getLastName());lastNameText.setX(firstNameText.getX());lastNameText.setY(firstNameText.getY() + 40);
        Text usernameText = new Text(this.currentUser.getUsername());usernameText.setX(firstNameText.getX());usernameText.setY(usernameTitleLabel.getLayoutY() + 40);
        Text passwordText = new Text(this.currentUser.getEncryptedPassword().replaceAll(".", "\\*"));passwordText.setX(firstNameText.getX());passwordText.setY(passwordTitleLabel.getLayoutY() + 40);
        Text passwordHelp = new Text();passwordHelp.setX(firstNameText.getX());passwordHelp.setY(passwordText.getY() + 75);passwordHelp.setFill(Color.RED);

        //TextFields
        TextField firstNameTextField = new TextField(this.currentUser.getFirstName());firstNameTextField.setLayoutX(firstNameText.getX());firstNameTextField.setLayoutY(firstNameText.getY() - 10);firstNameTextField.setVisible(false);
        TextField lastNameTextField = new TextField(this.currentUser.getLastName());lastNameTextField.setLayoutX(lastNameText.getX());lastNameTextField.setLayoutY(lastNameText.getY() - 10);lastNameTextField.setVisible(false);
        TextField usernameTextField = new TextField(this.currentUser.getUsername());usernameTextField.setLayoutX(usernameText.getX());usernameTextField.setLayoutY(usernameText.getY() - 10);usernameTextField.setVisible(false);
        TextField newCategoryTextField = new TextField();newCategoryTextField.setLayoutX((categoriesTitleLabel.getLayoutX() + 240));newCategoryTextField.setLayoutY(categoriesTitleLabel.getLayoutY() + 145);


        //Password Fields
        PasswordField passwordTextField = new PasswordField();passwordTextField.setLayoutX(passwordText.getX());passwordTextField.setLayoutY(passwordText.getY() - 10);passwordTextField.setVisible(false);passwordTextField.setPromptText("Enter New Password");
        PasswordField confirmPasswordTextField = new PasswordField();confirmPasswordTextField.setLayoutX(passwordText.getX());confirmPasswordTextField.setLayoutY(passwordTextField.getLayoutY() + 40);confirmPasswordTextField.setVisible(false);confirmPasswordTextField.setPromptText("Confirm New Password");

        //HyperText
        Hyperlink changeUserFirstName = new Hyperlink("change");changeUserFirstName.setLayoutX(firstNameText.getX() + 150);changeUserFirstName.setLayoutY(firstNameText.getY() - 15);
        Hyperlink changeUserLastName = new Hyperlink("change");changeUserLastName.setLayoutX(changeUserFirstName.getLayoutX());changeUserLastName.setLayoutY(lastNameText.getY() - 15);
        Hyperlink changeUsername = new Hyperlink("change");changeUsername.setLayoutX(changeUserFirstName.getLayoutX());changeUsername.setLayoutY(usernameText.getY() - 15);
        Hyperlink changePassword = new Hyperlink("change");changePassword.setLayoutX(changeUserFirstName.getLayoutX());changePassword.setLayoutY(passwordText.getY() - 15);

        //Radio Buttons
        ToggleGroup measurementPref = new ToggleGroup();
        RadioButton metricRB = new RadioButton();metricRB.setLayoutX(metricTitleLabel.getLayoutX() + 10);metricRB.setLayoutY(metricTitleLabel.getLayoutY() + 20);
        RadioButton imperialRB = new RadioButton();imperialRB.setLayoutX(imperialTitleLabel.getLayoutX() + 10);imperialRB.setLayoutY(metricRB.getLayoutY());

        metricRB.setToggleGroup(measurementPref);
        imperialRB.setToggleGroup(measurementPref);

        if(currentUser.getIsMetricUser()) //select the users choice of measurement system
        {
            metricRB.setSelected(true);
        }
        else
        {
            imperialRB.setSelected(true);
        }

        //List View
        ListView<String> categoryListView = new ListView<>(FXCollections.observableArrayList(this.currentUser.getCategories()));categoryListView.setPrefSize(235, 150);categoryListView.setLayoutX(categoriesTitleLabel.getLayoutX());categoryListView.setLayoutY(categoriesTitleLabel.getLayoutY() + 20);

        //Buttons
        Button removeCategory = new Button("Remove");removeCategory.setLayoutX(categoryListView.getLayoutX() + 240);removeCategory.setLayoutY(categoryListView.getLayoutY());
        Button addCategory = new Button("Add");addCategory.setLayoutX(removeCategory.getLayoutX());addCategory.setLayoutY(newCategoryTextField.getLayoutY() - 30);

        //User Settings Node Functionality
        //Hypertexts
        changeUserFirstName.setOnAction(action ->
        {
            if(firstNameTextField.isVisible())
            {
                firstNameTextField.setVisible(false);
                changeUserFirstName.setText("change");

                firstNameTextField.setText(this.currentUser.getFirstName());
            }
            else
            {
                firstNameTextField.setVisible(true);
                changeUserFirstName.setText("cancel change");
            }
        });

        changeUserLastName.setOnAction(action ->
        {
            if(lastNameTextField.isVisible())
            {
                lastNameTextField.setVisible(false);
                changeUserLastName.setText("change");

                lastNameTextField.setText(this.currentUser.getLastName());
            }
            else
            {
                lastNameTextField.setVisible(true);
                changeUserLastName.setText("cancel change");
            }
        });

        changeUsername.setOnAction(action ->
        {
            if(usernameTextField.isVisible())
            {
                usernameTextField.setVisible(false);
                changeUsername.setText("change");

                usernameTextField.setText(this.currentUser.getUsername());
            }
            else
            {
                usernameTextField.setVisible(true);
                changeUsername.setText("cancel change");
            }
        });

        changePassword.setOnAction(action ->
        {
            if(passwordTextField.isVisible())
            {
                passwordText.setVisible(true);
                passwordTextField.setVisible(false);
                confirmPasswordTextField.setVisible(false);
                passwordHelp.setVisible(false);

                changePassword.setText("change");

                measurementTitleLabel.setLayoutY(measurementTitleLabel.getLayoutY() - 55);
                metricTitleLabel.setLayoutY(metricTitleLabel.getLayoutY() - 55);
                metricRB.setLayoutY(metricRB.getLayoutY() - 55);
                imperialTitleLabel.setLayoutY(imperialTitleLabel.getLayoutY() - 55);
                imperialRB.setLayoutY(imperialRB.getLayoutY() - 55);
                categoriesTitleLabel.setLayoutY(categoriesTitleLabel.getLayoutY() - 55);
                categoryListView.setLayoutY(categoryListView.getLayoutY() - 55);
                removeCategory.setLayoutY(removeCategory.getLayoutY() - 55);
                addCategory.setLayoutY(addCategory.getLayoutY() - 55);
                newCategoryTextField.setLayoutY(newCategoryTextField.getLayoutY() - 55);

                passwordTextField.clear();
                confirmPasswordTextField.clear();
            }
            else
            {
                passwordText.setVisible(false);
                passwordTextField.setVisible(true);
                confirmPasswordTextField.setVisible(true);
                passwordHelp.setVisible(true);

                changePassword.setText("cancel change");

                measurementTitleLabel.setLayoutY(measurementTitleLabel.getLayoutY() + 55);
                metricTitleLabel.setLayoutY(metricTitleLabel.getLayoutY() + 55);
                metricRB.setLayoutY(metricRB.getLayoutY() + 55);
                imperialTitleLabel.setLayoutY(imperialTitleLabel.getLayoutY() + 55);
                imperialRB.setLayoutY(imperialRB.getLayoutY() + 55);
                categoriesTitleLabel.setLayoutY(categoriesTitleLabel.getLayoutY() + 55);
                categoryListView.setLayoutY(categoryListView.getLayoutY() + 55);
                removeCategory.setLayoutY(removeCategory.getLayoutY() + 55);
                addCategory.setLayoutY(addCategory.getLayoutY() + 55);
                newCategoryTextField.setLayoutY(newCategoryTextField.getLayoutY() + 55);

                passwordTextField.clear();
                confirmPasswordTextField.clear();
            }
        });

        //TextField
        confirmPasswordTextField.setOnKeyTyped(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if(!passwordTextField.getText().equals(confirmPasswordTextField.getText())) //If the passwords DON'T match
                {
                    passwordHelp.setText("Passwords don't match");
                }
                else if(currentUser.hash(passwordTextField.getText()).equals(currentUser.getEncryptedPassword())) //If user attempts to use an old password
                {
                    passwordHelp.setText("You cannot use your old password!");
                }
                else //if they're good to go
                {
                    passwordHelp.setText("");
                }
            }
        });

        //Buttons
        removeCategory.setOnAction(action ->
        {
            if(categoryListView.getSelectionModel().getSelectedItem().equals("Unassigned"))
            {
                popUpErrorWindow("You cannot remove category, \"Unassigned\"");
            }
            else {
                String notificationOfChange = "";
                for(int i = 0; i < this.contacts.size(); i++) //search through the current contacts and inform user that the contacts with the removed category have been changed to "Unassigned"
                {
                    for(int j = 0; j < this.contacts.get(i).getCategories().size(); j++) //this loop determines which category, in the list of categories a person has, is being replaced
                    {
                        if(this.contacts.get(i).getCategories().get(j).equals(categoryListView.getSelectionModel().getSelectedItem()))
                        {
                            notificationOfChange = notificationOfChange + "Your contact, \"" + this.contacts.get(i).getFirstName() + " " + this.contacts.get(i).getLastName() + "\",\n category was changed from \n\n\"" + this.contacts.get(i).getCategory(j) + "\"\n\n to \n\n" + "\"Unassigned\"" + "\n\n------------------------------------------------------\n\n";

                            this.contacts.get(i).setCategory("Unassigned", j);
                        }
                    }
                }
                if(!notificationOfChange.equals("")) {
                    popUpErrorWindow(notificationOfChange);
                }

                for(int i = 0; i < this.currentUser.getCategories().size(); i++) //actually removes the category from the list in currentUser
                {
                    if(this.currentUser.getCategories().get(i).equals(categoryListView.getSelectionModel().getSelectedItem()))
                    {
                        this.currentUser.removeCategory(i);
                    }
                }
                categoryListView.getItems().remove(categoryListView.getSelectionModel().getSelectedItem());
            }
        });

        addCategory.setOnAction(action ->
        {
            boolean categoryPresent = false;
            if(newCategoryTextField.getText().length() != 0) {
                for (int i = 0; i < this.currentUser.getCategories().size(); i++) //goes through the current list of categories
                {
                    if (newCategoryTextField.getText().equals(this.currentUser.getCategories().get(i))) //checks if the inputted text is already a category
                    {
                        categoryPresent = true;
                    }
                }
                if(categoryPresent) // if it is already a saved category, send an error message
                {
                    popUpErrorWindow("That category already exists");
                }
                else { //if not, add it to the listView to be added to the global list in the "save" button
                    categoryListView.getItems().add(newCategoryTextField.getText());
                    this.currentUser.addCategory(newCategoryTextField.getText());
                }
                newCategoryTextField.clear();
            } else {
                popUpErrorWindow("You need to add text to add a category");
            }
        });

        //add it to the pane
        userSettingsPane.getChildren().addAll(userSettingsLabel, nameTitleLabel, usernameTitleLabel, passwordTitleLabel, measurementTitleLabel, metricTitleLabel, imperialTitleLabel, categoriesTitleLabel, //labels
                firstNameText, lastNameText, usernameText, passwordText, passwordHelp, //text
                firstNameTextField, lastNameTextField, usernameTextField, passwordTextField, confirmPasswordTextField, newCategoryTextField, //TextFields
                changeUserFirstName, changeUserLastName, changeUsername, changePassword, //Hypertext
                metricRB, imperialRB, //Radio Buttons
                categoryListView, //ListView
                removeCategory, addCategory); //Buttons

        //CONTACT SETTINGS Nodes
        //Variables
        String dateOfBirthRegex = "\\b(0[1-9]|1[0-2])/(0[1-9]|[1-2]\\d|3[0-1]|)/\\d{4}$";
        String phoneNumberRegex = "[\\d|\\-|\\.|(|)| ]";
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd LLLL yyyy");
        String[] stateAbb = {"--", "AK", "AL", "AR", "AZ", "CA", "CO", "CT", "DE", "FL", "GA", "HI", "IA", "ID", "IL", "IN", "KS", "KY", "LA", "MA", "MD", "ME", "MI", "MN", "MO" , "MS" , "MT" , "NC" , "ND" , "NE" , "NH" , "NJ" , "NM" , "NV" , "NY" , "OH" , "OK" , "OR" , "PA" , "RI" , "SC" , "SD", "TN" , "TX" , "UT" , "VA", "VT", "WA", "WI", "WV", "WY"};
        String[] addressTypes = {"--", "Home", "Work", "Other"};
        String[] phoneNumberTypes = {"--", "Home", "Cell", "Work", "Other"};
        String[] emailTypes = {"--", "Personal", "Work", "Other"};
        String[] eyeColorTypes = {"--", "Brown", "Blue", "Hazel", "Amber", "Green", "Grey"};
        String[] hairColorTypes = {"--", "Brown", "Black", "Blond", "Red", "White"};

        //ArrayLists
            //Address
            //Text
            ArrayList<Text> streetAddressesTextObj = new ArrayList<>();
            ArrayList<Text> cityTextObj = new ArrayList<>();
            ArrayList<Text> stateTextObj = new ArrayList<>();
            ArrayList<Text> zipcodeTextObj = new ArrayList<>();

            //Textfield
            ArrayList<TextField> streetAddressesTxtField = new ArrayList<>();
            ArrayList<TextField> citiesTxtField = new ArrayList<>();
            ArrayList<ComboBox> statesComboBox = new ArrayList<>();
            ArrayList<TextField> zipcodesTxtField = new ArrayList<>();
            ArrayList<ComboBox> addressTypeComboBoxes = new ArrayList<>();
            ArrayList<Hyperlink> changeAddresses = new ArrayList<>();

            //phone number
            //text
            ArrayList<Text> phoneNumberTxtObj = new ArrayList<>();
            ArrayList<Text> phoneNumberTypeTxtObj = new ArrayList<>();

            //Textfield, combobox, & hyperlink
            ArrayList<TextField> phoneNumbersTxtField = new ArrayList<>();
            ArrayList<ComboBox> phoneNumberTypeComboBoxes = new ArrayList<>();
            ArrayList<Hyperlink> changePhoneNumbers = new ArrayList<>();

            //email
            //text
            ArrayList<Text> emailTxtObj = new ArrayList<>();
            ArrayList<Text> emailTypeTextObj = new ArrayList<>();

            //Textfield, combobox, & hyperlink
            ArrayList<TextField> emailsTxtField = new ArrayList<>();
            ArrayList<ComboBox> emailTypeComboBoxes = new ArrayList<>();
            ArrayList<Hyperlink> changeEmails = new ArrayList<>();

        //Labels
        Label editContactsLabel = new Label("Edit Contacts");editContactsLabel.setLayoutX(tempStage.getWidth() - 590);editContactsLabel.setLayoutY(tempStage.getHeight() - 790);editContactsLabel.setFont(Font.font("Times New Roman", FontWeight.EXTRA_BOLD, 18));
        Label searchPrefSubTitle = new Label("Search");searchPrefSubTitle.setLayoutX(editContactsLabel.getLayoutX());searchPrefSubTitle.setLayoutY(editContactsLabel.getLayoutY() + 20);searchPrefSubTitle.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.ITALIC, 14));
        Label contactNameTitle = new Label("Name");contactNameTitle.setLayoutX(editContactsLabel.getLayoutX());contactNameTitle.setLayoutY(searchPrefSubTitle.getLayoutY() + 40);contactNameTitle.setFont(Font.font("Times New Roman", FontWeight.BOLD, 14));contactNameTitle.setUnderline(true);contactNameTitle.setVisible(false);
        Label miscDetailsTitle = new Label("Details");miscDetailsTitle.setLayoutX(contactNameTitle.getLayoutX());miscDetailsTitle.setLayoutY(searchPrefSubTitle.getLayoutY() + 180);miscDetailsTitle.setFont(Font.font("Times New Roman", FontWeight.BOLD, 14));miscDetailsTitle.setUnderline(true);miscDetailsTitle.setVisible(false);
        Label contactSexTitle = new Label("Sex");contactSexTitle.setLayoutX(editContactsLabel.getLayoutX());contactSexTitle.setLayoutY(miscDetailsTitle.getLayoutY() + 120);contactSexTitle.setFont(Font.font("Times New Roman", FontWeight.BOLD, 14));contactSexTitle.setUnderline(true);contactSexTitle.setVisible(false);
        Label addressesDropDownLabel = new Label("Addresses");addressesDropDownLabel.setLayoutX(searchPrefSubTitle.getLayoutX());addressesDropDownLabel.setLayoutY(contactSexTitle.getLayoutY() + 100);addressesDropDownLabel.setFont(Font.font("Times New Roman", FontWeight.BOLD, 14));addressesDropDownLabel.setVisible(false);
        Label phoneNumberDropDownLabel = new Label("Phone Numbers");phoneNumberDropDownLabel.setLayoutX(addressesDropDownLabel.getLayoutX());phoneNumberDropDownLabel.setLayoutY(addressesDropDownLabel.getLayoutY() + 60);phoneNumberDropDownLabel.setFont(Font.font("Times New Roman", FontWeight.BOLD, 14));phoneNumberDropDownLabel.setVisible(false);
        Label emailsDropDownLabel = new Label("Emails");emailsDropDownLabel.setLayoutX(addressesDropDownLabel.getLayoutX());emailsDropDownLabel.setLayoutY(phoneNumberDropDownLabel.getLayoutY() + 60);emailsDropDownLabel.setFont(Font.font("Times New Roman", FontWeight.BOLD, 14));emailsDropDownLabel.setVisible(false);
        Label categoriesDropDownLabel = new Label("Categories");categoriesDropDownLabel.setLayoutX(addressesDropDownLabel.getLayoutX());categoriesDropDownLabel.setLayoutY(emailsDropDownLabel.getLayoutY() + 60);categoriesDropDownLabel.setFont(Font.font("Times New Roman", FontWeight.BOLD, 14));categoriesDropDownLabel.setVisible(false);
        Label commentTitle = new Label("Comment");commentTitle.setLayoutX(categoriesDropDownLabel.getLayoutX());commentTitle.setLayoutY(categoriesDropDownLabel.getLayoutY() + 60);commentTitle.setFont(Font.font("Times New Roman", FontWeight.BOLD, 14));commentTitle.setUnderline(true);commentTitle.setVisible(false);

        //Text
        Text contactFirstNameTitle = new Text("First:");contactFirstNameTitle.setX(editContactsLabel.getLayoutX());contactFirstNameTitle.setY(contactNameTitle.getLayoutY() + 30);contactFirstNameTitle.setFont(Font.font("Times New Roman", FontWeight.BOLD, 12));contactFirstNameTitle.setVisible(false);
        Text contactFirstName = new Text();contactFirstName.setX(editContactsLabel.getLayoutX() + 100);contactFirstName.setY(contactFirstNameTitle.getY());contactFirstName.setVisible(false);
        Text contactMiddleNameTitle = new Text("Middle:");contactMiddleNameTitle.setX(editContactsLabel.getLayoutX());contactMiddleNameTitle.setY(contactFirstNameTitle.getY() + 20);contactMiddleNameTitle.setFont(Font.font("Times New Roman", FontWeight.BOLD, 12));contactMiddleNameTitle.setVisible(false);
        Text contactMiddleName = new Text();contactMiddleName.setX(contactFirstName.getX());contactMiddleName.setY(contactMiddleNameTitle.getY());contactMiddleName.setVisible(false);
        Text contactLastNameTitle = new Text("Last:");contactLastNameTitle.setX(contactFirstNameTitle.getX());contactLastNameTitle.setY(contactMiddleNameTitle.getY() + 20);contactLastNameTitle.setFont(Font.font("Times New Roman", FontWeight.BOLD, 12));contactLastNameTitle.setVisible(false);
        Text contactLastName = new Text();contactLastName.setX(contactFirstName.getX());contactLastName.setY(contactLastNameTitle.getY());contactLastName.setVisible(false);
        Text contactDoBTitle = new Text("DoB:");contactDoBTitle.setX(contactFirstNameTitle.getX());contactDoBTitle.setY(contactLastNameTitle.getY() + 40);contactDoBTitle.setFont(Font.font("Times New Roman", FontWeight.BOLD, 12));contactDoBTitle.setVisible(false);
        Text contactDoB = new Text();contactDoB.setX(contactFirstName.getX());contactDoB.setY(contactDoBTitle.getY());contactDoB.setVisible(false);
        Text contactHeightTitle = new Text("Height:");contactHeightTitle.setX(miscDetailsTitle.getLayoutX());contactHeightTitle.setY(miscDetailsTitle.getLayoutY() + 30);contactHeightTitle.setFont(Font.font("Times New Roman", FontWeight.BOLD, 12));contactHeightTitle.setVisible(false);
        Text contactHeight = new Text();contactHeight.setX(contactFirstName.getX());contactHeight.setY(contactHeightTitle.getY());contactHeight.setVisible(false);
        Text contactWeightTitle = new Text("Weight:");contactWeightTitle.setX(contactFirstNameTitle.getX());contactWeightTitle.setY(contactHeightTitle.getY() + 20);contactWeightTitle.setFont(Font.font("Times New Roman", FontWeight.BOLD, 12));contactWeightTitle.setVisible(false);
        Text contactWeight = new Text();contactWeight.setX(contactFirstName.getX());contactWeight.setY(contactWeightTitle.getY());contactWeight.setVisible(false);
        Text contactHairColorTitle = new Text("Hair Color:");contactHairColorTitle.setX(contactFirstNameTitle.getX());contactHairColorTitle.setY(contactWeightTitle.getY() + 20);contactHairColorTitle.setFont(Font.font("Times New Roman", FontWeight.BOLD, 12));contactHairColorTitle.setVisible(false);
        Text contactHairColor = new Text();contactHairColor.setX(contactFirstName.getX());contactHairColor.setY(contactHairColorTitle.getY());contactHairColor.setVisible(false);
        Text contactEyeColorTitle = new Text("Eye Color:");contactEyeColorTitle.setX(contactFirstNameTitle.getX());contactEyeColorTitle.setY(contactHairColorTitle.getY() + 20);contactEyeColorTitle.setFont(Font.font("Times New Roman", FontWeight.BOLD, 12));contactEyeColorTitle.setVisible(false);
        Text contactEyeColor = new Text();contactEyeColor.setX(contactFirstName.getX());contactEyeColor.setY(contactEyeColorTitle.getY());contactEyeColor.setVisible(false);
        Text contactSexMale = new Text();contactSexMale.setX(contactFirstName.getX());contactSexMale.setY(contactSexTitle.getLayoutY() + 10);contactSexMale.setVisible(false);
        Text contactSexFemale = new Text();contactSexFemale.setX(contactSexMale.getX() + 20);contactSexFemale.setY(contactSexMale.getY());contactSexFemale.setVisible(false);
        Text contactComment = new Text();contactComment.setX(commentTitle.getLayoutX());contactComment.setY(commentTitle.getLayoutY() + 30);contactComment.setVisible(false);

        //TextFields
        TextField searchFirstNameTxtField = new TextField();searchFirstNameTxtField.setLayoutX(editContactsLabel.getLayoutX());searchFirstNameTxtField.setLayoutY(searchPrefSubTitle.getLayoutY() + 60);searchFirstNameTxtField.setPromptText("First Name");
        TextField searchLastNameTxtField = new TextField();searchLastNameTxtField.setLayoutX(searchFirstNameTxtField.getLayoutX());searchLastNameTxtField.setLayoutY(searchFirstNameTxtField.getLayoutY() + 40);searchLastNameTxtField.setPromptText("Last Name");
        TextField searchDoBTxtField = new TextField();searchDoBTxtField.setLayoutX(searchFirstNameTxtField.getLayoutX());searchDoBTxtField.setLayoutY(searchLastNameTxtField.getLayoutY() + 40);searchDoBTxtField.setPromptText("DoB (MM/dd/YYYY)");
        TextField searchPhoneNumTxtField = new TextField();searchPhoneNumTxtField.setLayoutX(searchFirstNameTxtField.getLayoutX());searchPhoneNumTxtField.setLayoutY(searchDoBTxtField.getLayoutY() + 40);searchPhoneNumTxtField.setPromptText("Phone Number");

        TextField changeFirstNameTxtField = new TextField();changeFirstNameTxtField.setLayoutX(contactFirstName.getX());changeFirstNameTxtField.setLayoutY(contactFirstName.getY() - 10);changeFirstNameTxtField.setMaxHeight(18);changeFirstNameTxtField.setMinHeight(18);changeFirstNameTxtField.setVisible(false);
        TextField changeMiddleNameTxtField = new TextField();changeMiddleNameTxtField.setLayoutX(contactMiddleName.getX());changeMiddleNameTxtField.setLayoutY(contactMiddleName.getY() - 10);changeMiddleNameTxtField.setMaxHeight(18);changeMiddleNameTxtField.setMinHeight(18);changeMiddleNameTxtField.setVisible(false);
        TextField changeLastNameTxtField = new TextField();changeLastNameTxtField.setLayoutX(contactLastName.getX());changeLastNameTxtField.setLayoutY(contactLastName.getY() - 10);changeLastNameTxtField.setMaxHeight(18);changeLastNameTxtField.setMinHeight(18);changeLastNameTxtField.setVisible(false);
        TextField changeDoBTxtField = new TextField();changeDoBTxtField.setLayoutX(contactDoB.getX());changeDoBTxtField.setLayoutY(contactDoB.getY() - 10);changeDoBTxtField.setMaxHeight(18);changeDoBTxtField.setMinHeight(18);changeDoBTxtField.setVisible(false);
        TextField changeHeightTxtField = new TextField();changeHeightTxtField.setLayoutX(contactHeight.getX());changeHeightTxtField.setLayoutY(contactHeight.getY() - 10);changeHeightTxtField.setMinHeight(18);changeHeightTxtField.setMaxHeight(18);changeHeightTxtField.setVisible(false);
        TextField changeWeightTxtField = new TextField();changeWeightTxtField.setLayoutX(contactWeight.getX());changeWeightTxtField.setLayoutY(contactWeight.getY() - 10);changeWeightTxtField.setMinHeight(18);changeWeightTxtField.setMaxHeight(18);changeWeightTxtField.setVisible(false);
        TextField changeHairColorTxtField = new TextField();changeHairColorTxtField.setLayoutX(contactHairColor.getX());changeHairColorTxtField.setLayoutY(contactHairColor.getY() - 10);changeHairColorTxtField.setMaxHeight(18);changeHairColorTxtField.setMinHeight(18);changeHairColorTxtField.setVisible(false);
        TextField changeEyeColorTxtField = new TextField();changeEyeColorTxtField.setLayoutX(contactEyeColor.getX());changeEyeColorTxtField.setLayoutY(contactEyeColor.getY() - 10);changeEyeColorTxtField.setMinHeight(18);changeEyeColorTxtField.setMaxHeight(18);changeEyeColorTxtField.setVisible(false);

        //TextAreas
        TextArea commentField = new TextArea();commentField.setLayoutX(contactComment.getX());commentField.setLayoutY(contactComment.getY());commentField.setVisible(false);

        //Hyperlinks
        Hyperlink changeContactFirstName = new Hyperlink("change");changeContactFirstName.setLayoutX(contactFirstNameTitle.getX() + 250);changeContactFirstName.setLayoutY(contactFirstNameTitle.getY() - 15);changeContactFirstName.setVisible(false);
        Hyperlink changeContactMiddleName = new Hyperlink("change");changeContactMiddleName.setLayoutX(changeContactFirstName.getLayoutX());changeContactMiddleName.setLayoutY(contactMiddleNameTitle.getY() - 15);changeContactMiddleName.setVisible(false);
        Hyperlink changeContactLastName = new Hyperlink("change");changeContactLastName.setLayoutX(changeContactFirstName.getLayoutX());changeContactLastName.setLayoutY(contactLastNameTitle.getY() - 15);changeContactLastName.setVisible(false);
        Hyperlink changeContactDoB = new Hyperlink("change");changeContactDoB.setLayoutX(changeContactFirstName.getLayoutX());changeContactDoB.setLayoutY(contactDoBTitle.getY() - 15);changeContactDoB.setVisible(false);
        Hyperlink changeContactHeight = new Hyperlink("change");changeContactHeight.setLayoutX(changeContactFirstName.getLayoutX());changeContactHeight.setLayoutY(contactHeightTitle.getY() - 15);changeContactHeight.setVisible(false);
        Hyperlink changeContactWeight = new Hyperlink("change");changeContactWeight.setLayoutX(changeContactFirstName.getLayoutX());changeContactWeight.setLayoutY(contactWeightTitle.getY() - 15);changeContactWeight.setVisible(false);
        Hyperlink changeContactHairColor = new Hyperlink("change");changeContactHairColor.setLayoutX(changeContactFirstName.getLayoutX());changeContactHairColor.setLayoutY(contactHairColorTitle.getY() - 15);changeContactHairColor.setVisible(false);
        Hyperlink changeContactEyeColor = new Hyperlink("change");changeContactEyeColor.setLayoutX(changeContactFirstName.getLayoutX());changeContactEyeColor.setLayoutY(contactEyeColorTitle.getY() - 15);changeContactEyeColor.setVisible(false);
        Hyperlink changeComment = new Hyperlink("change");changeComment.setLayoutX(changeContactFirstName.getLayoutX());changeComment.setLayoutY(commentTitle.getLayoutY() - 3);changeComment.setVisible(false);

        //Radio Buttons
        ToggleGroup contactSex = new ToggleGroup();
        RadioButton maleRB = new RadioButton("Male");maleRB.setLayoutX(contactSexTitle.getLayoutX());maleRB.setLayoutY(contactSexTitle.getLayoutY() + 20);maleRB.setVisible(false);
        RadioButton femaleRB = new RadioButton("Female");femaleRB.setLayoutX(maleRB.getLayoutX());femaleRB.setLayoutY(maleRB.getLayoutY() + 20);femaleRB.setVisible(false);

        maleRB.setToggleGroup(contactSex);
        femaleRB.setToggleGroup(contactSex);

        //ListView
        ListView<Person> searchContactsListView = new ListView<>();searchContactsListView.setLayoutX(searchFirstNameTxtField.getLayoutX() + 300);searchContactsListView.setLayoutY(searchFirstNameTxtField.getLayoutY());
        ListView<String> contactCategories = new ListView<>();contactCategories.setLayoutX(categoriesDropDownLabel.getLayoutX());contactCategories.setLayoutY(categoriesDropDownLabel.getLayoutY() + 40);contactCategories.setPrefSize(200, 250);contactCategories.setVisible(false);

        //Line objects
        Line addressesUnderline = new Line(addressesDropDownLabel.getLayoutX(), addressesDropDownLabel.getLayoutY() + 20, addressesDropDownLabel.getLayoutX() + 150, addressesDropDownLabel.getLayoutY() + 20);addressesUnderline.setVisible(false);
        Line phoneNumbersUnderline = new Line(phoneNumberDropDownLabel.getLayoutX(), phoneNumberDropDownLabel.getLayoutY() + 20, phoneNumberDropDownLabel.getLayoutX() + 150, phoneNumberDropDownLabel.getLayoutY() + 20);phoneNumbersUnderline.setVisible(false);
        Line emailsUnderline = new Line(emailsDropDownLabel.getLayoutX(), emailsDropDownLabel.getLayoutY() + 20, emailsDropDownLabel.getLayoutX() + 150, emailsDropDownLabel.getLayoutY() + 20);emailsUnderline.setVisible(false);
        Line categoriesUnderline = new Line(categoriesDropDownLabel.getLayoutX(), categoriesDropDownLabel.getLayoutY() + 20, categoriesDropDownLabel.getLayoutX() + 150, categoriesDropDownLabel.getLayoutY() + 20);categoriesUnderline.setVisible(false);

        //Buttons
        Button searchContactsButton = new Button("Search");searchContactsButton.setLayoutX(editContactsLabel.getLayoutX());searchContactsButton.setLayoutY(searchPhoneNumTxtField.getLayoutY() + 35);
        Button resetSearchButton = new Button("Reset");resetSearchButton.setLayoutX(searchContactsButton.getLayoutX() + 103);resetSearchButton.setLayoutY(searchContactsButton.getLayoutY());
        Button selectContactButton = new Button("Select");selectContactButton.setLayoutX(searchContactsListView.getLayoutX());selectContactButton.setLayoutY(searchContactsListView.getLayoutY() + 410);selectContactButton.setPrefSize(70, 40);selectContactButton.setDisable(true);
        Button clearButton = new Button("Clear");clearButton.setLayoutX(selectContactButton.getLayoutX() + 177);clearButton.setLayoutY(selectContactButton.getLayoutY());clearButton.setPrefSize(70, 40);

        Button expandAddressesButton = new Button("v");expandAddressesButton.setLayoutX(addressesUnderline.getEndX() + 10);expandAddressesButton.setLayoutY(addressesUnderline.getEndY() - 12.5);expandAddressesButton.setShape(new Circle(1.5));expandAddressesButton.setPrefSize(25, 25);expandAddressesButton.setVisible(false);
        Button expandPhoneNumberButton = new Button("v");expandPhoneNumberButton.setLayoutX(phoneNumbersUnderline.getEndX() + 10);expandPhoneNumberButton.setLayoutY(phoneNumbersUnderline.getEndY() - 12.5);expandPhoneNumberButton.setShape(new Circle(1.5));expandPhoneNumberButton.setPrefSize(25,25);expandPhoneNumberButton.setVisible(false);
        Button expandEmailButton = new Button("v");expandEmailButton.setLayoutX(emailsUnderline.getEndX() + 10);expandEmailButton.setLayoutY(emailsUnderline.getEndY() - 12.5);expandEmailButton.setShape(new Circle(1.5));expandEmailButton.setPrefSize(25, 25);expandEmailButton.setVisible(false);
        Button expandCategoryButton = new Button("v");expandCategoryButton.setLayoutX(categoriesUnderline.getEndX() + 10);expandCategoryButton.setLayoutY(categoriesUnderline.getEndY() - 12.5);expandCategoryButton.setShape(new Circle(1.5));expandCategoryButton.setPrefSize(25, 25);expandCategoryButton.setVisible(false);
        Button backToSearchButton = new Button("Back");backToSearchButton.setLayoutX(tempStage.getWidth() - 95);backToSearchButton.setLayoutY(userSettingsLabel.getLayoutY());backToSearchButton.setVisible(false);

        //node functionality
        //buttons
        searchContactsButton.setOnAction(action ->
        {
            if((searchFirstNameTxtField.getText().equals("") && searchLastNameTxtField.getText().equals("") && searchDoBTxtField.getText().equals("") && searchPhoneNumTxtField.getText().equals("")) || (!searchDoBTxtField.getText().matches(dateOfBirthRegex) && searchDoBTxtField.getText().length() > 0) || (!searchPhoneNumTxtField.getText().matches(phoneNumberRegex) && searchPhoneNumTxtField.getText().length() > 0)) //determines if user has parameters to search with
            {
                popUpErrorWindow("You must enter valid information before you search!");
            } else {
                searchContactsListView.getItems().clear();

                //search through all contacts and paste them to searchContactsListView
                for(Person p : this.contacts)
                {
                    if(p.getFirstName().toLowerCase().contains(searchFirstNameTxtField.getText().toLowerCase())) //search first name
                    {
                        searchContactsListView.getItems().add(p);
                    }
                    if(searchLastNameTxtField.getText().toLowerCase().contains(p.getLastName().toLowerCase()))
                    {
                        searchContactsListView.getItems().add(p);
                    }
                    if(searchDoBTxtField.getText().equals(p.getDateOfBirth().toString()))
                    {
                        searchContactsListView.getItems().add(p);
                    }
                    for(int i = 0; i < p.getPhoneNumbers().size(); i++)
                    {
                        if(searchPhoneNumTxtField.getText().contains(p.getPhoneNumber(i).getPhoneNumber()))
                        {
                            searchContactsListView.getItems().add(p);
                            break;
                        }
                    }
                }
            }
        });

        resetSearchButton.setOnAction(action ->
        {
            searchFirstNameTxtField.clear();
            searchLastNameTxtField.clear();
            searchDoBTxtField.clear();
            searchPhoneNumTxtField.clear();
        });

        selectContactButton.setOnAction(action -> //change the screen to display details of an editable contact
        {
            /* GUIDE TO THE selectContactButton
             * 1) Disable the visibility of currently visible nodes
             * 2) Populate the screen with newly visible nodes
             *   2.1) Set visibility to 'true'
             *   2.2) Set appropriate information to nodes
             *   2.3) Establish ArrayLists with appropriate information
             *     2.3.1) Establish info for Addresses
             *     2.3.2) Establish info for phone numbers
             *     2.3.3) Establish info for emails
             *     2.3.4) Establish info for categories
             */
            searchPrefSubTitle.setText(searchContactsListView.getSelectionModel().getSelectedItem().toString());
            contactSP.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED); // get the scroll bar to appear

            //1) hide all nodes on screen
            //TextFields
            searchFirstNameTxtField.setVisible(false);
            searchLastNameTxtField.setVisible(false);
            searchDoBTxtField.setVisible(false);
            searchPhoneNumTxtField.setVisible(false);

            //buttons
            searchContactsButton.setVisible(false);
            resetSearchButton.setVisible(false);
            selectContactButton.setVisible(false);
            clearButton.setVisible(false);

            //ListView
            searchContactsListView.setVisible(false);

            //2.1) show editable contact nodes
            //Labels
            contactSexTitle.setVisible(true);
            commentTitle.setVisible(true);
            contactNameTitle.setVisible(true);
            miscDetailsTitle.setVisible(true);
            addressesDropDownLabel.setVisible(true);
            phoneNumberDropDownLabel.setVisible(true);
            emailsDropDownLabel.setVisible(true);
            categoriesDropDownLabel.setVisible(true);

            //Text
            contactFirstNameTitle.setVisible(true);
            contactFirstName.setVisible(true);
            contactMiddleNameTitle.setVisible(true);
            contactMiddleName.setVisible(true);
            contactLastNameTitle.setVisible(true);
            contactLastName.setVisible(true);
            contactDoBTitle.setVisible(true);
            contactDoB.setVisible(true);
            contactHeightTitle.setVisible(true);
            contactHeight.setVisible(true);
            contactWeightTitle.setVisible(true);
            contactWeight.setVisible(true);
            contactEyeColorTitle.setVisible(true);
            contactEyeColor.setVisible(true);
            contactHairColorTitle.setVisible(true);
            contactHairColor.setVisible(true);
            contactSexMale.setVisible(true);
            contactSexFemale.setVisible(true);
            contactComment.setVisible(true);

            //lines
            addressesUnderline.setVisible(true);
            phoneNumbersUnderline.setVisible(true);
            emailsUnderline.setVisible(true);
            categoriesUnderline.setVisible(true);

            //Hyperlink
            changeContactFirstName.setVisible(true);
            changeContactMiddleName.setVisible(true);
            changeContactLastName.setVisible(true);
            changeContactDoB.setVisible(true);
            changeContactHeight.setVisible(true);
            changeContactWeight.setVisible(true);
            changeContactHairColor.setVisible(true);
            changeContactEyeColor.setVisible(true);
            changeComment.setVisible(true);

            //buttons
            backToSearchButton.setVisible(true);
            expandAddressesButton.setVisible(true);
            expandPhoneNumberButton.setVisible(true);
            expandEmailButton.setVisible(true);
            expandCategoryButton.setVisible(true);

            //2.2) set relevant info for nodes
            contactFirstName.setText(searchContactsListView.getSelectionModel().getSelectedItem().getFirstName());
            contactMiddleName.setText(searchContactsListView.getSelectionModel().getSelectedItem().getMiddleName());
            contactLastName.setText(searchContactsListView.getSelectionModel().getSelectedItem().getLastName());
            contactDoB.setText(searchContactsListView.getSelectionModel().getSelectedItem().getDateOfBirth().format(dateFormatter));
            contactHeight.setText(searchContactsListView.getSelectionModel().getSelectedItem().getHeight());
            contactWeight.setText(searchContactsListView.getSelectionModel().getSelectedItem().getWeight());
            contactEyeColor.setText(searchContactsListView.getSelectionModel().getSelectedItem().getEyeColor());
            contactHairColor.setText(searchContactsListView.getSelectionModel().getSelectedItem().getHairColor());
            contactComment.setText(searchContactsListView.getSelectionModel().getSelectedItem().getComment().replaceAll("<'r'>", "\n"));
            commentField.setText(searchContactsListView.getSelectionModel().getSelectedItem().getComment().replaceAll("<'r'>", "\n"));
            changeFirstNameTxtField.setText(searchContactsListView.getSelectionModel().getSelectedItem().getFirstName());
            changeMiddleNameTxtField.setText(searchContactsListView.getSelectionModel().getSelectedItem().getMiddleName());
            changeLastNameTxtField.setText(searchContactsListView.getSelectionModel().getSelectedItem().getLastName());
            changeDoBTxtField.setText(searchContactsListView.getSelectionModel().getSelectedItem().getDateOfBirth().format(dateFormatter));
            changeHeightTxtField.setText(searchContactsListView.getSelectionModel().getSelectedItem().getHeight());
            changeWeightTxtField.setText(searchContactsListView.getSelectionModel().getSelectedItem().getWeight());
            changeHairColorTxtField.setText(searchContactsListView.getSelectionModel().getSelectedItem().getHairColor());
            changeEyeColorTxtField.setText(searchContactsListView.getSelectionModel().getSelectedItem().getEyeColor());

            if(searchContactsListView.getSelectionModel().getSelectedItem().getSex().equals("Male"))
            {
                maleRB.setSelected(true);
            } else {
                femaleRB.setSelected(true);
            }
            maleRB.setVisible(true);
            femaleRB.setVisible(true);

            //2.3) Set info for lists
            //2.3.1) Set info for Addresses
            for(int i = 0; i < searchContactsListView.getSelectionModel().getSelectedItem().getAddresses().size(); i++) //set info for addresses
            {
                //Text Objects
                Text newAddressText = new Text(searchContactsListView.getSelectionModel().getSelectedItem().getAddresses().get(i).getStreetAddress());newAddressText.setX(addressesDropDownLabel.getLayoutX());newAddressText.setY((addressesDropDownLabel.getLayoutY() + 30) + (35 * (i + 1)));newAddressText.setVisible(false);
                Text newCityText = new Text(searchContactsListView.getSelectionModel().getSelectedItem().getAddresses().get(i).getCity());newCityText.setX(newAddressText.getX() + 200);newCityText.setY(newAddressText.getY());newCityText.setVisible(false);
                Text newStateText = new Text(searchContactsListView.getSelectionModel().getSelectedItem().getAddresses().get(i).getState());newStateText.setX(newCityText.getX() + 120);newStateText.setY(newAddressText.getY());newStateText.setVisible(false);
                Text newZipcodeText = new Text(searchContactsListView.getSelectionModel().getSelectedItem().getAddresses().get(i).getZipcode());newZipcodeText.setX(newStateText.getX() + 40);newZipcodeText.setY(newAddressText.getY());newZipcodeText.setVisible(false);
                Text newAddressTypeText = new Text(searchContactsListView.getSelectionModel().getSelectedItem().getAddresses().get(i).getType());newAddressTypeText.setX(newZipcodeText.getX() + 50);newAddressTypeText.setY(newAddressText.getY());newAddressTypeText.setVisible(false);

                Text newEmailText = new Text(searchContactsListView.getSelectionModel().getSelectedItem().getEmail(i).getEmailAddress());newEmailText.setX(emailsDropDownLabel.getLayoutX());newEmailText.setY((emailsDropDownLabel.getLayoutY() + 30) + (35 * (i + 1)));newEmailText.setVisible(false);
                Text newEmailTypeText = new Text(searchContactsListView.getSelectionModel().getSelectedItem().getEmail(i).getType());newEmailTypeText.setX(newEmailText.getX() + 80);newEmailTypeText.setY(newEmailText.getY());newEmailTypeText.setVisible(false);

                //Text Fields & Combo Boxes
                TextField newAddressTxtField = new TextField(searchContactsListView.getSelectionModel().getSelectedItem().getAddresses().get(i).getStreetAddress());newAddressTxtField.setLayoutX(addressesDropDownLabel.getLayoutX());newAddressTxtField.setLayoutY((addressesDropDownLabel.getLayoutY() + 10) + (35 * (i + 1)));newAddressTxtField.setVisible(false);
                TextField newCityTxtField = new TextField(searchContactsListView.getSelectionModel().getSelectedItem().getAddresses().get(i).getCity());newCityTxtField.setLayoutX(newAddressTxtField.getLayoutX() + 160);newCityTxtField.setLayoutY(newAddressTxtField.getLayoutY());newCityTxtField.setPrefWidth(70);newCityTxtField.setVisible(false);
                ComboBox<String> newStateComboBox = new ComboBox<>(FXCollections.observableArrayList(stateAbb));newStateComboBox.setLayoutX(newCityTxtField.getLayoutX() + 80);newStateComboBox.setLayoutY(newAddressTxtField.getLayoutY());newStateComboBox.getSelectionModel().select(searchContactsListView.getSelectionModel().getSelectedItem().getAddresses().get(i).getState());newStateComboBox.setPrefWidth(65);newStateComboBox.setVisible(false);
                TextField newZipcode = new TextField(searchContactsListView.getSelectionModel().getSelectedItem().getAddresses().get(i).getZipcode());newZipcode.setLayoutX(newStateComboBox.getLayoutX() + 80);newZipcode.setLayoutY(newAddressTxtField.getLayoutY());newZipcode.setPrefWidth(70);newZipcode.setVisible(false);
                ComboBox<String> newAddressTypeComboBox = new ComboBox<>(FXCollections.observableArrayList(addressTypes));newAddressTypeComboBox.setLayoutX(newZipcode.getLayoutX() + 80);newAddressTypeComboBox.setLayoutY(newAddressTxtField.getLayoutY());newAddressTypeComboBox.getSelectionModel().select(searchContactsListView.getSelectionModel().getSelectedItem().getAddresses().get(i).getType());newAddressTypeComboBox.setVisible(false);

               //Hyperlinks
                Hyperlink newChangeAddress = new Hyperlink("change");newChangeAddress.setLayoutX(newAddressTypeComboBox.getLayoutX() + 80);newChangeAddress.setLayoutY(newAddressTxtField.getLayoutY());newChangeAddress.setVisible(false);

                newChangeAddress.setOnAction(perform ->
                {
                    if(!newAddressTxtField.isVisible())
                    {
                        newChangeAddress.setText("cancel change");
                        //newAddressTxtField.setText(searchContactsListView.getSelectionModel().getSelectedItem().getAddresses().get(i).getStreetAddress());

                        //text objects
                        newAddressText.setVisible(false);
                        newCityText.setVisible(false);
                        newStateText.setVisible(false);
                        newZipcodeText.setVisible(false);

                        //textfields
                        newAddressTxtField.setVisible(true);
                        newCityTxtField.setVisible(true);
                        newStateComboBox.setVisible(true);
                        newZipcode.setVisible(true);
                        newAddressTypeComboBox.setVisible(true);

                    } else {
                        newChangeAddress.setText("change");

                        //text objects
                        newAddressText.setVisible(true);
                        newCityText.setVisible(true);
                        newStateText.setVisible(true);
                        newZipcodeText.setVisible(true);

                        //textfields
                        newAddressTxtField.setVisible(false);
                        newCityTxtField.setVisible(false);
                        newStateComboBox.setVisible(false);
                        newZipcode.setVisible(false);
                        newAddressTypeComboBox.setVisible(false);
                    }
                });

                //Texts
                streetAddressesTextObj.add(newAddressText);
                cityTextObj.add(newCityText);
                stateTextObj.add(newStateText);
                zipcodeTextObj.add(newZipcodeText);

                //Textfields
                streetAddressesTxtField.add(newAddressTxtField);
                citiesTxtField.add(newCityTxtField);
                statesComboBox.add(newStateComboBox);
                zipcodesTxtField.add(newZipcode);
                addressTypeComboBoxes.add(newAddressTypeComboBox);
                changeAddresses.add(newChangeAddress);
            }

            for(int i = 0; i < streetAddressesTxtField.size(); i++)
            {
                contactSettingsPane.getChildren().addAll(streetAddressesTextObj.get(i), streetAddressesTxtField.get(i), cityTextObj.get(i), citiesTxtField.get(i), stateTextObj.get(i), statesComboBox.get(i), zipcodeTextObj.get(i), zipcodesTxtField.get(i), addressTypeComboBoxes.get(i), changeAddresses.get(i));
            }

            //2.3.2) Establish info for phone numbers
            for(int i = 0; i < searchContactsListView.getSelectionModel().getSelectedItem().getPhoneNumbers().size(); i++)
            {
                //Text objects
                Text newPhoneNumberText = new Text(searchContactsListView.getSelectionModel().getSelectedItem().getPhoneNumber(i).getPhoneNumber());newPhoneNumberText.setX(addressesDropDownLabel.getLayoutX());newPhoneNumberText.setY((phoneNumberDropDownLabel.getLayoutY() + 30) + (35 * (i + 1)));newPhoneNumberText.setVisible(false);
                Text newPhoneNumberTypeText = new Text(searchContactsListView.getSelectionModel().getSelectedItem().getPhoneNumber(i).getType());newPhoneNumberTypeText.setX(newPhoneNumberText.getX() + 160);newPhoneNumberTypeText.setY(newPhoneNumberText.getY());newPhoneNumberTypeText.setVisible(false);

                //Textfields & Comboboxes
                TextField newPhoneNumberTxtField = new TextField(searchContactsListView.getSelectionModel().getSelectedItem().getPhoneNumber(i).getPhoneNumber());newPhoneNumberTxtField.setLayoutX(newPhoneNumberText.getX());newPhoneNumberTxtField.setLayoutY(newPhoneNumberText.getY() - 20);newPhoneNumberTxtField.setVisible(false);
                ComboBox<String> newPhoneNumberTypeComboBox = new ComboBox<>(FXCollections.observableArrayList(phoneNumberTypes));newPhoneNumberTypeComboBox.setLayoutX(newPhoneNumberTypeText.getX());newPhoneNumberTypeComboBox.setLayoutY(newPhoneNumberTypeText.getY() - 20);newPhoneNumberTypeComboBox.getSelectionModel().select(searchContactsListView.getSelectionModel().getSelectedItem().getPhoneNumber(i).getType());newPhoneNumberTypeComboBox.setVisible(false);

                //Hyperlinks
                Hyperlink newChangePhoneNumber = new Hyperlink("change");newChangePhoneNumber.setLayoutX(490);newChangePhoneNumber.setLayoutY(newPhoneNumberText.getY());newChangePhoneNumber.setVisible(false);

                //fucntions
                newChangePhoneNumber.setOnAction(perform ->
                {
                    if(!newPhoneNumberTxtField.isVisible())
                    {
                        newChangePhoneNumber.setText("cancel change");

                        //text objects
                        newPhoneNumberText.setVisible(false);
                        newPhoneNumberTypeText.setVisible(false);

                        //textfield & combobox objects
                        newPhoneNumberTxtField.setVisible(true);
                        newPhoneNumberTypeComboBox.setVisible(true);
                    } else {
                        newChangePhoneNumber.setText("change");

                        //text objects
                        newPhoneNumberText.setVisible(true);
                        newPhoneNumberTypeText.setVisible(true);

                        //textfield & combobox objects
                        newPhoneNumberTxtField.setVisible(false);
                        newPhoneNumberTypeComboBox.setVisible(false);
                    }
                });

                //add objects to their respective arrays
                phoneNumberTxtObj.add(newPhoneNumberText);
                phoneNumberTypeTxtObj.add(newPhoneNumberTypeText);
                phoneNumbersTxtField.add(newPhoneNumberTxtField);
                phoneNumberTypeComboBoxes.add(newPhoneNumberTypeComboBox);
                changePhoneNumbers.add(newChangePhoneNumber);
            }

            for(int i = 0; i < phoneNumberTxtObj.size(); i++)
            {
                contactSettingsPane.getChildren().addAll(phoneNumberTxtObj.get(i), phoneNumbersTxtField.get(i), phoneNumberTypeTxtObj.get(i), phoneNumberTypeComboBoxes.get(i), changePhoneNumbers.get(i));
            }

            //2.3.3) Establish info for emails
            for(int i = 0; i < searchContactsListView.getSelectionModel().getSelectedItem().getEmails().size(); i++)
            {
                //Text objects
                Text newEmailTxt = new Text(searchContactsListView.getSelectionModel().getSelectedItem().getEmail(i).getEmailAddress());newEmailTxt.setX(emailsDropDownLabel.getLayoutX());newEmailTxt.setY((emailsDropDownLabel.getLayoutY() + 30) + (35 * (i + 1)));newEmailTxt.setVisible(false);
                Text newEmailTypeTxt = new Text(searchContactsListView.getSelectionModel().getSelectedItem().getEmail(i).getType());newEmailTypeTxt.setX(newEmailTxt.getX() + 160);newEmailTypeTxt.setY(newEmailTxt.getY());newEmailTypeTxt.setVisible(false);

                //Textfields, comboboxes, & hyperlinks
                TextField newEmailTxtField = new TextField(searchContactsListView.getSelectionModel().getSelectedItem().getEmail(i).getEmailAddress());newEmailTxtField.setLayoutX(newEmailTxt.getX());newEmailTxtField.setLayoutY(newEmailTxt.getY() - 20);newEmailTxtField.setVisible(false);
                ComboBox<String> newEmailType = new ComboBox<>(FXCollections.observableArrayList(emailTypes));newEmailType.setLayoutX(newEmailTypeTxt.getX());newEmailType.setLayoutY(newEmailTypeTxt.getY()-20);newEmailType.getSelectionModel().select(searchContactsListView.getSelectionModel().getSelectedItem().getEmail(i).getType());newEmailType.setVisible(false);
                Hyperlink newChangeEmail = new Hyperlink("change");newChangeEmail.setLayoutX(490);newChangeEmail.setLayoutY(newEmailTxt.getY());newChangeEmail.setVisible(false);

                //node functionality
                newChangeEmail.setOnAction(perform ->
                {
                    if(!newEmailTxtField.isVisible())
                    {
                        newChangeEmail.setText("cancel change");

                        //text objects
                        newEmailTxt.setVisible(false);
                        newEmailTypeTxt.setVisible(false);

                        //Textfield & combobox
                        newEmailTxtField.setVisible(true);
                        newEmailType.setVisible(true);
                    } else {
                        newChangeEmail.setText("change");

                        //text objects
                        newEmailTxt.setVisible(true);
                        newEmailTypeTxt.setVisible(true);

                        //Textfield & combobox
                        newEmailTxtField.setVisible(false);
                        newEmailType.setVisible(false);
                    }
                });

                //add objects to their respective arrays
                emailTxtObj.add(newEmailTxt);
                emailTypeTextObj.add(newEmailTypeTxt);
                emailsTxtField.add(newEmailTxtField);
                emailTypeComboBoxes.add(newEmailType);
                changeEmails.add(newChangeEmail);
            }

            for(int i = 0; i < emailTxtObj.size(); i++)
            {
                contactSettingsPane.getChildren().addAll(changeEmails.get(i), emailTxtObj.get(i), emailsTxtField.get(i), emailTypeTextObj.get(i), emailTypeComboBoxes.get(i));
            }
        });

        backToSearchButton.setOnAction(action ->
        {
            searchPrefSubTitle.setText("Search");
            /*Guide to the backToSearchButton
            * ---------------------------------
            * 1) Make old nodes dissapear
            * 2) Make new nodes appear
            * 3) Get rid of old node lists*/

            //1) Disable visibility to currently visible nodes
            searchFirstNameTxtField.setVisible(true);
            searchLastNameTxtField.setVisible(true);
            searchDoBTxtField.setVisible(true);
            searchPhoneNumTxtField.setVisible(true);

            //buttons
            searchContactsButton.setVisible(true);
            resetSearchButton.setVisible(true);
            selectContactButton.setVisible(true);
            clearButton.setVisible(true);

            //ListView
            searchContactsListView.setVisible(true);
            
            //3) clear info from lists
            //Texts
            for(int i = 0; i < streetAddressesTxtField.size(); i++)
            {
                contactSettingsPane.getChildren().removeAll(streetAddressesTextObj.get(i), streetAddressesTxtField.get(i), cityTextObj.get(i), citiesTxtField.get(i), stateTextObj.get(i), statesComboBox.get(i), zipcodeTextObj.get(i), zipcodesTxtField.get(i), addressTypeComboBoxes.get(i), changeAddresses.get(i));
            }
            for(int i = 0; i < phoneNumberTxtObj.size(); i++)
            {
                contactSettingsPane.getChildren().removeAll(phoneNumberTxtObj.get(i), phoneNumbersTxtField.get(i), phoneNumberTypeTxtObj.get(i), phoneNumberTypeComboBoxes.get(i), changePhoneNumbers.get(i));
            }
            for(int i = 0; i < emailTxtObj.size(); i++)
            {
                contactSettingsPane.getChildren().removeAll(changeEmails.get(i), emailTxtObj.get(i), emailsTxtField.get(i), emailTypeTextObj.get(i), emailTypeComboBoxes.get(i));
            }

            //change text on changed hyperlinks
            if(expandPhoneNumberButton.getText().equals("^")) { expandPhoneNumberButton.fire(); }
            if(expandEmailButton.getText().equals("^")) { expandEmailButton.fire(); }
            if(expandAddressesButton.getText().equals("^")) { expandAddressesButton.fire(); }
            if(expandCategoryButton.getText().equals("^")) { expandCategoryButton.fire(); }
            if(changeContactFirstName.getText().equals("cancel change")) { changeContactFirstName.fire(); }
            if(changeContactMiddleName.getText().equals("cancel change")) { changeContactMiddleName.fire(); }
            if(changeContactLastName.getText().equals("cancel change")) { changeContactLastName.fire(); }
            if(changeContactDoB.getText().equals("cancel change")) { changeContactDoB.fire(); }
            if(changeContactHeight.getText().equals("cancel change")) { changeContactHeight.fire(); }
            if(changeContactWeight.getText().equals("cancel change")) { changeContactWeight.fire(); }
            if(changeContactEyeColor.getText().equals("cancel change")) { changeContactEyeColor.fire(); }
            if(changeContactHairColor.getText().equals("cancel change")) { changeContactHairColor.fire(); }
            if(changeComment.getText().equals("cancel change")) { changeComment.fire(); }

            //Clear all previous data from screen
            streetAddressesTextObj.clear();
            cityTextObj.clear();
            stateTextObj.clear();
            zipcodeTextObj.clear();
            phoneNumberTxtObj.clear();
            phoneNumberTypeTxtObj.clear();
            phoneNumbersTxtField.clear();
            emailTxtObj.clear();
            emailTypeTextObj.clear();
            emailsTxtField.clear();

            //Textfields
            streetAddressesTxtField.clear();
            citiesTxtField.clear();
            statesComboBox.clear();
            zipcodesTxtField.clear();

            //comboboxes and hyperlinks
            phoneNumberTypeComboBoxes.clear();
            changePhoneNumbers.clear();
            emailTypeComboBoxes.clear();
            changeEmails.clear();
            addressTypeComboBoxes.clear();
            changeAddresses.clear();

            //1.1) Clear old node values
            searchFirstNameTxtField.setText("");
            searchLastNameTxtField.setText("");
            searchDoBTxtField.setText("");
            searchPhoneNumTxtField.setText("");

            //2) Make new nodes visible
            //Labels
            contactSexTitle.setVisible(false);
            commentTitle.setVisible(false);
            contactNameTitle.setVisible(false);
            miscDetailsTitle.setVisible(false);
            addressesDropDownLabel.setVisible(false);
            phoneNumberDropDownLabel.setVisible(false);
            emailsDropDownLabel.setVisible(false);
            categoriesDropDownLabel.setVisible(false);

            //Text
            contactFirstNameTitle.setVisible(false);
            contactFirstName.setVisible(false);
            contactMiddleNameTitle.setVisible(false);
            contactMiddleName.setVisible(false);
            contactLastNameTitle.setVisible(false);
            contactLastName.setVisible(false);
            contactDoBTitle.setVisible(false);
            contactDoB.setVisible(false);
            contactHeightTitle.setVisible(false);
            contactHeight.setVisible(false);
            contactWeightTitle.setVisible(false);
            contactWeight.setVisible(false);
            contactEyeColorTitle.setVisible(false);
            contactEyeColor.setVisible(false);
            contactHairColorTitle.setVisible(false);
            contactHairColor.setVisible(false);
            contactSexMale.setVisible(false);
            contactSexFemale.setVisible(false);
            contactComment.setVisible(false);

            //lines
            addressesUnderline.setVisible(false);
            phoneNumbersUnderline.setVisible(false);
            emailsUnderline.setVisible(false);
            categoriesUnderline.setVisible(false);

            //Hyperlink
            changeContactFirstName.setVisible(false);
            changeContactMiddleName.setVisible(false);
            changeContactLastName.setVisible(false);
            changeContactDoB.setVisible(false);
            changeContactHeight.setVisible(false);
            changeContactWeight.setVisible(false);
            changeContactHairColor.setVisible(false);
            changeContactEyeColor.setVisible(false);
            changeComment.setVisible(false);

            //buttons
            backToSearchButton.setVisible(false);
            expandAddressesButton.setVisible(false);
            expandPhoneNumberButton.setVisible(false);
            expandEmailButton.setVisible(false);
            expandCategoryButton.setVisible(false);

            //radio buttons
            maleRB.setVisible(false);
            femaleRB.setVisible(false);

            //listViews & Text areas
            contactCategories.setVisible(false);
            commentField.setVisible(false);

            searchContactsListView.getItems().clear();
            selectContactButton.setDisable(true);
        });

        clearButton.setOnAction(action ->
        {
            selectContactButton.setDisable(true);
            searchContactsListView.getItems().clear();
        });

        expandAddressesButton.setOnAction(action ->
        {
            int yIncrement = searchContactsListView.getSelectionModel().getSelectedItem().getAddresses().size() * 35;

            if(expandAddressesButton.getText().equals("v"))
            {
                expandAddressesButton.setText("^");
                //move everything under this button down accordingly
                phoneNumberDropDownLabel.setLayoutY(phoneNumberDropDownLabel.getLayoutY() + yIncrement);
                phoneNumbersUnderline.setStartY(phoneNumbersUnderline.getStartY() + yIncrement);phoneNumbersUnderline.setEndY(phoneNumbersUnderline.getEndY() + yIncrement);
                expandPhoneNumberButton.setLayoutY(expandPhoneNumberButton.getLayoutY() + yIncrement);
                for(int i = 0; i < phoneNumberTxtObj.size(); i++)
                {
                    phoneNumberTxtObj.get(i).setY(phoneNumberTxtObj.get(i).getY() + yIncrement);
                    phoneNumberTypeTxtObj.get(i).setY(phoneNumberTypeTxtObj.get(i).getY() + yIncrement);
                    phoneNumbersTxtField.get(i).setLayoutY(phoneNumbersTxtField.get(i).getLayoutY() + yIncrement);
                    phoneNumberTypeComboBoxes.get(i).setLayoutY(phoneNumberTypeComboBoxes.get(i).getLayoutY() + yIncrement);
                    changePhoneNumbers.get(i).setLayoutY(changePhoneNumbers.get(i).getLayoutY() + yIncrement);
                }

                emailsDropDownLabel.setLayoutY(emailsDropDownLabel.getLayoutY() + yIncrement);
                emailsUnderline.setStartY(emailsUnderline.getStartY() + yIncrement);emailsUnderline.setEndY(emailsUnderline.getEndY() + yIncrement);
                expandEmailButton.setLayoutY(expandEmailButton.getLayoutY() + yIncrement);
                for(int i = 0; i < emailTxtObj.size(); i++)
                {
                    emailTxtObj.get(i).setY(emailTxtObj.get(i).getY() + yIncrement);
                    emailTypeTextObj.get(i).setY(emailTypeTextObj.get(i).getY() + yIncrement);
                    changeEmails.get(i).setLayoutY(changeEmails.get(i).getLayoutY()+yIncrement);
                    emailsTxtField.get(i).setLayoutY(emailsTxtField.get(i).getLayoutY() + yIncrement);
                    emailTypeComboBoxes.get(i).setLayoutY(emailTypeComboBoxes.get(i).getLayoutY() + yIncrement);
                }

                categoriesDropDownLabel.setLayoutY(categoriesDropDownLabel.getLayoutY() + yIncrement);
                categoriesUnderline.setStartY(categoriesUnderline.getStartY() + yIncrement);categoriesUnderline.setEndY(categoriesUnderline.getEndY() + yIncrement);
                expandCategoryButton.setLayoutY(expandCategoryButton.getLayoutY() + yIncrement);
                contactCategories.setLayoutY(contactCategories.getLayoutY() + yIncrement);

                commentTitle.setLayoutY(commentTitle.getLayoutY() + yIncrement);
                changeComment.setLayoutY(changeComment.getLayoutY() + yIncrement);
                commentField.setLayoutY(commentField.getLayoutY() + yIncrement);
                contactComment.setY(contactComment.getY() + yIncrement);

                for(int i = 0; i < streetAddressesTextObj.size(); i++)
                {
                    streetAddressesTextObj.get(i).setVisible(true);
                    cityTextObj.get(i).setVisible(true);
                    stateTextObj.get(i).setVisible(true);
                    zipcodeTextObj.get(i).setVisible(true);

                    changeAddresses.get(i).setVisible(true);
                    changeAddresses.get(i).setText("change");
                }
            } else {
                expandAddressesButton.setText("v");

                //move everything below this button up accordingly
                phoneNumberDropDownLabel.setLayoutY(phoneNumberDropDownLabel.getLayoutY() - yIncrement);
                phoneNumbersUnderline.setStartY(phoneNumbersUnderline.getStartY() - yIncrement);
                phoneNumbersUnderline.setEndY(phoneNumbersUnderline.getEndY() - yIncrement);
                expandPhoneNumberButton.setLayoutY(expandPhoneNumberButton.getLayoutY() - yIncrement);
                for(int i = 0; i < phoneNumberTxtObj.size(); i++) //loop to shift all phone number objects down yIncrement amount
                {
                    phoneNumberTxtObj.get(i).setY(phoneNumberTxtObj.get(i).getY() - yIncrement);
                    phoneNumberTypeTxtObj.get(i).setY(phoneNumberTypeTxtObj.get(i).getY() - yIncrement);
                    phoneNumbersTxtField.get(i).setLayoutY(phoneNumbersTxtField.get(i).getLayoutY() - yIncrement);
                    phoneNumberTypeComboBoxes.get(i).setLayoutY(phoneNumberTypeComboBoxes.get(i).getLayoutY() - yIncrement);
                    changePhoneNumbers.get(i).setLayoutY(changePhoneNumbers.get(i).getLayoutY() - yIncrement);
                }

                emailsDropDownLabel.setLayoutY(emailsDropDownLabel.getLayoutY() - yIncrement);
                emailsUnderline.setStartY(emailsUnderline.getStartY() - yIncrement);
                emailsUnderline.setEndY(emailsUnderline.getEndY() - yIncrement);
                expandEmailButton.setLayoutY(expandEmailButton.getLayoutY() - yIncrement);

                for (int i = 0; i < emailTxtObj.size(); i++) { //Loop to shift all email objects down by yIncrement amount
                    emailsTxtField.get(i).setLayoutY(emailsTxtField.get(i).getLayoutY() - yIncrement);
                    emailTypeComboBoxes.get(i).setLayoutY(emailTypeComboBoxes.get(i).getLayoutY() - yIncrement);
                    emailTxtObj.get(i).setY(emailTxtObj.get(i).getY()-yIncrement);
                    emailTypeTextObj.get(i).setY(emailTypeTextObj.get(i).getY()-yIncrement);
                    changeEmails.get(i).setLayoutY(changeEmails.get(i).getLayoutY()-yIncrement);
                }

                categoriesDropDownLabel.setLayoutY(categoriesDropDownLabel.getLayoutY() - yIncrement);
                categoriesUnderline.setStartY(categoriesUnderline.getStartY() - yIncrement);
                categoriesUnderline.setEndY(categoriesUnderline.getEndY() - yIncrement);
                expandCategoryButton.setLayoutY(expandCategoryButton.getLayoutY() - yIncrement);
                contactCategories.setLayoutY(contactCategories.getLayoutY() - yIncrement);

                commentTitle.setLayoutY(commentTitle.getLayoutY() - yIncrement);
                changeComment.setLayoutY(changeComment.getLayoutY() - yIncrement);
                commentField.setLayoutY(commentField.getLayoutY() - yIncrement);
                contactComment.setY(contactComment.getY() - yIncrement);

                //hide address nodes
                //display all addresses
                for(int i = 0; i < addressTypeComboBoxes.size(); i++)
                {
                    //Text objects
                    streetAddressesTextObj.get(i).setVisible(false);
                    cityTextObj.get(i).setVisible(false);
                    stateTextObj.get(i).setVisible(false);
                    zipcodeTextObj.get(i).setVisible(false);

                    //Textfield
                    streetAddressesTxtField.get(i).setVisible(false);
                    citiesTxtField.get(i).setVisible(false);
                    statesComboBox.get(i).setVisible(false);
                    zipcodesTxtField.get(i).setVisible(false);
                    addressTypeComboBoxes.get(i).setVisible(false);
                    changeAddresses.get(i).setVisible(false);
                }
            }
        });

        expandPhoneNumberButton.setOnAction(action ->
        {
            int yIncrement = searchContactsListView.getSelectionModel().getSelectedItem().getPhoneNumbers().size() * 40;

            if(expandPhoneNumberButton.getText().equals("v"))
            {
                expandPhoneNumberButton.setText("^");
                emailsDropDownLabel.setLayoutY(emailsDropDownLabel.getLayoutY() + yIncrement);
                emailsUnderline.setStartY(emailsUnderline.getStartY() + yIncrement);emailsUnderline.setEndY(emailsUnderline.getEndY() + yIncrement);
                expandEmailButton.setLayoutY(expandEmailButton.getLayoutY() + yIncrement);
                for(int i = 0; i < emailTxtObj.size(); i++)
                {
                    emailTxtObj.get(i).setY(emailTxtObj.get(i).getY() + yIncrement);
                    emailTypeTextObj.get(i).setY(emailTypeTextObj.get(i).getY() + yIncrement);
                    changeEmails.get(i).setLayoutY(changeEmails.get(i).getLayoutY() + yIncrement);
                    emailsTxtField.get(i).setLayoutY(emailsTxtField.get(i).getLayoutY() + yIncrement);
                    emailTypeComboBoxes.get(i).setLayoutY(emailTypeComboBoxes.get(i).getLayoutY() + yIncrement);
                }

                categoriesDropDownLabel.setLayoutY(categoriesDropDownLabel.getLayoutY() + yIncrement);
                categoriesUnderline.setStartY(categoriesUnderline.getStartY() + yIncrement);categoriesUnderline.setEndY(categoriesUnderline.getEndY() + yIncrement);
                expandCategoryButton.setLayoutY(expandCategoryButton.getLayoutY() + yIncrement);
                contactCategories.setLayoutY(contactCategories.getLayoutY() + yIncrement);

                commentTitle.setLayoutY(commentTitle.getLayoutY() + yIncrement);
                changeComment.setLayoutY(changeComment.getLayoutY() + yIncrement);
                commentField.setLayoutY(commentField.getLayoutY() + yIncrement);
                contactComment.setY(contactComment.getY() + yIncrement);

                for(int i = 0; i < phoneNumberTxtObj.size(); i++)
                {
                    phoneNumberTxtObj.get(i).setVisible(true);
                    phoneNumberTypeTxtObj.get(i).setVisible(true);
                    changePhoneNumbers.get(i).setVisible(true);
                    changePhoneNumbers.get(i).setText("change");
                }
            } else {
                expandPhoneNumberButton.setText("v");

                emailsDropDownLabel.setLayoutY(emailsDropDownLabel.getLayoutY() - yIncrement);
                emailsUnderline.setStartY(emailsUnderline.getStartY() - yIncrement);emailsUnderline.setEndY(emailsUnderline.getEndY() - yIncrement);
                expandEmailButton.setLayoutY(expandEmailButton.getLayoutY() - yIncrement);
                for(int i = 0; i < emailTxtObj.size(); i++)
                {
                    emailTxtObj.get(i).setY(emailTxtObj.get(i).getY() - yIncrement);
                    emailTypeTextObj.get(i).setY(emailTypeTextObj.get(i).getY() - yIncrement);
                    changeEmails.get(i).setLayoutY(changeEmails.get(i).getLayoutY() - yIncrement);
                    emailsTxtField.get(i).setLayoutY(emailsTxtField.get(i).getLayoutY() - yIncrement);
                    emailTypeComboBoxes.get(i).setLayoutY(emailTypeComboBoxes.get(i).getLayoutY() - yIncrement);
                }

                categoriesDropDownLabel.setLayoutY(categoriesDropDownLabel.getLayoutY() - yIncrement);
                categoriesUnderline.setStartY(categoriesUnderline.getStartY() - yIncrement);categoriesUnderline.setEndY(categoriesUnderline.getEndY() - yIncrement);
                expandCategoryButton.setLayoutY(expandCategoryButton.getLayoutY() - yIncrement);
                contactCategories.setLayoutY(contactCategories.getLayoutY() - yIncrement);

                commentTitle.setLayoutY(commentTitle.getLayoutY() - yIncrement);
                changeComment.setLayoutY(changeComment.getLayoutY() - yIncrement);
                commentField.setLayoutY(commentField.getLayoutY() - yIncrement);
                contactComment.setY(contactComment.getY() - yIncrement);

                for(int i = 0; i < phoneNumberTxtObj.size(); i++)
                {
                    phoneNumberTxtObj.get(i).setVisible(false);
                    phoneNumberTypeTxtObj.get(i).setVisible(false);
                    changePhoneNumbers.get(i).setVisible(false);

                    phoneNumbersTxtField.get(i).setVisible(false);
                    phoneNumberTypeComboBoxes.get(i).setVisible(false);
                }
            }
        });

        expandEmailButton.setOnAction(action ->
        {
            int yIncrement = searchContactsListView.getSelectionModel().getSelectedItem().getEmails().size() * 30;

            if(expandEmailButton.getText().equals("v")) //change visibility
            {
                expandEmailButton.setText("^");

                for(int i = 0; i < emailTxtObj.size(); i++)
                {
                    emailTxtObj.get(i).setVisible(true);
                    emailTypeTextObj.get(i).setVisible(true);
                    changeEmails.get(i).setVisible(true);
                }

                categoriesDropDownLabel.setLayoutY(categoriesDropDownLabel.getLayoutY() + yIncrement);
                categoriesUnderline.setStartY(categoriesUnderline.getStartY() + yIncrement);categoriesUnderline.setEndY(categoriesUnderline.getEndY() + yIncrement);
                expandCategoryButton.setLayoutY(expandCategoryButton.getLayoutY() + yIncrement);
                contactCategories.setLayoutY(contactCategories.getLayoutY() + yIncrement);

                commentTitle.setLayoutY(commentTitle.getLayoutY() + yIncrement);
                changeComment.setLayoutY(changeComment.getLayoutY() + yIncrement);
                commentField.setLayoutY(commentField.getLayoutY() + yIncrement);
                contactComment.setY(contactComment.getY() + yIncrement);
            } else {
                expandEmailButton.setText("v");

                for(int i = 0; i < emailTxtObj.size(); i++)
                {
                    emailTxtObj.get(i).setVisible(false);
                    emailTypeTextObj.get(i).setVisible(false);
                    changeEmails.get(i).setVisible(false);
                }

                categoriesDropDownLabel.setLayoutY(categoriesDropDownLabel.getLayoutY() - yIncrement);
                categoriesUnderline.setStartY(categoriesUnderline.getStartY() - yIncrement);categoriesUnderline.setEndY(categoriesUnderline.getEndY() - yIncrement);
                expandCategoryButton.setLayoutY(expandCategoryButton.getLayoutY() - yIncrement);
                contactCategories.setLayoutY(contactCategories.getLayoutY() - yIncrement);

                commentTitle.setLayoutY(commentTitle.getLayoutY() - yIncrement);
                changeComment.setLayoutY(changeComment.getLayoutY() - yIncrement);
                commentField.setLayoutY(commentField.getLayoutY() - yIncrement);
                contactComment.setY(contactComment.getY() - yIncrement);
            }
        });

        expandCategoryButton.setOnAction(action ->
        {
            if(expandCategoryButton.getText().equals("v"))
            {
                expandCategoryButton.setText("^");
                contactCategories.setVisible(true);

                //shift everything down
                commentTitle.setLayoutY(commentTitle.getLayoutY() + 300);
                changeComment.setLayoutY(changeComment.getLayoutY() + 300);
                commentField.setLayoutY(commentField.getLayoutY() + 300);
                contactComment.setY(contactComment.getY() + 300);
            } else {
                expandCategoryButton.setText("v");
                contactCategories.setVisible(false);

                //shift everything up
                commentTitle.setLayoutY(commentTitle.getLayoutY() - 300);
                changeComment.setLayoutY(changeComment.getLayoutY() - 300);
                commentField.setLayoutY(commentField.getLayoutY() - 300);
                contactComment.setY(contactComment.getY() - 300);
            }
        });

        //TextFields
        searchFirstNameTxtField.setOnAction(action ->
        {
            if(searchFirstNameTxtField.getText().length() == 0)
            {
                popUpErrorWindow("You must enter some text before searching!");
            }
            else {
                for(Person p : this.contacts)
                {
                    if(p.getFirstName().toLowerCase().contains(searchFirstNameTxtField.getText().toLowerCase())) //search first name
                    {
                        searchContactsListView.getItems().add(p);
                    }
                }
            }
        });

        searchLastNameTxtField.setOnAction(action ->
        {
            if(searchLastNameTxtField.getText().length() == 0)
            {
                popUpErrorWindow("You must enter some text before searching!");
            }
            else {
                for(Person p : this.contacts)
                {
                    if(p.getLastName().toLowerCase().contains(searchLastNameTxtField.getText().toLowerCase())) //search first name
                    {
                        searchContactsListView.getItems().add(p);
                    }
                }
            }
        });

        searchDoBTxtField.setOnAction(action ->
        {
            if(searchDoBTxtField.getText().length() == 0)
            {
                popUpErrorWindow("You must enter some text before searching!");
            } else {
                for(Person p : this.contacts)
                {
                    if(searchDoBTxtField.getText().equals(p.getDateOfBirth().toString()))
                    {
                        searchContactsListView.getItems().add(p);
                    }
                }
            }
        });

        searchPhoneNumTxtField.setOnAction(action ->
        {
            if(searchPhoneNumTxtField.getText().length() == 0)
            {
                popUpErrorWindow("You must enter some text before searching!");
            } else {
                for(Person p : this.contacts)
                {
                    for(PersonPhoneNumber pn : p.getPhoneNumbers())
                    {
                        if(pn.getPhoneNumber().contains(searchPhoneNumTxtField.getText()));
                        {
                            searchContactsListView.getItems().add(p);
                            break;
                        }
                    }
                }
            }
        });

        //ListView
        searchContactsListView.setOnMouseClicked(new EventHandler<MouseEvent>() { //This allows user to select a person from the recently searched listview
            @Override
            public void handle(MouseEvent mouseEvent) {
                selectContactButton.setDisable(false);

                if(mouseEvent.getClickCount() == 2) //same function as the selectContactButton
                {
                    selectContactButton.fire();
                }
            }
        });

        //Hyperlinks
        changeContactFirstName.setOnAction(action ->
        {
            if(changeContactFirstName.getText().equals("change"))
            {
                changeContactFirstName.setText("cancel change");
                changeFirstNameTxtField.setVisible(true);
                contactFirstName.setVisible(false);
            } else {
                changeContactFirstName.setText("change");
                changeFirstNameTxtField.setVisible(false);
                changeFirstNameTxtField.setText(contactFirstName.getText());
                contactFirstName.setVisible(true);
            }
        });
        changeContactMiddleName.setOnAction(action ->
        {
            if(changeContactMiddleName.getText().equals("change"))
            {
                changeContactMiddleName.setText("cancel change");
                changeMiddleNameTxtField.setVisible(true);
                contactMiddleName.setVisible(false);
            } else {
                changeContactMiddleName.setText("change");
                changeMiddleNameTxtField.setVisible(false);
                contactMiddleName.setVisible(true);
                changeMiddleNameTxtField.setText(contactMiddleName.getText());
            }
        });
        changeContactLastName.setOnAction(action ->
        {
            if(changeContactLastName.getText().equals("change"))
            {
                changeContactLastName.setText("cancel change");
                changeLastNameTxtField.setVisible(true);
                contactLastName.setVisible(false);
            } else {
                changeContactLastName.setText("change");
                changeLastNameTxtField.setVisible(false);
                contactLastName.setVisible(true);
                changeLastNameTxtField.setText(contactLastName.getText());
            }
        });
        changeContactDoB.setOnAction(action ->
        {
            if(changeContactDoB.getText().equals("change"))
            {
                changeContactDoB.setText("cancel change");
                changeDoBTxtField.setVisible(true);
                contactDoB.setVisible(false);
            } else {
                changeContactDoB.setText("change");
                changeDoBTxtField.setVisible(false);
                contactDoB.setVisible(true);
                changeDoBTxtField.setText(contactDoB.getText());
            }
        });
        changeContactHeight.setOnAction(action ->
        {
            if(changeContactHeight.getText().equals("change"))
            {
                changeContactHeight.setText("cancel change");
                changeHeightTxtField.setVisible(true);
                contactHeight.setVisible(false);
            } else {
                changeContactHeight.setText("change");
                changeHeightTxtField.setVisible(false);
                contactHeight.setVisible(true);
                changeContactHeight.setText(contactHeight.getText());
            }
        });
        changeContactWeight.setOnAction(action ->
        {
            if(changeContactWeight.getText().equals("change"))
            {
                changeContactWeight.setText("cancel change");
                changeWeightTxtField.setVisible(true);
                contactWeight.setVisible(false);
            } else {
                changeContactWeight.setText("change");
                changeWeightTxtField.setVisible(false);
                contactWeight.setVisible(true);
                changeWeightTxtField.setText(contactWeight.getText());
            }
        });
        changeContactHairColor.setOnAction(action ->
        {
            if(changeContactHairColor.getText().equals("change"))
            {
                changeContactHairColor.setText("cancel change");
                changeHairColorTxtField.setVisible(true);
                contactHairColor.setVisible(false);
            } else {
                changeContactHairColor.setText("change");
                changeHairColorTxtField.setVisible(false);
                contactHairColor.setVisible(true);
                changeHairColorTxtField.setText(contactHairColor.getText());
            }
        });
        changeContactEyeColor.setOnAction(action ->
        {
            if(changeContactEyeColor.getText().equals("change"))
            {
                changeContactEyeColor.setText("cancel change");
                changeEyeColorTxtField.setVisible(true);
                contactEyeColor.setVisible(false);
            } else {
                changeContactEyeColor.setText("change");
                changeEyeColorTxtField.setVisible(false);
                contactEyeColor.setVisible(true);
                changeEyeColorTxtField.setText(contactEyeColor.getText());
            }
        });
        changeComment.setOnAction(action ->
        {
            if(changeComment.getText().equals("change"))
            {
                changeComment.setText("cancel change");
                commentField.setVisible(true);
                contactComment.setVisible(false);
            } else {
                changeComment.setText("change");
                commentField.setVisible(false);
                contactComment.setVisible(true);
            }

        });

        //add it to the pane
        contactSettingsPane.getChildren().addAll(editContactsLabel, searchPrefSubTitle, commentTitle, contactNameTitle, miscDetailsTitle, contactSexTitle, addressesDropDownLabel, phoneNumberDropDownLabel, emailsDropDownLabel, categoriesDropDownLabel,//Labels
                contactFirstNameTitle, contactFirstName, contactMiddleNameTitle, contactMiddleName, contactLastNameTitle, contactLastName, contactDoBTitle, contactDoB, contactHeightTitle, contactHeight, contactWeightTitle, contactWeight, contactHairColorTitle, contactHairColor, contactEyeColorTitle, contactEyeColor, contactSexMale, contactSexFemale, contactComment, //text
                searchFirstNameTxtField, searchLastNameTxtField, searchDoBTxtField, searchPhoneNumTxtField, changeFirstNameTxtField, changeMiddleNameTxtField, changeLastNameTxtField, changeDoBTxtField, changeHeightTxtField, changeWeightTxtField, changeHairColorTxtField, changeEyeColorTxtField,//TextFields
                commentField, //TextArea
                addressesUnderline, phoneNumbersUnderline, emailsUnderline, categoriesUnderline, //lines
                maleRB, femaleRB, //Radio Buttons
                searchContactsListView, contactCategories, //ListView
                changeContactFirstName, changeContactMiddleName, changeContactLastName, changeContactDoB, changeContactHeight, changeContactWeight, changeContactHairColor, changeContactEyeColor, changeComment, //Hyperlinks
                searchContactsButton, resetSearchButton, selectContactButton, clearButton, expandAddressesButton, expandPhoneNumberButton, expandEmailButton, expandCategoryButton, backToSearchButton); //buttons

        //SEARCH PREFERENCE Nodes
        //Labels
        Label editSearchPreferenceLabel = new Label("Edit Search Preferences");editSearchPreferenceLabel.setLayoutX(tempStage.getWidth() - 590);editSearchPreferenceLabel.setLayoutY(tempStage.getHeight() - 790);editSearchPreferenceLabel.setFont(Font.font("Times New Roman", FontWeight.EXTRA_BOLD, 18));

        searchSettingsPane.getChildren().addAll(editSearchPreferenceLabel);

        //Bottom Pane Nodes
        Button saveButton = new Button("Save");
        saveButton.setPrefHeight(45);
        saveButton.setPrefWidth(500);

        Button cancelButton = new Button("Cancel");
        cancelButton.setPrefHeight(45);
        cancelButton.setPrefWidth(500);

        //Bottom Pane Node Functionality
        saveButton.setOnAction(action ->
        {
            /*  --GUIDE TO THE SAVE BUTTON--
             *1) SAVE CHANGES TO THE USER SETTINGS TAB
             *  1.1) save the desired measurement preference to the string, "measurementPreference"
             *  1.2) concatenate the categories into one string into the string, "concatenatedCategories"
             *  1.3) make checks for the user settings tab to make sure everything is gucci
             *2) SAVE CHANGES TO THE CONTACT SETTINGS TAB
             *
             *3) SAVE CHANGES TO SEARCH PREFERENCES TAB
             *
             *4) WRITE OUT ALL INFORMATION TO RESPECTIVE FILES
             *  4.1) Write out info for the User Settings Tab
             *  4.2) Write out info for the contact settings tab
             *  4.3) Write out info for the search preferences tab
             */

            //Variables
            boolean everythingGucci = true;
            String password = passwordTextField.getText();
            String errorMsg = "The following areas had errors\n-----------------------------------\n\n";

            //1) Save changes to the User Settings tab
            //variables for User Settings tab
            String concatenatedCategories = "";
            String measurementPreference = "";

            //1.1) put the user's new measurement preference choice into a string
            if(metricRB.isSelected())
            {
                measurementPreference = measurementPreference + "T";
            }
            else
            {
                measurementPreference = measurementPreference + "F";
            }

            //1.2)concatenate the categories into one string
            for(int i = 0; i < categoryListView.getItems().size(); i++)
            {
                if(i + 1 == categoryListView.getItems().size())
                {
                    concatenatedCategories = concatenatedCategories + this.currentUser.getCategory(i);
                }
                else
                {
                    concatenatedCategories = concatenatedCategories + this.currentUser.getCategory(i) + ">~>";
                }
            }

            //1.3) make checks for the user settings tab to make sure everything is gucci
            if(firstNameTextField.getText().equals(""))
            {
                errorMsg = errorMsg + "You cannot leave the First Name field blank!\n\n";
                everythingGucci = false;
            }
            if(lastNameTextField.getText().equals(""))
            {
                errorMsg = errorMsg + "You cannot leave the Last Name field blank!\n\n";
                everythingGucci = false;
            }
            if(usernameTextField.getText().equals(""))
            {
                errorMsg = errorMsg + "You cannot leave the Username field blank!\n\n";
                everythingGucci = false;
            }
            if((passwordTextField.getText().equals("") && passwordTextField.isVisible()) || !passwordTextField.getText().equals(confirmPasswordTextField.getText()))
            {
                errorMsg = errorMsg + "Password cannot be blank & must match!\n\n";
                everythingGucci = false;
            }

            //2) Save changes to the Contact Settings tab

            //3) Save changes to the Search Preferences tab

            //4) If everything is gucci, then save it to the file
            if(everythingGucci)
            {
                try
                {
                    //4.1) Save it & write out info for the User Settings Tab
                    this.currentUser.setFirstName(firstNameTextField.getText());
                    this.currentUser.setLastName(lastNameTextField.getText());
                    PrintWriter writer = new PrintWriter("C:\\Creeper\\" + this.currentUser.getUsername() + "\\accountInfo.txt");
                    writer.println(firstNameTextField.getText() + ">~>" + lastNameTextField.getText());
                    writer.println(usernameTextField.getText());
                    if(passwordTextField.getText().length() == 0)
                    {
                        writer.println(this.currentUser.getEncryptedPassword());
                    } else {
                        writer.println(this.currentUser.hash(passwordTextField.getText()));
                    }

                    writer.println(concatenatedCategories);
                    writer.println(measurementPreference);

                    writer.close();

                    //4.2) Write out info for the contact settings tab

                    //4.3) Write out info for the search preferences tab


                    tempStage.close();
                }catch(Exception e)
                {
                    popUpErrorWindow(e.toString());
                }
            }
            else {
                popUpErrorWindow(errorMsg);
            }
        });

        cancelButton.setOnAction(action ->
        {
            tempStage.close();
        });

        bottomPane.setMargin(saveButton, new Insets(10, 10, 10, 10));
        bottomPane.setMargin(cancelButton, new Insets(10, 10, 10, 10));

        bottomPane.getChildren().addAll(saveButton, cancelButton);

        //Set the scene & show the stage
        Scene scene = new Scene(root);
        tempStage.setScene(scene);
        tempStage.showAndWait();
    }

    private Person searchPersonWindow() //opens a window enabling the user to search through their current list of contacts
    {
        //local variables
        int listBoxIndex = 0;

        //make new stage & pane
        Stage tempStage = new Stage();tempStage.initModality(Modality.APPLICATION_MODAL);tempStage.setTitle("Search Person");tempStage.setWidth(550);tempStage.setHeight(600);tempStage.setResizable(false);
        Pane tempPane = new Pane();

        try //set the icon image
        {
            tempStage.getIcons().add(new Image("https://i.pinimg.com/originals/7e/67/79/7e6779bf6d689ef9d288052bdbfdcf41.jpg"));
        }catch(Exception e)
        {
            popUpErrorWindow("ERROR: " + e);
        }

        //make all objects in searchPersonWindow
        //Text objects
        Text advancedSearch = new Text("more advanced search");advancedSearch.setX(tempStage.getWidth() - 545);advancedSearch.setY(tempStage.getHeight() - 50);

        //TextBox's
        TextField nameTextField = new TextField();nameTextField.setLayoutX(tempStage.getWidth() - 545);nameTextField.setLayoutY(tempStage.getHeight() - 580);nameTextField.setPromptText("Last, First Middle Name");
        TextField phoneNumberTextField = new TextField();phoneNumberTextField.setLayoutX(tempStage.getWidth() - 357.5);phoneNumberTextField.setLayoutY(tempStage.getHeight() - 580);phoneNumberTextField.setPromptText("Phone Number");
        TextField addressTextField = new TextField();addressTextField.setLayoutX(tempStage.getWidth() - 170);addressTextField.setLayoutY(tempStage.getHeight() - 580);addressTextField.setPromptText("Address");

        //ListBox
        ListView<Person> searchBox = new ListView<>();searchBox.setLayoutX(tempStage.getWidth() - 515);searchBox.setLayoutY(tempStage.getHeight() - 540);searchBox.setMinHeight(400);searchBox.setMinWidth(470);

        //Buttons
        Button selectButton = new Button("Select");selectButton.setLayoutX(searchBox.getLayoutX());selectButton.setLayoutY(tempStage.getHeight() - 125);selectButton.setMinHeight(40);selectButton.setMinWidth(80);selectButton.setDisable(true);
        Button cancelButton = new Button("Cancel");cancelButton.setLayoutX(tempStage.getWidth() - 125);cancelButton.setLayoutY(tempStage.getHeight() - 125);cancelButton.setMinHeight(40);cancelButton.setMinWidth(80);

        //auto-populate search list with all contacts
        for(Person currentContact : this.contacts)
        {
            searchBox.getItems().add(currentContact);
        }

        //Node Functionality
        //TextBox's
        nameTextField.setOnAction(action ->
        {
            searchBox.getItems().clear();
            String tempFirstName = "", tempMiddleName = "", tempLastName = "";
            String[] tempName = nameTextField.getText().split("\\s"); //splits the string based on the spaces
            for(int i = 0; i < tempName.length; i++) //forloop to replace all commas within name
            {
                if (tempName[i].contains(","))
                {
                    tempName[i] = tempName[i].replace(",", "");
                }
            }
            //if block to determine the order of the names
            if(tempName.length == 1)
            {
                tempFirstName = tempName[0];
            }
            else if(tempName.length == 2)
            {
                tempFirstName = tempName[0];
                tempLastName = tempName[1];
            }
            else if(tempName.length == 3)
            {
                tempLastName = tempName[0];
                tempFirstName = tempName[1];
                tempMiddleName = tempName[2];
            }
            for(int i = 0; i < this.contacts.size(); i++) //add items to the list box based on if they're found in the list of current contacts
            {
                String currentFullName = this.contacts.get(i).getFirstName() + this.contacts.get(i).getMiddleName() + this.contacts.get(i).getLastName(); //TODO: Make the currentFullName all lowercase as well as all the tempNames lowercase as well
                currentFullName = currentFullName.toLowerCase();
                tempFirstName = tempFirstName.toLowerCase();
                tempLastName = tempLastName.toLowerCase();
                tempMiddleName = tempMiddleName.toLowerCase();
                if(currentFullName.contains(tempLastName.toLowerCase()) && !tempLastName.equals(""))
                {
                    searchBox.getItems().add(listBoxIndex, this.contacts.get(i));//this.contacts.get(i).getLastName() + ", " + this.contacts.get(i).getFirstName() + " " + this.contacts.get(i).getMiddleName());
                }
                if(currentFullName.contains(tempFirstName.toLowerCase()) && !tempFirstName.equals(""))
                {
                    if(!searchBox.getItems().contains(this.contacts.get(i))) //tests to see if the person has already been added
                    {
                        searchBox.getItems().add(listBoxIndex, this.contacts.get(i));// this.contacts.get(i).getLastName() + ", " + this.contacts.get(i).getFirstName() + " " + this.contacts.get(i).getMiddleName());
                    }
                }
                if(currentFullName.contains(tempMiddleName.toLowerCase()) && !tempMiddleName.equals(""))
                {
                    if(!searchBox.getItems().contains(this.contacts.get(i)))//this.contacts.get(i).getLastName() + ", " + this.contacts.get(i).getFirstName() + " " + this.contacts.get(i).getMiddleName()))
                    {
                        searchBox.getItems().add(listBoxIndex, this.contacts.get(i)); // this.contacts.get(i).getLastName() + ", " + this.contacts.get(i).getFirstName() + " " + this.contacts.get(i).getMiddleName());
                    }
                }
            }
        });

        phoneNumberTextField.setOnAction(action ->
        {
            searchBox.getItems().clear();

            String tempPhoneNumber = phoneNumberTextField.getText();

            for(int i = 0; i < contacts.size(); i++)
            {
                if(this.contacts.get(i).getPhoneNumbers().get(0).getPhoneNumber().contains(tempPhoneNumber) && !tempPhoneNumber.equals(""))
                {
                    if(!searchBox.getItems().contains(this.contacts.get(i)))
                    {
                        searchBox.getItems().add(listBoxIndex, this.contacts.get(i));
                    }
                }
            }
        });

        addressTextField.setOnAction(action ->
        {
            searchBox.getItems().clear();

            String tempAddress = addressTextField.getText();

            for(int i = 0; i < contacts.size(); i++)//goes through each contact
            {
                if(this.contacts.get(i).getAddresses().get(0).getStreetAddress().contains(tempAddress) && !tempAddress.equals(""))
                {
                    if(!searchBox.getItems().contains(this.contacts.get(i)))
                    {
                        searchBox.getItems().add(listBoxIndex, this.contacts.get(i));
                    }
                }
            }
        });

        searchBox.setOnMouseClicked(new EventHandler<MouseEvent>()  //Make it so the select button becomes available once you select an item in the search box
        {
            @Override
            public void handle(MouseEvent event) {
                selectButton.setDisable(false);
                if(event.getClickCount() == 2)
                {
                    tempStage.close();
                }
            }
        });

        //Buttons
        selectButton.setOnAction(action ->
        {
            tempStage.close();
        });

        cancelButton.setOnAction(action ->
        {
            tempStage.close();
        });

        tempPane.getChildren().addAll(advancedSearch, nameTextField, phoneNumberTextField, addressTextField ,searchBox, selectButton, cancelButton);
        Scene tempScene = new Scene(tempPane);
        tempStage.setScene(tempScene);
        tempStage.showAndWait();
        //ListBox
        return searchBox.getSelectionModel().getSelectedItem();
    }

    private Person addPersonWindow() //opens a window enabling the user to add someone to their list of contacts & writes it out to the file
    {
        //local variables
        final int xInitial = 830;
        final int xIncrement = 200;
        final int yIncrement = 10;
        Person newPerson = new Person(); //Person object to save to contacts.txt
        final File filePath = new File("C:\\Creeper\\" + this.currentUser.getUsername() + "\\contacts.txt");//File Path...Won't change
        String[] stateAbb = {"--", "AK", "AL", "AR", "AZ", "CA", "CO", "CT", "DE", "FL", "GA", "HI", "IA", "ID", "IL", "IN", "KS", "KY", "LA", "MA", "MD", "ME", "MI", "MN", "MO" , "MS" , "MT" , "NC" , "ND" , "NE" , "NH" , "NJ" , "NM" , "NV" , "NY" , "OH" , "OK" , "OR" , "PA" , "RI" , "SC" , "SD", "TN" , "TX" , "UT" , "VA", "VT", "WA", "WI", "WV", "WY"};
        String[] addressTypes = {"--", "Home", "Work", "Other"};
        String[] phoneNumberTypes = {"--", "Home", "Cell", "Work", "Other"};
        String[] emailTypes = {"--", "Personal", "Work", "Other"};
        String[] eyeColorTypes = {"--", "Brown", "Blue", "Hazel", "Amber", "Green", "Grey"};
        String[] hairColorTypes = {"--", "Brown", "Black", "Blond", "Red", "White"};

        //ArrayLists
          //Buttons
          ArrayList<Button> removeAddressButtons = new ArrayList<>();
          ArrayList<Button> removePhoneNumberButtons = new ArrayList<>();
          ArrayList<Button> removeEmailButtons = new ArrayList<>();

          //TextFields
            //Address
            ArrayList<TextField> streetAddressTextFields = new ArrayList<>();
            ArrayList<TextField> cityTextFields = new ArrayList<>();
            ArrayList<TextField> zipTextFields = new ArrayList<>();
          //Phone# & email
          ArrayList<TextField> phoneNumberTextFields = new ArrayList<>();
          ArrayList<TextField> emailTextFields = new ArrayList<>();

          //Combo Box's
          ArrayList<ComboBox> stateComboBoxes = new ArrayList<>();
          ArrayList<ComboBox> addressComboBoxes = new ArrayList<>();
          ArrayList<ComboBox> phoneNumberComboBoxes = new ArrayList<>();
          ArrayList<ComboBox> emailComboBoxes = new ArrayList<>();

        //make a new stage and pane
        Stage tempStage = new Stage();tempStage.initModality(Modality.APPLICATION_MODAL);tempStage.setTitle("Add Contact");tempStage.setWidth(850);tempStage.setHeight(1000);tempStage.setResizable(false); //details for the new window
        Pane tempPane = new Pane();
        ScrollPane sp = new ScrollPane();
        sp.setContent(tempPane);
        sp.setPannable(true);

        try //set the icon image
        {
            tempStage.getIcons().add(new Image("https://i.pinimg.com/originals/7e/67/79/7e6779bf6d689ef9d288052bdbfdcf41.jpg"));
        }catch(Exception e)
        {
            popUpErrorWindow("ERROR: " + e.toString());
        }

        //add default buttons & textfields to arraylist
        Button initialButton = new Button("+");initialButton.setMinHeight(20);initialButton.setMinWidth(40);initialButton.setShape(new Circle(1.5));initialButton.setMaxSize(25,25);initialButton.setMinSize(25, 25);

        //make all nodes in add person window
        //Tool Tips
        final Tooltip measurementToolTip = new Tooltip();measurementToolTip.setText("You can change your measurement \npreference in Account Settings");measurementToolTip.setShowDelay(new Duration(20));

        //Text Objects
        Text nameTitleText = new Text("Name");nameTitleText.setX(tempStage.getWidth() - xInitial);nameTitleText.setY(tempStage.getHeight() - 970);nameTitleText.setFont(Font.font("Times New Roman", FontWeight.BOLD, 17));
        Text dateOfBirthTitleText = new Text("Date of Birth");dateOfBirthTitleText.setX(tempStage.getWidth() - xInitial);dateOfBirthTitleText.setY(nameTitleText.getY() + (yIncrement * 7));dateOfBirthTitleText.setFont(Font.font("Times New Roman", FontWeight.BOLD, 17));
        Text sexTitleText = new Text("Sex");sexTitleText.setX(dateOfBirthTitleText.getX() + xIncrement);sexTitleText.setY(dateOfBirthTitleText.getY());sexTitleText.setFont(Font.font("Times New Roman", FontWeight.BOLD, 17));
        Text weightTitleText = new Text("Weight");weightTitleText.setX(dateOfBirthTitleText.getX());weightTitleText.setY(dateOfBirthTitleText.getY() + (yIncrement * 7));weightTitleText.setFont(Font.font("Times New Roman", FontWeight.BOLD, 17));
        Text weightHelperText = new Text();weightHelperText.setX(weightTitleText.getX() + 65);weightHelperText.setY(weightTitleText.getY() + (yIncrement * 2.9));weightHelperText.setFont(Font.font("Times New Roman", 16));Tooltip.install(weightHelperText, measurementToolTip);
        Text heightTitleText = new Text("Height");heightTitleText.setX(weightTitleText.getX() + xIncrement);heightTitleText.setY(weightTitleText.getY());heightTitleText.setFont(Font.font("Times New Roman", FontWeight.BOLD, 17));
        Text heightHelperText = new Text();heightHelperText.setX(heightTitleText.getX() + 65);heightHelperText.setY(weightHelperText.getY());heightHelperText.setFont(Font.font("Times New Roman", 16));Tooltip.install(heightHelperText, measurementToolTip);
        Text eyeColorTitleText = new Text("Eye Color");eyeColorTitleText.setX(heightTitleText.getX() + xIncrement);eyeColorTitleText.setY(weightTitleText.getY());eyeColorTitleText.setFont(Font.font("Times New Roman", FontWeight.BOLD, 17));
        Text hairColorTitleText = new Text("Hair Color");hairColorTitleText.setX(eyeColorTitleText.getX() + xIncrement);hairColorTitleText.setY(eyeColorTitleText.getY());hairColorTitleText.setFont(Font.font("Times New Roman", FontWeight.BOLD, 17));
        Text addressTitleText = new Text("Address");addressTitleText.setX(tempStage.getWidth() - xInitial);addressTitleText.setY(dateOfBirthTitleText.getY() + yIncrement * 14);addressTitleText.setFont(Font.font("Times New Roman", FontWeight.BOLD, 17));
        Text phoneNumberTitleText = new Text("Phone Number");phoneNumberTitleText.setX(nameTitleText.getX());phoneNumberTitleText.setY(addressTitleText.getY() + yIncrement * 9);phoneNumberTitleText.setFont(Font.font("Times New Roman", FontWeight.BOLD, 17));
        Text emailTitleText = new Text("Email");emailTitleText.setX(phoneNumberTitleText.getX());emailTitleText.setY(phoneNumberTitleText.getY() + yIncrement * 9);emailTitleText.setFont(Font.font("Times New Roman", FontWeight.BOLD, 17));
        Text categoryTitleText = new Text("Category");categoryTitleText.setX(emailTitleText.getX());categoryTitleText.setY(emailTitleText.getY() + yIncrement * 9);categoryTitleText.setFont(Font.font("Times New Roman", FontWeight.BOLD, 17));
        Text commentTitleText = new Text("Comment");commentTitleText.setX(categoryTitleText.getX());commentTitleText.setY(categoryTitleText.getY() + (yIncrement * 14));commentTitleText.setFont(Font.font("Times New Roman", FontWeight.BOLD, 17));

        //Initial TextFields are created & added to their respective ArrayLists
        TextField firstNameTextField = new TextField();firstNameTextField.setLayoutX(nameTitleText.getX());firstNameTextField.setLayoutY(nameTitleText.getY() + 10);firstNameTextField.setPromptText("First Name");
        TextField middleNameTextField = new TextField();middleNameTextField.setLayoutX(nameTitleText.getX() + xIncrement);middleNameTextField.setLayoutY(firstNameTextField.getLayoutY());middleNameTextField.setPromptText("Middle Name");
        TextField lastNameTextField = new TextField();lastNameTextField.setLayoutX(nameTitleText.getX() + (xIncrement * 2));lastNameTextField.setLayoutY(firstNameTextField.getLayoutY());lastNameTextField.setPromptText("Last Name");
        TextField weightTextField = new TextField();weightTextField.setLayoutX(weightTitleText.getX());weightTextField.setLayoutY(weightTitleText.getY() + yIncrement);weightTextField.setPrefWidth(60);weightTextField.setPromptText("Weight");
        TextField heightTextField = new TextField();heightTextField.setLayoutX(heightTitleText.getX());heightTextField.setLayoutY(heightTitleText.getY() + yIncrement);heightTextField.setPrefWidth(60);heightTextField.setPromptText("Height");
        TextField initialStreetAddressTextField = new TextField();initialStreetAddressTextField.setLayoutX(addressTitleText.getX());initialStreetAddressTextField.setLayoutY(addressTitleText.getY() + yIncrement);initialStreetAddressTextField.setPromptText("Street Address");
        streetAddressTextFields.add(initialStreetAddressTextField);
        TextField initialCityAddressTextField = new TextField();initialCityAddressTextField.setLayoutX(addressTitleText.getX() + xIncrement);initialCityAddressTextField.setLayoutY(initialStreetAddressTextField.getLayoutY());initialCityAddressTextField.setPromptText("City / Town");
        cityTextFields.add(initialCityAddressTextField);
        TextField initialZipcodeTextField = new TextField();initialZipcodeTextField.setLayoutX(initialCityAddressTextField.getLayoutX() + (xIncrement * 1.3));initialZipcodeTextField.setLayoutY(initialCityAddressTextField.getLayoutY());initialZipcodeTextField.setPromptText("Zip Code");
        zipTextFields.add(initialZipcodeTextField);
        TextField initialPhoneNumberTextField = new TextField();initialPhoneNumberTextField.setLayoutX(phoneNumberTitleText.getX());initialPhoneNumberTextField.setLayoutY(phoneNumberTitleText.getY() + yIncrement * 1.5);initialPhoneNumberTextField.setPromptText("Phone Number");
        phoneNumberTextFields.add(initialPhoneNumberTextField);
        TextField initialEmailTextField = new TextField();initialEmailTextField.setLayoutX(emailTitleText.getX());initialEmailTextField.setLayoutY(emailTitleText.getY() + yIncrement);initialEmailTextField.setPromptText("Email");
        emailTextFields.add(initialEmailTextField);
        TextField addCategoryTextField = new TextField();addCategoryTextField.setLayoutX(categoryTitleText.getX() + 245);addCategoryTextField.setLayoutY(categoryTitleText.getY() + yIncrement * 8.5);addCategoryTextField.setPromptText("Enter in new category");addCategoryTextField.setVisible(false);

        //Text Area's
        TextArea commentTextArea = new TextArea();commentTextArea.setLayoutX(commentTitleText.getX());commentTextArea.setLayoutY(commentTitleText.getY() + (yIncrement * 1.5));commentTextArea.setPrefWidth(400);commentTextArea.setPrefHeight(200);commentTextArea.setWrapText(true);commentTextArea.setPromptText("Enter comment here...");

        //Radio Buttons
        ToggleGroup sexes = new ToggleGroup();
        RadioButton maleRadioButton = new RadioButton("Male");maleRadioButton.setLayoutX(sexTitleText.getX());maleRadioButton.setLayoutY(sexTitleText.getY() + yIncrement * (1.5));
        RadioButton femaleRadioButton = new RadioButton("Female");femaleRadioButton.setLayoutX(sexTitleText.getX() + 60);femaleRadioButton.setLayoutY(maleRadioButton.getLayoutY());

        maleRadioButton.setToggleGroup(sexes);
        femaleRadioButton.setToggleGroup(sexes);

        //Datepicker
        TextField dateOfBirth = new TextField();dateOfBirth.setLayoutX(nameTitleText.getX());dateOfBirth.setLayoutY(dateOfBirthTitleText.getY() + yIncrement);dateOfBirth.setPromptText("MM/dd/YYYY");

        //Initial ComboBox's are created & added to their respective ArrayLists
        ComboBox<String> eyecolorComboBox = new ComboBox<>(FXCollections.observableArrayList(eyeColorTypes));eyecolorComboBox.setLayoutX(eyeColorTitleText.getX());eyecolorComboBox.setLayoutY(eyeColorTitleText.getY() + yIncrement);eyecolorComboBox.getSelectionModel().selectFirst();
        ComboBox<String> hairColorComboBox = new ComboBox<>(FXCollections.observableArrayList(hairColorTypes));hairColorComboBox.setLayoutX(hairColorTitleText.getX());hairColorComboBox.setLayoutY(hairColorTitleText.getY() + yIncrement);hairColorComboBox.getSelectionModel().selectFirst();
        ComboBox<String> stateComboBox = new ComboBox<>(FXCollections.observableArrayList(stateAbb));stateComboBox.setLayoutX(initialCityAddressTextField.getLayoutX() + (xIncrement * .85));stateComboBox.setLayoutY(initialCityAddressTextField.getLayoutY());stateComboBox.setMinWidth(65);stateComboBox.setMaxWidth(65);stateComboBox.getSelectionModel().selectFirst();
        stateComboBoxes.add(stateComboBox);
        ComboBox<String> addressTypeComboBox = new ComboBox<>(FXCollections.observableArrayList(addressTypes));addressTypeComboBox.setLayoutX(stateComboBox.getLayoutX() + (xIncrement * 1.25));addressTypeComboBox.setLayoutY(stateComboBox.getLayoutY());addressTypeComboBox.setMinWidth(75);addressTypeComboBox.setMaxWidth(75);addressTypeComboBox.getSelectionModel().selectFirst();
        addressComboBoxes.add(addressTypeComboBox);
        ComboBox<String> phoneNumberTypeComboBox = new ComboBox<>(FXCollections.observableArrayList(phoneNumberTypes));phoneNumberTypeComboBox.setLayoutX(initialPhoneNumberTextField.getLayoutX() + (xIncrement * .8));phoneNumberTypeComboBox.setLayoutY(initialPhoneNumberTextField.getLayoutY());phoneNumberTypeComboBox.setMaxWidth(75);phoneNumberTypeComboBox.setMinWidth(75);phoneNumberTypeComboBox.getSelectionModel().selectFirst();
        phoneNumberComboBoxes.add(phoneNumberTypeComboBox);
        ComboBox<String> emailTypeComboBox = new ComboBox<>(FXCollections.observableArrayList(emailTypes));emailTypeComboBox.setLayoutX(phoneNumberTypeComboBox.getLayoutX());emailTypeComboBox.setLayoutY(initialEmailTextField.getLayoutY());emailTypeComboBox.setMinWidth(75);emailTypeComboBox.setMaxWidth(75);emailTypeComboBox.getSelectionModel().selectFirst();
        emailComboBoxes.add(emailTypeComboBox);

        //add the newly made fields to pane
        tempPane.getChildren().addAll(streetAddressTextFields.get(0), cityTextFields.get(0), zipTextFields.get(0), stateComboBoxes.get(0), addressComboBoxes.get(0), phoneNumberTextFields.get(0), emailTextFields.get(0));

        //ListView
        ListView<String> categoryListView = new ListView<>(FXCollections.observableArrayList(this.currentUser.getCategories()));categoryListView.setLayoutX(categoryTitleText.getX());categoryListView.setLayoutY(categoryTitleText.getY() + yIncrement);categoryListView.setPrefHeight(100);categoryListView.setPrefWidth(235);categoryListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        //Buttons
        Button initialAddAddressPlusButton = new Button("+");initialAddAddressPlusButton.setMinHeight(20);initialAddAddressPlusButton.setMinWidth(40);initialAddAddressPlusButton.setShape(new Circle(1.5));initialAddAddressPlusButton.setMaxSize(25,25);initialAddAddressPlusButton.setMinSize(25, 25);initialAddAddressPlusButton.setLayoutX(addressTypeComboBox.getLayoutX() + (xIncrement * .43));initialAddAddressPlusButton.setLayoutY(initialStreetAddressTextField.getLayoutY());
        Button initialMinusAddressButton = new Button("-");initialMinusAddressButton.setMinHeight(20);initialMinusAddressButton.setMinWidth(20);initialMinusAddressButton.setLayoutX(addressTypeComboBox.getLayoutX() + (xIncrement * .43));initialMinusAddressButton.setLayoutY(initialAddAddressPlusButton.getLayoutY());initialMinusAddressButton.setShape(new Circle(1.5));initialMinusAddressButton.setMaxSize(25, 25);initialMinusAddressButton.setMinSize(25, 25);initialMinusAddressButton.setVisible(false);
        Button initialAddPhoneNumberPlusButton = new Button("+");initialAddPhoneNumberPlusButton.setMinHeight(20);initialAddPhoneNumberPlusButton.setMinWidth(40);initialAddPhoneNumberPlusButton.setShape(new Circle(1.5));initialAddPhoneNumberPlusButton.setMaxSize(25,25);initialAddPhoneNumberPlusButton.setMinSize(25, 25);initialAddPhoneNumberPlusButton.setLayoutX(phoneNumberTypeComboBox.getLayoutX() + (xIncrement * .43));initialAddPhoneNumberPlusButton.setLayoutY(phoneNumberTypeComboBox.getLayoutY());
        Button initialMinusPhoneNumberButton = new Button("-");initialMinusPhoneNumberButton.setMinHeight(20);initialMinusPhoneNumberButton.setMinWidth(40);initialMinusPhoneNumberButton.setShape(new Circle(1.5));initialMinusPhoneNumberButton.setMaxSize(25,25);initialMinusPhoneNumberButton.setMinSize(25, 25);initialMinusPhoneNumberButton.setLayoutX(initialAddPhoneNumberPlusButton.getLayoutX());initialMinusPhoneNumberButton.setLayoutY(initialAddPhoneNumberPlusButton.getLayoutY());
        Button initialAddEmailPlusButton = new Button("+");initialAddEmailPlusButton.setMinHeight(20);initialAddEmailPlusButton.setMinWidth(40);initialAddEmailPlusButton.setShape(new Circle(1.5));initialAddEmailPlusButton.setMaxSize(25,25);initialAddEmailPlusButton.setMinSize(25, 25);initialAddEmailPlusButton.setLayoutX(initialAddPhoneNumberPlusButton.getLayoutX());initialAddEmailPlusButton.setLayoutY(initialEmailTextField.getLayoutY());
        Button initialMinusEmailButton = new Button("-");initialMinusEmailButton.setMinHeight(20);initialMinusEmailButton.setMinWidth(40);initialMinusEmailButton.setShape(new Circle(1.5));initialMinusEmailButton.setMaxSize(25,25);initialMinusEmailButton.setMinSize(25, 25);initialMinusEmailButton.setLayoutX(initialAddEmailPlusButton.getLayoutX());initialMinusEmailButton.setLayoutY(initialAddEmailPlusButton.getLayoutY());
        Button cancelButton = new Button("Cancel");cancelButton.setMinHeight(50);cancelButton.setMinWidth(100);cancelButton.setLayoutX(tempStage.getWidth() - 130); cancelButton.setLayoutY(tempStage.getHeight() - 100);
        Button addPersonButton = new Button("Submit");addPersonButton.setMinHeight(50);addPersonButton.setMinWidth(100);addPersonButton.setLayoutX(tempStage.getWidth() - xInitial);addPersonButton.setLayoutY(cancelButton.getLayoutY());
        Button addCategoryPlusButton = new Button(">");addCategoryPlusButton.setMinHeight(20);addCategoryPlusButton.setMinWidth(40);addCategoryPlusButton.setLayoutX(initialMinusPhoneNumberButton.getLayoutX());addCategoryPlusButton.setLayoutY(categoryListView.getLayoutY());addCategoryPlusButton.setShape(new Circle(1.5));addCategoryPlusButton.setMaxSize(25,25);addCategoryPlusButton.setMinSize(25, 25);
        Button removeCategoryMinusButton = new Button("<");removeCategoryMinusButton.setMinHeight(20);removeCategoryMinusButton.setMinWidth(40);removeCategoryMinusButton.setLayoutX(addCategoryPlusButton.getLayoutX());removeCategoryMinusButton.setLayoutY(addCategoryPlusButton.getLayoutY());removeCategoryMinusButton.setShape(new Circle(1.5));removeCategoryMinusButton.setMaxSize(25, 25);removeCategoryMinusButton.setMinSize(25, 25);removeCategoryMinusButton.setVisible(false);
        Button addCategoryButton = new Button("Add");addCategoryButton.setMinHeight(20);addCategoryButton.setMinWidth(40);addCategoryButton.setLayoutX(addCategoryTextField.getLayoutX());addCategoryButton.setLayoutY(addCategoryTextField.getLayoutY() - 30);addCategoryButton.setVisible(false);

        //NODE FUNCTIONALITY
        //Text
        if(currentUser.getIsMetricUser())
        {
            weightHelperText.setText("Kg");
            heightHelperText.setText("cm");
        }
        else
        {
            weightHelperText.setText("lbs");
            heightHelperText.setText("in");
        }

        //Buttons
        initialAddAddressPlusButton.setOnAction(action ->
        {
            Button newButton = new Button("-");newButton.setMinHeight(20);newButton.setMinWidth(20);newButton.setLayoutX(addressTypeComboBox.getLayoutX() + (xIncrement * .43));newButton.setLayoutY(initialAddAddressPlusButton.getLayoutY());newButton.setShape(new Circle(1.5));newButton.setMaxSize(25, 25);newButton.setMinSize(25, 25);
            newButton.setOnAction(function -> //create the action for the button before you add it to the array
            {
                /* GUIDE TO WHAT THIS STUPIDLY COMPLICATED BUTTON DOES
                 * -----------------------------------------------------
                 * 1) Make sure that if there's only one removeAddressButton left, to hide it from the pane and adjust the position of the addressPlusButton
                 * 2) Shifts all the nodes on the screen up accordingly
                 * 3) Redraw the relevant nodes (addressTextField, cityTextField, stateComboBox, zipTextField, addressComboBox, removeAddressButton)
                 *  3a) Delete the nodes from the pane
                 *  3b) Delete the appropriate nodes from the their corresponding ArrayLists
                 *  3c) add the modified list of nodes to the pane
                 * 4) Make sure that if there's only one removeAddressButton left, to hide it from the pane
                 */

                //1) Shift all nodes on the pane up accordingly
                //1a)Shift all the non-array nodes up
                initialAddAddressPlusButton.setLayoutY(initialAddAddressPlusButton.getLayoutY() - (yIncrement * 3));
                initialMinusAddressButton.setLayoutY(initialMinusAddressButton.getLayoutY() - (yIncrement * 3));
                phoneNumberTitleText.setY(phoneNumberTitleText.getY() - (yIncrement * 3));
                initialAddPhoneNumberPlusButton.setLayoutY(initialAddPhoneNumberPlusButton.getLayoutY() - (yIncrement * 3));
                initialMinusPhoneNumberButton.setLayoutY(initialMinusPhoneNumberButton.getLayoutY() - (yIncrement * 3));
                emailTitleText.setY(emailTitleText.getY() - (yIncrement * 3));
                initialAddEmailPlusButton.setLayoutY(initialAddEmailPlusButton.getLayoutY() - (yIncrement * 3));
                initialMinusEmailButton.setLayoutY(initialMinusEmailButton.getLayoutY() - (yIncrement * 3));
                categoryTitleText.setY(categoryTitleText.getY() - (yIncrement * 3));
                categoryListView.setLayoutY(categoryListView.getLayoutY() - (yIncrement * 3));
                addCategoryPlusButton.setLayoutY(addCategoryPlusButton.getLayoutY() - (yIncrement * 3));
                removeCategoryMinusButton.setLayoutY(removeCategoryMinusButton.getLayoutY() - (yIncrement * 3));
                addCategoryButton.setLayoutY(addCategoryButton.getLayoutY() - (yIncrement * 3));
                addCategoryTextField.setLayoutY(addCategoryTextField.getLayoutY() - (yIncrement * 3));
                commentTitleText.setY(commentTitleText.getY() - (yIncrement * 3));
                commentTextArea.setLayoutY(commentTextArea.getLayoutY() - (yIncrement * 3));
                cancelButton.setLayoutY(cancelButton.getLayoutY() - (yIncrement * 3));
                addPersonButton.setLayoutY(addPersonButton.getLayoutY() - (yIncrement * 3));

                //1b)Shift all the phone number & email ArrayLists up
                for(int i = 0; i < removePhoneNumberButtons.size(); i++)
                {
                    removePhoneNumberButtons.get(i).setLayoutY(removePhoneNumberButtons.get(i).getLayoutY() - (yIncrement * 3));
                }
                for(int i = 0; i < phoneNumberTextFields.size(); i++)
                {
                    phoneNumberTextFields.get(i).setLayoutY(phoneNumberTextFields.get(i).getLayoutY() - (yIncrement * 3));
                    phoneNumberComboBoxes.get(i).setLayoutY(phoneNumberComboBoxes.get(i).getLayoutY() - (yIncrement * 3));
                }
                for(int i = 0; i < removeEmailButtons.size(); i ++)
                {
                    removeEmailButtons.get(i).setLayoutY(removeEmailButtons.get(i).getLayoutY() - (yIncrement * 3));
                }
                for(int i = 0; i < emailTextFields.size(); i ++)
                {
                    emailTextFields.get(i).setLayoutY(emailTextFields.get(i).getLayoutY() - (yIncrement * 3));
                    emailComboBoxes.get(i).setLayoutY(emailComboBoxes.get(i).getLayoutY() - (yIncrement * 3));
                }
                //2) Redraw buttons
                //2a) Delete nodes from pane
                for(int i = 0; i < removeAddressButtons.size(); i++)//delete
                {
                    tempPane.getChildren().remove(removeAddressButtons.get(i)); //the actual deleting of buttons from pane
                }
                int unluckyNode = 0;
                for(int i = 0; i < streetAddressTextFields.size(); i++) //delete the nodes associated with that button
                {
                    tempPane.getChildren().removeAll(streetAddressTextFields.get(i), cityTextFields.get(i), stateComboBoxes.get(i), zipTextFields.get(i), addressComboBoxes.get(i));
                    if(streetAddressTextFields.get(i).getLayoutY() == newButton.getLayoutY())//Delete the corresponding Address Box's based on their Y-coordinate as well as remove them from the their respective array lists
                    {
                        unluckyNode = i; //save the unlucky node positions for later
                    }
                }

                //2b) Delete nodes from their respective ArrayLists
                streetAddressTextFields.remove(streetAddressTextFields.get(unluckyNode));
                cityTextFields.remove(cityTextFields.get(unluckyNode));
                stateComboBoxes.remove(stateComboBoxes.get(unluckyNode));
                zipTextFields.remove(zipTextFields.get(unluckyNode));
                addressComboBoxes.remove(addressComboBoxes.get(unluckyNode));

                removeAddressButtons.remove(newButton);//remove button from ArrayList

                //2c) Add nodes back to the pane
                for(int i = 0; i < removeAddressButtons.size(); i++)//redraw buttons
                {
                    removeAddressButtons.get(i).setLayoutY((addressTitleText.getY() + yIncrement) + (yIncrement * 3 * i)); //reset the Y value
                    tempPane.getChildren().add(removeAddressButtons.get(i));
                }

                for(int i = 0; i < streetAddressTextFields.size(); i++)//redraw other nodes
                {
                    //reset Y values
                    streetAddressTextFields.get(i).setLayoutY((addressTitleText.getY() + yIncrement) + (yIncrement * 3 * i));
                    cityTextFields.get(i).setLayoutY(streetAddressTextFields.get(i).getLayoutY());
                    stateComboBoxes.get(i).setLayoutY(streetAddressTextFields.get(i).getLayoutY());
                    zipTextFields.get(i).setLayoutY(streetAddressTextFields.get(i).getLayoutY());
                    addressComboBoxes.get(i).setLayoutY(streetAddressTextFields.get(i).getLayoutY());

                    //add them back to the pane
                    tempPane.getChildren().addAll(streetAddressTextFields.get(i), cityTextFields.get(i), stateComboBoxes.get(i), zipTextFields.get(i), addressComboBoxes.get(i));
                }

                if(removeAddressButtons.size() == 0)
                {
                    initialAddAddressPlusButton.setLayoutX(addressTypeComboBox.getLayoutX() + (xIncrement * .43));
                    initialMinusAddressButton.setVisible(false);
                }
            });
            removeAddressButtons.add(newButton); //add the newly created button to the ArrayList

            for(int i = 0; i < removeAddressButtons.size(); i++) //loop to remove all "-" buttons from the pane
            {
                tempPane.getChildren().remove(removeAddressButtons.get(i));
            }

            for(int i = 0; i < removeAddressButtons.size(); i++) //redraw with the correct amount of "-" buttons(+1)
            {
                tempPane.getChildren().addAll(removeAddressButtons.get(i));
            }
            
            //shift everything below address down accordingly
            initialAddAddressPlusButton.setLayoutY(initialAddAddressPlusButton.getLayoutY() + (yIncrement * 3));
            initialMinusAddressButton.setLayoutY(initialMinusAddressButton.getLayoutY() + (yIncrement * 3));
            phoneNumberTitleText.setY(phoneNumberTitleText.getY() + (yIncrement * 3));
            initialAddPhoneNumberPlusButton.setLayoutY(initialAddPhoneNumberPlusButton.getLayoutY() + (yIncrement * 3));
            initialMinusPhoneNumberButton.setLayoutY(initialMinusPhoneNumberButton.getLayoutY() + (yIncrement * 3));
            emailTitleText.setY(emailTitleText.getY() + (yIncrement * 3));
            initialAddEmailPlusButton.setLayoutY(initialAddEmailPlusButton.getLayoutY() + (yIncrement * 3));
            initialMinusEmailButton.setLayoutY(initialMinusEmailButton.getLayoutY() + (yIncrement * 3));
            categoryTitleText.setY(categoryTitleText.getY() + (yIncrement * 3));
            categoryListView.setLayoutY(categoryListView.getLayoutY() + (yIncrement * 3));
            addCategoryPlusButton.setLayoutY(addCategoryPlusButton.getLayoutY() + (yIncrement * 3));
            removeCategoryMinusButton.setLayoutY(removeCategoryMinusButton.getLayoutY() + (yIncrement * 3));
            addCategoryButton.setLayoutY(addCategoryButton.getLayoutY() + (yIncrement * 3));
            addCategoryTextField.setLayoutY(addCategoryTextField.getLayoutY() + (yIncrement * 3));
            commentTitleText.setY(commentTitleText.getY() + (yIncrement * 3));
            commentTextArea.setLayoutY(commentTextArea.getLayoutY() + (yIncrement * 3));
            cancelButton.setLayoutY(cancelButton.getLayoutY() + (yIncrement * 3));
            addPersonButton.setLayoutY(addPersonButton.getLayoutY() + (yIncrement * 3));

            for(int i = 0; i < removePhoneNumberButtons.size(); i++) //shift the "-" phone number buttons down
            {
                removePhoneNumberButtons.get(i).setLayoutY(removePhoneNumberButtons.get(i).getLayoutY() + (yIncrement * 3));
            }
            for(int i = 0; i < phoneNumberTextFields.size(); i++)
            {
                phoneNumberTextFields.get(i).setLayoutY(phoneNumberTextFields.get(i).getLayoutY() + (yIncrement * 3));
                phoneNumberComboBoxes.get(i).setLayoutY(phoneNumberComboBoxes.get(i).getLayoutY() + (yIncrement * 3));
            }
            for(int i = 0; i < removeEmailButtons.size(); i++)//shift the "-" email buttons down
            {
                removeEmailButtons.get(i).setLayoutY(removeEmailButtons.get(i).getLayoutY() + (yIncrement * 3));
            }
            for(int i = 0; i < emailTextFields.size(); i++)
            {
                emailTextFields.get(i).setLayoutY(emailTextFields.get(i).getLayoutY() + (yIncrement * 3));
                emailComboBoxes.get(i).setLayoutY(emailComboBoxes.get(i).getLayoutY() + (yIncrement * 3));
            }

            //add another row of address elements to the page
            //generate their properties
            TextField newStAddress = new TextField();newStAddress.setLayoutX(addressTitleText.getX());newStAddress.setLayoutY(initialStreetAddressTextField.getLayoutY() + ((yIncrement * 3) * streetAddressTextFields.size()));newStAddress.setPromptText("Street Address");
            streetAddressTextFields.add(newStAddress);
            TextField newCity = new TextField();newCity.setLayoutX(initialCityAddressTextField.getLayoutX());newCity.setLayoutY(initialCityAddressTextField.getLayoutY() + ((yIncrement * 3) * cityTextFields.size()));newCity.setPromptText("City / Town");
            cityTextFields.add(newCity);
            ComboBox<String> newState = new ComboBox<>(FXCollections.observableArrayList(stateAbb));newState.setLayoutX(stateComboBox.getLayoutX());newState.setLayoutY(stateComboBox.getLayoutY() + ((yIncrement * 3) * stateComboBoxes.size()));newState.setMinWidth(65);newState.setMaxWidth(65);newState.getSelectionModel().selectFirst();
            stateComboBoxes.add(newState);
            TextField newZip = new TextField();newZip.setLayoutX(initialZipcodeTextField.getLayoutX());newZip.setLayoutY(initialZipcodeTextField.getLayoutY() + ((yIncrement * 3) * zipTextFields.size()));newZip.setPromptText("Zip Code");
            zipTextFields.add(newZip);
            ComboBox<String> newAddressType = new ComboBox<>(FXCollections.observableArrayList(addressTypes));newAddressType.setLayoutX(addressTypeComboBox.getLayoutX());newAddressType.setLayoutY(addressTypeComboBox.getLayoutY() + ((yIncrement * 3) * addressComboBoxes.size()));newAddressType.getSelectionModel().selectFirst();
            addressComboBoxes.add(newAddressType);

            //add them to the pane
            for(int i = 0; i < streetAddressTextFields.size(); i++) //delete all the text fields before I redraw them
            {
                tempPane.getChildren().removeAll(streetAddressTextFields.get(i), cityTextFields.get(i), stateComboBoxes.get(i), zipTextFields.get(i), addressComboBoxes.get(i));
            }

            for(int i = 0; i < streetAddressTextFields.size(); i++)
            {
                tempPane.getChildren().addAll(streetAddressTextFields.get(i), cityTextFields.get(i), stateComboBoxes.get(i), zipTextFields.get(i), addressComboBoxes.get(i));
            }

            //if there's more than one removeAddressButtons, then displace the add button to the right
            if(removeAddressButtons.size() > 0)
            {
                initialAddAddressPlusButton.setLayoutX((addressTypeComboBox.getLayoutX() + (xIncrement * .43)) + 30);
                initialMinusAddressButton.setVisible(true);
            }
        });
        initialMinusAddressButton.setOnAction(action ->
        {
            //move everything up
            initialAddAddressPlusButton.setLayoutY(initialAddAddressPlusButton.getLayoutY() - (yIncrement * 3));
            initialMinusAddressButton.setLayoutY(initialMinusAddressButton.getLayoutY() - (yIncrement * 3));
            phoneNumberTitleText.setY(phoneNumberTitleText.getY() - (yIncrement * 3));
            initialAddPhoneNumberPlusButton.setLayoutY(initialAddPhoneNumberPlusButton.getLayoutY() - (yIncrement * 3));
            initialMinusPhoneNumberButton.setLayoutY(initialMinusPhoneNumberButton.getLayoutY() - (yIncrement * 3));
            emailTitleText.setY(emailTitleText.getY() - (yIncrement * 3));
            initialAddEmailPlusButton.setLayoutY(initialAddEmailPlusButton.getLayoutY() - (yIncrement * 3));
            initialMinusEmailButton.setLayoutY(initialMinusEmailButton.getLayoutY() - (yIncrement * 3));
            categoryTitleText.setY(categoryTitleText.getY() - (yIncrement * 3));
            categoryListView.setLayoutY(categoryListView.getLayoutY() - (yIncrement * 3));
            addCategoryPlusButton.setLayoutY(addCategoryPlusButton.getLayoutY() - (yIncrement * 3));
            removeCategoryMinusButton.setLayoutY(removeCategoryMinusButton.getLayoutY() - (yIncrement * 3));
            addCategoryButton.setLayoutY(addCategoryButton.getLayoutY() - (yIncrement * 3));
            addCategoryTextField.setLayoutY(addCategoryTextField.getLayoutY() - (yIncrement * 3));
            commentTitleText.setY(commentTitleText.getY() - (yIncrement * 3));
            commentTextArea.setLayoutY(commentTextArea.getLayoutY() - (yIncrement * 3));
            cancelButton.setLayoutY(cancelButton.getLayoutY() - (yIncrement * 3));
            addPersonButton.setLayoutY(addPersonButton.getLayoutY() - (yIncrement * 3));

            //shift all the Phone Number elements down
            for(int i = 0; i < removePhoneNumberButtons.size(); i++)
            {
                removePhoneNumberButtons.get(i).setLayoutY(removePhoneNumberButtons.get(i).getLayoutY() - (yIncrement * 3));
            }
            for(int i = 0; i < phoneNumberTextFields.size(); i++)
            {
                phoneNumberTextFields.get(i).setLayoutY(phoneNumberTextFields.get(i).getLayoutY() - (yIncrement * 3));
                phoneNumberComboBoxes.get(i).setLayoutY(phoneNumberComboBoxes.get(i).getLayoutY() - (yIncrement * 3));
            }
            //Shift all the Email elements down
            for(int i = 0; i < removeEmailButtons.size(); i++)
            {
                removeEmailButtons.get(i).setLayoutY(removeEmailButtons.get(i).getLayoutY() - (yIncrement * 3));
            }

            for(int i = 0; i < emailTextFields.size(); i++)
            {
                emailTextFields.get(i).setLayoutY(emailTextFields.get(i).getLayoutY() - (yIncrement * 3));
                emailComboBoxes.get(i).setLayoutY(emailComboBoxes.get(i).getLayoutY() - (yIncrement * 3));
            }


            //redraw
            //delete the nodes from the pane
            for(int i = 0; i < removeAddressButtons.size(); i++)
            {
                tempPane.getChildren().remove(removeAddressButtons.get(i));
            }
            for(int i = 0; i < streetAddressTextFields.size(); i++)
            {
                tempPane.getChildren().removeAll(streetAddressTextFields.get(i), cityTextFields.get(i), stateComboBoxes.get(i), zipTextFields.get(i), addressComboBoxes.get(i));
            }
            //delete the nodes from their respective arraylists
            removeAddressButtons.remove(removeAddressButtons.get(removeAddressButtons.size() - 1)); //removes the last element
            streetAddressTextFields.remove(streetAddressTextFields.get(streetAddressTextFields.size() - 1));
            cityTextFields.remove(cityTextFields.get(cityTextFields.size() - 1));
            stateComboBoxes.remove(stateComboBoxes.get(stateComboBoxes.size() - 1));
            zipTextFields.remove(zipTextFields.get(zipTextFields.size() - 1));
            addressComboBoxes.remove(addressComboBoxes.get(addressComboBoxes.size() - 1));

            //add nodes back into the pane
            for(int i = 0; i< removeAddressButtons.size(); i++) //add the nodes back to the pane
            {
                //reset the Y coordinate
                removeAddressButtons.get(i).setLayoutY((addressTitleText.getY() + yIncrement) + (yIncrement * 3 * i));

                //add the nodes back to the pane
                tempPane.getChildren().add(removeAddressButtons.get(i));
            }

            for(int i = 0; i < streetAddressTextFields.size(); i++)//redraw other nodes
            {
                //reset Y values
                streetAddressTextFields.get(i).setLayoutY((addressTitleText.getY() + yIncrement) + (yIncrement * 3 * i));
                cityTextFields.get(i).setLayoutY(streetAddressTextFields.get(i).getLayoutY());
                stateComboBoxes.get(i).setLayoutY(streetAddressTextFields.get(i).getLayoutY());
                zipTextFields.get(i).setLayoutY(streetAddressTextFields.get(i).getLayoutY());
                addressComboBoxes.get(i).setLayoutY(streetAddressTextFields.get(i).getLayoutY());

                //add them back to the pane
                tempPane.getChildren().addAll(streetAddressTextFields.get(i), cityTextFields.get(i), stateComboBoxes.get(i), zipTextFields.get(i), addressComboBoxes.get(i));
            }

            if(removeAddressButtons.size() == 0)
            {
                initialMinusAddressButton.setVisible(false);
                initialAddAddressPlusButton.setLayoutX(addressTypeComboBox.getLayoutX() + (xIncrement * .43));
            }
        });

        initialAddPhoneNumberPlusButton.setOnAction(action ->
        {
            //make a minus button where the plus button was
            Button newButton = new Button("-");newButton.setMinHeight(20);newButton.setMinWidth(20);newButton.setLayoutX(phoneNumberTypeComboBox.getLayoutX() + (xIncrement * .43));newButton.setLayoutY(initialAddPhoneNumberPlusButton.getLayoutY());newButton.setShape(new Circle(1.5));newButton.setMaxSize(25, 25);newButton.setMinSize(25, 25);
            newButton.setOnAction(function ->
            {
                //1)Shift everything up
                initialAddPhoneNumberPlusButton.setLayoutY(initialAddPhoneNumberPlusButton.getLayoutY() - (yIncrement * 3));
                initialMinusPhoneNumberButton.setLayoutY(initialMinusPhoneNumberButton.getLayoutY() - (yIncrement * 3));
                emailTitleText.setY(emailTitleText.getY() - (yIncrement * 3));
                initialAddEmailPlusButton.setLayoutY(initialAddEmailPlusButton.getLayoutY() - (yIncrement * 3));
                initialMinusEmailButton.setLayoutY(initialMinusEmailButton.getLayoutY() - (yIncrement * 3));
                categoryTitleText.setY(categoryTitleText.getY() - (yIncrement * 3));
                categoryListView.setLayoutY(categoryListView.getLayoutY() - (yIncrement * 3));
                addCategoryPlusButton.setLayoutY(addCategoryPlusButton.getLayoutY() - (yIncrement * 3));
                removeCategoryMinusButton.setLayoutY(removeCategoryMinusButton.getLayoutY() - (yIncrement * 3));
                addCategoryButton.setLayoutY(addCategoryButton.getLayoutY() - (yIncrement * 3));
                addCategoryTextField.setLayoutY(addCategoryTextField.getLayoutY() - (yIncrement * 3));
                commentTitleText.setY(commentTitleText.getY() - (yIncrement * 3));
                commentTextArea.setLayoutY(commentTextArea.getLayoutY() - (yIncrement * 3));
                cancelButton.setLayoutY(cancelButton.getLayoutY() - (yIncrement * 3));
                addPersonButton.setLayoutY(addPersonButton.getLayoutY() - (yIncrement * 3));

                for(int i = 0; i < removeEmailButtons.size(); i++)
                {
                    removeEmailButtons.get(i).setLayoutY(removeEmailButtons.get(i).getLayoutY() - (yIncrement * 3));
                }

                for(int i = 0; i < emailTextFields.size(); i++)
                {
                    emailTextFields.get(i).setLayoutY(emailTextFields.get(i).getLayoutY() - (yIncrement * 3));
                    emailComboBoxes.get(i).setLayoutY(emailComboBoxes.get(i).getLayoutY() - (yIncrement * 3));
                }

                //2) Redraw buttons
                //2a) Delete nodes from pane
                for(int i = 0; i < removePhoneNumberButtons.size(); i++)//delete
                {
                    tempPane.getChildren().remove(removePhoneNumberButtons.get(i)); //the deleting of buttons from pane
                }
                int unluckyNode = 0;
                for(int i = 0; i < phoneNumberTextFields.size(); i++) //delete the nodes associated with that button
                {
                    double currentYCoordinate = phoneNumberTextFields.get(i).getLayoutY();
                    tempPane.getChildren().removeAll(phoneNumberTextFields.get(i), phoneNumberComboBoxes.get(i));
                    if(currentYCoordinate == newButton.getLayoutY())//Delete the corresponding Address Box's based on their Y-coordinate as well as remove them from the their respective array lists
                    {
                        unluckyNode = i; //save the unlucky node positions for later
                    }
                }

                //2b) Delete nodes from their respective ArrayLists
                phoneNumberTextFields.remove(unluckyNode);
                phoneNumberComboBoxes.remove(unluckyNode);
                removePhoneNumberButtons.remove(newButton);

                //2c) Add nodes back to the pane
                for(int i = 0; i < removePhoneNumberButtons.size(); i++)//redraw buttons
                {
                    removePhoneNumberButtons.get(i).setLayoutY((phoneNumberTitleText.getY() + yIncrement * 1.5) + (yIncrement * 3 * i)); //reset the Y value
                    tempPane.getChildren().add(removePhoneNumberButtons.get(i));
                }

                for(int i = 0; i < phoneNumberTextFields.size(); i++)//redraw other nodes
                {
                    //reset Y values
                    phoneNumberTextFields.get(i).setLayoutY((phoneNumberTitleText.getY() + yIncrement * 1.5) + (yIncrement * 3 * i));
                    phoneNumberComboBoxes.get(i).setLayoutY(phoneNumberTextFields.get(i).getLayoutY());

                    //add them back to the pane
                    tempPane.getChildren().addAll(phoneNumberTextFields.get(i), phoneNumberComboBoxes.get(i));
                }

                if(removePhoneNumberButtons.size() == 0)
                {
                    initialAddPhoneNumberPlusButton.setLayoutX(phoneNumberTypeComboBox.getLayoutX() + (xIncrement * .43));
                    initialMinusPhoneNumberButton.setVisible(false);
                }
            });
            removePhoneNumberButtons.add(newButton);

            //remove & add the removePhoneNumberButtons
            for(int i = 0; i < removePhoneNumberButtons.size(); i++)
            {
                tempPane.getChildren().remove(removePhoneNumberButtons.get(i));
            }

            for(int i = 0; i < removePhoneNumberButtons.size(); i++)
            {
                tempPane.getChildren().add(removePhoneNumberButtons.get(i));
            }

            //shift everything below this down accordingly
            initialAddPhoneNumberPlusButton.setLayoutY(initialAddPhoneNumberPlusButton.getLayoutY() + (yIncrement * 3));
            initialMinusPhoneNumberButton.setLayoutY(initialMinusPhoneNumberButton.getLayoutY() + (yIncrement * 3));
            emailTitleText.setY(emailTitleText.getY() + (yIncrement * 3));
            initialAddEmailPlusButton.setLayoutY(initialAddEmailPlusButton.getLayoutY() + (yIncrement * 3));
            initialMinusEmailButton.setLayoutY(initialMinusEmailButton.getLayoutY() + (yIncrement * 3));
            categoryTitleText.setY(categoryTitleText.getY() + (yIncrement * 3));
            categoryListView.setLayoutY(categoryListView.getLayoutY() + (yIncrement * 3));
            addCategoryPlusButton.setLayoutY(addCategoryPlusButton.getLayoutY() + (yIncrement * 3));
            removeCategoryMinusButton.setLayoutY(removeCategoryMinusButton.getLayoutY() + (yIncrement * 3));
            addCategoryButton.setLayoutY(addCategoryButton.getLayoutY() + (yIncrement * 3));
            addCategoryTextField.setLayoutY(addCategoryTextField.getLayoutY() + (yIncrement * 3));
            commentTitleText.setY(commentTitleText.getY() + (yIncrement * 3));
            commentTextArea.setLayoutY(commentTextArea.getLayoutY() + (yIncrement * 3));
            cancelButton.setLayoutY(cancelButton.getLayoutY() + (yIncrement * 3));
            addPersonButton.setLayoutY(addPersonButton.getLayoutY() + (yIncrement * 3));
            for(int i = 0; i < removeEmailButtons.size(); i++) //shift all the "-" buttons down
            {
                removeEmailButtons.get(i).setLayoutY(removeEmailButtons.get(i).getLayoutY() + (yIncrement * 3));
            }
            for(int i = 0; i < emailTextFields.size(); i++)
            {
                emailTextFields.get(i).setLayoutY(emailTextFields.get(i).getLayoutY() + (yIncrement * 3));
                emailComboBoxes.get(i).setLayoutY(emailComboBoxes.get(i).getLayoutY() + (yIncrement * 3));
            }

            //add another round of elements to the page
            //generate their properties
            TextField newPhoneNumberTxtBox = new TextField();newPhoneNumberTxtBox.setLayoutX(initialPhoneNumberTextField.getLayoutX());newPhoneNumberTxtBox.setLayoutY(initialPhoneNumberTextField.getLayoutY() + (yIncrement * 3) * phoneNumberTextFields.size());newPhoneNumberTxtBox.setPromptText("Phone Number");
            phoneNumberTextFields.add(newPhoneNumberTxtBox);
            ComboBox<String> newPhoneNumberTypeComboBox = new ComboBox<>(FXCollections.observableArrayList(phoneNumberTypes));newPhoneNumberTypeComboBox.setLayoutX(phoneNumberTypeComboBox.getLayoutX());newPhoneNumberTypeComboBox.setLayoutY(phoneNumberTypeComboBox.getLayoutY() + (yIncrement * 3) * phoneNumberComboBoxes.size());newPhoneNumberTypeComboBox.getSelectionModel().selectFirst();
            phoneNumberComboBoxes.add(newPhoneNumberTypeComboBox);

            //redraw
            for(int i = 0; i < phoneNumberTextFields.size(); i++)//delete
            {
                tempPane.getChildren().removeAll(phoneNumberTextFields.get(i), phoneNumberComboBoxes.get(i));
            }
            for(int i = 0; i < phoneNumberTextFields.size(); i++)//add
            {
                tempPane.getChildren().addAll(phoneNumberTextFields.get(i), phoneNumberComboBoxes.get(i));
            }

            if(phoneNumberTextFields.size() == 0)
            {
                phoneNumberTextFields.get(0).setLayoutY((phoneNumberTitleText.getY() + yIncrement * 2));
                phoneNumberComboBoxes.get(0).setLayoutY((phoneNumberTitleText.getY() + yIncrement * 2));
            }

            if(removePhoneNumberButtons.size() > 0)
            {
                initialAddPhoneNumberPlusButton.setLayoutX(phoneNumberTypeComboBox.getLayoutX() + (xIncrement * .43) + 30);
                initialMinusPhoneNumberButton.setVisible(true);
            }
        });
        initialMinusPhoneNumberButton.setOnAction(action ->
        {
            initialAddPhoneNumberPlusButton.setLayoutY(initialAddPhoneNumberPlusButton.getLayoutY() - (yIncrement * 3));
            emailTitleText.setY(emailTitleText.getY() - (yIncrement * 3));
            initialAddEmailPlusButton.setLayoutY(initialAddEmailPlusButton.getLayoutY() - (yIncrement * 3));
            initialMinusEmailButton.setLayoutY(initialMinusEmailButton.getLayoutY() - (yIncrement * 3));
            categoryTitleText.setY(categoryTitleText.getY() - (yIncrement * 3));
            categoryListView.setLayoutY(categoryListView.getLayoutY() - (yIncrement * 3));
            addCategoryPlusButton.setLayoutY(addCategoryPlusButton.getLayoutY() - (yIncrement * 3));
            removeCategoryMinusButton.setLayoutY(removeCategoryMinusButton.getLayoutY() - (yIncrement * 3));
            addCategoryButton.setLayoutY(addCategoryButton.getLayoutY() - (yIncrement * 3));
            addCategoryTextField.setLayoutY(addCategoryTextField.getLayoutY() - (yIncrement * 3));
            initialMinusPhoneNumberButton.setLayoutY(initialMinusPhoneNumberButton.getLayoutY() - (yIncrement * 3));
            commentTitleText.setY(commentTitleText.getY() - (yIncrement * 3));
            commentTextArea.setLayoutY(commentTextArea.getLayoutY() - (yIncrement * 3));
            cancelButton.setLayoutY(cancelButton.getLayoutY() - (yIncrement * 3));
            addPersonButton.setLayoutY(addPersonButton.getLayoutY() - (yIncrement * 3));

            //Shift all of the email elements down
            for(int i = 0; i < removeEmailButtons.size(); i++)
            {
                removeEmailButtons.get(i).setLayoutY(removeEmailButtons.get(i).getLayoutY() - (yIncrement * 3));
            }

            for(int i = 0; i < emailTextFields.size(); i++)
            {
                emailTextFields.get(i).setLayoutY(emailTextFields.get(i).getLayoutY() - (yIncrement * 3));
                emailComboBoxes.get(i).setLayoutY(emailComboBoxes.get(i).getLayoutY() - (yIncrement * 3));
            }

            //redraw
            //delete the minusPhoneNumberButtons nodes from the pane
            for(int i = 0; i < removePhoneNumberButtons.size(); i++)
            {
                tempPane.getChildren().remove(removePhoneNumberButtons.get(i));
            }
            for(int i = 0; i < phoneNumberTextFields.size(); i ++)
            {
                tempPane.getChildren().removeAll(phoneNumberTextFields.get(i), phoneNumberComboBoxes.get(i));
            }

            //remove nodes from their respective ArrayLists
            removePhoneNumberButtons.remove(removePhoneNumberButtons.size() - 1);
            phoneNumberTextFields.remove(phoneNumberTextFields.size() - 1);
            phoneNumberComboBoxes.remove(phoneNumberComboBoxes.size() - 1);

            //reAdd them to the pane
            for(int i = 0; i < removePhoneNumberButtons.size(); i++)
            {
                removePhoneNumberButtons.get(i).setLayoutY((phoneNumberTitleText.getY() + yIncrement * 1.5) + (yIncrement * 3 * i)); //reset the Y value
                tempPane.getChildren().add(removePhoneNumberButtons.get(i));
            }

            for(int i = 0; i < phoneNumberTextFields.size(); i++)
            {
                //reset Y values
                phoneNumberTextFields.get(i).setLayoutY((phoneNumberTitleText.getY() + yIncrement * 1.5) + (yIncrement * 3 * i));
                phoneNumberComboBoxes.get(i).setLayoutY(phoneNumberTextFields.get(i).getLayoutY());

                //add them back to the pane
                tempPane.getChildren().addAll(phoneNumberTextFields.get(i), phoneNumberComboBoxes.get(i));
            }

            if(removePhoneNumberButtons.size() == 0)
            {
                initialMinusPhoneNumberButton.setVisible(false);
                initialAddPhoneNumberPlusButton.setLayoutX(phoneNumberTypeComboBox.getLayoutX() + (xIncrement * .43));
            }
        });

        initialAddEmailPlusButton.setOnAction(action ->
        {
            //make a minus button where the plus button was
            Button newButton = new Button("-");newButton.setMinHeight(20);newButton.setMinWidth(20);newButton.setLayoutX(emailTypeComboBox.getLayoutX() + (xIncrement * .43));newButton.setLayoutY(initialAddEmailPlusButton.getLayoutY());newButton.setShape(new Circle(1.5));newButton.setMaxSize(25, 25);newButton.setMinSize(25, 25);
            newButton.setOnAction(function ->
            {
                //1)Shift everything up
                initialAddEmailPlusButton.setLayoutY(initialAddEmailPlusButton.getLayoutY() - (yIncrement * 3));
                initialMinusEmailButton.setLayoutY(initialMinusEmailButton.getLayoutY() - (yIncrement * 3));
                categoryTitleText.setY(categoryTitleText.getY() - (yIncrement * 3));
                categoryListView.setLayoutY(categoryListView.getLayoutY() - (yIncrement * 3));
                addCategoryPlusButton.setLayoutY(addCategoryPlusButton.getLayoutY() - (yIncrement * 3));
                removeCategoryMinusButton.setLayoutY(removeCategoryMinusButton.getLayoutY() - (yIncrement * 3));
                addCategoryButton.setLayoutY(addCategoryButton.getLayoutY() - (yIncrement * 3));
                addCategoryTextField.setLayoutY(addCategoryTextField.getLayoutY() - (yIncrement * 3));
                commentTitleText.setY(commentTitleText.getY() - (yIncrement * 3));
                commentTextArea.setLayoutY(commentTextArea.getLayoutY() - (yIncrement * 3));
                cancelButton.setLayoutY(cancelButton.getLayoutY() - (yIncrement * 3));
                addPersonButton.setLayoutY(addPersonButton.getLayoutY() - (yIncrement * 3));

                //Redraw Buttons
                for (int i = 0; i < removeEmailButtons.size(); i++)
                {
                    tempPane.getChildren().remove(removeEmailButtons.get(i));
                }
                int unluckyNode = 0;
                for (int i = 0; i < emailTextFields.size(); i++)
                {
                    tempPane.getChildren().removeAll(emailTextFields.get(i), emailComboBoxes.get(i));
                    if (emailTextFields.get(i).getLayoutY() == newButton.getLayoutY())
                    {
                        unluckyNode = i;
                    }
                }

                //delete the unlucky node from its respective arrayList
                emailTextFields.remove(emailTextFields.get(unluckyNode));
                emailComboBoxes.remove(emailComboBoxes.get(unluckyNode));

                removeEmailButtons.remove(newButton);

                //add nodes back to the pane
                for (int i = 0; i < removeEmailButtons.size(); i++)
                {
                    removeEmailButtons.get(i).setLayoutY((emailTitleText.getY() + yIncrement) + (yIncrement * 3 * i));
                    tempPane.getChildren().add(removeEmailButtons.get(i));
                }

                for (int i = 0; i < emailTextFields.size(); i++)
                {
                    emailTextFields.get(i).setLayoutY((emailTitleText.getY() + yIncrement) + (yIncrement * i * 3));
                    emailComboBoxes.get(i).setLayoutY((emailTitleText.getY() + yIncrement) + (yIncrement * i * 3));

                    tempPane.getChildren().addAll(emailTextFields.get(i), emailComboBoxes.get(i));
                }

                if(removeEmailButtons.size() == 0)
                {
                    initialAddEmailPlusButton.setLayoutX((emailTypeComboBox.getLayoutX()) + (xIncrement * .43));
                    initialMinusEmailButton.setVisible(false);
                }
            });
            removeEmailButtons.add(newButton);

            for(int i = 0; i < removeEmailButtons.size(); i++)
            {
                tempPane.getChildren().remove(removeEmailButtons.get(i));
            }

            for(int i = 0; i < removeEmailButtons.size(); i++)
            {
                tempPane.getChildren().add(removeEmailButtons.get(i));
            }

            //shift everything below, down
            initialAddEmailPlusButton.setLayoutY(initialAddEmailPlusButton.getLayoutY() + (yIncrement * 3));
            initialMinusEmailButton.setLayoutY(initialMinusEmailButton.getLayoutY() + (yIncrement * 3));
            categoryTitleText.setY(categoryTitleText.getY() + (yIncrement * 3));
            categoryListView.setLayoutY(categoryListView.getLayoutY() + (yIncrement * 3));
            addCategoryPlusButton.setLayoutY(addCategoryPlusButton.getLayoutY() + (yIncrement * 3));
            removeCategoryMinusButton.setLayoutY(removeCategoryMinusButton.getLayoutY() + (yIncrement * 3));
            addCategoryButton.setLayoutY(addCategoryButton.getLayoutY() + (yIncrement * 3));
            addCategoryTextField.setLayoutY(addCategoryTextField.getLayoutY() + (yIncrement * 3));
            commentTitleText.setY(commentTitleText.getY() + (yIncrement * 3));
            commentTextArea.setLayoutY(commentTextArea.getLayoutY() + (yIncrement * 3));
            cancelButton.setLayoutY(cancelButton.getLayoutY() + (yIncrement * 3));
            addPersonButton.setLayoutY(addPersonButton.getLayoutY() + (yIncrement * 3));

            //add another row of email elements to the page
            TextField newEmailTextField = new TextField();newEmailTextField.setLayoutX(emailTitleText.getX());newEmailTextField.setLayoutY((emailTitleText.getY() + yIncrement) + (emailTextFields.size() * 3 * yIncrement));newEmailTextField.setPromptText("Email");
            emailTextFields.add(newEmailTextField);
            ComboBox<String> newEmailComboBox = new ComboBox<>(FXCollections.observableArrayList(emailTypes));newEmailComboBox.setLayoutX(emailTitleText.getX() + (xIncrement * .8));newEmailComboBox.setLayoutY((emailTitleText.getY() + yIncrement) + (emailComboBoxes.size() * 3 * yIncrement));newEmailComboBox.setMinWidth(75);newEmailComboBox.setMaxWidth(75);newEmailComboBox.getSelectionModel().selectFirst();
            emailComboBoxes.add(newEmailComboBox);

            //add them to the pane
            //first remove everything to redraw
            for(int i = 0; i < emailTextFields.size(); i++)
            {
                tempPane.getChildren().removeAll(emailTextFields.get(i), emailComboBoxes.get(i));
            }
            //second, add them back to the pane
            for(int i = 0; i < emailTextFields.size(); i++)
            {
                tempPane.getChildren().addAll(emailTextFields.get(i), emailComboBoxes.get(i));
            }

            //if there's more than one removeEmailButtons, then displace the add button to the right
            if(removeEmailButtons.size() > 0)
            {
                initialAddEmailPlusButton.setLayoutX((phoneNumberTypeComboBox.getLayoutX() + (xIncrement * .43)) + 30);
                initialMinusEmailButton.setVisible(true);
            }
        });
        initialMinusEmailButton.setOnAction(action ->
        {
            //shift everything down
            initialAddEmailPlusButton.setLayoutY(initialAddEmailPlusButton.getLayoutY() - (yIncrement * 3));
            initialMinusEmailButton.setLayoutY(initialMinusEmailButton.getLayoutY() - (yIncrement * 3));
            categoryTitleText.setY(categoryTitleText.getY() - (yIncrement * 3));
            categoryListView.setLayoutY(categoryListView.getLayoutY() - (yIncrement * 3));
            addCategoryPlusButton.setLayoutY(addCategoryPlusButton.getLayoutY() - (yIncrement * 3));
            removeCategoryMinusButton.setLayoutY(removeCategoryMinusButton.getLayoutY() - (yIncrement * 3));
            addCategoryButton.setLayoutY(addCategoryButton.getLayoutY() - (yIncrement * 3));
            addCategoryTextField.setLayoutY(addCategoryTextField.getLayoutY() - (yIncrement * 3));
            commentTitleText.setY(commentTitleText.getY() - (yIncrement * 3));
            commentTextArea.setLayoutY(commentTextArea.getLayoutY() - (yIncrement * 3));
            cancelButton.setLayoutY(cancelButton.getLayoutY() - (yIncrement * 3));
            addPersonButton.setLayoutY(addPersonButton.getLayoutY() - (yIncrement * 3));

            //redraw
            for(int i = 0; i < removeEmailButtons.size(); i++)
            {
                tempPane.getChildren().remove(removeEmailButtons.get(i));
            }
            for(int i = 0; i < emailTextFields.size(); i++)
            {
                tempPane.getChildren().removeAll(emailComboBoxes.get(i), emailTextFields.get(i));
            }

            //remove nodes from their respective Arraylists
            removeEmailButtons.remove(removeEmailButtons.size() - 1);
            emailTextFields.remove(emailTextFields.size() - 1);
            emailComboBoxes.remove(emailComboBoxes.size() - 1);

            //reAdd it to the pane
            for(int i = 0; i < removeEmailButtons.size(); i++)
            {
                removeEmailButtons.get(i).setLayoutY((emailTitleText.getY() + yIncrement) + (yIncrement * 3 * i)); //reset the Y value
                tempPane.getChildren().add(removeEmailButtons.get(i));
            }

            for(int i = 0; i < emailTextFields.size(); i++)
            {
                emailTextFields.get(i).setLayoutY((emailTitleText.getY() + yIncrement) + (yIncrement * i * 3));
                emailComboBoxes.get(i).setLayoutY((emailTitleText.getY() + yIncrement) + (yIncrement * i * 3));

                tempPane.getChildren().addAll(emailTextFields.get(i), emailComboBoxes.get(i));
            }

            if(removeEmailButtons.size() == 0)
            {
                initialMinusEmailButton.setVisible(false);
                initialAddEmailPlusButton.setLayoutX((emailTypeComboBox.getLayoutX()) + (xIncrement * .43));
            }
        });

        addCategoryPlusButton.setOnAction(action ->
        {
            addCategoryPlusButton.setVisible(false);
            addCategoryButton.setVisible(true);
            addCategoryTextField.setVisible(true);
            removeCategoryMinusButton.setVisible(true);
            categoryListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        });
        removeCategoryMinusButton.setOnAction(action ->
        {
            addCategoryButton.setVisible(false);
            addCategoryTextField.setVisible(false);
            removeCategoryMinusButton.setVisible(false);
            addCategoryPlusButton.setVisible(true);
            categoryListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        });
        addCategoryButton.setOnAction(action ->
        {
            //Button variables
            boolean valuePresent = false;

            for(int i = 0; i < this.currentUser.getCategories().size(); i++) //search through the User's list of categories.
            {
                if(this.currentUser.getCategories().get(i).equals(addCategoryTextField.getText())) //If they already have a category under that name, send an error
                {
                    valuePresent = true;
                    break;
                }
                else //otherwise, add it to the list of categories & listView
                {
                    valuePresent = false;
                }
            }

            if(!valuePresent) //If the new value is unique, add it to the currentUser, listView, & .txt file
            {
                this.currentUser.addCategory(addCategoryTextField.getText());
                categoryListView.getItems().add(addCategoryTextField.getText());

                ArrayList<String> fileContents = new ArrayList<>();
                File readFile;
                BufferedReader br;
                PrintWriter writer;
                String str;
                String concatonatedCategories = "";

                try
                {
                    readFile = new File("C:\\Creeper\\" + this.currentUser.getUsername() + "\\accountInfo.txt");
                    br = new BufferedReader(new FileReader(readFile));

                    while((str = br.readLine()) != null) //save the file contents to a ArrayList
                    {
                        fileContents.add(str);
                    }

                    br.close(); //close BufferedReader

                    writer = new PrintWriter("C:\\Creeper\\" + this.currentUser.getUsername() + "\\accountInfo.txt");

                    for(int i = 0; i < this.currentUser.getCategories().size(); i++)
                    {
                        if(i + 1 == this.currentUser.getCategories().size())
                        {
                            concatonatedCategories = concatonatedCategories + this.currentUser.getCategory(i);
                        }
                        else
                        {
                            concatonatedCategories = concatonatedCategories + this.currentUser.getCategory(i) + ">~>";
                        }
                    }

                    writer.println(fileContents.get(0)); //write out the name
                    writer.println(fileContents.get(1)); //write out the username
                    writer.println(fileContents.get(2)); //write out the encrypted password
                    writer.println(concatonatedCategories); //write out the new categories instead of the old
                    writer.println(fileContents.get(4)); //write out the metric system preference
                    writer.close();
                } catch (Exception e)
                {
                    popUpErrorWindow("Error: " + e.toString());
                }
            }
            else
            {
                //Error message
                popUpErrorWindow("Category Already Exists.");
            }
            addCategoryTextField.clear();
        });

        cancelButton.setOnAction(action ->
        {
            tempStage.close();
        });

        addPersonButton.setOnAction(action ->
        {
            /* GUIDE TO THIS BUTTON:
             *--------------------------
             * Step 1: Write out a new Person to contacts.txt
             * Step 2: Check to make sure fields are valid
             */
            //local Button variables
            BufferedReader br;//BufferedReader to read in the contacts.txt file
            ArrayList<String> fileContents = new ArrayList<>();//ArrayList to store the contents of contacts.txt where each object stored in the ArrayList is its own contact
            PrintWriter writer;//PrintWriter to write out to the contacts.txt file
            String str;//a placeholder string to read in the files and save them to the ArrayList 'fileContents'
            String emailRegex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$"; //a regex to check for valid emails
            String phoneNumberRegex = "^\\(?\\d{3}\\)?\\s?[-.\\s]?\\d{3}[-.\\s]?\\d{4}$";
            String dateOfBirthRegex = "\\b(0[1-9]|1[0-2])/(0[1-9]|[1-2]\\d|3[0-1]|)/\\d{4}$";
            String dividerOne = "<<!>>"; //The string which divides the different sections that compose a Person
            String dividerTwo = "/&/"; //The string which divides different objects from one another (i.e. Address, PhoneNumber, Email)
            String dividerThree = ">~>"; //The string which divides the aspects of the different objects apart from eachother (i.e. Street Address, city, zipcode, etc)
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy"); //The format that the DateTime must fit

            //STEP 1:
            //take the array of address', phone numbers, & emails and save them to an arraylist to pass into the function
            ArrayList<PersonAddress> listOfAddresses = new ArrayList<>();
            for(int i = 0; i < streetAddressTextFields.size(); i++)
            {
                PersonAddress newAddress = new PersonAddress(streetAddressTextFields.get(i).getText(), cityTextFields.get(i).getText(), zipTextFields.get(i).getText(), stateComboBoxes.get(i).getSelectionModel().getSelectedItem().toString(), addressComboBoxes.get(i).getSelectionModel().getSelectedItem().toString());
                listOfAddresses.add(newAddress);
            }

            ArrayList<PersonPhoneNumber> listOfPhoneNumbers = new ArrayList<>();
            for(int i = 0; i < phoneNumberTextFields.size(); i++)
            {
                PersonPhoneNumber newPhoneNumber = new PersonPhoneNumber(phoneNumberTextFields.get(i).getText(), phoneNumberComboBoxes.get(i).getSelectionModel().getSelectedItem().toString());
                listOfPhoneNumbers.add(newPhoneNumber);
            }

            ArrayList<PersonEmail> listOfEmails = new ArrayList<>();
            for(int i = 0; i < emailTextFields.size(); i++)
            {
                PersonEmail newEmail = new PersonEmail(emailTextFields.get(i).getText(), emailComboBoxes.get(i).getSelectionModel().getSelectedItem().toString());
                listOfEmails.add(newEmail);
            }

            ArrayList<String> listOfSelectedCategories = new ArrayList<>(categoryListView.getSelectionModel().getSelectedItems());

            //STEP 2: Check everything to make sure the values are valid entries
            boolean everythingGucci = true;
            String errorMsg = "ERROR: the following values did not have valid entries...\n\n";

            //check DateTime value
            if(!dateOfBirth.getText().matches(dateOfBirthRegex))
            {
                everythingGucci = false;
                errorMsg = errorMsg + "Date of Birth is not in the right format.\nPlease use MM/DD/YYYY\n";
                dateOfBirth.setStyle("-fx-text-inner-color: red;");
            }

            //check the weightTextField
            for(int i = 0; i < weightTextField.getText().length(); i++)
            {
                if(!(((int)weightTextField.getText().charAt(i) > 47 && (int)weightTextField.getText().charAt(i) < 58) || (int)weightTextField.getText().charAt(i) == 46 || (int)weightTextField.getText().charAt(i) == 44))
                {
                    everythingGucci = false;
                    errorMsg = errorMsg + "Weight value must be a number!\n";
                    weightTextField.setStyle("-fx-text-inner-color: red;");
                }
            }
            errorMsg = errorMsg + "\n";
            //check the heightTextField
            for(int i = 0; i < heightTextField.getText().length(); i++)
            {
                if(!(((int)heightTextField.getText().charAt(i) > 47 && (int)heightTextField.getText().charAt(i) < 58) || (int)heightTextField.getText().charAt(i) == 46 || (int)heightTextField.getText().charAt(i) == 44))
                {
                    everythingGucci = false;
                    heightTextField.setStyle("-fx-text-inner-color: red;");
                    errorMsg = errorMsg + "Height value must be a number!\n";
                }
            }
            errorMsg = errorMsg + "\n";
            //check all zipcodes
            for(int i = 0; i < zipTextFields.size(); i++)
            {
                if(!(zipTextFields.get(i).getText().length() == 5 || zipTextFields.get(i).getText().length() == 0))
                {
                    everythingGucci = false;
                    errorMsg = errorMsg + "The " + (i+1) + " zipcode has an incorrect number of characters!\n";
                    zipTextFields.get(i).setStyle("-fx-text-inner-color: red;");
                }

                for(int j = 0; j < zipTextFields.get(i).getText().length(); j++)
                {
                    if(!(((int)zipTextFields.get(i).getText().charAt(j) > 47 && (int)zipTextFields.get(i).getText().charAt(j) < 58)))
                    {
                        everythingGucci = false;
                        errorMsg = errorMsg + "The " + (i+1) + " zipcode has an invalid character\n";
                        zipTextFields.get(i).setStyle("-fx-text-inner-color: red;");
                    }
                }
            }
            errorMsg = errorMsg + "\n";

            //check the phone numbers
            for(int i = 0; i < phoneNumberTextFields.size(); i++)
            {
                if(phoneNumberTextFields.get(i).getText().length() > 0)
                {
                    if (!phoneNumberTextFields.get(i).getText().matches(phoneNumberRegex))
                    {
                        everythingGucci = false;
                        errorMsg = errorMsg + "The " + (i + 1) + " phone number doesn't match!\nAssure it's in the correct format: ###-###-####\n";
                        phoneNumberTextFields.get(i).setStyle("-fx-text-inner-color: red;");
                    }
                }
            }
            errorMsg = errorMsg + "\n";
            //check the emails
            for(int i = 0; i < emailTextFields.size(); i++)
            {
                if(emailTextFields.get(i).getText().length() > 0) //make sure the email isn't blank
                {
                    if(!emailTextFields.get(i).getText().matches(emailRegex))
                    {
                        everythingGucci = false;
                        errorMsg = errorMsg + "The " + (i+1) + " email is not valid\n";
                        emailTextFields.get(i).setStyle("-fx-text-inner-color: red;");
                    }
                }
            }

            if(!everythingGucci)
            {
                popUpErrorWindow(errorMsg);

                //reset the color of the TextFields
                dateOfBirth.setStyle("-fx-text-inner-color: black");
                weightTextField.setStyle("-fx-text-inner-color: black;");
                heightTextField.setStyle("-fx-text-inner-color: black;");

                for(TextField tf : zipTextFields)
                {
                    tf.setStyle("-fx-text-inner-color: black;");
                }
                for(TextField tf : emailTextFields)
                {
                    tf.setStyle("-fx-text-inner-color: black;");
                }
                for(TextField tf : phoneNumberTextFields)
                {
                    tf.setStyle("-fx-text-inner-color: black;");
                }
            }
            else
            {
                //set the information inputted by the user to the newPerson object
                newPerson.setFirstName(firstNameTextField.getText());
                newPerson.setMiddleName(middleNameTextField.getText());
                newPerson.setLastName(lastNameTextField.getText());
                newPerson.setDateOfBirth(LocalDate.parse(dateOfBirth.getText(), formatter));
                newPerson.setWeight(weightTextField.getText());
                newPerson.setHeight(heightTextField.getText());
                newPerson.setAddresses(listOfAddresses);
                newPerson.setPhoneNumbers(listOfPhoneNumbers);
                newPerson.setEmails(listOfEmails);
                newPerson.setCategories(listOfSelectedCategories);
                newPerson.setComment(commentTextArea.getText().replaceAll("\\n", "<'r'>")); //replace the returns with `*` to correctly save the file

                //set if the Person is a male or female
                if(maleRadioButton.isSelected())
                {
                    newPerson.setSex("Male");
                }
                else if(femaleRadioButton.isSelected())
                {
                    newPerson.setSex("Female");
                }
                else
                {
                    newPerson.setSex("Empty");
                }
                //set Eye Color & Hair Color
                //Eye Color First
                if(eyecolorComboBox.getSelectionModel().getSelectedItem().equals("--"))
                {
                    newPerson.setEyeColor("Empty");
                }
                else
                {
                    newPerson.setEyeColor(eyecolorComboBox.getSelectionModel().getSelectedItem());
                }

                //and now Hair Color
                if(hairColorComboBox.getSelectionModel().getSelectedItem().equals("--"))
                {
                    newPerson.setHairColor("Empty");
                }
                else
                {
                    newPerson.setHairColor(hairColorComboBox.getSelectionModel().getSelectedItem());
                }

                //read in the current contacts.txt for future & write the new person out to the file
                try
                {
                    //read in the text and save it locally before overwriting it with the new information
                    br = new BufferedReader(new FileReader(filePath));//set the path to read in from

                    while((str = br.readLine()) != null)
                    {
                        fileContents.add(str);
                    }

                    br.close();//don't need this anymore
                    writer = new PrintWriter(filePath);//do need this now

                    for(int i = 0; i < fileContents.size(); i++)//loops through fileContents and writes it out to the file
                    {
                        writer.println(fileContents.get(i));
                    }

                    //concatenate string to contain all the information on the new Person
                    String personInformation = "";

                    personInformation = personInformation + newPerson.getLastName() + dividerThree
                            + newPerson.getFirstName() + dividerThree
                            + newPerson.getMiddleName() + dividerThree
                            + newPerson.getDateOfBirth().toString() + dividerThree
                            + newPerson.getSex() + dividerThree
                            + newPerson.getWeight() + dividerThree
                            + newPerson.getHeight() + dividerThree
                            + newPerson.getEyeColor() + dividerThree
                            + newPerson.getHairColor() + dividerThree
                            + newPerson.getComment() + dividerOne;

                    for(int i = 0; i < newPerson.getAddresses().size(); i++)
                    {
                        if(i + 1 == newPerson.getAddresses().size())
                        {
                            personInformation = personInformation + newPerson.getAddresses().get(i).getStreetAddress() + dividerThree
                                    + newPerson.getAddresses().get(i).getCity() + dividerThree
                                    + newPerson.getAddresses().get(i).getZipcode() + dividerThree
                                    + newPerson.getAddresses().get(i).getState() + dividerThree
                                    + newPerson.getAddresses().get(i).getType() + dividerOne;
                        }
                        else
                        {
                            personInformation = personInformation + newPerson.getAddresses().get(i).getStreetAddress() + dividerThree
                                    + newPerson.getAddresses().get(i).getCity() + dividerThree
                                    + newPerson.getAddresses().get(i).getZipcode() + dividerThree
                                    + newPerson.getAddresses().get(i).getState() + dividerThree
                                    + newPerson.getAddresses().get(i).getType() + dividerTwo;
                        }
                    }
                    for(int i = 0; i < newPerson.getPhoneNumbers().size(); i++)
                    {
                        if(i + 1 == newPerson.getPhoneNumbers().size())
                        {
                            personInformation = personInformation + newPerson.getPhoneNumbers().get(i).getPhoneNumber() + dividerThree
                                    + newPerson.getPhoneNumbers().get(i).getType() + dividerOne;
                        }
                        else
                        {
                            personInformation = personInformation + newPerson.getPhoneNumbers().get(i).getPhoneNumber() + dividerThree
                                    + newPerson.getPhoneNumbers().get(i).getType() + dividerTwo;
                        }
                    }
                    for(int i = 0; i < newPerson.getEmails().size(); i++)
                    {
                        if(i + 1 == newPerson.getEmails().size())
                        {
                            personInformation = personInformation + newPerson.getEmails().get(i).getEmailAddress() + dividerThree
                                    + newPerson.getEmails().get(i).getType() + dividerOne;
                        }
                        else
                        {
                            personInformation = personInformation + newPerson.getEmails().get(i).getEmailAddress() + dividerThree
                                    + newPerson.getEmails().get(i).getType() + dividerTwo;
                        }
                    }
                    for(int i = 0; i < newPerson.getCategories().size(); i++)
                    {
                        if(i + 1 == newPerson.getCategories().size())
                        {
                            personInformation = personInformation + newPerson.getCategories().get(i);
                        }
                        else
                        {
                            personInformation = personInformation + newPerson.getCategories().get(i) + dividerThree;
                        }
                    }

                    writer.println(personInformation); //write out all information on the new person on the next line in contacts.txt
                    writer.close();//don't need this either
                }catch(Exception e)
                {
                    popUpErrorWindow("Error: " + e.toString());
                }

                tempStage.close();
            }
;
        });

        tempPane.getChildren().addAll(nameTitleText, dateOfBirthTitleText, sexTitleText, eyeColorTitleText, hairColorTitleText, weightTitleText, weightHelperText, heightTitleText, heightHelperText, commentTitleText, addressTitleText, phoneNumberTitleText, emailTitleText, categoryTitleText, firstNameTextField, middleNameTextField, lastNameTextField, dateOfBirth, heightTextField, weightTextField, eyecolorComboBox, hairColorComboBox, addCategoryTextField, commentTextArea, cancelButton, addPersonButton, removeCategoryMinusButton, addCategoryPlusButton, addCategoryButton, initialMinusAddressButton, initialAddAddressPlusButton, initialMinusPhoneNumberButton, initialAddPhoneNumberPlusButton, initialMinusEmailButton, initialAddEmailPlusButton, categoryListView, phoneNumberTypeComboBox, emailTypeComboBox, maleRadioButton, femaleRadioButton);
        Scene tempScene = new Scene(sp);
        tempStage.setScene(tempScene);
        tempStage.showAndWait();
        return newPerson;
    }

    private void popUpErrorWindow(String l_error) //a pop up window to display any errors the user may encounter
    {
        Stage tempStage = new Stage();tempStage.initModality(Modality.APPLICATION_MODAL);tempStage.setTitle("Error Message");tempStage.setWidth(400);tempStage.setHeight(250);tempStage.setResizable(false);
        Pane tempPane = new Pane();

        Text errorception = new Text();
        try //set the icon image
        {
            tempStage.getIcons().add(new Image("https://c8.alamy.com/comp/J0MWT6/error-message-icon-with-exclamation-mark-J0MWT6.jpg"));
        }catch(Exception e)
        {
            errorception.setText("(local) ERROR: " + e.toString());errorception.setX((tempStage.getWidth() / 2) - (errorception.getLayoutBounds().getWidth() / 2));errorception.setY((tempStage.getHeight() / 2));errorception.setFill(Color.YELLOW);
        }

        Text errorText = new Text(l_error);
        errorText.applyCss();
        errorText.setLayoutX((tempStage.getWidth() / 2) - errorText.getLayoutBounds().getWidth() / 2);
        errorText.setLayoutY(tempStage.getHeight() - 200);
        errorText.setFill(Color.RED);

        tempStage.setHeight(tempStage.getHeight() + errorText.getLayoutBounds().getHeight());


        Button acknowledgeButton = new Button("OK");acknowledgeButton.setPrefWidth(50);acknowledgeButton.setPrefHeight(30);acknowledgeButton.setLayoutX((tempStage.getWidth() / 2) - (acknowledgeButton.getPrefWidth()));acknowledgeButton.setLayoutY(tempStage.getHeight() - acknowledgeButton.getPrefHeight() - 50);acknowledgeButton.setMinHeight(40);acknowledgeButton.setMinWidth(80);
        acknowledgeButton.setOnAction(action ->
        {
            tempStage.close();
        });

        tempPane.getChildren().addAll(errorText, errorception, acknowledgeButton);
        Scene tempScene = new Scene(tempPane);
        tempStage.setScene(tempScene);
        tempStage.showAndWait();
    }
}