package com.JavaBackEnd.spring_boot_journey_Day_6;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class SpringBootJourneyApplication {
    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(SpringBootJourneyApplication.class, args);
        
        System.out.println("\n✅ Application Started Successfully!");
        System.out.println("📊 Total beans: " + context.getBeanDefinitionCount());
        
        // Check specific beans
        String[] keyBeans = {
            "dispatcherServlet",
            "requestMappingHandlerAdapter",
            "jacksonObjectMapper",
            "defaultServletHandlerMapping",
            "errorPageCustomizer"
        };
        
        System.out.println("\n📋 Looking for 5 auto-configured beans:");
        for (String beanName : keyBeans) {
            try {
                Object bean = context.getBean(beanName);
                System.out.println("  ✅ " + beanName + " → " + bean.getClass().getSimpleName());
            } catch (Exception e) {
                System.out.println("  ❌ " + beanName + " → Not found");
            }
        }
        
        // Check ObjectMapper
        ObjectMapper mapper = context.getBean(ObjectMapper.class);
        System.out.println("\n🔧 ObjectMapper used: " + mapper.getClass().getSimpleName());
    }
}