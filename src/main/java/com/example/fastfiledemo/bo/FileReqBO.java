package com.example.fastfiledemo.bo;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class FileReqBO{

    @NotBlank
    private String filename;

    @NotNull
    private Long size;

}
