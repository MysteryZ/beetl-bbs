package com.ibeetl.bbs.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.ibeetl.bbs.es.service.EsService;
import com.ibeetl.bbs.es.service.SearchService;
import com.ibeetl.bbs.lucene.LuceneService;


@Configuration
public class SearchConfig {
	
	
	@Bean
	@ConditionalOnProperty(prefix = "search" , name = "type",havingValue = "es",matchIfMissing = false)
	public SearchService getEs() {
		return new EsService();
	}
	
	@Bean
	@ConditionalOnProperty(prefix = "search" , name = "type",havingValue = "lucene",matchIfMissing = false)
	public SearchService getLucene() {
		return new LuceneService();
	}

}
