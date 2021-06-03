package com.flextech.building.service;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.reactive.result.view.freemarker.FreeMarkerConfigurer;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;

@Service
@Slf4j
public class EmailService {

    @Autowired
    private FreeMarkerConfigurer freemarkerConfigurer;

    @Autowired
    private JavaMailSender emailSender;

    public void sendMessage(String from, String to, String subject, String templateName, Map<String, Object> params)
            throws IOException, TemplateException, MessagingException {

        Template freemarkerTemplate = freemarkerConfigurer.getConfiguration()
                .getTemplate(templateName, Locale.JAPANESE, "UTF-8");
        String htmlBody = FreeMarkerTemplateUtils.processTemplateIntoString(freemarkerTemplate, params);
        log.info(htmlBody);
        sendHtmlMessage(from, to, subject, htmlBody);
    }

    private void sendHtmlMessage(String from, String to, String subject, String htmlBody) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom(from);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlBody, true);
        emailSender.send(message);
    }
}
