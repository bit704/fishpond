package edu.bit.fishpondops;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = {"edu.bit.fishpondops.DAO.mapper"})
public class FishpondOpsApplication {

    public static void main(String[] args) {
        SpringApplication.run(FishpondOpsApplication.class, args);
    }

}
