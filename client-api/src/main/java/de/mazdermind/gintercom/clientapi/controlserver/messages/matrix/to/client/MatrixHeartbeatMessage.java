package de.mazdermind.gintercom.clientapi.controlserver.messages.matrix.to.client;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@JsonSerialize
public class MatrixHeartbeatMessage {
}
