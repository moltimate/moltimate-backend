package com.moltimate.moltimatebackend.alignment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlignmentResponse {

    List<Alignment> alignments;
}
