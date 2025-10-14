import java.net.*;
import java.util.Arrays;
import java.util.Scanner;
import java.io.*;
public class Client 
{
	private static Socket socket;
	private static DataOutputStream dataOutputStream = null;
	private static DataInputStream dataInputStream = null;
	private static FileOutputStream fileOutputStream;
	private static BufferedOutputStream bufferedOutputStream;
	private static final File currentDirectory = new File(System.getProperty("user.dir"));
	

	
	// Client application 
	public static void main(String[] args) {
		
        Scanner myObj = new Scanner(System.in);
		System.out.println("Enter IP adress :");
		
		String serverAdress; // Read user Input
		// IP address validation
		do {
			System.out.println("Enter IP address:");
			serverAdress = myObj.nextLine();
			if (!IPValidation(serverAdress)) {
				System.out.println("Erreur: l'adresse IP est invalide.");	
			}
		} while (!IPValidation(serverAdress)); 

		int serverPort;
		// Validation port
		do {
			System.out.println("Enter Port between 5000 and 5050:");
			serverPort = Integer.parseInt(myObj.nextLine());
			if (serverPort<5000 || serverPort > 5050) {
				System.out.println("Erreur: la valeur du port doit se situer entre 5000 et 5050.");	
			}
		} while (serverPort<5000 || serverPort > 5050); 
        try {
           

            socket = new Socket(serverAdress, serverPort);
            System.out.format("Serveur lancé sur [%s:%d]\n", serverAdress, serverPort);

            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());

            String message = dataInputStream.readUTF();
            System.out.println("Message reçu du serveur: " + message);

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
                dataOutputStream.writeUTF(input);

                switch (cmd) {
                    case "upload":
                        if (arg == null) {
                            System.out.println("⚠️ Syntaxe: upload<chemin_du_fichier>");
                        } else {
                            sendFile(arg);
                        }
                        break;
                    case "mkdir":
                    	System.out.println("Name of new folder: " + arg);
                    	break;
                    //case "cd":
                    //case "ls":
                    case "download":
                    	if (arg == null)  {
                    		System.out.println("⚠️ Syntaxe: upload<chemin_du_fichier>");
                    	} else {
                    		
                    		receiveFile();
                    	}
                    	break;
                    //case "delete":

                    case "exit":
                        running = false; 
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
	private static void receiveFile()
	        throws Exception
	    {
		String fileName = dataInputStream.readUTF();
		long fileSize = dataInputStream.readLong();
		File newFile = new File(currentDirectory, fileName);
		System.out.println("📥 Téléchargement vers : " + newFile.getAbsolutePath());

		
		try (FileOutputStream fos = new FileOutputStream(newFile);
		         BufferedOutputStream bos = new BufferedOutputStream(fos)) {

		        byte[] buffer = new byte[4096];
		        int bytesRead;
		        long totalRead = 0;
		        while (totalRead < fileSize && (bytesRead = dataInputStream.read(buffer)) != -1) {
		            bos.write(buffer, 0, bytesRead);
		            totalRead += bytesRead;
		        }
		        bos.flush();
		    }

		    System.out.println("✅ Fichier reçu : " + fileName + " (" + fileSize + " octets)");
		  }
	        
	        // Here we received file
	        	
    private static void sendFile(String path)
            throws Exception
        {
    		File file = new File(path);
    		dataOutputStream.writeUTF(file.getName());
    		dataOutputStream.writeLong(file.length());
    		dataOutputStream.flush();
            int bytes = 0;
            // Open the File where he located in your pc
            
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
