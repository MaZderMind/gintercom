package de.mazdermind.gintercom.clientsupport.hostid;

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
public class FileBasedHostId {
	private String hostId;

	private static String createNewHostId(Path hostIdFilePath) throws IOException {
		String newHostId = RandomHostIdGenerator.generateRandomHostId();
		BufferedWriter outStream = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(hostIdFilePath)));
		outStream.write(newHostId);
		outStream.close();
		log.info("Host-ID {} saved to {}", newHostId, hostIdFilePath);

		return newHostId;
	}

	private static String readHostId(Path hostIdFilePath) throws IOException {
		InputStream inStream = Files.newInputStream(hostIdFilePath);
		String readHostId = new BufferedReader(new InputStreamReader(inStream)).readLine().trim();
		log.info("Read Host-ID {} from {}", readHostId, hostIdFilePath);

		return readHostId;
	}

	public String readOrCreateHostId() {
		Path hostIdFilePath = Paths.get(System.getProperty("user.home"), ".gintercom-host-id");
		log.info("Using Host-ID File {}", hostIdFilePath);

		try {
			log.debug("Trying to read Host-ID");
			hostId = readHostId(hostIdFilePath);
		} catch (IOException e) {
			log.info("Host-ID could not be read from File, creating a new one");
			try {
				hostId = createNewHostId(hostIdFilePath);
			} catch (IOException e1) {
				log.error("Host-ID could not be written to from File, using a temporary one");
				hostId = RandomHostIdGenerator.generateRandomHostId();
			}
		}

		return hostId;
	}

	public String getHostId() {
		if (hostId != null) {
			return hostId;
		} else {
			log.debug("Host-ID not yet known");
			return readOrCreateHostId();
		}
	}
}
