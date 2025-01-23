package updaterClass;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;

public class DownloadManager {
		
	public static Boolean download(String fileURLToDownload, String saveFileTo) throws IOException, MalformedURLException {
		URL url = new URL(fileURLToDownload);
		URLConnection connection = url.openConnection();
		connection.setConnectTimeout(3000);
		connection.setReadTimeout(5000);
		
		File tempFile = new File(saveFileTo + ".tmp");
				
		try(InputStream inputStream = connection.getInputStream();
			FileOutputStream outputStream = new FileOutputStream(tempFile)){
			byte[] buffer = new byte[4096];
			int byteRead;
			while((byteRead = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, byteRead);
			}
		} catch(SocketTimeoutException e) {
	       UpdaterClass.log("Updater" , "Timeout erreicht: " + e.getMessage());
	        if (tempFile.exists()) {
	            tempFile.delete();
	        }
	        return false;
		} catch (IOException e) {
			UpdaterClass.log("Updater" , "Download fehlgeschlagen: " +e.getMessage());
	        if (tempFile.exists()) {
	            tempFile.delete();
	        }
	        return false;
		}
		
		Boolean isFileAvailable = checkIfDownloadIsComplete(tempFile.getAbsolutePath());
		//TODO die TrayIcon existiert bereits ich muss replace exisiting anwenden
		if(isFileAvailable) {
			File finalFile = new File(saveFileTo);
			finalFile.delete();
			if(tempFile.renameTo(finalFile)) {
				return true;
			}else {
				UpdaterClass.log("Updater", "Fehler beim umbenennen der Datei.");
				tempFile.delete();
				return false;
			}
		}else {
			tempFile.delete();
			return false;
		}
	}
	
	private static Boolean checkIfDownloadIsComplete(String downloadedFile){
		File fileToDownload = new File(downloadedFile);
		
		if(fileToDownload.exists()) {
			return true;
		}else {
			return false;
		}
	}
}
