package ua.com.clm.filestorage.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import ua.com.clm.filestorage.model.File;

import java.util.Set;

@Repository
public interface FileRepository extends ElasticsearchRepository<File, String> {

    Page<File> findAllByTagsAndNameContains(Set<String> tags, String nameSubstring, Pageable pageable);

    Page<File> findAllByNameContains(String nameSubstring, PageRequest of);
}
