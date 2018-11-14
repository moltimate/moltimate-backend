package org.moltimate.moltimatebackend.request;

import lombok.Data;

import java.util.List;

/**
 * AlignmentRequest stores options and filters that are useful for either type of alignment.
 */
@Data
public abstract class AlignmentRequest {

    private List<String> options;
    private List<String> filters;
}
