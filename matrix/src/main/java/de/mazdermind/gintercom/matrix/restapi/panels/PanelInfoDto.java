package de.mazdermind.gintercom.matrix.restapi.panels;

import javax.annotation.Nullable;

import de.mazdermind.gintercom.matrix.configuration.model.PanelConfig;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@NoArgsConstructor
public class PanelInfoDto {
	private String id;
	private String display;
	private boolean online;

	@Nullable
	private String clientId;

	@Nullable
	private String clientModel;

	public PanelInfoDto(String id, PanelConfig config, boolean online) {
		this.id = id;
		display = config.getDisplay();
		clientId = config.getClientId();
		this.online = online;
	}

	public boolean isAssigned() {
		return clientId != null;
	}
}
