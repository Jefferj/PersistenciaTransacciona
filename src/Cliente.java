import javax.swing.*;

import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;


public class Cliente {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		MarcoCliente mimarco=new MarcoCliente();
		
		mimarco.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}

}

//creamos la interfax grafica para el cliente
class MarcoCliente extends JFrame{
	
	public MarcoCliente(){
		
		setBounds(600,300,280,350);
				
		LaminaMarcoCliente milamina=new LaminaMarcoCliente();
		
		add(milamina);
		
		setVisible(true);
		
		addWindowListener(new EnvioOnline());
		
		}
}

// Envio de señal online	-------------------------------------------------
	class EnvioOnline extends WindowAdapter{
		
		public void windowOpened(WindowEvent e) {
			
			try {
				
				Socket misocket=new Socket("192.168.140.81", 9999);
				
				PaqueteEnvio datos=new PaqueteEnvio();
				
				datos.setMensaje(" Online"); 
				
				ObjectOutputStream paquete_datos=new ObjectOutputStream(misocket.getOutputStream());
				
				paquete_datos.writeObject(datos);
				
				misocket.close();
				
			}catch(Exception e2){}
		}
	}
	

	
//----------------------------------------
	
//creación de interfaz
class LaminaMarcoCliente extends JPanel implements Runnable{
	
	public LaminaMarcoCliente(){
		
		String nick_usuario=JOptionPane.showInputDialog("Nick: "); //Para que el usuario ingrese su apodo
		
		JLabel n_nick=new JLabel("Nick: "); //almacenamos el apodo del usuario
		
		add(n_nick); //agregamos el apodo introducido
		
		nick=new JLabel(); //llamamos la variable
		
		nick.setText(nick_usuario); //insertar el apodo del usuario
		
		add(nick); //
	
		JLabel texto=new JLabel(" En linea:"); //Encabezado de la interfaz
		
		add(texto);
		
		ip=new JComboBox();
		
		/*ip.addItem("Usuario 1");
		
		ip.addItem("Usuario 2");
		
		ip.addItem("Usuario 3");*/
		
		add(ip);
		
		campochat=new JTextArea(12,20); //para visualizar chat 
		
		add(campochat);
	
		campo1=new JTextField(20); //cuadro de texto para enviar mensaje
	
		add(campo1);		
	
		miboton=new JButton("Enviar");//boton para enviar el texto escrito
		
		EnviaTexto mievento=new EnviaTexto(); //se crea instanacia para ser utiliza en el metodo privado EnviaTexto
		
		miboton.addActionListener(mievento);
		
		add(miboton);	
		
		Thread mihilo=new Thread(this);
		
		mihilo.start();
		
	}
	
	
	private class EnviaTexto implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			
			campochat.append("\n" + campo1.getText());
			
			try {
				Socket misocket=new Socket("192.168.140.81", 9999); //se crea el socket y se estable ip local del equipo donde se va a ejecutar con el puerto de escucha
				
				
				PaqueteEnvio datos=new PaqueteEnvio(); //establecemos el paquete que vamos a enviar
				
				datos.setNick(nick.getText()); //nick seria nuestro nombre o apodo en el chat
				datos.setIp(ip.getSelectedItem().toString()); //dirección del destinatario
				datos.setMensaje(campo1.getText()); //mensaje a enviar
				
				ObjectOutputStream paquete_datos=new ObjectOutputStream (misocket.getOutputStream()); //flujo de salida del paquete que enviamos por la red
				
				paquete_datos.writeObject(datos); //salida del paquete por la red para ser recibida por el receptor
				
				misocket.close(); //cierre del flujo
							
			} catch (UnknownHostException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} 
			
		}
		
}
		
	private JTextField campo1;
	
	private JComboBox ip; 
	
	private JLabel nick;
	
	private JTextArea campochat;
	
	private JButton miboton;

	@Override
	public void run() { //realizacion de socket servidor cliente
		// TODO Auto-generated method stub
		
		try {
			
			ServerSocket servidor_cliente=new ServerSocket(9090); //creamos socket para cliente para recibir mensaje
			
			Socket cliente; //declaromos el socket
			
			PaqueteEnvio paqueteRecibido; //creamos variable
			
			while(true) { //creamos el siglo para repetir la conexión 
				
				cliente=servidor_cliente.accept();
				
				ObjectInputStream flujoentrada=new ObjectInputStream(cliente.getInputStream());
				
				paqueteRecibido=(PaqueteEnvio) flujoentrada.readObject();
				
				//creamos if y else para saber que mensaje se imprime en pantalla de campochat
				
				if(!paqueteRecibido.getMensaje().equals(" Online")) {
					
					campochat.append("\n" + paqueteRecibido.getNick() + ": " + paqueteRecibido.getMensaje());
				}else {
					
					//campochat.append("\n" + paqueteRecibido.getIps());
					
					ArrayList <String> IpsMenu=new ArrayList<String>();
					
					IpsMenu=paqueteRecibido.getIps();
					
					ip.removeAllItems();
					
					for(String z:IpsMenu) {
						
						ip.addItem(z);
						
					}
				}
					
			}
			
			
		}catch(Exception e){
			
			System.out.println(e.getMessage());
		}
	}
}

//creamos una clase que envie toda la información que necesitamos a nuestro destinatario para establer comunicacion

class PaqueteEnvio implements Serializable{
	
	private String nick, ip, mensaje;
	
	private ArrayList<String> Ips;

	public ArrayList<String> getIps() {
		return Ips;
	}

	public void setIps(ArrayList<String> ips) {
		Ips = ips;
	}

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getMensaje() {
		return mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	} 
	
}
