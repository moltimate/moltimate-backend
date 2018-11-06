package org.moltimate.moltimatebackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.moltimate.moltimatebackend.constant.AminoAcidType;
import org.moltimate.moltimatebackend.model.ActiveSite;
import org.moltimate.moltimatebackend.model.Residue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(value = ActiveSiteController.class, secure = false)
public class ActiveSiteControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ActiveSiteController activeSiteController;

    private static final String CREATE_ACTIVE_SITE_ENDPOINT = "/activesite";
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static Residue mockResidue;
    private static ActiveSite mockActiveSite;

    @Before
    public void setup() {
        mockResidue = Residue.builder()
                             .residueName(AminoAcidType.ASP.name())
                             .chainId("A")
                             .residueId("7")
                             .build();
        mockActiveSite = ActiveSite.builder()
                                   .pdbId("yeet")
                                   .ecNumber("4.3.2.1")
                                   .residues(Collections.singletonList(mockResidue))
                                   .build();
    }

    @Test
    public void When_CreatingActiveSiteWithAllRequiredProperties_Expect_Success() throws Exception {
        when(activeSiteController.createActiveSite(any())).thenReturn(mockActiveSite);
        String response = mvc.perform(post(CREATE_ACTIVE_SITE_ENDPOINT)
                                              .contentType(MediaType.APPLICATION_JSON)
                                              .content(MAPPER.writeValueAsString(mockActiveSite)))
                             .andExpect(status().isOk())
                             .andReturn()
                             .getResponse()
                             .getContentAsString();
        ActiveSite responseActiveSite = MAPPER.readValue(response, ActiveSite.class);
        assertEquals(mockActiveSite, responseActiveSite);
    }

    @Test
    public void When_CreatingActiveSiteMissingPdbId_Expect_Failure() throws Exception {
        mockActiveSite.setPdbId(null);
        when(activeSiteController.createActiveSite(any())).thenReturn(mockActiveSite);
        mvc.perform(post(CREATE_ACTIVE_SITE_ENDPOINT)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(MAPPER.writeValueAsString(mockActiveSite)))
           .andExpect(status().is4xxClientError());
    }

    @Test
    public void When_CreatingActiveSiteMissingEcNumber_Expect_Failure() throws Exception {
        mockActiveSite.setEcNumber("1.2.3");
        when(activeSiteController.createActiveSite(any())).thenReturn(mockActiveSite);
        mvc.perform(post(CREATE_ACTIVE_SITE_ENDPOINT)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(MAPPER.writeValueAsString(mockActiveSite)))
           .andExpect(status().is4xxClientError());
    }

    @Test
    public void When_CreatingActiveSiteWithResidueMissingResidueName_Expect_Failure() throws Exception {
        mockResidue.setResidueName(null);
        mockActiveSite.setResidues(Collections.singletonList(mockResidue));
        when(activeSiteController.createActiveSite(any())).thenReturn(mockActiveSite);
        mvc.perform(post(CREATE_ACTIVE_SITE_ENDPOINT)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(MAPPER.writeValueAsString(mockActiveSite)))
           .andExpect(status().is4xxClientError());
    }

    @Test
    public void When_CreatingActiveSiteWithResidueMissingChainId_Expect_Failure() throws Exception {
        mockResidue.setChainId(null);
        mockActiveSite.setResidues(Collections.singletonList(mockResidue));
        when(activeSiteController.createActiveSite(any())).thenReturn(mockActiveSite);
        mvc.perform(post(CREATE_ACTIVE_SITE_ENDPOINT)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(MAPPER.writeValueAsString(mockActiveSite)))
           .andExpect(status().is4xxClientError());
    }

    @Test
    public void When_CreatingActiveSiteWithResidueMissingResidueId_Expect_Failure() throws Exception {
        mockResidue.setResidueId(null);
        mockActiveSite.setResidues(Collections.singletonList(mockResidue));
        when(activeSiteController.createActiveSite(any())).thenReturn(mockActiveSite);
        mvc.perform(post(CREATE_ACTIVE_SITE_ENDPOINT)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(MAPPER.writeValueAsString(mockActiveSite)))
           .andExpect(status().is4xxClientError());
    }
}
