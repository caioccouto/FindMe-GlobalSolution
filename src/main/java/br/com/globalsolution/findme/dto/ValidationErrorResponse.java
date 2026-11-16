package br.com.globalsolution.findme.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ValidationErrorResponse(LocalDateTime timestamp, int status, String error, List<ErrorFieldCustom> errors) {
}
