package tech.build.jbank.exception.dto;

public record InvalidParamDto(String field,
                              String reason) {
}
