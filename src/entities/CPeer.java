/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package entities;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 *
 * @author esteban
 */
public class CPeer implements Serializable{
    private String nickname;
    private String HostAddress;
    private Date lastPing;

    public CPeer(String nickname, String HostAddress) {
	this.nickname = nickname;
	this.HostAddress = HostAddress;
    }
    
    /**
     * Indica si el peer ha hecho algún ping en los
     * últimos 5 segundos.
     * @return 
     */
    public boolean isAlive(){
	Calendar c = new GregorianCalendar();
	c.setTime(lastPing);
	c.add(Calendar.SECOND, 5);
	return c.after(new Date());
    }
    
    public void updateTime(){
	lastPing = new Date();
    }

    public String getNickname() {
	return nickname;
    }

    public void setNickname(String nickname) {
	this.nickname = nickname;
    }

    public String getHostAddress() {
	return HostAddress;
    }

    public void setHostAddress(String HostAddress) {
	this.HostAddress = HostAddress;
    }

    public Date getLastPing() {
	return lastPing;
    }

    public void setLastPing(Date lastPing) {
	this.lastPing = lastPing;
    }
    
    
}
