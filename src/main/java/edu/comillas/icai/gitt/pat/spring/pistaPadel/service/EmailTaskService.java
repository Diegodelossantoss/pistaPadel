package edu.comillas.icai.gitt.pat.spring.pistaPadel.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service // Esto le dice a Spring que es un componente de servicio
public class EmailTaskService {
    private static final Logger logger = LoggerFactory.getLogger(EmailTaskService.class);

    // Se ejecuta cada noche a las 2:00 AM [cite: 329]
    @Scheduled(cron = "0 0 2 * * *")
    public void recordatorioReservas() {
        logger.info("Tarea Programada: Enviando recordatorios de reservas para hoy..."); [cite: 327]
        // Aquí se implementaría el envío real de email más adelante
    }

    // Se ejecuta el primer día de cada mes [cite: 330]
    @Scheduled(cron = "0 0 0 1 * *")
    public void disponibilidadMensual() {
        logger.info("Tarea Programada: Enviando disponibilidad mensual a los usuarios..."); [cite: 327]
    }
}