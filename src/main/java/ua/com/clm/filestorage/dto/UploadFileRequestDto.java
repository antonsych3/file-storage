package ua.com.clm.filestorage.dto;

import lombok.Data;

@Data
public class UploadFileRequestDto {

    private String name;
    private Long size;
}
