package com.su.es;

import com.su.pojo.Talk;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TalkRepository extends ElasticsearchRepository<Talk, String> {
}