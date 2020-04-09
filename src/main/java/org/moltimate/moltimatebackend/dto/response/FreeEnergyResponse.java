package org.moltimate.moltimatebackend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FreeEnergyResponse {
	private String babelJobId;
	private List<Double[]> dockingData;
	private List<ActiveSiteFilters> activeSites;


	public static List<Double[]> parseLog( byte[] log ) {
		String[] numbers = new String( log ).split("-----\\+------------\\+----------\\+----------\\n *")[1].split("\\n+ *| +");
		List<Double[]> table = new ArrayList<>();

		for( int i = 0; !numbers[i].toLowerCase().contains("writing"); i += 4 ) {
			Double[] row = new Double[]{
					Double.parseDouble( numbers[i] ),
					Double.parseDouble( numbers[i + 1] ),
					Double.parseDouble( numbers[i + 2] ),
					Double.parseDouble( numbers[i + 3] )
			};
			table.add(row);
		}
		return table;
	}
}
