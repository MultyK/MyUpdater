package updaterClass;

import java.awt.Desktop;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.commons.net.ftp.FTPClient;

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
		String pathToRoaming = System.getenv("APPDATA");
		
		Path pathToAutostart = Paths.get(pathToRoaming+"\\Microsoft\\Windows\\Start Menu\\Programs\\Startup\\client.jar");
		File file = new File(""+pathToAutostart);

    	if(file.exists()) {
    		file.delete();
    		log("Updater","Client JAR wurde geloescht.");
    		downloadLatestJAR();
    	}else {
    		log("Updater","Client JAR exisitert nicht!");
    		log("Updater","Versuche neuste JAR zu downloaden...");
    		downloadLatestJAR();
    		}
	}
	private static void downloadLatestJAR() {
		FTPClient client = new FTPClient();
		String server = "45.146.253.134";
		int port = 21;
		String user = "clientUpdater";
		String password = "Solarium123!";
		
		try {
			
			client.connect(server ,port);
			client.login(user, password);
			
			log("Updater","Erfolgreich beim FTP Server angemeldet.");
			
			String remoteFile = "client.jar";
			String pathToRoaming = System.getenv("APPDATA");
			String downloadJar = (pathToRoaming+"\\Microsoft\\Windows\\Start Menu\\Programs\\Startup\\client.jar");
			OutputStream os = new BufferedOutputStream(new FileOutputStream(downloadJar));
			boolean status = client.retrieveFile(remoteFile, os);
			os.close();
			
			if(status) {
				log("Updater","Neue Jar wurde erfolgreich heruntergeladen.");
				log("Updater","Versuche neue Jar zu starten...");
				startClient();
			}
			
		}catch(IOException e) {
			log("Updater","Fehler beim Verusch neue JAR zu downloaden: " +e);
		}
		
	}

	private static void startClient() {
		String pathToRoaming = System.getenv("APPDATA");
		File file = new File(pathToRoaming+"\\Microsoft\\Windows\\Start Menu\\Programs\\Startup\\client.jar");
		
		try {
			Desktop.getDesktop().open(file);
		} catch (IOException e) {
			log("Updater","client.jar konnte nicht gestartet werden: "+e);
		}
		log("Updater","Update erfolgreich durchgeführt. Updater wird beendet.");
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

