package com.thread_exec.thread_executor.model;

import com.univocity.parsers.annotations.Parsed;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerInfo {
    @Parsed(field = "id")
    private String id;
    @Parsed(field = "name")
    private String name;
    @Parsed(field = "mobile")
    private String mobile;
    @Parsed(field = "email")
    private String email;
    @Parsed(field = "address")
    private String address;
    @Parsed(field = "city")
    private String city;
    @Parsed(field = "state")
    private String state;
    @Parsed(field = "pincode")
    private Integer pincode;
}
