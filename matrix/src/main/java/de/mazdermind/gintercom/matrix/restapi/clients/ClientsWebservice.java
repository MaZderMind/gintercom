package de.mazdermind.gintercom.matrix.restapi.clients;

import java.util.stream.Stream;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/rest/clients")
@RequiredArgsConstructor
public class ClientsWebservice {
	private final ClientsService clientsService;

	@GetMapping
	public Stream<ClientDto> getOnlineClients() {
		return clientsService.getOnlineClients();
	}

	@GetMapping("/provisioned")
	public Stream<ClientDto> getProvisionedClients() {
		return clientsService.getProvisionedClients();
	}

	@GetMapping("/unprovisioned")
	public Stream<ClientDto> getUnprovisionedClients() {
		return clientsService.getUnprovisionedClients();
	}
}
