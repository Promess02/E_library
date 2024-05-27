package com.mikolaj.e_library.DTO;

import lombok.Data;

@Data
public class AddNewsPostForm {
    private int workerId;
    private int newsPostId;
    private String name;
    private String contents;
    private String imageUrl;
}
