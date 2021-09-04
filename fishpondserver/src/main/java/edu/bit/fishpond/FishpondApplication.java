package edu.bit.fishpond;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = {"edu.bit.fishpond.DAO.mapper"})
public class FishpondApplication {

    public static void main(String[] args) {
        SpringApplication.run(FishpondApplication.class, args);
    }

}
