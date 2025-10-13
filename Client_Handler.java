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
			public void run() {
		        String cmd;
		        String arg = null;

		        try {
		            dataInputStream = new DataInputStream(socket.getInputStream());
		            dataOutputStream = new DataOutputStream(socket.getOutputStream());
		            dataOutputStream.writeUTF("Hello from server - you are client#" + clientNumber);

		            boolean running = true; // ✅ permet de sortir proprement de la boucle
		            while (running) {
		                try {
		                    String input = dataInputStream.readUTF();
		                    System.out.println(input);

		                    if (input.contains("<")) {
		                        String[] parts = input.split("<");
		                        cmd = parts[0];
		                        arg = parts[1].replace(">", "");
		                    } else {
		                        cmd = input;
		                    }

		                    switch (cmd) {
		                        case "upload":
		                            receiveFile("Client " + clientNumber + arg);
		                            break;

		                        case "exit":
		                            System.out.println("Client " + clientNumber + " déconnecté.");
		                            running = false; // ✅ on sort de la boucle
		                            break;

		                        default:
		                            System.out.println("Commande inconnue : " + cmd);
		                            break;
		                    }

		                } catch (IOException e) {
		                    System.out.println("Erreur lecture client#" + clientNumber + " : " + e.getMessage());
		                    break; // ✅ sortir si la connexion est coupée
		                }
		            }

		        } catch (IOException e) {
		            System.out.println("Erreur initialisation client#" + clientNumber + " : " + e.getMessage());
		        } catch (Exception e) {
		            e.printStackTrace();
		        } finally {
		            try {
		                if (dataInputStream != null) dataInputStream.close();
		                if (dataOutputStream != null) dataOutputStream.close();
		                if (socket != null && !socket.isClosed()) socket.close();
		            } catch (IOException e) {
		                System.out.println("Erreur fermeture client#" + clientNumber + " : " + e.getMessage());
		            }
		        }
		    }




			private static void receiveFile(String fileName)
			        throws Exception
			    {
			        int bytes = 0;
			        FileOutputStream fileOutputStream = new FileOutputStream(fileName);

			        long size = dataInputStream.readLong(); // read file size
			        byte[] buffer = new byte[4 * 1024];
			        while (size > 0 && (bytes = dataInputStream.read( buffer, 0,(int)Math.min(buffer.length, size)))!= -1) {
			            // Here we write the file using write method
			            fileOutputStream.write(buffer, 0, bytes);
			            size -= bytes; // read upto file size
			        }
			        // Here we received file
			        System.out.println("File is Received");
			        fileOutputStream.close();
			    }


}
