package com.exelynt.booking.resource.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResourceResponseDTO {

    private Long id;

    private String name;

    private String description;

    private String type;

    private BigDecimal price;

    private Boolean available;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}