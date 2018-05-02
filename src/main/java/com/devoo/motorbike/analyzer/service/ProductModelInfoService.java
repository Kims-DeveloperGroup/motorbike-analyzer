package com.devoo.motorbike.analyzer.service;

import com.devoo.motorbike.analyzer.domain.Model;
import com.devoo.motorbike.analyzer.repository.paxxo.PaxxoModelRepository;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RegexpQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
@Slf4j
public class ProductModelInfoService {
    private final PaxxoModelRepository paxxoModelRepository;

    @Autowired
    public ProductModelInfoService(PaxxoModelRepository paxxoModelRepository) {
        this.paxxoModelRepository = paxxoModelRepository;
    }

    public Optional<Model> findMatchedModelByName(String like) {
        String regexPattern = Pattern.compile(".*" + like + ".*").toString();
        RegexpQueryBuilder matchQueryBuilder =
                QueryBuilders.regexpQuery("name", regexPattern);
        List<Model> result =
                this.paxxoModelRepository.search(matchQueryBuilder, PageRequest.of(0, 1)).getContent();
        if (result.size() > 0) {
            log.debug("Matched model by given input, \'{}\' is \'{}\'", like, result.get(0).getName());
            return Optional.of(result.get(0));
        }
        log.debug("Matched model by given input, \'{}\' is not found", like);
        return Optional.empty();
    }
}