package ua.com.cml.filestorage.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonPropertyOrder({"name", "size"})
public class FileRequestDto {

    private String name;
    private Long size;

    @Override
    public String toString() {
        return "{" + "\"name\":" + name + "\", \"size\":" + size + "}";
    }
}
