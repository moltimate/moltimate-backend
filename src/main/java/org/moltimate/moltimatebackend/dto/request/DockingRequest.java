package org.moltimate.moltimatebackend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DockingRequest {
	private MultipartFile macromolecule;
	private MultipartFile ligand;
	private double center_x;
	private double center_y;
	private double center_z;
	private double size_x;
	private double size_y;
	private double size_z;

	public MultiValueMap<String, Object> toMap() throws IOException {
		MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
		map.add("macromolecule", new ByteArrayResource( macromolecule.getBytes() ){
			@Override
			public String getFilename() {
				return macromolecule.getOriginalFilename();
			}
		});
		map.add("ligend", new ByteArrayResource( ligand.getBytes() ){//TODO: Fix spelling when API is fixed
			@Override
			public String getFilename() {
				return ligand.getOriginalFilename();
			}
		});
		map.add("center_x", center_x);
		map.add("center_y", center_y);
		map.add("center_z", center_z);
		map.add("size_x", size_x);
		map.add("size_y", size_y);
		map.add("size_z", size_z);
		return map;
	}
}
