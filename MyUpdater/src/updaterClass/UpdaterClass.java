package updaterClass;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Path;
import java.nio.file.Paths;

public class UpdaterClass {
	public static void main (String[] args) {

		createAndCheckLog();
		deleteClientJAR();
		
	}

	private static void createAndCheckLog() {
		String systemEnviromentDekstop = System.getenv("HOMEPATH"); //Hier wird der Systempfad zu Home gesucht.
    	File logFile = new File(systemEnviromentDekstop+"\\desktop\\Client\\updaterLog.log"); 
    	
    	if(!logFile.exists()) {
    		try {
				logFile.createNewFile();
				log("Updater","Log File wurde erfolgreich erstellt");
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
		
	}

	private static void deleteClientJAR() {
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			log("Updater","Thread konnte nicht zu sleep bewegt werden: "+e);
		}
		String pathToRoaming = System.getenv("APPDATA");
		
		Path pathToAutostart = Paths.get(pathToRoaming+"\\Microsoft\\Windows\\Start Menu\\Programs\\Startup\\client.jar");
		File file = new File(""+pathToAutostart);

    	if(file.exists()) {
    		file.delete();
    		log("Updater","Client JAR wurde geloescht.");
    		downloadLatestJAR();
    	}else {
    		log("Updater","Client JAR exisitert nicht!");
    		log("Updater","Versuche neuste Client JAR zu downloaden...");
    		downloadLatestJAR();
    		}
	}
	private static void downloadLatestJAR() {
		
		String URl = "http://45.146.253.134/client.jar";
		String pathToRoaming = System.getenv("APPDATA");
		String saveDownloadedJarAs = (pathToRoaming+"\\Microsoft\\Windows\\Start Menu\\Programs\\Startup\\client.jar");
		
		try {
			log("Updater","Versuche neuste Version zu downloaden...");
			URL url = new URL(URl);
			URLConnection connection = url.openConnection();
			
			InputStream inputStream = connection.getInputStream();
			FileOutputStream outputStream = new FileOutputStream(saveDownloadedJarAs);
					
			byte[] buffer = new byte[4096];
			int byteRead;
					
			while((byteRead = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, byteRead);
						
			}
					outputStream.close();
					log("Updater","Neuste Jar erfolgreich heruntergeladen...");
					startClient();
		} catch (MalformedURLException e) {
			
			log("Updater","URL ist ungueltig: " +e);
		} catch (IOException e) {
			
			log("Updater","Neuste Jar konnte nicht gedownloaded werden: " +e);
		}		
		
	}

	private static void startClient() {
		String pathToRoaming = System.getenv("APPDATA");
		File file = new File(pathToRoaming+"\\Microsoft\\Windows\\Start Menu\\Programs\\Startup\\client.jar");
		
		try {
			Desktop.getDesktop().open(file);
			log("Updater","Versuche die client.jar zu starten...");
		} catch (IOException e) {
			log("Updater","client.jar konnte nicht gestartet werden: "+e);
		}
		log("Updater","Update erfolgreich durchgefuehrt. Updater wird beendet.");
		System.exit(0);
	}

	private static void log(String Source, String Message) {
        try {
        	String systemEnviromentDekstop = System.getenv("HOMEPATH");	//Hier wird der Systempfad zum Dekstop gesucht.
            FileWriter fw = new FileWriter(systemEnviromentDekstop+"\\desktop\\Client\\updaterLog.log", true); //Hier wird der FileWriter initialisiert f�r den gefunden Pfad zu systemEnviromentDekstop.
            long millis = System.currentTimeMillis();
            java.util.Date date = new java.util.Date(millis);

            fw.write("[" + date + "]" + " " + Source + ": " + Message + "\n");
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

