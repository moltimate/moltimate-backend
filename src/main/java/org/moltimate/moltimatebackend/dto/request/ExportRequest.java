package org.moltimate.moltimatebackend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExportRequest {
	private List<ExportLigand> ligands;
	private String babelJobId;
	private List<Boolean> selectedConfigs;
}
