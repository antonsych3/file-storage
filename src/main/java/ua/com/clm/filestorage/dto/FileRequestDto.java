package ua.com.clm.filestorage.dto;

import lombok.Data;

@Data
public class FileRequestDto {

    private String name;
    private long size;

    @Override
    public String toString() {
        return "{" + "\"name\":" + name + "\", \"size\":" + size + "}";
    }
}
