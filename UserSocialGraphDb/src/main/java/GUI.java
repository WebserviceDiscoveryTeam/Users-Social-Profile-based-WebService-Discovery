import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

import java.util.*;

/**
 * Created by JINESH on 4/11/2017.
 */

public class GUI extends Application{

    Button button;
    Stage window;
    Scene scene;
    GraphDataBase graphDataBase = new GraphDataBase();
    TextArea textArea = new TextArea();



    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        window = primaryStage;
        window.setTitle("FOAF DISCOVERY");
        button = new Button();
        button.setText("Invoke");

        final ChoiceBox<String> choiceBox = new ChoiceBox<String>();


        graphDataBase.createGraphDB();  // Start the process

        Set<String> category = graphDataBase.getCategory_set();
        System.out.println(category);


        String temp = "";
        for (String cat:category){

            temp = cat;
            choiceBox.getItems().add(cat);
        }

        choiceBox.setValue(temp);
        choiceBox.setPrefSize(155, 10);


        final FlowPane flowPane = new FlowPane();
        flowPane.setHgap(10);
        flowPane.setVgap(10);
        flowPane.setPadding(new Insets(20, 20, 20, 20));
        flowPane.getChildren().addAll(choiceBox, button);


        button.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {

                String query = choiceBox.getValue();
                graphDataBase.setQuery(query);
                graphDataBase.startAlgo("Jinesh Dhruv");

                HashMap result = graphDataBase.getResultDict();
                System.out.println("Dict = " + result);
                String text = getResult(result);

                // TextArea
                flowPane.getChildren().remove(textArea);
                textArea.setMaxSize(450, 300);
                textArea.setText(text);
                textArea.setStyle("-fx-highlight-fill: lightgray; -fx-highlight-text-fill: firebrick; -fx-font-size: 12px;");

                // Text Box
                final TextField textField1 = new TextField();
                final TextField textField2 = new TextField();
                Label label1 = new Label("Your Choice:");
                Label label2 = new Label("Satisfied (yes/no):");

                flowPane.getChildren().removeAll(textField1, textField2, label1, label2);


                Button submit = new Button();
                submit.setText("Submit");


                // New layout for next scene
                final TextField textField3 = new TextField();
                textField3.setText("Feedback Recorded!!!!");
                final FlowPane flowPane1 = new FlowPane();
                flowPane1.setHgap(10);
                flowPane1.setVgap(10);
                flowPane1.setPadding(new Insets(20, 20, 20, 20));
                Button back = new Button("Go Back");
                flowPane1.getChildren().addAll(textField3, back);
                final Scene scene2 = new Scene(flowPane1, 540, 400);

                back.setOnAction(new EventHandler<ActionEvent>() {

                    public void handle(ActionEvent event) {
                        window.setScene(scene);
                        flowPane1.getChildren().removeAll(textField1, textField2);

                    }
                });


                submit.setOnAction(new EventHandler<ActionEvent>() {


                    public void handle(ActionEvent event) {
                        window.setScene(scene2);
                        String comment = textField2.getText();
                        String choice = textField1.getText();

                        if (comment.equals("yes")) {

                            graphDataBase.updateUserPastInvocationList(choice, "Jinesh Dhruv");
                        }
                    }
                });


                flowPane.getChildren().addAll(textArea, label1, textField1, label2, textField2, submit);
            }
        });

        scene = new Scene(flowPane,540,400);
        window.setScene(scene);
        window.show();

    }


    String getResult(HashMap result){

        String text = "";
        if (result == null) {

        }else if (!result.isEmpty()) {
            Iterator it = result.entrySet().iterator();
            System.out.println(result);
            while (it.hasNext()) {

                Map.Entry pair = (Map.Entry) it.next();
                String webServiceName = pair.getKey().toString();

                text += "Web Service Name: " + webServiceName + "\n";

                ArrayList propList = (ArrayList) pair.getValue();
                String description = propList.get(2).toString();
                String link = propList.get(3).toString();
                String user[] = propList.get(0).toString().split("-");
                String friend = "";
                for (int i = 0; i < user.length; i++) {
                    friend += user[i] + ", ";
                }

                if (friend.length() >= 2){
                    friend = friend.substring(0, friend.length() - 2);
                }else{
                    friend = friend.substring(0, friend.length() - 1);
                }

                if (!description.equals(" ")) {
//                System.out.println("Description: \n" + description);

                }
                text += "Link :" + link + "\n";
                text += "Friend :" + friend + "\n \n";
                it.remove(); // avoids a ConcurrentModificationException

            }
        }

        return text;
    }
}
