package edu.comillas.icai.gitt.pat.spring.pistaPadel.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class EmailTaskService {
    private static final Logger logger = LoggerFactory.getLogger(EmailTaskService.class);

    // Se ejecuta cada día a las 2:00 AM
    @Scheduled(cron = "0 0 2 * * *")
    public void recordatorioReservas() {
        logger.info("Tarea Programada: Enviando recordatorios de reservas para hoy...");
    }

    // Se ejecuta el primer día del mes a las 00:00
    @Scheduled(cron = "0 0 0 1 * *")
    public void disponibilidadMensual() {
        logger.info("Tarea Programada: Enviando disponibilidad mensual a los usuarios...");
    }
}