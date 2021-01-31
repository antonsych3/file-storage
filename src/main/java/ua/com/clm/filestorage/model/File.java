package ua.com.clm.filestorage.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import ua.com.clm.filestorage.dto.FileRequestDto;

import java.util.*;

@Data
@NoArgsConstructor
@JsonPropertyOrder({"id", "name", "size", "tags"})
@Document(indexName = "file")
public class File implements BaseFile {

    @Id
    private String id;

    @Field(type = FieldType.Text)
    private String name;

    @Field(type = FieldType.Long)
    private long size;

    @Field(type = FieldType.Nested)
    private List<String> tags = new ArrayList<>();

    public File(FileRequestDto fileRequestDto) {
        name = fileRequestDto.getName();
        size = fileRequestDto.getSize();
    }
}
