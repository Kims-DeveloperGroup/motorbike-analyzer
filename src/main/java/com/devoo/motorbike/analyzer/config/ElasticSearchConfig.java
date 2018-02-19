package com.devoo.motorbike.analyzer.config;


import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Configuration
@EnableElasticsearchRepositories(basePackages = {"com.devoo.motorbike.analyzer.repository.paxxo", "com.devoo.motorbike.analyzer.repository.naver"})
public class ElasticSearchConfig {

    @Value("${elasticsearch.host}")
    private String esHost;

    @Value("${elasticsearch.port}")
    private int esPort;

    @Value("${elasticsearch.clustername}")
    private String esClusterName;

    @Bean
    public ElasticsearchOperations elasticsearchTemplate() throws UnknownHostException {
        return new ElasticsearchTemplate(esClient());
    }

    @Bean
    public Client esClient() throws UnknownHostException {
        Settings esSettings = Settings.builder()
                .put("cluster.name", esClusterName)
                .build();
        return new PreBuiltTransportClient(esSettings)
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(esHost), esPort));
    }
}