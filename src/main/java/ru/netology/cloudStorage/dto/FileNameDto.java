package ru.netology.cloudStorage.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@Data
@NoArgsConstructor
public class FileNameDto {
    @NotBlank
    private String filename;

    @JsonCreator
    public FileNameDto(String filename) {
        this.filename = filename;
    }
}