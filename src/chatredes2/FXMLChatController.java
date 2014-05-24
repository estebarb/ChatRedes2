/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatredes2;

import entities.CChat;
import entities.CMessage;
import entities.CPeer;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import servers.Servidor;

/**
 * FXML Controller class
 *
 * @author esteban
 */
public class FXMLChatController implements Initializable {

    private final ObjectProperty<CChat> chat = new SimpleObjectProperty<>();

    public CChat getChat() {
	return chat.get();
    }

    public void setChat(CChat value) {
	chat.set(value);
    }

    public ObjectProperty chatProperty() {
	return chat;
    }

    @FXML
    private TableView TableUsers;

    @FXML
    private TableColumn colUsers;

    @FXML
    private TableView TableMsgs;

    @FXML
    private TableColumn colSender;

    @FXML
    private TableColumn colMsgTime;

    @FXML
    private TableColumn colMessage;

    @FXML
    private Button cmdEnviar;

    @FXML
    private TextArea txtMensaje;

    //private final ObservableList<CMessage> mensajes = FXCollections.observableArrayList();
    //private final ObservableList<CPeer> peers = FXCollections.observableArrayList();
    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
	chatProperty().setValue(new CChat());

	colUsers.setCellValueFactory(new PropertyValueFactory("nickname"));
	TableUsers.setItems(getChat().peersProperty());
	//Bindings.bindBidirectional(TableUsers.itemsProperty(),
	//	getChat().messagesProperty());

	colSender.setCellValueFactory(new PropertyValueFactory("nickname"));
	colMsgTime.setCellValueFactory(new PropertyValueFactory("timeSend"));
	colMessage.setCellValueFactory(new PropertyValueFactory("message"));
	TableMsgs.setItems(getChat().messagesProperty());
	//Bindings.bindBidirectional(TableMsgs.itemsProperty(),
	//	getChat().messagesProperty());

	cmdEnviar.setOnAction((ActionEvent e) -> {
	    Enviar(e);
	});
    }

    public void UpdateBindings() {
	TableUsers.setItems(getChat().peersProperty());
	TableMsgs.setItems(getChat().messagesProperty());
    }

    @FXML
    /**
     * Envía un mensaje a todos los miembros de la conversación
     */
    private void Enviar(ActionEvent e) {
	Servidor srv = Servidor.getInstance();
	String texto = txtMensaje.getText();
	System.out.println("- click ->" + texto);
	CMessage msg = new CMessage(getChat(), texto);
	srv.SendMessage(msg);
    }

    /**
     * Permite invitar a otro usuario al chat actual
     *
     * @param peer
     */
    public void Añadir(CPeer peer) {
	getChat().getPeers().add(peer);
    }

}
