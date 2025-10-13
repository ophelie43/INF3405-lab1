import java.net.*;
import java.io.File;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.util.Scanner;
import java.util.Arrays;


public class Server 
{
	private static DataOutputStream dataOutputStream = null;
	private static DataInputStream dataInputStream = null;
	private static ServerSocket Listener;

	// Server application
	
	public static void  main(String[] args) throws Exception 
	{
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
			// Number of client counter
			int clientNumber = 0;
			
			


			if (IPValidation(serverAdress) && serverPort >= 5000 && serverPort <= 5050) {
				Listener = new ServerSocket();
				Listener.setReuseAddress(true);
				// Association of port address and IP
				InetAddress serverIP = InetAddress.getByName(serverAdress);
				Listener.bind(new InetSocketAddress(serverIP, serverPort));
				System.out.format("The server is running %s:%d%n", serverAdress, serverPort);
				//Creating a connection with the clients
				try {
					
					
					while(true)
					{
								// We wait for the next client
								// Note: the accept function is blocking
								
						new Client_Handler(Listener.accept(),clientNumber++).start();
								
					}
				}
				finally  {
					Listener.close();
					
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


}
		

