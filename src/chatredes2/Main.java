/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package chatredes2;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author esteban
 */
public class Main extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
	Parent root = FXMLLoader.load(getClass().getResource("frmMain.fxml"));
	
	Scene scene = new Scene(root);
	
	stage.setScene(scene);
	stage.setTitle("Chat Redes - Esteban Rodríguez Betancourt B15512");
	stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
	launch(args);
    }
    
}
