package ch.epfl.tchu.gui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Launcher extends Application {
    public static final int PLAYER_NUMBER = 2;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        LauncherViewCreator.createLauncherView(primaryStage);
    }
}

final class LauncherViewCreator {

    private LauncherViewCreator() {
    }

    public static void createLauncherView(Stage primaryStage) {
        VBox box = new VBox();
        TabPane tabPane = new TabPane();
        
        Text sceneTitleC = new Text("Welcome to the Tchu Game");
        sceneTitleC.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        Text sceneTitleS = new Text("Welcome to the Tchu Game");
        sceneTitleS.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        
        

        //Server Tab UI
        GridPane serverGrid = new GridPane();
        serverGrid.setAlignment(Pos.CENTER);
        serverGrid.setHgap(10);
        serverGrid.setVgap(10);
        serverGrid.setPadding(new Insets(10, 10, 10, 10));
        
        serverGrid.add(sceneTitleS, 0, 0, 2, 1);
        
        Label text = new Label("number of players : ");
        serverGrid.add(text, 0, 2);
        
        Slider nbPlayers = new Slider(2, 5, 2);
        nbPlayers.setBlockIncrement(1.0);
        nbPlayers.setMajorTickUnit(1.0);
        nbPlayers.setMinorTickCount(0);
        nbPlayers.setSnapToTicks(true);
        nbPlayers.setShowTickMarks(true);
        nbPlayers.setShowTickLabels(true);
        serverGrid.add(nbPlayers, 1, 2);
        
        Label nbPlayersText = new Label(Long.toString(Math.round(nbPlayers.getValue())));
        nbPlayers.valueProperty().addListener((obs,oV,nV) -> {nbPlayers.setValue(Math.round(nV.doubleValue())); nbPlayersText.setText(Long.toString(Math.round(nV.doubleValue())));});
        serverGrid.add(nbPlayersText, 3, 2);
        
        Tab serverTab = new Tab("Server", serverGrid);

        //Client Tab UI
        GridPane clientGrid = new GridPane();
        clientGrid.setAlignment(Pos.CENTER);
        clientGrid.setHgap(10);
        clientGrid.setVgap(10);
        clientGrid.setPadding(new Insets(10, 10, 10, 10));
        
        clientGrid.add(sceneTitleC, 0, 0, 2, 1);
        
        Label name = new Label("Name : ");
        clientGrid.add(name, 0, 1);
        TextField nameText = new TextField();
        clientGrid.add(nameText, 1, 1);
        
        Label hostName = new Label("HostName : ");
        clientGrid.add(hostName, 0, 2);
        TextField hostNameText = new TextField();
        clientGrid.add(hostNameText, 1, 2);
        
        Label port = new Label("Port : ");
        clientGrid.add(port, 2, 2);
        TextField portText = new TextField();
        clientGrid.add(portText, 3, 2);
        
        Tab clientTab = new Tab("Client", clientGrid);
        
        tabPane.getTabs().add(clientTab);
        tabPane.getTabs().add(serverTab);
        
        Button btnServer = new Button("Launch Server");
        btnServer.setOnMouseClicked(e -> {
            String[] args = new String[]{nbPlayersText.getText()};
            ServerMain server = new ServerMain();
            try {
                server.init(args);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            server.start(new Stage());
        });
        
        Button btnClient = new Button("Connect to server");
        btnClient.setOnMouseClicked(e -> {
            String[] args = new String[]{hostNameText.getText(), portText.getText(), nameText.getText()};
            ClientMain client = new ClientMain();
            try {
                client.init(args);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            client.start(new Stage());
            //ClientMain.main(args);
        });
                
        serverGrid.add(btnServer, 4, 6);    
        clientGrid.add(btnClient, 2, 4);

        box.getChildren().add(tabPane);
        Scene scene = new Scene(box, 680, 280);
        Stage stage = new Stage(StageStyle.UTILITY);
        stage.setScene(scene);
        stage.setTitle("tChu");
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(primaryStage);
        stage.setX(500);
        stage.setY(400);
        stage.show();
    }
}
