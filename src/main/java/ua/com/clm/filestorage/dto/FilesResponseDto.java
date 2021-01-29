package ua.com.clm.filestorage.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import ua.com.clm.filestorage.entity.File;

import java.util.List;

@Data
public class FilesResponseDto {

    private int total;
    @JsonProperty("page")
    private List<File> files;
}
