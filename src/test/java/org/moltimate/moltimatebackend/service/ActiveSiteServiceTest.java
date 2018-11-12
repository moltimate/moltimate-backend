package org.moltimate.moltimatebackend.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.moltimate.moltimatebackend.model.ActiveSite;
import org.moltimate.moltimatebackend.repository.ActiveSiteRepository;
import org.moltimate.moltimatebackend.validation.exceptions.InvalidEcNumberException;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class ActiveSiteServiceTest {

    @InjectMocks
    private ActiveSiteService activeSiteService;

    @Mock
    private ActiveSiteRepository mockActiveSiteRepository;

    @Before
    public void setup() {
        when(mockActiveSiteRepository.findByEcNumberStartingWith(anyString())).thenReturn(Collections.singletonList(new ActiveSite()));
    }

    @Test
    public void When_QueryingByValidEcNumber_Expect_Success() {
        activeSiteService.queryByEcNumber("3.4");
    }

    @Test(expected = InvalidEcNumberException.class)
    public void When_QueryingByInvalidEcNumber_Expect_Success() {
        activeSiteService.queryByEcNumber("3.4.abc");
    }
}
