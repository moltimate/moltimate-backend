package org.moltimate.moltimatebackend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActiveSiteFilters {
	private int residueId;
	private String residueChainName;
	private String residueAltLoc;
}
