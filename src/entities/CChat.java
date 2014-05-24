/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.util.ArrayList;
import servers.Servidor;
import java.util.List;
import javafx.application.Platform;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;

/**
 *
 * @author esteban
 */
public class CChat {

    private final StringProperty chatID = new SimpleStringProperty();

    private final ListProperty<CMessage> messages;
    private final ListProperty<CPeer> peers;

    public CChat() {
	messages = new SimpleListProperty<>(FXCollections.observableArrayList(new ArrayList<CMessage>()));
	chatID.setValue(java.util.UUID.randomUUID().toString());
	peers = new SimpleListProperty<CPeer>(FXCollections.observableArrayList(new ArrayList<CPeer>()));
    }

    public CChat(String chatID, CMessage firstMsg, List<CPeer> peers) {
	this.chatID.setValue(chatID);
	this.messages = new SimpleListProperty<>(FXCollections.observableArrayList(new ArrayList<CMessage>()));
	this.messages.add(firstMsg);

	this.peers = new SimpleListProperty<>(FXCollections.observableArrayList(new ArrayList<CPeer>()));
	Platform.runLater(() -> {
	    mixPeers(firstMsg.getPeers());
	    mixPeers(peers);
	});
    }

    public void SendMessage(String message) {
	CMessage msg = new CMessage(this, message);
	Servidor s = Servidor.getInstance();
	s.SendMessage(msg);
    }

    public void ReceiveMessage(CMessage msg) {
	System.out.println(msg.getMessage());
	Platform.runLater(() -> {
	    mixPeers(msg.getPeers());
	    messagesProperty().add(msg);
	});
    }

    public String getChatID() {
	return chatID.get();
    }

    public void setChatID(String value) {
	chatID.set(value);
    }

    public StringProperty chatIDProperty() {
	return chatID;
    }

    public List<CMessage> getMessages() {
	return messages;
    }

    public void setMessages(List<CMessage> messages) {
	this.messages.setAll(messages);
    }

    public ListProperty<CMessage> messagesProperty() {
	return messages;
    }

    public List<CPeer> getPeers() {
	return peers;
    }

    public void setPeers(List<CPeer> peers) {
	this.peers.setAll(peers);
    }

    public ListProperty<CPeer> peersProperty() {
	return peers;
    }

    private void mixPeers(List<CPeer> lstPeers) {
	for (CPeer np : lstPeers) {
	    boolean encontrado = false;
	    for (CPeer op : getPeers()) {
		// Si ya estaba conectado se actualizan los datos:
		if (np.getHostAddress().equals(op.getHostAddress())) {
		    op.setNickname(np.getNickname());
		    encontrado = true;
		    break;
		}
		// Si soy yo mismo entonces no me agrego...
		if(np.getNickname().equals(Servidor.getInstance().getNickname())
			&& !np.getHostAddress().startsWith("127.")){
		    encontrado = true;
		}
	    }
	    if (!encontrado && !np.getHostAddress().startsWith("127.")) {
		peersProperty().add(np);
	    }
	}
    }

}
