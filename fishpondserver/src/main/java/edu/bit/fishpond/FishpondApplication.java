package edu.bit.fishpond;

import com.alicp.jetcache.anno.config.EnableCreateCacheAnnotation;
import com.alicp.jetcache.anno.config.EnableMethodCache;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = {"edu.bit.fishpond.DAO.mapper"})
@EnableMethodCache(basePackages = {"edu.bit.fishpond.DAO"})
@EnableCreateCacheAnnotation
public class FishpondApplication {

    public static void main(String[] args) {
        SpringApplication.run(FishpondApplication.class, args);
    }

}
