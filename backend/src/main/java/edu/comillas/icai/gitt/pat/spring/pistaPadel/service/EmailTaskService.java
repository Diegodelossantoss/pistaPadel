package edu.comillas.icai.gitt.pat.spring.pistaPadel.service;

import edu.comillas.icai.gitt.pat.spring.pistaPadel.model.Reserva;
import edu.comillas.icai.gitt.pat.spring.pistaPadel.model.Usuario;
import edu.comillas.icai.gitt.pat.spring.pistaPadel.repository.ReservaRepository;
import edu.comillas.icai.gitt.pat.spring.pistaPadel.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class EmailTaskService {
    private static final Logger logger = LoggerFactory.getLogger(EmailTaskService.class);

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private UserRepository userRepository;

    @Scheduled(cron = "0 0 2 * * *")
    public void recordatorioReservas() {
        logger.info("Tarea Programada: Enviando recordatorios de reservas para hoy...");
        LocalDate hoy = LocalDate.now();

        List<Reserva> reservasHoy = reservaRepository.findAll().stream()
                .filter(r -> r.getFechaReserva().equals(hoy) && "ACTIVA".equals(r.getEstado()))
                .toList();

        for (Reserva reserva : reservasHoy) {
            userRepository.findById(reserva.getIdUsuario()).ifPresent(usuario -> {
                SimpleMailMessage msg = new SimpleMailMessage();
                msg.setTo(usuario.getEmail());
                msg.setSubject("Recordatorio de tu reserva hoy");
                msg.setText("Hola " + usuario.getNombre() + ", tienes una pista reservada hoy a las " + reserva.getHoraInicio());
                mailSender.send(msg);
                logger.info("Recordatorio enviado a {}", usuario.getEmail());
            });
        }
    }

    @Scheduled(cron = "0 0 0 1 * *")
    public void disponibilidadMensual() {
        logger.info("Tarea Programada: Enviando disponibilidad mensual...");
        List<Usuario> usuarios = userRepository.findAll();

        for (Usuario usuario : usuarios) {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(usuario.getEmail());
            msg.setSubject("Disponibilidad de pistas este mes");
            msg.setText("Hola " + usuario.getNombre() + ", ya puedes reservar tus pistas para este mes en nuestra app.");
            mailSender.send(msg);
        }
    }
}