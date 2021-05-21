package ch.epfl.tchu.gui;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class Launcher extends Application {
    private final String DEFAULT_HOSTNAME = "localhost";
    private final int DEFAULT_PORT = 5108;
    
    private final int DEFAULT_PLAYERNUMBER = 2;
    private final String DEFAULT_P1NAME = "Ada";
    private final String DEFAULT_P2NAME = "Charles";
    private final String Default_PXNAME = "Player_";
    
    
    
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // TODO Auto-generated method stub
        
    }

}

final class LauncherViewCreator{
    
    private LauncherViewCreator() {}
    
    public static Node createLauncherView() {
        VBox launcherView = new VBox();
        //launcherView.setStyle(null); 
        
        
        return launcherView;
    }
    
}