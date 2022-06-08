package com.example.reweb.entity;

import com.example.reweb.annotation.Column;
import com.example.reweb.annotation.Id;
import com.example.reweb.annotation.Table;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "products")
public class Product {
    @Id(AutoIncrement = true)
    @Column(name = "id", type = "INT")
    private Integer id;
    @Column(name = "name", type = "VARCHAR(255)")
    private String name;
    @Column(name = "price", type = "DECIMAL")
    private BigDecimal price;
}
