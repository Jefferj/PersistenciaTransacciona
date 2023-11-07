import javax.swing.*;

import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class Servidor  {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		MarcoServidor mimarco=new MarcoServidor();
		
		mimarco.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			
	}	
}
//interfax grafica del servidor
class MarcoServidor extends JFrame implements Runnable{
	
	public MarcoServidor(){
		
		setBounds(1200,300,280,350);				
			
		JPanel milamina= new JPanel();
		
		milamina.setLayout(new BorderLayout());
		
		areatexto=new JTextArea();
		
		milamina.add(areatexto,BorderLayout.CENTER);
		
		add(milamina);
		
		setVisible(true);
		
		Thread mihilo=new Thread(this);//creación de hilo para ejecutar en segundo plano 
		
		mihilo.start(); //inicio del hilo
		
		}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		try {
			ServerSocket servidor=new ServerSocket(9999);//se crea el socket del servidor con el puerto para escuchar
			
			String nick, ip, mensaje;
			
			ArrayList <String> ListaIp=new ArrayList<String>();
			
			PaqueteEnvio paquete_recibido;
			
			//creamos un bucle para que se repita la conexión cada vez que enviemos un mensaje
			
			while(true) {
			
				Socket misocket=servidor.accept();//aceptamos todas las conexiones entrantes
						
				ObjectInputStream paquete_datos=new ObjectInputStream(misocket.getInputStream()); //creamos flujo para recibir paquete con información
				
				paquete_recibido=(PaqueteEnvio) paquete_datos.readObject(); //declaramos el paquete recibido
						
				nick=paquete_recibido.getNick();
				ip=paquete_recibido.getIp();
				mensaje=paquete_recibido.getMensaje();
				
				if(!mensaje.equals(" Online")) {
				
				areatexto.append("\n" + nick + ": " + mensaje + " para " + ip );
				
				Socket enviaDestinatario=new Socket(ip,9090); //creamos un socket de envio con el puerto donde llegara el paquete recibido al servidor
				
				ObjectOutputStream paqueteReenvio=new  ObjectOutputStream(enviaDestinatario.getOutputStream()); //creamos flujo de envio de paquete recibido
			
				paqueteReenvio.writeObject(paquete_recibido); //ejecutamos el flujo
				
				enviaDestinatario.close();	//cerramos flujo
				
				paqueteReenvio.close(); //cerramos el flujo
				
				misocket.close(); }//cerramos al conexión
				
				else {
					
					//-------------------Detectar online------------
					
					InetAddress localizacion=misocket.getInetAddress();
					
					String IpRemota=localizacion.getHostAddress();
					
					System.out.println("Online " + IpRemota);
					
					ListaIp.add(IpRemota);
					
					paquete_recibido.setIps(ListaIp);
					
					for (String z:ListaIp) {
						
						System.out.println("Array " + z);
						
						
						Socket enviaDestinatario=new Socket(z,9090); //creamos un socket de envio con el puerto donde llegara el paquete recibido al servidor
						
						ObjectOutputStream paqueteReenvio=new  ObjectOutputStream(enviaDestinatario.getOutputStream()); //creamos flujo de envio de paquete recibido
					
						paqueteReenvio.writeObject(paquete_recibido); //ejecutamos el flujo
						
						enviaDestinatario.close();	//cerramos flujo
						
						paqueteReenvio.close(); //cerramos el flujo
						
						misocket.close();
					}
					
															
					//------------------------------------------------
				}
				
				
			}
			
		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private	JTextArea areatexto;
}
