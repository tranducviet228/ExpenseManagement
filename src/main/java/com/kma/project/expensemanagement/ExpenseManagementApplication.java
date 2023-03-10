package com.kma.project.expensemanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.kma.project.expensemanagement"})
public class ExpenseManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExpenseManagementApplication.class, args);
    }

}
