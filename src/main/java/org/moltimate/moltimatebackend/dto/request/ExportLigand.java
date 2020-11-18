package org.moltimate.moltimatebackend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExportLigand {
	private String name;
	private double bindingEnergy;
	private double modeNumber;
	private double rmsdUpper;
	private double rmsdLower;
}
