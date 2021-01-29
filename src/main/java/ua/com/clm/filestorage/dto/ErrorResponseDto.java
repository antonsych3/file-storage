package ua.com.clm.filestorage.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonPropertyOrder({"success", "error"})
public class ErrorResponseDto extends ResponseDto {

    public ErrorResponseDto(boolean isSuccess, String error) {
        super(isSuccess);
        this.error = error;
    }

    private String error;
}