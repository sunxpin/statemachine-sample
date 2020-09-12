package com.baoxian.common.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.util.Date;
import java.util.Properties;

public class MailUtil {
	private static Logger logger = LoggerFactory.getLogger(MailUtil.class);

	public static void send(Properties props, String server, String from, String password, String to, String cc,
			String title, String subject, String content, String[] filenames, String[] files) {
		Session session = null;
		String username = from;
		// String username = from.substring(0, from.indexOf("@"));
		session = Session.getDefaultInstance(props, null);
		// session.setDebug(true);
		Transport trans = null;
		try {
			Message msg = new MimeMessage(session);
			Address from_address = new InternetAddress(from, title);
			msg.setFrom(from_address);
			if (StringUtils.isBlank(to)) {
				logger.info("收件人不能为空");
				return;
			}
			String[] tos = to.split(";");
			InternetAddress[] toAddress = new InternetAddress[tos.length];
			for (int i = 0; i < tos.length; i++) {
				toAddress[i] = new InternetAddress(tos[i]);
			}
			msg.setRecipients(Message.RecipientType.TO, toAddress);

			if (StringUtils.isNotBlank(cc)) {
				String[] ccs = cc.split(";");
				InternetAddress[] ccAddress = new InternetAddress[ccs.length];
				for (int i = 0; i < ccs.length; i++) {
					ccAddress[i] = new InternetAddress(ccs[i]);
				}
				msg.setRecipients(Message.RecipientType.CC, ccAddress);
			}

			msg.setSubject(subject);
			Multipart mp = new MimeMultipart();
			MimeBodyPart mbp = new MimeBodyPart();
			mbp.setContent(content.toString(), "text/html;charset=GB2312");
			mp.addBodyPart(mbp);
			if (files != null && files.length > 0)
				for (int i = 0; i < files.length; i++) {
					BodyPart mdp = new MimeBodyPart();
					FileDataSource fds = new FileDataSource(files[i]);
					DataHandler dh = new DataHandler(fds);
					mdp.setFileName(MimeUtility.encodeWord(filenames[i], "GB2312", null));
					mdp.setDataHandler(dh);
					mp.addBodyPart(mdp);
				}

			msg.setContent(mp); // Multipart加入到信件
			msg.setSentDate(new Date()); // 设置信件头的发送日期
			msg.saveChanges(); // 发送信件
			trans = session.getTransport("smtp");
			trans.connect(server, username, password);
			trans.sendMessage(msg, msg.getAllRecipients());
			trans.close();
		} catch (Exception e) {
			logger.error("发送邮件失败", e);
		}
	}

	public static void send(String to, String cc, String title, String subject, String content, String[] filenames,
			String[] files) {
		String host = PropertiesUtil.get("email.host");
		String sender = PropertiesUtil.get("email.sender");
		String password = PropertiesUtil.get("email.password");
		String port = PropertiesUtil.get("email.port");
		Properties props = new Properties();
		props.put("mail.transport.protocol", "SMTP");
		if (StringUtils.isNotBlank(port))
			props.put("mail.smtp.port", port);
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.auth", "false");
		send(props, host, sender, password, to, cc, title, subject, content, filenames, files);
	}

	public static void send(String to, String cc, String title, String subject, String content) {
		send(to, cc, title, subject, content, null, null);
	}

	public static void send(String to, String title, String subject, String content) {
		send(to, null, title, subject, content, null, null);
	}
	
}