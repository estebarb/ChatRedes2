/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servers;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author esteban
 */
class ServidorBroadcast extends TimerTask {

    private int Puerto;

    public ServidorBroadcast(int PuertoBroadcast) {
	Puerto = PuertoBroadcast;
    }

    @Override
    public void run() {
	SendPing();
    }

    public void SendPing() {
	try {
	    DatagramSocket socket;
	    InetAddress grupo = InetAddress.getByName("224.255.255.255");
	    socket = new DatagramSocket();

	    Servidor srv = Servidor.getInstance();
	    String mensaje = srv.getNickname();
	    byte[] bufer = mensaje.getBytes();

	    DatagramPacket paquete = new DatagramPacket(bufer,
		    bufer.length, grupo, Puerto);
	    socket.send(paquete);
	} catch (UnknownHostException ex) {
	    Logger.getLogger(ServidorBroadcast.class.getName()).log(Level.SEVERE, null, ex);
	} catch (SocketException ex) {
	    Logger.getLogger(ServidorBroadcast.class.getName()).log(Level.SEVERE, null, ex);
	} catch (IOException ex) {
	    Logger.getLogger(ServidorBroadcast.class.getName()).log(Level.SEVERE, null, ex);
	}
    }

}
