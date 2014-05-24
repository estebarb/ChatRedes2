/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author esteban
 */
public class CMessage implements Serializable {
    
    private String nickname;
    private Date timeSend;
    private String message;

    // Datos del chat: ID y peers
    private String chatID;
    private List<CPeer> peers;
    
    public CMessage() {
	
    }
    
    public CMessage(CChat chat, String message) {
	chatID = chat.getChatID();
	peers = chat.getPeers();
	this.message = message;
	timeSend = new Date();
    }
    
    public String getNickname() {
	return nickname;
    }
    
    public void setNickname(String nickname) {
	this.nickname = nickname;
    }
    
    public Date getTimeSend() {
	return timeSend;
    }
    
    public void setTimeSend(Date timeSend) {
	this.timeSend = timeSend;
    }
    
    public String getMessage() {
	return message;
    }
    
    public void setMessage(String message) {
	this.message = message;
    }
    
    public String getChatID() {
	return chatID;
    }
    
    public void setChatID(String chatID) {
	this.chatID = chatID;
    }
    
    public List<CPeer> getPeers() {
	return peers;
    }
    
    public void setPeers(List<CPeer> peers) {
	this.peers = peers;
    }
    
    private void writeObject(ObjectOutputStream oos) throws IOException {
	// default serialization 
//	oos.defaultWriteObject();
	// write the object
	oos.writeObject(getChatID());
	oos.writeObject(getMessage());
	oos.writeObject(getTimeSend());
	oos.writeObject(getNickname());
	
	List<CPeer> ips = new ArrayList<>();
	for (CPeer p : getPeers()) {
	    ips.add(p);
	}
	oos.writeObject(ips);
    }
    
    private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
	// Lee el objeto:
	setChatID((String) ois.readObject());
	setMessage((String) ois.readObject());
	setTimeSend((Date) ois.readObject());
	setNickname((String) ois.readObject());
	
	List<CPeer> ips;
	ips = (List<CPeer>) ois.readObject();
	
	setPeers(ips);
    }
}
