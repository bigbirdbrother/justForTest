package com.uxsino.entity;

import lombok.*;

@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Dog {

    @Setter
    @Getter
    private String name;

    @Setter
    @Getter
    private int id;

}
