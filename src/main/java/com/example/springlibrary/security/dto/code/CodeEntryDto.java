package com.example.springlibrary.security.dto.code;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;


@Data
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class CodeEntryDto {
    private String code;
    private final long timestamp;
}
