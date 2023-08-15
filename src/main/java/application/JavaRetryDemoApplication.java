package application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@EnableRetry
@SpringBootApplication
public class JavaRetryDemoApplication {
    public static void main(String [] args){
        SpringApplication.run(JavaRetryDemoApplication.class,args);
    }

}
