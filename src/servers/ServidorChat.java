/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servers;

import entities.CMessage;
import entities.CPeer;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author esteban
 */
class ServidorChat extends Thread {

    private final int Puerto;
    
    public ServidorChat(int PuertoChat) {
	Puerto = PuertoChat;
    }
    
    @Override
    public void run() {
	try {
	    ServerSocket server = new ServerSocket(Puerto, 10);
	    while (true) {
		// Comienza una conexión:
		Socket conexion = server.accept();

		// Se inicializa:
		ObjectOutputStream s = new ObjectOutputStream(conexion.getOutputStream());
		s.flush(); //Envia informacion de encabezado
		ObjectInputStream e = new ObjectInputStream(conexion.getInputStream());
		
		CMessage mensaje = (CMessage) e.readObject();

		// ¿De cual IP me hablan? El otro servidor "no"
		// sabe su IP, de forma que va a etiquetar sus
		// mensajes como que vienen de 127.?.?.?
		// (por hacks en Debian no siempre es 127.0.0.1).
		for (CPeer p : mensaje.getPeers()) {
		    if (p.getHostAddress().startsWith("127.")) {
			p.setHostAddress(conexion.getInetAddress().getHostAddress());
			p.setNickname(mensaje.getNickname());
		    }
		}
		
		Servidor srv = Servidor.getInstance();
		srv.ReceiveMessage(mensaje);

		// Finalmente cerramos la conexión
		conexion.close();
		
	    }
	} catch (IOException | ClassNotFoundException ex) {
	    Logger.getLogger(ServidorChat.class.getName()).log(Level.SEVERE, null, ex);
	}
    }
    
}
