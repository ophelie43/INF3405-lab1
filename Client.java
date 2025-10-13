import java.net.*;
import java.util.Arrays;
import java.util.Scanner;
import java.io.*;
public class Client 
{
	private static Socket socket;
	private static DataOutputStream dataOutputStream = null;
	private static DataInputStream dataInputStream = null;
	
	// Client application 
	
	public static void main(String[] args) throws Exception
	{
		Scanner myObj = new Scanner(System.in);
		String serverAdress; // Read user Input
		// Validation of IP address
		do {
			System.out.println("Enter IP address:");
			serverAdress = myObj.nextLine();
			if (!IPValidation(serverAdress)) {
				System.out.println("Erreur: l'adresse IP est invalide.");	
			}
		} while (!IPValidation(serverAdress)); 
		
		
		int serverPort;
		// Validation of port
		do {
			System.out.println("Enter Port between 5000 and 5050:");
			serverPort = Integer.parseInt(myObj.nextLine());
			if (serverPort<5000 || serverPort > 5050) {
				System.out.println("Erreur: la valeur du port doit se situer entre 5000 et 5050.");	
			}
		} while (serverPort<5000 || serverPort > 5050); 

		
		// Creating a connection with the server
		
		socket = new Socket(serverAdress, serverPort);
		System.out.format("Serveur lancé sur [%s:%d]", serverAdress, serverPort);
		dataInputStream = new DataInputStream(socket.getInputStream());
		dataOutputStream = new DataOutputStream(socket.getOutputStream());
		String message = dataInputStream.readUTF();
		System.out.println("Message reçu du serveur: " + message);
		try {
			sendFile("/Users/opheliesenechal/Desktop/Session 7/INF3405 - Réseaux informatiques/Labo 1/Exemple de code JAVA.pdf");
			dataInputStream.close();
			dataOutputStream.close();
		}
		catch(Exception e){
			e.printStackTrace();
			
		}
		
//		while(true) {
//			String cmd;
//			String arg = null;
//			System.out.print("> :");
//			String input = myObj.nextLine();
//			dataOutputStream.writeUTF(input);
//			if (input.contains("<")) {
//				String [] parts = input.split("<")	;		
//				cmd = parts[0];
//				arg = parts[1].replace(">", "");
//			} else {
//				cmd = input;
//			}
//			if (cmd.equals("cd")) {	
//			}
//			else if (cmd.equals("ls")) {
//			}
//			else if (cmd.equals("mkdir")) {
//			}
//			else if (cmd.equals("upload")) {
//				sendFile("/Users/opheliesenechal/Desktop/Session 7/INF3405 - Réseaux informatiques/Labo 1/Exemple de code JAVA.pdf");
//
////				String serverResponse = dataInputStream.readUTF();
////			    if (serverResponse.equals("READY_FOR_UPLOAD")) {
////			        upload(arg);
////			        String confirmation = dataInputStream.readUTF();
////			        System.out.println("Serveur: " + confirmation);
////			    } else {
////			        System.out.println("Le serveur n'est pas prêt : " + serverResponse);
////			    }
//
//			}
//			else if (cmd.equals("upload")) {
//				
//			}
//			
//			else if (cmd.equals("delete")) {
//				
//			}
//			else if (cmd.equals("exit")) {
//				socket.close();
//				
//			}
//		}


		}
		
		
		
		
		
		
	public static boolean IPValidation(String address) {

		// x.x.x.x with x between 0 and 255
		try
		{
			int periodCount = 0;
			
			for(int i = 0; i < address.length(); i ++)
			{
				if (address.charAt(i) == '.')
				{
					periodCount++;
				}
			}
			if(periodCount != 3)
			{
				return false;
			}

			String [] ipNums = address.split("\\.");
			
			for(int i = 0; i < ipNums.length; i++ )
			{
				if(!ipNums[i].equals("0"))
				{
					if (ipNums[i].charAt(0) == '0')
					{
						return false;
					}
				}
				
				int currentNum = ProcessOctets(ipNums[i]);
				if(currentNum > 255 || currentNum < 0)
				{
					return false;
				}
			}
			return true;
			
		}
		catch(Exception e)
		{
			System.out.println(e);
			
		}
		return false;
	}
	public static int ProcessOctets(String number) {
		int result;
		try
		{
			result = Integer.parseInt(number);
			
		}
		catch(Exception e)
		{
			return -1;
		}
		return result;
	}
	public static void cd() {
		// se déplacer vers un répertoire enfant ou parent
	}
	public static void ls() {
		//afficher tous les dossiers et fichiers dans le 
		//répertoire courant de l'utilisateur au niveau du serveur
		
	}
	public static void mkdir() {
		// Création d'un dossier au niveau du serveur de stockage
		
	}
	public static void upload(String path) {
		try {
			sendFile(path);
			System.out.println("Fichier envoyé avec succès !");

		}
		catch (Exception e) {
			System.out.println(e);
		}
		
		
		
	}
	public static void sendFile(String path) throws Exception{
		// Upload a file from the local directory of the client towards the stocking server
		int bytes = 0;
		// Open the File where located in your PC
		File file = new File(path);
		
		FileInputStream fileInputStream = new FileInputStream(file);
		// send the File to the Server
		// send the name of the file
		dataOutputStream.writeLong(file.length());
		
		
		// Break file into chunks
		byte[] buffer = new byte[4 * 1024];
		while ((bytes = fileInputStream.read(buffer)) != -1) {
			// Send the file to Server Socket
		dataOutputStream.write(buffer, 0, bytes);
			dataOutputStream.flush();
		}
		// close the file
		fileInputStream.close();
		}
		
			
		
	public static void download() {
		//téléchargement d'un fichier, se trouvant dans le répertoire courant de 
		//l'utilisateur au niveau du serveur de stockage, vers répertoire local du client
	}
	public static void delete() {
		// Supprimer un fichier ou un dossier se trouvant dans le répertoire courant
		// de l'utilisateur au niveau du serveur de stockage
	}


}
