package ch.epfl.tchu.gui;

import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.text.DecimalFormat;
import java.text.ParsePosition;

public class Launcher extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        LauncherViewCreator.createLauncherView(primaryStage);
    }
    
    @Override
    public void stop()
    {
        System.exit(0);
    }
}

final class LauncherViewCreator {

    private LauncherViewCreator() {
    }
//todo javadoc
    public static void createLauncherView(Stage primaryStage) {

        /// ***Main scene UI***
        VBox box = new VBox();
        TabPane tabPane = new TabPane();

        box.getChildren().add(tabPane);

        Scene scene = new Scene(box, 680, 280);
        Stage stage = new Stage(StageStyle.DECORATED);

        stage.setScene(scene);
        stage.setTitle("tChu");
        stage.initModality(Modality.NONE);
        stage.setResizable(false);
        stage.centerOnScreen();

        /// ***Server Tab UI***
        GridPane serverGrid = new GridPane();
        serverGrid.setAlignment(Pos.TOP_CENTER);
        serverGrid.setHgap(10);
        serverGrid.setVgap(10);
        serverGrid.setPadding(new Insets(10, 10, 10, 10));

        //Server tab title
        Text sceneTitleS = new Text("Welcome to the Tchu Game");
        sceneTitleS.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        GridPane.setHalignment(sceneTitleS, HPos.CENTER);
        serverGrid.add(sceneTitleS, 0, 0, 10, 1);

        //Player number slider
        Slider nbPlayers = new Slider(2, 5, 2);
        nbPlayers.setBlockIncrement(1.0);
        nbPlayers.setMajorTickUnit(1.0);
        nbPlayers.setMinorTickCount(0);
        nbPlayers.setSnapToTicks(true);
        nbPlayers.setShowTickMarks(true);
        serverGrid.add(nbPlayers, 1, 2);

        //Player number slider text label
        Label nbPlayersLabel = new Label("number of players : ");
        nbPlayersLabel.setLabelFor(nbPlayers);
        serverGrid.add(nbPlayersLabel, 0, 2);

        //Player number slider value label
        Label nbPlayerValueLabel = new Label(Long.toString(Math.round(nbPlayers.getValue())));
        nbPlayerValueLabel.setLabelFor(nbPlayers);
        serverGrid.add(nbPlayerValueLabel, 2, 2);

        //Port text box warning label
        Text portWarntextS = new Text("WARNING: TCP port 0 is reserved !");
        portWarntextS.setFont(Font.font(Font.getDefault().getName(), FontWeight.BOLD, FontPosture.REGULAR, Font.getDefault().getSize()));
        portWarntextS.setFill(Color.RED);
        portWarntextS.setVisible(false);
        serverGrid.add(portWarntextS, 4, 1);

        //Port text box
        TextField portTextS = new TextField();
        serverGrid.add(portTextS, 4, 2);

        //Port text box label
        Label portLabelS = new Label("Server port : ");
        portLabelS.setLabelFor(portTextS);
        serverGrid.add(portLabelS, 3, 2);

        //Launch server button
        Button btnServer = new Button("Launch Server");
        serverGrid.add(btnServer, 4, 3);
        
        //Port text change event handler
        portTextS.setTextFormatter(new TextFormatter<>(c -> {

            String s = c.getControlNewText();
            ParsePosition parsePos = new ParsePosition(0);

            if (s.isEmpty()) {
                portWarntextS.setVisible(false);
                return c;
            }
            Number n = new DecimalFormat().parse(s, parsePos);

            if (n == null || parsePos.getIndex() < s.length()) {
                portWarntextS.setVisible(false);
                return null;
            } else {
                portWarntextS.setVisible(n.intValue() == 0);
                btnServer.setDisable(n.intValue() == 0);
                return c;
            }
        }));
        
        //Player number slider value change event handler
        nbPlayers.valueProperty().addListener((obs, oV, nV) -> {
            nbPlayers.setValue(Math.round(nV.doubleValue()));
            nbPlayerValueLabel.setText(Long.toString(Math.round(nV.doubleValue())));
        });
        
        //Server button onClick event handler
        btnServer.setOnMouseClicked(e -> {
            stage.setIconified(true);
            String[] args = new String[]{nbPlayerValueLabel.getText(), portTextS.getText()};
            ServerMain server = new ServerMain();
            try {
                server.init(args);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            server.start(new Stage());
            
        });

        Tab serverTab = new Tab("Server", serverGrid);
        serverTab.setClosable(false);


        /// ***Client Tab UI***
        GridPane clientGrid = new GridPane();
        clientGrid.setAlignment(Pos.TOP_CENTER);
        clientGrid.setHgap(10);
        clientGrid.setVgap(10);
        clientGrid.setPadding(new Insets(10, 10, 10, 10));

        //Client tab title
        Text sceneTitleC = new Text("Bienvenue dans tCHu !");
        sceneTitleC.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        GridPane.setHalignment(sceneTitleC, HPos.CENTER);
        clientGrid.add(sceneTitleC, 0, 0, 4, 1);

        //Name text box
        TextField nameText = new TextField();
        clientGrid.add(nameText, 1, 1);

        //Name text box label
        Label nameLabel = new Label("Nom : ");
        nameLabel.setLabelFor(nameText);
        GridPane.setHalignment(nameLabel, HPos.RIGHT);
        clientGrid.add(nameLabel, 0, 1);

        //Hostname text box
        TextField hostnameText = new TextField();
        clientGrid.add(hostnameText, 1, 2);

        //Hostname text box label
        Label hostnameLabel = new Label("Nom d'hôte : ");
        hostnameLabel.setLabelFor(hostnameText);
        GridPane.setHalignment(hostnameLabel, HPos.RIGHT);
        clientGrid.add(hostnameLabel, 0, 2);

        //Port text box warning label
        Text portWarntextC = new Text("ATTENTION: le port TCP 0 est réservé !");
        portWarntextC.setFont(Font.font(Font.getDefault().getName(), FontWeight.BOLD, FontPosture.REGULAR, Font.getDefault().getSize()));
        portWarntextC.setFill(Color.RED);
        portWarntextC.setVisible(false);
        clientGrid.add(portWarntextC, 3, 1);

        //Port text box
        TextField portTextC = new TextField();        
        clientGrid.add(portTextC, 3, 2);

        //Port text box label
        Label portLabelC = new Label("Port : ");
        portLabelC.setAlignment(Pos.CENTER_LEFT);
        portLabelC.setLabelFor(portTextC);
        GridPane.setHalignment(portLabelC, HPos.RIGHT);
        clientGrid.add(portLabelC, 2, 2);

        //Run client button
        Button btnClient = new Button("Connect to server");
        clientGrid.add(btnClient, 3, 3);
        
        //Port text change event handler
        portTextC.setTextFormatter(new TextFormatter<>(c -> {

            String s = c.getControlNewText();
            ParsePosition parsePos = new ParsePosition(0);

            if (s.isEmpty()) {
                portWarntextC.setVisible(false);
                return c;
            }
            Number n = new DecimalFormat().parse(s, parsePos);

            if (n == null || parsePos.getIndex() < s.length()) {
                portWarntextC.setVisible(false);
                return null;
            } else {
                portWarntextC.setVisible(n.intValue() == 0);
                return c;
            }
        }));
        
      //Client button onClick event handler
        btnClient.setOnMouseClicked(e -> {
            String[] args = new String[]{hostnameText.getText(), portTextC.getText(), nameText.getText()};
            ClientMain client = new ClientMain();
            
            client.init(args);
           
            client.start(new Stage());
            btnClient.setText("Waiting on server");
            
            serverTab.setDisable(true);
            btnClient.setDisable(true);
            
            hostnameText.setDisable(true);
            portTextC.setDisable(true);
            nameText.setDisable(true);
            
            stage.setIconified(true);
        });

        Tab clientTab = new Tab("Client", clientGrid);
        clientTab.setClosable(false);

        tabPane.getTabs().add(clientTab);
        tabPane.getTabs().add(serverTab);

        stage.show();
        
    }
}
