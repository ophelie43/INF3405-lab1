
import java.io.*;
import java.net.Socket;

public class Client_Handler extends Thread{ // pour traiter la demande de chaque client sur un socket particulier

			private static Socket socket;
			private static int clientNumber;
			private static  DataInputStream dataInputStream;
			private static DataOutputStream dataOutputStream;
			private static FileInputStream fileInputStream;
			private static BufferedInputStream bufferedInputStream;
			private static OutputStream os;
			private static File currentDirectory = new File(System.getProperty("user.dir"));
			
			
			
			public Client_Handler(Socket socket, int clientNumber){
				this.socket = socket;
				this.clientNumber = clientNumber;
				System.out.println("New connection with client#" + clientNumber + " at " + socket);
				
			}
			public void run(){

				
		        String cmd;
		        String arg = null;

		        try {
		            dataInputStream = new DataInputStream(socket.getInputStream());
		            dataOutputStream = new DataOutputStream(socket.getOutputStream());
		            dataOutputStream.writeUTF("Hello from server - you are client#" + clientNumber);

		            boolean running = true; 
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
		                            receiveFile();
		                            break;
		                        case "mkdir":
		                        	String NEW_FOLDER = arg;
		                        	mkdir(NEW_FOLDER);
		                        	break;
		                        case "cd":
		                        	String TARGET_PATH = arg;
		                        	File newDirectory = resolvePath(TARGET_PATH);
		                        	if (newDirectory.exists() && newDirectory.isDirectory()) {
		                        		currentDirectory = newDirectory;
		                        		System.out.println("OK: changed directory to " + currentDirectory.getAbsolutePath());
		                        	} else {
		                        		System.out.println("EROOR: Directory not found or not a directory: " + TARGET_PATH);
		                        	}
		                        	break;
		                        case "ls":
		                        	File CURRENT_PATHWAY = currentDirectory;
		                        	listFiles(CURRENT_PATHWAY);
		                        	break;
		                        case "download":
		                        	sendFile(arg);
		                        	break;
		                        case "delete":
		                        	deleteFile(arg);
		                        	break;
		                        case "exit":
		                            System.out.println("Client " + clientNumber + " déconnecté.");
		                            running = false; 
		                            break;

		                        default:
		                            System.out.println("Commande inconnue : " + cmd);
		                            break;
		                    }

		                } catch (IOException e) {
		                    System.out.println("Erreur lecture client#" + clientNumber + " : " + e.getMessage());
		                    break;
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
		            } catch (IOException e) {
		                System.out.println("Erreur fermeture client#" + clientNumber + " : " + e.getMessage());
		            }
		        }
		    }
			 private static void sendFile(String fileName)
			            throws Exception
			        {
				 	File myFile = new File (currentDirectory, fileName);
				 	System.out.println("Downloaded file " + myFile.getAbsolutePath());
				 	dataOutputStream.writeUTF(myFile.getName());
		    		dataOutputStream.writeLong(myFile.length());
		    		dataOutputStream.flush();
				// send file
				 try {
					 fileInputStream = new FileInputStream(myFile);
					 bufferedInputStream = new BufferedInputStream(fileInputStream);
			          byte [] mybytearray  = new byte [(int)myFile.length()];
			          
			          bufferedInputStream.read(mybytearray,0,mybytearray.length);
			          os = socket.getOutputStream();
			          System.out.println("Sending " + fileName + "(" + mybytearray.length + " bytes)");
			          os.write(mybytearray,0,mybytearray.length);
			          os.flush();
			          System.out.println("Done.");
				 } finally {
					 System.out.println("File " + fileName + "sent to client " + clientNumber);
				 }


			 }

			private static void receiveFile()
			        throws Exception
			    {
					String fileName = dataInputStream.readUTF();
					long fileSize = dataInputStream.readLong();
					File newFile = new File(fileName);
					System.out.println("Réception du fichier: " + newFile.getAbsolutePath());
					try (FileOutputStream fileOutputStream = new FileOutputStream(newFile);){
						int bytes = 0;
				        
				        byte[] buffer = new byte[4 * 1024];
				        while (fileSize > 0 && (bytes = dataInputStream.read( buffer, 0,(int)Math.min(buffer.length, fileSize)))!= -1) {
				            // Here we write the file using write method
				            fileOutputStream.write(buffer, 0, bytes);
				            fileSize -= bytes; // read upto file size
				        }
				       
				       fileOutputStream.flush();
					}
					System.out.println("File is Received");
			        
			        // Here we received file
			        
			    }
			private static void deleteFile(String FILE_NAME) {
				File file = new File(FILE_NAME);
				if (file.delete()) {
					System.out.println("Deleted the file/folder: " + file.getName());
				} else {
					System.out.println("Failed to delete the file/folder " + file.getName());
				}
			}
			private static void mkdir(String PATHWAY) {
				// Create an abstract pathname
				File file = new File(PATHWAY);
				// Check if the directory can be created using the abstract path name
				if (file.mkdir()) {
					System.out.println("Directory is created.");
				} else {
					System.out.println("Directory cannot be created");
				}
			}
			private static void listFiles(File CURRENT_PATHWAY) {
				
				if (CURRENT_PATHWAY.isDirectory()) {
					String [] fileNames = CURRENT_PATHWAY.list();
					if (fileNames != null) {
						System.out.println("Files in directory " + CURRENT_PATHWAY + ":");
						for (String name : fileNames) {
							System.out.println(name);
						}
					} else {
						System.out.println("Could not list files in " + CURRENT_PATHWAY);
					}
				} else {
					System.out.println(CURRENT_PATHWAY + "is not a directory");
				}
			}
			
			private File resolvePath(String TARGET_PATH) {
				File resolvedPath;
				File currentDirectory = new File(System.getProperty("user.dir"));// Initial directory
				if (new File(TARGET_PATH).isAbsolute()) {
					resolvedPath = new File(TARGET_PATH);
				} else {
					resolvedPath = new File(currentDirectory, TARGET_PATH);
					
				}
				try {
					return resolvedPath.getCanonicalFile();
					} catch (IOException e) {
						return resolvedPath.getAbsoluteFile();
					}
			}


}
