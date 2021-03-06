package ua.com.cml.filestorage.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import ua.com.cml.filestorage.dto.FileRequestDto;

import java.util.*;

@Data
@NoArgsConstructor
@JsonPropertyOrder({"id", "name", "size", "tags"})
@Document(indexName = "file")
public class File {

    @Id
    private String id;

    @Field(type = FieldType.Text)
    private String name;

    @Field(type = FieldType.Long)
    private long size;

    @Field(type = FieldType.Nested, searchAnalyzer = "true")
    private Set<String> tags = new HashSet<>();

    public File(FileRequestDto fileRequestDto) {
        name = fileRequestDto.getName();
        size = fileRequestDto.getSize();
    }
}
