/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatredes2;

import entities.CChat;
import entities.CMessage;
import entities.CPeer;
import java.io.IOException;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import servers.Servidor;

/**
 *
 * @author esteban
 */
public class ChatTab extends VBox {

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

    public ChatTab(CChat chat) {
	super();
	this.chat.set(chat);
    }

    public ChatTab(String string, CChat chat) {
	FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
		"FXMLChat.fxml"));
	FXMLChatController ctrl = new FXMLChatController();
	fxmlLoader.setController(ctrl);
	fxmlLoader.setRoot(this);

	try {
	    fxmlLoader.load();
	} catch (IOException exception) {
	    throw new RuntimeException(exception);
	}
	//BuildGUI();
	this.setChat(chat);
	ctrl.setChat(chat);
	Bindings.bindBidirectional(chatProperty(), ctrl.chatProperty());
	ctrl.UpdateBindings();
	//ctrl.initialize(null, null);
	//InitGUI();
    }

    /**
     * Permite invitar a otro usuario al chat actual
     */
    public void Añadir(CPeer peer) {
	getChat().peersProperty().add(peer);
	//getChat().getPeers().add(peer);
    }
}
