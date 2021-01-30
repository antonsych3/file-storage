package ua.com.clm.filestorage.dto;

import lombok.Data;
import ua.com.clm.filestorage.model.BaseFile;

@Data
public class FileRequestDto implements BaseFile {

    private String name;
    private long size;

    @Override
    public String toString() {
        return "{" + "\"name\":" + name + "\", \"size\":" + size + "}";
    }
}
