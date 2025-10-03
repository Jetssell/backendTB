package pe.gob.salud.tb.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

import java.io.IOException;

/**
 * Sirve recursos estáticos desde classpath:/static y,
 * si no existe el recurso solicitado, devuelve index.html (SPA fallback).
 */
@Configuration
public class SpaWebConfig implements WebMvcConfigurer {

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/**")
        .addResourceLocations("classpath:/static/")
        .resourceChain(true)
        .addResolver(new SpaFallbackResourceResolver());
  }

  static class SpaFallbackResourceResolver extends PathResourceResolver {
    @Override
    protected Resource getResource(String resourcePath, Resource location) throws IOException {
      // Primero intentamos servir el recurso tal cual (assets, index.html, etc.)
      Resource requested = location.createRelative(resourcePath);
      if (requested.exists() && requested.isReadable()) {
        return requested;
      }
      // Si NO existe (deep link tipo /dashboard), devolvemos index.html
      return location.createRelative("index.html");
    }
  }
}
