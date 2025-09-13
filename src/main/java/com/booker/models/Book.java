package com.booker.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "books")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Book {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String synopsis;
    
    @Column(name = "page_count")
    private Integer pageCount;
    
    @Column(name = "author_id", nullable = false)
    private Long authorId;
    
    @Column(name = "cover_url")
    private String coverUrl;
}
