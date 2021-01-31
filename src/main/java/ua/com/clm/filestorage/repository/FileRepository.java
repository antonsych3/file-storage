package ua.com.clm.filestorage.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import ua.com.clm.filestorage.model.File;

import java.util.List;

@Repository
public interface FileRepository extends ElasticsearchRepository<File, String> {

    Page<File> findAllByTags(List<String> tags, Pageable pageable);


    @Query("{\"bool\": {\"must\": [{\"match\": {\"tags\": \"?0\"}}]}}")
    Page<File> findByTagUsingDeclaredQuery(List<String> tags, Pageable pageable);
}
