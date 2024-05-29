package com.thread_exec.thread_executor.model;

import com.univocity.parsers.annotations.Format;
import com.univocity.parsers.annotations.Parsed;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Date;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StoreData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Parsed(field = "invoice_number")
    private String inVoiceNumber;

    @Parsed(field = "invoice_date")
    private String inDate;

    @Parsed(field = "gender")
    private String gender;

    @Parsed(field = "age")
    private Integer age;

    @Parsed(field = "category")
    private String category;

    @Parsed(field = "quantity")
    private Integer quantity;

    @Parsed(field = "selling_price_per_unit")
    private double sellingPricePerUnit;

    @Parsed(field = "total_profit")
    private double totalProfit;

    @Parsed(field = "payment_method")
    private String paymentMethod;

    @Parsed(field = "region")
    private String region;

    @Parsed(field = "state")
    private String state;

    @Parsed(field = "shopping_mall")
    private String shoppingMall;

}
