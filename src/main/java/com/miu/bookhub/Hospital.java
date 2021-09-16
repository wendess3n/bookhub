package com.miu.bookhub;

import lombok.Data;

import java.util.List;

@Data
public class Hospital {

    private List<Department> departments;
}
