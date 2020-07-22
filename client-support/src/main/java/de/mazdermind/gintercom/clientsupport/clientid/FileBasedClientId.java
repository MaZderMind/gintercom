package de.mazdermind.gintercom.clientsupport.clientid;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class FileBasedClientId {
	private String clientId;

	private static String createNewClientId(Path clientIdFilePath) throws IOException {
		String newClientId = RandomClientIdGenerator.generateRandomClientId();
		BufferedWriter outStream = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(clientIdFilePath)));
		outStream.write(newClientId);
		outStream.close();
		log.info("Client-Id {} saved to {}", newClientId, clientIdFilePath);

		return newClientId;
	}

	private static String readClientId(Path clientIdFilePath) throws IOException {
		InputStream inStream = Files.newInputStream(clientIdFilePath);
		String readClientId = new BufferedReader(new InputStreamReader(inStream)).readLine().trim();
		log.info("Read Client-Id {} from {}", readClientId, clientIdFilePath);

		return readClientId;
	}

	public String readOrCreateClientId() {
		Path clientIdFilePath = Paths.get(System.getProperty("user.home"), ".gintercom-client-id");
		log.info("Using Client-Id File {}", clientIdFilePath);

		try {
			log.debug("Trying to read Client-Id");
			clientId = readClientId(clientIdFilePath);
		} catch (IOException e) {
			log.info("Client-Id could not be read from File, creating a new one");
			try {
				clientId = createNewClientId(clientIdFilePath);
			} catch (IOException e1) {
				log.error("Client-Id could not be written to from File, using a temporary one");
				clientId = RandomClientIdGenerator.generateRandomClientId();
			}
		}

		return clientId;
	}

	public String getClientId() {
		if (clientId != null) {
			return clientId;
		} else {
			log.debug("Client-Id not yet known");
			return readOrCreateClientId();
		}
	}
}
