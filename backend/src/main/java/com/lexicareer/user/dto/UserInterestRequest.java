package com.lexicareer.user.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInterestRequest {
    
    @NotNull(message = "CategoryId é obrigatório")
    private Long categoryId;
}
