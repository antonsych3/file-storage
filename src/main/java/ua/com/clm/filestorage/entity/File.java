package ua.com.clm.filestorage.entity;

import lombok.Data;

import java.util.List;

@Data
public class File {

    private String id;
    private String name;
    private long size;
    private List<String> tags;
}
