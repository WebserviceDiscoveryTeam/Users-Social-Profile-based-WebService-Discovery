import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

import java.util.*;

/**
 * Created by JINESH on 4/11/2017.
 */

public class GUI2 extends Application{

    Stage window;
    Scene scene1, scene2;

    GraphDataBase graphDataBase = new GraphDataBase();


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        graphDataBase.createGraphDB();  // Start the process

        window = primaryStage;
        window.setTitle("FOAF DISCOVERY");

        Button landing_button = new Button("Add Web Service");
        Button search_button = new Button("Search Web Sercice");
        search_button.setOnAction(e1 -> {

            final ChoiceBox<String> choiceBox = new ChoiceBox<String>();

            Set<String> category = graphDataBase.getCategory_set();
//            System.out.println(category);


            String temp = "";
            for (String cat:category){

                temp = cat;
                choiceBox.getItems().add(cat);
            }

            choiceBox.setValue(temp);
            choiceBox.setPrefSize(155, 10);
            Button searchWSsButton = new Button("Search");
            final FlowPane flowPane2 = new FlowPane();
            searchWSsButton.setOnAction(e2 -> {

                String query = choiceBox.getValue();
                graphDataBase.setQuery(query);
                graphDataBase.startAlgo("Jinesh Dhruv");

                HashMap result = graphDataBase.getResultDict();
                String text = getResult(result);


                TextArea textArea = new TextArea();
                textArea.setMaxSize(540, 400);
                textArea.setText(text);
                textArea.setStyle("-fx-highlight-fill: lightgray; -fx-highlight-text-fill: firebrick; -fx-font-size: 12px;");
                textArea.setEditable(false);
                textArea.setFocusTraversable(true);

                // Text Box
                final TextField textField1 = new TextField();
                final TextField textField2 = new TextField();
                Label label1 = new Label("Your Choice:");
                Label label2 = new Label("Satisfied (yes/no):");


                Button submitFeedbackButton = new Button("Submit FeedBack");
                submitFeedbackButton.setOnAction(e3 -> {

                    String user_feedback = textField2.getText();
                    String webServiceName = textField1.getText();

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setHeaderText(null);
                    alert.setTitle("Notification");

                    window.hide();
                    if (user_feedback.equals("yes")) {

                        graphDataBase.updateUserPastInvocationList(webServiceName, "Jinesh Dhruv");
                        alert.setContentText("Feedback Recorded!!! \nUser Past Invocation History Updated");

                    } else {
                        alert.setContentText("Feedback Recorded!!! \nUser Past Invocation History Updated");

                    }
                    alert.showAndWait();
                    window.setScene(scene1);
                    window.show();
                });
                flowPane2.getChildren().addAll(textArea, label1, textField1, label2, textField2, submitFeedbackButton);
            });

            flowPane2.setHgap(10);
            flowPane2.setVgap(10);
            flowPane2.setPadding(new Insets(20, 20, 20, 20));
            flowPane2.getChildren().addAll(choiceBox, searchWSsButton);
            scene2 = new Scene(flowPane2, 550, 400);
            window.setScene(scene2);
            window.show();
        });


        // Add new Web Service by User
        landing_button.setOnAction(e1 ->{

            // Text Box
            final TextField textField1 = new TextField();
            final TextField textField2 = new TextField();
            final TextField textField3 = new TextField();
            final TextField textField4 = new TextField();
            Label label1 = new Label("Web Service Name:");
            Label label2 = new Label("Link:");
            Label label3 = new Label("Category:");
            Label label4 = new Label("Used Count:");
            Button submitNewWSsButton = new Button("Submit");

            final FlowPane flowPane2 = new FlowPane();
            flowPane2.setHgap(10);
            flowPane2.setVgap(10);
            flowPane2.setPadding(new Insets(20, 20, 20, 20));

            submitNewWSsButton.setOnAction(e2 -> {

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText(null);
                alert.setTitle("Notification");
                window.hide();
                alert.setContentText("Feedback Recorded!!! \nUser Past Invocation History Updated");
                alert.showAndWait();
                window.setScene(scene1);
                window.show();

                String webServiceName = textField1.getText();
                String link = textField2.getText();
                String category = textField3.getText();
                int userCount = Integer.parseInt(textField4.getText());
                graphDataBase.addWebServiceOfUser("Jinesh Dhruv", webServiceName, link, category, userCount);

            });
            flowPane2.getChildren().addAll(label1, textField1, label2, textField2, label3,
                    textField3,label4, textField4,submitNewWSsButton);
            scene2 = new Scene(flowPane2, 550, 400);
            window.setScene(scene2);


        });

        final FlowPane flowPane = new FlowPane();
        flowPane.setHgap(10);
        flowPane.setVgap(10);
        flowPane.setPadding(new Insets(20, 20, 20, 20));
        flowPane.getChildren().addAll(landing_button, search_button);
        scene1 = new Scene(flowPane,540,400);
        window.setScene(scene1);
        window.show();
    }


    String getResult(HashMap result){

        String text = "";
        if (result == null) {

        }else if (!result.isEmpty()) {
            Iterator it = result.entrySet().iterator();
//            System.out.println(result);
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
