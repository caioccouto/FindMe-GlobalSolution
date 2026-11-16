package br.com.globalsolution.findme.configurations;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig {

    public static final String ALERTAS_PUBLICOS = "alertas-publicos";
    public static final String ALERTA_PUBLICO = "alerta-publico";
    public static final String ALERTAS_RECENTES = "alertas-recentes";
    public static final String CASOS_ESTATISTICAS = "casos-estatisticas";
}
