package com.arounders.web.serviceImpl;

import com.arounders.web.dto.RequestMail;
import com.arounders.web.entity.EmailAuth;
import com.arounders.web.entity.Member;
import com.arounders.web.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@RequiredArgsConstructor
@Service
public class MailServiceImpl implements MailService {

    private final JavaMailSender mailSender;

    @Override
    public Long sendMail(RequestMail mail) {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(message, "utf-8");
        String htmlMsg = "";
        try {
            messageHelper.setText(htmlMsg, true);
            messageHelper.setTo("jihwaang@naver.com");
            messageHelper.setSubject("Email Test");
            messageHelper.setFrom("Arounders");

            mailSender.send(message);
            return 1L;
        } catch (MessagingException e) {
            e.printStackTrace();
            return -1L;
        }
    }

    @Override
    public Long sendAuthMail(EmailAuth mail) {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(message, "utf-8");
        String url = "https://arounders.herokuapp.com/emailAuth/confirm?confirmKey="+mail.getConfirmKey();
        String onClick = "onclick=\"alert('회원가입을 마무리해주세요 :)')\"";
        String htmlMsg = "<form style=\"width: 550px;\n" +
                "      padding: 2em; border: 1px solid black;\n" +
                "      display: flex;\n" +
                "      flex-direction: column;\n" +
                "      place-items: center;\">\n" +
                "    <h1>Arounders에 오신것을 환영합니다.</h1>\n" +
                "    <p>회원가입 인증을 위해 아래 인증버튼을 눌러주세요.</p></br></br>\n" +
                /*"    <div style=\"width: 200px;\n" +
                "        height: 50px;\n" +
                "        background-color: skyblue;\n" +
                "        border: none;\n" +
                "        color: #fff;\n" +
                "        font-size: 1rem;\n" +
                "        cursor: pointer;\n" +
                "        display: flex;\n" +
                "        align-items: center;\n" +
                "        justify-content: center;\">\n" +*/
                "        <a href=\""+url+"\" style=\"color: #fff; text-decoration: none; font-weight: bold; display: block; width: 200px; height: 50px; background-color: skyblue; padding: 1em; margin-top: 3em;\">인증하기</a>\n" +
                "    </div>\n" +
                "\n" +
                "\n" +
                "</form>"
                ;
        try {
            messageHelper.setText(htmlMsg, true);
            messageHelper.setTo(mail.getEmail());
            messageHelper.setSubject("Arounders Confirmation Request");
            messageHelper.setFrom("Arounders");

            mailSender.send(message);
            return 1L;
        } catch (MessagingException e) {
            e.printStackTrace();
            return -1L;
        }
    }

    @Override
    public int sendNewPassword(Member memberEntity) {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(message, "utf-8");
        String htmlMsg = "<form style=\"width: 550px;\n" +
                "      height: 200px;\n" +
                "      display: flex;\n" +
                "      flex-direction: column;\n" +
                "      place-items: center;\">\n" +
                "    <h1>Arounder를 이용해주셔서 감사합니다.</h1>\n" +
                "    <p>변경된 임시비밀번호는 아래와 같습니다.</p></br></br>\n" +
                "    <p>"+memberEntity.getPassword()+"</p>"+
                "    </div>\n" +
                "\n" +
                "\n" +
                "</form>"
                ;
        try {
            messageHelper.setText(htmlMsg, true);
            messageHelper.setTo(memberEntity.getEmail());
            messageHelper.setSubject("Arounders Temporary Password Notification");
            messageHelper.setFrom("Arounders");

            mailSender.send(message);
            return 1;
        } catch (MessagingException e) {
            e.printStackTrace();
            return -1;
        }
    }

}
