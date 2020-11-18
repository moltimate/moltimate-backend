package org.moltimate.moltimatebackend.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalCause;
import lombok.extern.slf4j.Slf4j;
import org.moltimate.moltimatebackend.dto.response.QueryAlignmentResponse;
import org.moltimate.moltimatebackend.repository.FailedAlignmentRespository;
import org.moltimate.moltimatebackend.repository.QueryAlignmentResponseRepository;
import org.moltimate.moltimatebackend.repository.QueryResponseDataRepository;
import org.moltimate.moltimatebackend.repository.SuccessfulAlignmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@Slf4j
public class CacheService {
    @Autowired
    private QueryAlignmentResponseRepository queryAlignmentResponseRepository;

    @Autowired
    private QueryResponseDataRepository queryResponseDataRepository;

    @Autowired
    private SuccessfulAlignmentRepository successfulAlignmentRepository;

    @Autowired
    private FailedAlignmentRespository failedAlignmentRespository;

    public Cache<String, QueryAlignmentResponse> cache = Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(Duration.ofDays(1))
            .removalListener((String key, QueryAlignmentResponse value, RemovalCause cause) -> {
                log.info("Key {} was removed from cache", key);
                assert value != null;
                saveQueryAlignmentResponse(value);
            })
            .build();

    private void saveQueryAlignmentResponse(QueryAlignmentResponse queryAlignmentResponse) {
        queryAlignmentResponse.getEntries()
                .forEach(entry -> {
                    entry.getAlignments()
                            .forEach(success -> successfulAlignmentRepository.save(success));
                    entry.getFailedAlignments()
                            .forEach(failedAlignment -> failedAlignmentRespository.save(failedAlignment));
                    queryResponseDataRepository.save(entry);
                });
        queryAlignmentResponseRepository.save(queryAlignmentResponse);
    }

    public QueryAlignmentResponse findQueryAlignmentResponse(String cacheKey) {
        return queryAlignmentResponseRepository.findByCacheKey(cacheKey);
    }
}
