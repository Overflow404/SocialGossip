import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JTextArea;

public class FileTransferServer implements Runnable {
	/**
	 * @overview	Questo thread fa da server nella trasmissione p2p di un file.
	 */
	private int port;
	private JTextArea notArea;

	/**
	 * @constructor	Inizializza la porta alla quale il client si connettera' e la
	 * 				textarea sulla quale mostrare i progressi relativi all'invio del file.
	 * @param		port
	 * @param		notArea
	 */
	public FileTransferServer(int port, JTextArea notArea) {
		this.notArea = notArea;
		this.port = port;
	}

	/**
	 * @effects	Contiene valori di utilita'.
	 */
	class Utils {
		final int PORT = port;
		final short HEADER_SIZE = 4;
		final short MAX_FILENAME_LENGTH = 260;
		final short LONG_BYTES = 8;
		final short INT_BYTES = 4;
		final int MB = 1024 * 1024;
	}

	/**
	 * @effects	Dopo aver accettato la connessione viene settata
	 * 			una op_read sulla selectedkey ed al prossimo giro 
	 * 			si procede alla ricezione del file. In particolare viene
	 * 			prima ricevuto: dimensione del filename e filename e dopo
	 * 			dimensione file e file.
	 */
	public void run() {
		notArea.append("Starting file transfer\n");
		Utils u = new Utils();

		try {
			InetSocketAddress address = new InetSocketAddress(InetAddress.getLocalHost(), u.PORT);
			ServerSocketChannel mySocket = ServerSocketChannel.open();
			ServerSocket serverSocket = mySocket.socket();
			mySocket.configureBlocking(false);
			serverSocket.bind(address);

			Selector selector = Selector.open();
			mySocket.register(selector, mySocket.validOps());

			boolean stop = false;
			while (!stop) {
				selector.select();
				Set<SelectionKey> selectedKeys = selector.selectedKeys();
				Iterator<SelectionKey> it = selectedKeys.iterator();
				
				while (it.hasNext()) {
					SelectionKey key = it.next();
					
					if (key.isAcceptable()) {
						SocketChannel myClient = mySocket.accept();
						myClient.configureBlocking(false);
						myClient.register(selector, SelectionKey. OP_READ);
					} else if (key.isReadable()) {
						//Leggo il file name e apro il file channel
						SocketChannel myClient = (SocketChannel) key.channel();
						ByteBuffer myBuffer = ByteBuffer.allocate(u.MAX_FILENAME_LENGTH + u.HEADER_SIZE);

						boolean getFileSize = true;
						boolean thereAreData = true;
						boolean clientDisconnected = false;
						int currentBytesRead = 0;
						int fileNameSize = Integer.MAX_VALUE;
						long totalReadBytes = currentBytesRead;
						
						while (thereAreData) {
							try {
								currentBytesRead = myClient.read(myBuffer);
							}
							catch (IOException e) {
								clientDisconnected = true;
								break;
							}
							totalReadBytes += currentBytesRead;
							if (getFileSize && currentBytesRead > 0) {
								fileNameSize = myBuffer.getInt(0);
								notArea.append("Filename size: " + fileNameSize + "\n");
								shiftBytes(myBuffer, u.INT_BYTES);
								getFileSize = false;
							}
							if (totalReadBytes >= fileNameSize) {
								thereAreData = false;
							}
						}

						if (clientDisconnected) { 
							notArea.append("Disconnesso\n");
							key.cancel();
							it.remove();
							stop = true; 
						}

						String absolutePath = new String(myBuffer.array()).trim();
						String fileName = new String(absolutePath.substring(0,fileNameSize));
						notArea.append("Absolute path received: " + fileName + "\n");
						String s = fileName.substring(fileName.lastIndexOf("\\") + 1);
						notArea.append("Filename received: " + s + "\n");
						
						int sepLength = "FEND".getBytes().length;
						ByteBuffer endFileName = ByteBuffer.allocateDirect(sepLength);
						endFileName.put("FEND".getBytes());
						endFileName.flip();
						while (endFileName.hasRemaining()) {
							myClient.write(endFileName);
						}

						File toRcv = new File(System.getProperty("user.dir") + "/res/" + s);
						toRcv.createNewFile();
						
						FileChannel outChannel = FileChannel.open(Paths.get(toRcv.toString()),
								StandardOpenOption.WRITE);
						
						getFileSize = true;
						thereAreData = true;
						boolean corrupted = false;
						currentBytesRead = 0;
						long fileSize = Long.MAX_VALUE;
						totalReadBytes = currentBytesRead;
						ByteBuffer fileChunkByteBuffer = ByteBuffer.allocateDirect(u.MB);
						
						//Attesa ricezione dal client
						while (thereAreData && !corrupted) {
							//Leggo dal socket channel e aggiorno la variabile che tiene traccia dei byte letti.
							currentBytesRead = myClient.read(fileChunkByteBuffer);
							totalReadBytes += currentBytesRead;

							//Se ho appena iniziato la lettura, devo prima estrarre la dimensione dall'header.
							if (getFileSize && currentBytesRead != 0) {
								fileSize = fileChunkByteBuffer.getLong(0);
									notArea.append("File size: " + fileSize + "\n");
									//Dopo aver estratto l'header shifto di 8 byte il buffer
									shiftBytes(fileChunkByteBuffer, u.LONG_BYTES);
									getFileSize = false;
							}
							if (totalReadBytes >= fileSize) {
								thereAreData = false;
							}

							//Devo leggere => .flip()
							fileChunkByteBuffer.flip();
							while (fileChunkByteBuffer.hasRemaining())
								outChannel.write(fileChunkByteBuffer);
							fileChunkByteBuffer.clear();
						}
						outChannel.close();
						stop = true;
					}
					it.remove();
				}
			}
			serverSocket.close();
			mySocket.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		notArea.append("Ending file transfer\n");
	}

	/**
	 * @effects	Shifta il buffer di k posizioni effettuando
				una cancellazione logica(scrive zero) sulle posizioni
				che non serviranno piu' e spostando il puntatore position
				al "nuovo zero".
	 * @param	byteBuffer
	 * @param	k
	 */
	private void shiftBytes(ByteBuffer byteBuffer, int k) {
		int index = 0;
		for (int i = k; i < byteBuffer.position(); i++) {
			byteBuffer.put(index, byteBuffer.get(i));
			byteBuffer.put(i, (byte)0);
			index++;
		}
		byteBuffer.position(index);
	}

}
