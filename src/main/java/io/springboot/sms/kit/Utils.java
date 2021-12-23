package io.springboot.sms.kit;

import com.aliyuncs.CommonResponse;
import com.google.gson.Gson;
import io.springboot.sms.core.SmsException;
import io.springboot.sms.core.SmsTemplate;
import lombok.experimental.UtilityClass;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 工具类，内部使用.
 *
 * @author cn-src
 */
@UtilityClass
public class Utils {

	private static final String SUCCESS_CODE = "OK";

	/**
	 * 宽松校验即可.
	 */
	private static final String PHONE_NUMBER_REGEX = "\\d{5,}";

	/**
	 * 生成随机验证码.
	 * @return 随机数
	 */
	public int randomCode() {
		return 100_000 + ThreadLocalRandom.current().nextInt(1_000_000 - 100_000);
	}

	/**
	 * Map 转 json 字符串的简单实现.
	 * @param map the map
	 * @return the json string
	 */
	public String toJsonStr(final Map<String, String> map) {
		if (null == map || map.isEmpty()) {
			return null;
		}

		final StringBuilder sb = new StringBuilder();
		sb.append('{');
		for (final Map.Entry<String, String> entry : map.entrySet()) {
			sb.append('"').append(entry.getKey().replace("\"", "\\\"")).append('"').append(':').append('"')
					.append(entry.getValue().replace("\"", "\\\"")).append('"').append(',');
		}
		sb.deleteCharAt(sb.length() - 1);
		sb.append('}');
		return sb.toString();
	}

	/**
	 * 校验 SmsTemplate.
	 * @param template the SmsTemplate
	 */
	public void checkSmsTemplate(final SmsTemplate template) {

		checkNotEmpty(template.getSignName(), "SmsTemplate signName must be not empty");
		checkNotEmpty(template.getTemplateCode(), "SmsTemplate templateCode must be not empty");
		checkNotEmpty(template.getPhoneNumbers(), "SmsTemplate phoneNumbers must be not empty");

	}

	/**
	 * 校验 SendSmsResponse 状态.
	 * @param response the SendSmsResponse
	 */
	public void checkSmsResponse(final CommonResponse response) {
		if (null == response) {
			throw new SmsException("Response is null");
		}
		final Gson gson = new Gson();
		final Map<String, String> json = gson.fromJson(response.getData(), Map.class);
		if (!SUCCESS_CODE.equalsIgnoreCase(json.get("Code"))) {
			throw new SmsException("Http status: " + response.getHttpStatus() + ", response: " + response.getData());
		}
	}

	/**
	 * 校验手机号码（中国）.
	 * @param phoneNumbers the phone numbers
	 */
	public void checkPhoneNumber(final String... phoneNumbers) {

		if (phoneNumbers.length == 0) {
			throw new IllegalArgumentException("Invalid phone size");
		}

		for (String phoneNumber : phoneNumbers) {
			if (!phoneNumber.matches(PHONE_NUMBER_REGEX)) {
				throw new IllegalArgumentException("Invalid phone number " + phoneNumber);
			}
		}
	}

	/**
	 * 校验字符串不为空.
	 * @param str the str
	 * @param message the message
	 */
	public void checkNotEmpty(final String str, final String message) {
		if (null == str || str.isEmpty()) {
			throw new IllegalArgumentException(message);
		}
	}

	/**
	 * 校验集合不为空.
	 * @param coll the Collection
	 * @param message the message
	 */
	public void checkNotEmpty(final Collection coll, final String message) {
		if (null == coll || coll.isEmpty()) {
			throw new IllegalArgumentException(message);
		}
	}

}
