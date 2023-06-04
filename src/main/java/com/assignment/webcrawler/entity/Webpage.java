package com.assignment.webcrawler.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="webpage", indexes = {
        @Index(columnList = "url")
})
public class Webpage {

    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "APIUSERS_PK_SEQ")
    private Long id;

    @Column(nullable = false, unique = true, length = 4096)
    private String url;

    @Column(length = 4096)
    private String title;

}