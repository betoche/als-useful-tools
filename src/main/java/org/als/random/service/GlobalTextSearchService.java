package org.als.random.service;

import org.als.random.entity.GlobalTextSearch;
import org.als.random.repository.GlobalTextSearchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GlobalTextSearchService {
    @Autowired
    private GlobalTextSearchRepository globalTextSearchRepository;

    public Iterable<GlobalTextSearch> getSearchHistoryList() {
        return globalTextSearchRepository.findAll();
    }
}
