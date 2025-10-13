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
	public static void main(String[] args) {
        Scanner myObj = new Scanner(System.in);

        try {
            // === 1️⃣ Connexion ===
            System.out.println("Enter IP address:");
            String serverAdress = myObj.nextLine();

            System.out.println("Enter Port between 5000 and 5050:");
            int serverPort = Integer.parseInt(myObj.nextLine());

            socket = new Socket(serverAdress, serverPort);
            System.out.format("Serveur lancé sur [%s:%d]\n", serverAdress, serverPort);

            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());

            String message = dataInputStream.readUTF();
            System.out.println("Message reçu du serveur: " + message);

            // === 2️⃣ Boucle de commande ===
            boolean running = true;
            while (running) {
                System.out.print("> : ");
                String input = myObj.nextLine();

                String cmd, arg = null;
                if (input.contains("<")) {
                    String[] parts = input.split("<");
                    cmd = parts[0];
                    arg = parts[1].replace(">", "");
                } else {
                    cmd = input;
                }

                // On envoie toujours la commande au serveur
                dataOutputStream.writeUTF(cmd);

                switch (cmd) {
                    case "upload":
                        if (arg == null) {
                            System.out.println("⚠️ Syntaxe: upload<chemin_du_fichier>");
                        } else {
                            sendFile(arg);
                        }
                        break;

                    case "exit":
                        running = false; // ✅ quitte proprement la boucle
                        System.out.println("Déconnexion du serveur...");
                        break;

                    default:
                        System.out.println("Commande inconnue ou non supportée pour le moment.");
                        break;
                }
            }

        } catch (Exception e) {
            System.out.println("❌ Erreur client : " + e.getMessage());
            e.printStackTrace();
        } finally {
            // === 3️⃣ Fermeture propre ===
            try {
                if (dataInputStream != null) dataInputStream.close();
                if (dataOutputStream != null) dataOutputStream.close();
                if (socket != null && !socket.isClosed()) socket.close();
                System.out.println("✅ Client déconnecté avec succès.");
            } catch (IOException e) {
                System.out.println("Erreur lors de la fermeture : " + e.getMessage());
            }
        }
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
    private static void sendFile(String path)
            throws Exception
        {
            int bytes = 0;
            // Open the File where he located in your pc
            File file = new File(path);
            FileInputStream fileInputStream
                = new FileInputStream(file);

            // Here we send the File to Server
            dataOutputStream.writeLong(file.length());
            // Here we  break file into chunks
            byte[] buffer = new byte[4 * 1024];
            while ((bytes = fileInputStream.read(buffer))
                   != -1) {
              // Send the file to Server Socket  
              dataOutputStream.write(buffer, 0, bytes);
                dataOutputStream.flush();
            }
            // close the file here
            fileInputStream.close();
        }

	


}
