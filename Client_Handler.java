import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Client_Handler extends Thread{ // pour traiter la demande de chaque client sur un socket particulier

			private Socket socket;
			private int clientNumber;
			private static DataInputStream dataInputStream;
			private DataOutputStream dataOutputStream;
			
			public Client_Handler(Socket socket, int clientNumber){
				this.socket = socket;
				this.clientNumber = clientNumber;
				
				System.out.println("New connection with client#" + clientNumber + " at " + socket);
				
			}
			public void run(){ // Création d'un thread qui envoie un message à un client

			try 
			
			{
				
				// Création d'un canal pour envoyer des messages au client
				dataInputStream = new DataInputStream(socket.getInputStream());
				
				dataOutputStream = new DataOutputStream(socket.getOutputStream());
				String cmd;
				String arg = null;
				
				// Envoie d'un message
				
				dataOutputStream.writeUTF("Hello from server - you are client#" + clientNumber);
				receiveFile("/Users/opheliesenechal/Desktop/Session 7/INF3405 - Réseaux informatiques/Labo 1/Exemple de code JAVA.pdf");
				dataInputStream.close();
				dataOutputStream.close();
//				while(true) {
//					String input = dataInputStream.readUTF();
//					System.out.println(input);
//					if (input.contains("<")) {
//						String [] parts = input.split("<")	;		
//						cmd = parts[0];
//						arg = parts[1].replace(">", "");
//						
//					} else {
//						cmd = input;
//					}
//					System.out.println("cmd:" + cmd);
//					System.out.println(" arg:" + arg );
//					if (cmd.equals("cd")) {	
//					}
//					else if (cmd.equals("ls")) {
//					}
//					else if (cmd.equals("mkdir")) {
//					}
//					else if (cmd.equals("upload")) {
//						receiveFile("/Users/opheliesenechal/Desktop/Session 7/INF3405 - Réseaux informatiques/Labo 1/Exemple de code JAVA.pdf");
//
////						try {
////							dataOutputStream.writeUTF("READY_FOR_UPLOAD"); // signal au client
////							receiveFile(arg);
////							dataOutputStream.writeUTF("Fichier " + arg + " reçu avec succès !");
////						} catch (Exception e) {
////								// TODO Auto-generated catch block
////							e.printStackTrace();
////							dataOutputStream.writeUTF("Erreur lors de la réception du fichier : " + e.getMessage());						
////						}
//						
//					}	
//					
//					else if (cmd.equals("delete")) {
//						
//					}
//					else if (cmd.equals("exit")) {
//						try
//						{
//								// Fermeture de la connexion avec le client
//							socket.close();
//						}
//						catch (IOException e)
//						{
//							dataOutputStream.writeUTF("Could not close a socket");
//							
//						}
//						System.out.println("Client " + clientNumber + " déconnecté");
//						
//					}
//				}
				
				

//				}
				
			}
			catch(IOException e)
			{
				System.out.println("Error handling client#" + clientNumber + " : " + e);
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println("Error receiving file from client# "+ clientNumber + ":" + e);
				e.printStackTrace();
			}
			finally
			{
				try {
					socket.close();
					} catch (IOException e) {
					System.out.println("Couldn't close a socket, what's going on?");
					}
					System.out.println("Connection with client# " + clientNumber + " closed");
			}
		}



	private static void receiveFile(String fileName) throws Exception{
	// Upload a file from the local directory of the client towards the stocking server
		int bytes = 0;
		FileOutputStream fileOutputStream = new FileOutputStream(fileName);

		long size = dataInputStream.readLong();// read file size
		byte[] buffer = new byte[4 * 1024];
		while (size > 0 && (bytes = dataInputStream.read(buffer,0, (int)Math.min(buffer.length,  size))) != -1) {
			// Here we write the file using write method
			fileOutputStream.write(buffer, 0, bytes);
			size -= bytes; // read upto file size
		}
		// Here we received the file
		System.out.println("File is received: " + fileName);
		fileOutputStream.close();
	}			

}
