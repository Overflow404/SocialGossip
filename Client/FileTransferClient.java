import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Paths;

import javax.swing.JTextArea;

public class FileTransferClient implements Runnable {
	/**
	 * @overview	Questo thread fa da client nella trasmissione p2p di un file.
	 */
	private String ip;
	private Integer port;
	private JTextArea notArea;
	private File toSend;

	/**
	 * @constructor	Inizializza l'ip e la porta del client remoto alla quale
	 * 				connettersi, il file da inviare e la textarea sulla quale
	 * 				mostrare i log dell'invio del file.
	 * @param		ip
	 * @param		port
	 * @param		toSend
	 * @param		notArea
	 */
	public FileTransferClient(String ip, Integer port, File toSend, JTextArea notArea) {
		this.ip = ip;
		this.port = port;
		this.toSend = toSend;
		this.notArea = notArea;
	}

	/**
	 * @effects	Contiene valori di utilita'.
	 */
	class Utils {
		final Integer PORT = port;
		final short HEADER_SIZE = 4;

		/*
		 * 260 e' la massima dimensione pathname + filename consentita
		 * in ambienti Windows/Unix/OSX
		 */
		final short MAX_FILENAME_LENGTH = 260;
		final short LONG_BYTES = 8;
		final int MB = 1024 * 1024;
	}

	@Override
	/**
	 * @effects	Prova a connettersi con il server remoto(che sara' un altro
	 * 			client dato che la trasmissione avviene in p2p). Se la connessione
	 * 			viene effettuata con successo e il nome del file non e' vuoto o nullo
	 * 			si procede all'invio del file inviando prima il filename con la sua dimensione
	 * 			e poi la dimensione del file ed il file vero e proprio.
	 */
	public void run() {
		//Istanza di Utils per le varie costanti
		Utils u = new Utils();


		try (SocketChannel socketChannel = SocketChannel.open()) {
			SocketAddress address = new InetSocketAddress(ip, u.PORT);
			socketChannel.configureBlocking(false);
			socketChannel.connect(address);
			while (!socketChannel.finishConnect());
			notArea.append("Connected for file transfer\n");

			ByteBuffer fileNameByteBuffer = ByteBuffer.allocate(u.MAX_FILENAME_LENGTH + u.HEADER_SIZE);
			String fileName = toSend.getAbsolutePath();

			if (fileName.equals("") || fileName.length() > u.MAX_FILENAME_LENGTH) {
				notArea.append("Fatal error\n");
				return;
			}

			//Inserisco nell'header(4 byte) la dimensione del file name
			fileNameByteBuffer.putInt(fileName.length());

			//Inserisco nel buffer il file name vero e proprio
			fileNameByteBuffer.put(fileName.getBytes());

			//Devo leggere => .flip()
			fileNameByteBuffer.flip();

			//Mi assicuro di scrivere tutto il buffer sul socket channel.
			while (fileNameByteBuffer.hasRemaining()) {
				socketChannel.write(fileNameByteBuffer);
			}

			notArea.append("Sending this filename: " + fileName + " to client" + "\n");
			
			boolean getFileSize = true;
			boolean thereAreData = true;
			int sepLength = "FEND".getBytes().length;
			ByteBuffer myBuffer = ByteBuffer.allocateDirect(sepLength);
			while (socketChannel.read(myBuffer) == 0) {
				myBuffer.clear();
			}

			FileChannel inChannel =  FileChannel.open(Paths.get(fileName));
			ByteBuffer byteBuffer = ByteBuffer.allocateDirect(u.MB);
			thereAreData = true;
			getFileSize = true;
			while (thereAreData) {
				if (getFileSize) {
					byteBuffer.putLong(inChannel.size());
					getFileSize = false;
				}

				int bytesRead = inChannel.read(byteBuffer);
				if (bytesRead == -1)
					thereAreData = false;
				byteBuffer.flip();
				while (byteBuffer.hasRemaining())
					socketChannel.write(byteBuffer);
				byteBuffer.clear();
			}
			notArea.append("End file transfer\n");
			inChannel.close();
		} catch (IOException e) {
			notArea.append("IOException\n");
			
		}
	}
}