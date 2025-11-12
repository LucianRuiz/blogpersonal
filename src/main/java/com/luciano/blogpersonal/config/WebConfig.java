package com.luciano.blogpersonal.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuración web para el proyecto
 * Maneja CORS, recursos estáticos y otras configuraciones web
 */

@Configuration
public class WebConfig implements WebMvcConfigurer {
    /**
     * Configuración de CORS (Cross-Origin Resource Sharing)
     * Permite que el frontend se comunique con el backend desde diferentes dominios
     */
    @Override
    public void addCorsMappings(CorsRegistry registry){
        registry.addMapping("/api/**") //Agrega la siguiente ruta a la configuración cors
                .allowedOrigins(//Permite solicitudes solo desde los siguientes origenes
                        "http://localhost:3000",
                        "http://localhost:4200",
                        "http://localhost:8081",
                        "http://localhost:5173",
                        "http://mi-dominio.com"
                )
                .allowedMethods("GET","POST","PUT","DELETE","OPTIONS","PATCH") //permitir la consulta de los metodos solo desde los origenes mencionados
                .allowedHeaders("*") //Permite cualquier tipo de header en las solicitudes
                .allowCredentials(true) //Permite mandar cookies en las solicitudes
                .maxAge(3600); //Tiempo de expiración para solicitud OPTION.
    }
    /**
     * Configuración de manejo de recursos estáticos
     * Para servir imágenes, CSS, JS y otros archivos estáticos
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry){
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/")
                .setCachePeriod(3600);

        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/")
                .setCachePeriod(3600);

        registry.addResourceHandler("/swagger-ui/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/swagger-ui/")
                .setCachePeriod(3600);
    }



}
