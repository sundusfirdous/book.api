package com.book.api.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookDto {

    private Long isbn;

    @NotBlank(message = "This field is required!")
    private String title;

    @NotBlank(message = "This field is required!")
    private String author;

    @NotBlank(message = "This field is required!")
    private String description;

    @NotBlank(message = "This field is required!")
    private String category;

    @NotNull(message = "This field is required")
    private Double price;

    @NotNull(message = "This field is required")
    private Integer quantity;

    private String bookCover;

    private String bookCoverUrl;
}