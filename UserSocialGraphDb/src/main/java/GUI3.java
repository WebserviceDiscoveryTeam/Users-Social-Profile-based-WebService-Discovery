import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.util.*;

/**
 * Created by JINESH on 4/11/2017.
 */

public class GUI3 extends Application{

    Stage window;
    Scene scene2;
    Scene main_scene, foaf_scene, option_scene;
    Scene uddi_scene_1;
    static GraphDataBase graphDataBase = new GraphDataBase();


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        graphDataBase.createGraphDB();  // Start the process

        window = primaryStage;
        window.setTitle("Web Service DISCOVERY");

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(20);
        grid.setVgap(20);
        grid.setPadding(new Insets(20, 20, 20, 20));

        final ChoiceBox<String> choiceBox = new ChoiceBox<String>();
        ArrayList<String> userName = graphDataBase.getUserList();
        String temp = "";
        for (String cat:userName){
            temp = cat;
            choiceBox.getItems().add(cat);
        }
        choiceBox.setValue(temp);
        Button userNameButton = new Button("Submit");
        userNameButton.setOnAction(a -> {

            window.setTitle("Web Service DISCOVERY");
            graphDataBase.setCurrentUser(choiceBox.getValue());
            GridPane grid2 = new GridPane();
            grid2.setAlignment(Pos.CENTER);
            grid2.setHgap(20);
            Button uddiButton = new Button("Use UDDI Registry");
            Button foafButton = new Button("Use FOAF");

            // uddi button
            uddiButton.setOnAction(u1 -> {

                window.setTitle("Web Service DISCOVERY using UDDI Registry");
                final ChoiceBox<String> keyword = new ChoiceBox<String>();
                keyword.getItems().addAll("weather", "unit", "convert","service");
                keyword.setValue("weather");

                Button searchWSsButton = new Button("Search");
                FlowPane flowPane = new FlowPane();
                flowPane.setHgap(10);
                flowPane.setVgap(10);
                flowPane.setPadding(new Insets(20, 20, 20, 20));
                flowPane.getChildren().addAll(keyword, searchWSsButton);
                uddi_scene_1 = new Scene(flowPane, 550, 400);
                window.setScene(uddi_scene_1);
                window.show();

                searchWSsButton.setOnAction(u2 -> {

                    HashMap<String, ArrayList<String>> result = graphDataBase.uddiSearch(graphDataBase.getCurrentUser(), keyword.getValue());
                    String text = getUddiResult(result);


                    javafx.scene.control.TextArea textArea = new javafx.scene.control.TextArea();
                    textArea.setMaxSize(540, 450);
                    textArea.setText(text);
                    textArea.setStyle("-fx-highlight-fill: lightgray; -fx-highlight-text-fill: firebrick; -fx-font-size: 12px;");
                    textArea.setEditable(false);
                    textArea.setFocusTraversable(true);
                    textArea.setPrefSize(460, 300);

                    Button back = new Button("Back");
                    back.setOnAction(u3 -> {
                        window.setScene(main_scene);
                    });
                    flowPane.getChildren().addAll(textArea, back);
                });

            });


            // FOAF button
            foafButton.setOnAction(f1 ->{

                GridPane grid3 = new GridPane();
                grid3.setAlignment(Pos.CENTER);
                grid3.setHgap(20);
                HBox hb = new HBox();
                hb.setSpacing(10.0);


                Button landing_button = new Button("Add Web Service");
                Button search_button = new Button("Search Web Sercice");

                // Search Web Service
                search_button.setOnAction(e1 -> {

                    window.setTitle("Web Service DISCOVERY");
                    final ChoiceBox<String> foafchoiceBox = new ChoiceBox<String>();
                    foafchoiceBox.getItems().addAll("All Friends", "Best Friends");
                    foafchoiceBox.setValue("All Friends");

                    final ChoiceBox<String> choiceBox2 = new ChoiceBox<String>();
                    Set<String> category = graphDataBase.getCategory_set();
                    String temp2 = "";
                    for (String cat : category) {

                        temp2 = cat;
                        choiceBox2.getItems().add(cat);
                    }

                    choiceBox2.setValue(temp2);
                    choiceBox2.setPrefSize(155, 10);
                    Button searchWSsButton = new Button("Search");

                    final FlowPane flowPane2 = new FlowPane();
                    searchWSsButton.setOnAction(e2 -> {

                        String optionFOAF = foafchoiceBox.getValue();
                        if (optionFOAF.equals("All Friends")){
                            window.setTitle("Web Service DISCOVERY using All FOAF Social Graph");
                            graphDataBase.allFOAFflag = true;
                        }else{
                            window.setTitle("Web Service DISCOVERY using Best FOAF Social Graph");
                            graphDataBase.allFOAFflag = false;
                        }

                        String query = choiceBox2.getValue();
                        graphDataBase.setQuery(query);
                        graphDataBase.startAlgo(graphDataBase.getCurrentUser());

                        HashMap result = graphDataBase.getResultDict();
                        String text = getResult(result);


                        TextArea textArea = new TextArea();
                        textArea.setMaxSize(540, 400);
                        textArea.setText(text);
                        textArea.setStyle("-fx-highlight-fill: lightgray; -fx-highlight-text-fill: firebrick; -fx-font-size: 12px;");
                        textArea.setEditable(false);
                        textArea.setFocusTraversable(true);
                        textArea.setPrefSize(460, 300);

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

                                graphDataBase.updateUserPastInvocationList(webServiceName, graphDataBase.getCurrentUser());
                                alert.setContentText("Feedback Recorded!!! \nUser Past Invocation History Updated");

                            } else {
                                alert.setContentText("Feedback Recorded!!! \nUser Past Invocation History Updated");

                            }
                            alert.showAndWait();
                            window.setScene(main_scene);
                            window.show();
                        });
                        flowPane2.getChildren().addAll(textArea, label1, textField1, label2, textField2, submitFeedbackButton);
                    });

                    flowPane2.setHgap(10);
                    flowPane2.setVgap(10);
                    flowPane2.setPadding(new Insets(20, 20, 20, 20));
                    flowPane2.getChildren().addAll(foafchoiceBox, choiceBox2, searchWSsButton);
                    scene2 = new Scene(flowPane2, 550, 400);
                    window.setScene(scene2);
                    window.show();
                });


                // Add new Web Service by User
                landing_button.setOnAction(e1 -> {

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
                        window.setScene(main_scene);
                        window.show();

                        String webServiceName = textField1.getText();
                        String link = textField2.getText();
                        String category = textField3.getText();
                        int userCount = Integer.parseInt(textField4.getText());
                        graphDataBase.addWebServiceOfUser(graphDataBase.getCurrentUser(), webServiceName, link, category, userCount);

                    });
                    flowPane2.getChildren().addAll(label1, textField1, label2, textField2, label3,
                            textField3, label4, textField4, submitNewWSsButton);
                    scene2 = new Scene(flowPane2, 550, 400);
                    window.setScene(scene2);


                });

                hb.getChildren().addAll(landing_button, search_button);
                grid3.add(hb, 0, 2, 2, 1);
                foaf_scene = new Scene(grid3, 540,400);
                window.setScene(foaf_scene);
                window.show();
            });

            HBox hbButtons = new HBox();
            hbButtons.setSpacing(10.0);
            hbButtons.getChildren().addAll(uddiButton, foafButton);
            grid2.add(hbButtons, 0, 2, 2, 1);
            option_scene = new Scene(grid2, 540,400);
            window.setScene(option_scene);

        });

        grid.add(choiceBox, 0, 0);
        grid.add(userNameButton,1,0);
        main_scene = new Scene(grid, 540, 400);
        window.setScene(main_scene);
        window.show();

    }


    String getResult(HashMap result){

        String text = "";
        if (result == null) {

        }else if (!result.isEmpty()) {
            Iterator it = result.entrySet().iterator();
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

                }
                text += "Link :" + link + "\n";
                text += "Friend :" + friend + "\n \n";
                it.remove(); // avoids a ConcurrentModificationException

            }
        }

        return text;
    }



    String getUddiResult(HashMap result){
        String text = "";
        Iterator it = result.entrySet().iterator();
        while (it.hasNext()) {

            Map.Entry pair = (Map.Entry) it.next();
            String webServiceName = pair.getKey().toString();

            text += "Web Service Name: " + webServiceName + "\n";

            ArrayList propList = (ArrayList) pair.getValue();
            String link = propList.get(1).toString();

            text += "Link :" + link + "\n \n";
            it.remove(); // avoids a ConcurrentModificationException

        }
        System.out.println("Text = "+text);
        return text;
    }
}
