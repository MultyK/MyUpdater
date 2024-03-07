package updaterClass;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class UpdaterClass {
	public static void main (String[] args) {
		updateConfigFile();
		createAndCheckLog();
		deleteClientJAR();
	}

	private static void updateConfigFile() {
		String systemEnviromentDekstop = System.getenv("HOMEPATH"); //Hier wird der Systempfad zu Home gesucht.
    	File configFile = new File(systemEnviromentDekstop+"\\desktop\\Client\\config.ini");
    	String configFileLine = "";
    	StringBuilder configFileLineWithoutData = new StringBuilder();
    	StringBuilder configFileContent = new StringBuilder();
    	List<String[]> newConfigFileList = new ArrayList<>();
		
    	if(!configFile.exists()) {
    		
    	  	log("Updater","config.ini existiert nicht. oder wurde nicht gefunden.");
    	}else {
    		try {
    			BufferedReader br = new BufferedReader(new FileReader(configFile));  			
    			
				while((configFileLine = br.readLine()) != null) {
					int index = configFileLine.indexOf("="); // Int für den Index des "=" Zeichen im String
					//if Abfrage: Wenn index != null des configFileLine String dann alles nach dem = Zeichen löschen. 
					if(index != -1) {
						configFileLineWithoutData.append(configFileLine.substring(0, index +1)+"\n"); // +1 Damit das = Zeichen noch geschrieben wird.
						
					}
					configFileContent.append(configFileLine + "\n"); // Die gelesenen Linien in den StringBuilder stecken.
				}
				br.close();
				
				URL newConfigURL = new URL("http://45.146.253.134/config.ini");
				BufferedReader br2 = new BufferedReader(new InputStreamReader(newConfigURL.openStream()));
				String newConfigFileLine;
				StringBuilder newConfigFileStringBuilder = new StringBuilder();
				
				while((newConfigFileLine = br2.readLine()) != null) {
					newConfigFileStringBuilder.append(newConfigFileLine+"\n");
					newConfigFileList.add(new String[] {newConfigFileLine});
				}
				br2.close();
				
				
				/* Dieser Abschnitt kann ausgeklammert werden, fuer Debug zwecke.
				System.out.println("---Locale Config File---\n");
				System.out.println(configFileContent);
				System.out.println("---Locale Config File Sauber---\n");
				System.out.println(configFileLineWithoutData);
				System.out.println("---Newest Config File From Server---\n");
				System.out.println(newConfigFileStringBuilder);
				System.out.println("---Odds in New-Old Config File---\n");*/
				
				for(String[] pair: newConfigFileList) {// Hier wird jedes Keyword in der neuen Config durchgegangen.
					for (String element : pair) {//Ohne diese Schleife wird nur die Speicheradresse des Strings ausgegeben, mit der Schleife kann man auf den Inhalt der Speicheradresse zugreiffen
						if(configFileLineWithoutData.toString().contains(element)) {//Hier wird die Neue Config vom Server mit der lokalen alten Config verglichen und die fehlenden Keywords "gesammelt"	
						}else {
							FileWriter fw = new FileWriter(configFile, true);
							fw.write("\n" +element+"\n");
							fw.close();
							log("Updater", "Config Key"+ element + " wurde in die config.ini geschrieben.");
						}
					}				
				}
			
			} catch (IOException e) {
				log("Updater","Fehler beim lesen der config.ini");
				e.printStackTrace();
			}
    	}
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
			//TODO in jedem Catch muss die CLient Jar wieder gestartet werden. Ansonsten bleibt das System ungeschuetzt.
		} catch (IOException e) {
			
			log("Updater","Neuste Jar konnte nicht gedownloaded werden: " +e);
		}		
		
	}

	private static void startClient() {
		String pathToRoaming = System.getenv("APPDATA");
		File file = new File(pathToRoaming+"\\Microsoft\\Windows\\Start Menu\\Programs\\Startup\\client.jar");
		
		try {
			log("Updater","Versuche die client.jar zu starten...");
			Desktop.getDesktop().open(file);
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

