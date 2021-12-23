package io.springboot.sms.core;

import lombok.*;

import java.util.List;
import java.util.Map;

/**
 * 阿里云 SMS 短信模板.
 *
 * @author cn-src
 */
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SmsTemplate {

	private String accessKeyId;

	private String accessKeySecret;

	private String signName;

	private String proxyIp;

	private Integer proxyPort;

	private String templateCode;

	private Map<String, String> templateParam;

	private List<String> phoneNumbers;

}
