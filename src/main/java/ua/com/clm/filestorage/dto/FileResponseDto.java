package ua.com.clm.filestorage.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import ua.com.clm.filestorage.model.BaseFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileResponseDto implements BaseFile {

    @Id
    @JsonProperty("ID")
    private String id;
}
