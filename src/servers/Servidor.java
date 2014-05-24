/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servers;

import chatredes2.FrmMainController;
import entities.CChat;
import entities.CMessage;
import entities.CPeer;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Contiene los datos del usuario actual, además de encargarse del anuncio por
 * broadcast, transmisión de mensajes, mantener la lista de usuarios conectados
 * y la actualización de los chats.
 *
 * @author esteban
 */
public class Servidor {

    private final StringProperty nickname = new SimpleStringProperty();

    public CPeer getMe() {
	String myHostname = "127.0.0.1";
	try {
	    myHostname = InetAddress.getLocalHost().getHostAddress();
	} catch (UnknownHostException ex) {
	    Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
	}
	return new CPeer(getNickname(), myHostname);
    }

    public String getNickname() {
	return nickname.get();
    }

    public void setNickname(String value) {
	nickname.set(value);
    }

    public StringProperty nicknameProperty() {
	return nickname;
    }

    private final ListProperty<CPeer> peers = new SimpleListProperty<>(FXCollections.observableArrayList(new ArrayList<CPeer>()));

    public ObservableList getPeers() {
	return peers.get();
    }

    public void setPeers(ObservableList value) {
	peers.set(value);
    }

    public ListProperty peersProperty() {
	return peers;
    }

    private static final int PuertoChat = 13345;
    private static final int PuertoBroadcast = 13340;
    private ConcurrentHashMap<String, CChat> chats;

    private ServidorBroadcast servidorBroadcast;
    private ClienteBroadcast clienteBroadcast;
    private ServidorChat servidorChat;

    private static Servidor instance;

    public CChat currentChat = null;

    private Servidor() {
	nickname.set("");

	chats = new ConcurrentHashMap<>();
	servidorBroadcast = new ServidorBroadcast(PuertoBroadcast);
	servidorChat = new ServidorChat(PuertoChat);
	clienteBroadcast = new ClienteBroadcast(PuertoBroadcast);

	Timer timer = new Timer("BroadcastPing", true);
	timer.scheduleAtFixedRate(servidorBroadcast, 1000, 1000);

	servidorChat.setDaemon(true);
	servidorChat.start();

	clienteBroadcast.setDaemon(true);
	clienteBroadcast.start();
    }

    public synchronized static Servidor getInstance() {
	if (instance == null) {
	    instance = new Servidor();
	}
	return instance;
    }

    /**
     * Envia un mensaje de chat al peer especificado
     *
     * @param peer
     * @param msg
     */
    public void SendMessage(CPeer peer, CMessage msg) {
	// Añade información adicional al paquete:
	msg.setNickname(getNickname());
	try {
	    Socket socket = new Socket(peer.getHostAddress(), PuertoChat);
	    // Se inicializa:
	    ObjectOutputStream s = new ObjectOutputStream(socket.getOutputStream());
	    s.flush(); //Envia informacion de encabezado
	    ObjectInputStream e = new ObjectInputStream(socket.getInputStream());
	    DebugMsg(msg);
	    s.writeObject(msg);
	    s.flush();
	    socket.close();
	} catch (IOException ex) {
	    Logger.getLogger(CPeer.class.getName()).log(Level.SEVERE, null, ex);
	}
    }

    private void DebugMsg(CMessage msg) {
	System.out.println("Debug mensaje:");
	System.out.println("ID:\t" + msg.getChatID());
	System.out.println("MSG:\t" + msg.getMessage());
	System.out.println("NICK:\t" + msg.getNickname());
	System.out.println("NumPeers:\t" + msg.getPeers().size());
	if (msg.getPeers() != null) {
	    for (CPeer p : msg.getPeers()) {
		System.out.println("Peer:\t" + p.getNickname() + "(" + p.getHostAddress() + ")");
	    }
	}
    }

    /**
     * Envía un mensaje a todos los peers asignados en el mensaje
     *
     * @param msg
     */
    public void SendMessage(CMessage msg) {
	String myHostname = "";
	try {
	    myHostname = InetAddress.getLocalHost().getHostAddress();
	} catch (UnknownHostException ex) {
	    myHostname = "";
	    Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
	}

	//ReceiveMessage(msg);
	for (CPeer peer : peers) {
		SendMessage(peer, msg);
	};
    }

    /**
     * Envía el mensaje recibido al chat correspondiente, o bien crea un nuevo
     * chat.
     *
     * @param msg
     */
    public synchronized void ReceiveMessage(CMessage msg) {
	DebugMsg(msg);
	CChat chat;
	if (!chats.containsKey(msg.getChatID())) {
	    System.out.println("Creando nuevo chat");
	    chat = new CChat(msg.getChatID(), msg, msg.getPeers());
	    chats.put(chat.getChatID(), chat);
	    controlador.OpenChat(msg.getChatID());
	} else {
	    System.out.println("Actualizando chat");
	    chat = chats.get(msg.getChatID());
	    chat.ReceiveMessage(msg);
	}
    }

    public ConcurrentHashMap<String, CChat> getChats() {
	return chats;
    }

    public void setChats(ConcurrentHashMap<String, CChat> chats) {
	this.chats = chats;
    }

    public synchronized void RefreshPeer(String address, String nickname) {
	Platform.runLater(() -> {
	    boolean encontrado = false;
	    for(int i = 0; i < peersProperty().getSize(); i++){
		CPeer p = (CPeer) peersProperty().get(i);
		if (p.getHostAddress().equals(address)) {
		    encontrado = true;
		    p.updateTime();
		    p.setNickname(nickname);
		    peersProperty().set(i, p);
		}
	    }
	    if (!encontrado) {
		CPeer newPeer = new CPeer(nickname, address);
		peersProperty().add(newPeer);
	    }
	});
	/*
	 for (CPeer p : peers) {
	 System.out.println(p.getNickname() + " " + p.getHostName());
	 }
	 System.out.println("-------------");
	 */
    }

    private FrmMainController controlador;

    public void setGUI(FrmMainController aThis) {
	controlador = aThis;
    }

}
