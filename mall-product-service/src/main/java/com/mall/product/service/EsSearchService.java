package com.mall.product.service;

import com.mall.product.document.ProductDocument;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EsSearchService {

    private final ElasticsearchRestTemplate esTemplate;

    public EsSearchService(ElasticsearchRestTemplate esTemplate) {
        this.esTemplate = esTemplate;
    }

    public List<ProductDocument> search(String keyword, Long categoryId, Long brandId,
                                         String sortBy, int page, int size) {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        // Only show listed products
        boolQuery.must(QueryBuilders.termQuery("status", 1));

        if (keyword != null && !keyword.isEmpty()) {
            boolQuery.must(QueryBuilders.multiMatchQuery(keyword, "name", "description"));
        }
        if (categoryId != null) {
            boolQuery.filter(QueryBuilders.termQuery("categoryId", categoryId));
        }
        if (brandId != null) {
            boolQuery.filter(QueryBuilders.termQuery("brandId", brandId));
        }

        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder()
                .withQuery(boolQuery)
                .withPageable(PageRequest.of(page - 1, size));

        // Sort
        if ("price_asc".equals(sortBy)) {
            queryBuilder.withSorts(SortBuilders.fieldSort("minPrice").order(SortOrder.ASC));
        } else if ("price_desc".equals(sortBy)) {
            queryBuilder.withSorts(SortBuilders.fieldSort("minPrice").order(SortOrder.DESC));
        } else if ("sales_desc".equals(sortBy)) {
            queryBuilder.withSorts(SortBuilders.fieldSort("totalSales").order(SortOrder.DESC));
        }

        NativeSearchQuery searchQuery = queryBuilder.build();
        SearchHits<ProductDocument> hits = esTemplate.search(searchQuery, ProductDocument.class);

        return hits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
    }
}
