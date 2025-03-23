package ca.mb.wcb.wpmonitor;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

@Service
public class WebsiteMonitorService {

    private static final Logger logger = LoggerFactory.getLogger(WebsiteMonitorService.class);
    private final JavaMailSender mailSender;
    private final RestTemplate restTemplate;

    @Value("${monitor.url}")
    private String websiteUrl;

    @Value("${monitor.text}")
    private String monitorText;

    @Value("${alert.email}")
    private String alertEmail;
    
    public WebsiteMonitorService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
        this.restTemplate = new RestTemplate();
    }

    @Scheduled(fixedRateString = "${monitor.checkIntervalHours:1}", timeUnit = TimeUnit.HOURS) // Check every 1 hour if not specified
    public void checkWebsite() {
        try {
            String pageContent = getPageContent();
            if (pageContent == null) return;

            if (!pageContent.contains(this.monitorText)) {
                logger.warn("ALERT: '{}' not found on page!", this.monitorText);
                sendEmailAlert();
            } else {
                logger.info("'{}' is present on the page.", this.monitorText);
            }

        } catch (Exception e) {
            logger.error("Error while checking website content", e);
        }
    }

    private String getPageContent() {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(websiteUrl, HttpMethod.GET, entity, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        } else {
            logger.error("Failed to fetch website content. Status: {}", response.getStatusCode());
            return null;
        }
    }

    private void sendEmailAlert() {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(alertEmail);
        message.setSubject("Website Alert: Expected Text Missing");
        message.setText("The monitored website is missing the expected text: '" + this.monitorText + "'.\nCheck the page: " + websiteUrl);

        mailSender.send(message);
        logger.info("Email alert sent!");
    }
}
