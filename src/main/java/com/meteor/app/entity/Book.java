package com.meteor.app.entity;

import lombok.Data;
import lombok.ToString;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("B")
@Data
@ToString
public class Book extends Item{
    private String author;
    private String isbn;
}
