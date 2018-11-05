package org.moltimate.moltimatebackend.motif;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * CreateMotifRequest stores all the data required to construct a new, custom motif
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateMotifRequest {

    List<String> formFields;
}
