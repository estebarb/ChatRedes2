/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servers;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author esteban
 */
class ClienteBroadcast extends Thread {

    private int Puerto;

    public ClienteBroadcast(int PuertoBroadcast) {
	Puerto = PuertoBroadcast;
    }

    @Override
    public void run() {
	try {
	    MulticastSocket socket = new MulticastSocket(Puerto);
	    InetAddress grupo = InetAddress.getByName("224.255.255.255");
	    socket.joinGroup(grupo);

	    byte[] bufer = new byte[256];
	    DatagramPacket paquete;
	    while (true) {
		paquete = new DatagramPacket(bufer, bufer.length);
		socket.receive(paquete);

		// Se registra el peer:
		String nickname = new String(Arrays.copyOfRange(
			paquete.getData(),
			0,
			paquete.getLength()));

		Servidor.getInstance().RefreshPeer(paquete.getAddress().getHostAddress(), nickname);
	    }

	    // Se registra el peer:
	} catch (IOException ex) {
	    Logger.getLogger(ClienteBroadcast.class.getName()).log(Level.SEVERE, null, ex);
	}
    }
}
