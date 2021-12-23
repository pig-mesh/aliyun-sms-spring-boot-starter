/*
 * Copyright (c) 2018 the original author or authors.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.springboot.sms.core;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.HttpClientConfig;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.aliyuncs.utils.StringUtils;
import io.springboot.sms.SmsProperties;
import io.springboot.sms.kit.Utils;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

import static io.springboot.sms.kit.Utils.checkPhoneNumber;
import static io.springboot.sms.kit.Utils.checkSmsResponse;

/**
 * 阿里云 SMS 客户端.
 *
 * @author cn-src
 */
@Slf4j
public class SmsClient {

	/**
	 * key 是阿里云 的templateCode
	 */
	private Map<String, IAcsClient> acsClients = new HashMap<>();

	private Map<String, SmsTemplate> smsTemplates = new HashMap<>();

	public SmsClient(SmsProperties smsProperties) {
		if (smsProperties.getSms() == null) {
			log.warn("阿里云短信通道未配置,短信发送暂不可用");
			return;
		}

		this.smsTemplates = smsProperties.getSms();

		smsTemplates.forEach((k, v) -> {
			String accessKeyId = v.getAccessKeyId();
			String accessKeySecret = v.getAccessKeySecret();
			final IClientProfile clientProfile = DefaultProfile.getProfile("default", accessKeyId, accessKeySecret);

			// 设置代理参数
			if (!StringUtils.isEmpty(v.getProxyIp())) {
				HttpClientConfig clientConfig = HttpClientConfig.getDefault();
				clientConfig.setHttpProxy(String.format("http://%s:%s", v.getProxyIp(), v.getProxyPort()));
				clientConfig.setHttpsProxy(String.format("http://%s:%s", v.getProxyIp(), v.getProxyPort()));
				clientProfile.setHttpClientConfig(clientConfig);
			}

			IAcsClient acsClient = new DefaultAcsClient(clientProfile);
			this.acsClients.put(k, acsClient);
		});

	}

	/**
	 * 发送短信验证码. (使用第一个模板)
	 * @param phoneNumbers 手机号码(中国)
	 * @param code 验证码
	 * @return 6 位数的随机码
	 */
	public String sendVerificationCode(final String code, final String... phoneNumbers) {
		checkSmsTemplate();
		checkPhoneNumber(phoneNumbers);
		int size = smsTemplates.size();
		if (size == 0) {
			throw new IllegalArgumentException("Invalid Templates");
		}

		String smsTemplateKey = smsTemplates.keySet().iterator().next();
		final SmsTemplate smsTemplate = this.smsTemplates.get(smsTemplateKey);
		Objects.requireNonNull(smsTemplate, () -> "SmsTemplate must be not null, key:" + smsTemplateKey);

		smsTemplate.setTemplateCode(smsTemplateKey);
		smsTemplate.setTemplateParam(Collections.singletonMap("code", code));
		smsTemplate.setPhoneNumbers(Arrays.asList(phoneNumbers));
		send(smsTemplate);
		return code;
	}

	/**
	 * 校验是否可用
	 */
	private boolean checkSmsTemplate() {
		if (this.smsTemplates.isEmpty()) {
			log.warn("阿里云短信通道配置错误,短信发送暂不可用,验证码注意 日志输出");
			return false;
		}
		else {
			return true;
		}
	}

	/**
	 * 发送短信验证码.(通过短信模板key)
	 * @param smsTemplateKey 模板key
	 * @param phoneNumbers 手机号码(中国)
	 * @param code 验证码
	 * @return 验证码
	 */
	public String sendCodeByKey(final String smsTemplateKey, final String code, final String... phoneNumbers) {
		if (!checkSmsTemplate()) {
			return null;
		}
		checkPhoneNumber(phoneNumbers);
		final SmsTemplate smsTemplate = this.smsTemplates.get(smsTemplateKey);
		Objects.requireNonNull(smsTemplate, () -> "SmsTemplate must be not null, key:" + smsTemplateKey);

		smsTemplate.setTemplateCode(smsTemplateKey);
		smsTemplate.setTemplateParam(Collections.singletonMap("code", code));
		smsTemplate.setPhoneNumbers(Arrays.asList(phoneNumbers));
		send(smsTemplate);
		return code;
	}

	/**
	 * 发送短信验证码.(通过短信模板key)
	 * @param smsTemplateKey 模板key
	 * @param phoneNumbers 手机号码(中国)
	 * @param params 模板参数
	 */
	public void sendParamByKey(final String smsTemplateKey, final Map<String, String> params,
			final String... phoneNumbers) {
		if (!checkSmsTemplate()) {
			return;
		}
		checkPhoneNumber(phoneNumbers);
		final SmsTemplate smsTemplate = this.smsTemplates.get(smsTemplateKey);
		Objects.requireNonNull(smsTemplate, () -> "SmsTemplate must be not null, key:" + smsTemplateKey);

		smsTemplate.setTemplateCode(smsTemplateKey);
		smsTemplate.setTemplateParam(params);
		smsTemplate.setPhoneNumbers(Arrays.asList(phoneNumbers));
		send(smsTemplate);
	}

	/**
	 * 发送短信.
	 * @param smsTemplateKey 预置短信模板 key
	 */
	public void send(final String smsTemplateKey) {
		if (!checkSmsTemplate()) {
			return;
		}
		final SmsTemplate smsTemplate = this.smsTemplates.get(smsTemplateKey);
		smsTemplate.setTemplateCode(smsTemplateKey);
		Objects.requireNonNull(smsTemplate, () -> "SmsTemplate must be not null, key:" + smsTemplateKey);

		send(smsTemplate);
	}

	/**
	 * 发送短信.
	 * @param smsTemplateKey 预置短信模板 key
	 * @param phoneNumbers 手机号码，优先于预置短信模板中配置的手机号码
	 */
	public void send(final String smsTemplateKey, final String... phoneNumbers) {
		if (!checkSmsTemplate()) {
			return;
		}
		final SmsTemplate smsTemplate = this.smsTemplates.get(smsTemplateKey);
		smsTemplate.setTemplateCode(smsTemplateKey);
		Objects.requireNonNull(smsTemplate, () -> "SmsTemplate must be not null, key:" + smsTemplateKey);

		smsTemplate.setPhoneNumbers(Arrays.asList(phoneNumbers));
		send(smsTemplate);
	}

	/**
	 * 发送短信.
	 * @param smsTemplate 短信模板
	 */
	public void send(final SmsTemplate smsTemplate) {
		Objects.requireNonNull(smsTemplate);
		if (!checkSmsTemplate()) {
			return;
		}

		final CommonRequest request = new CommonRequest();
		request.setSysMethod(MethodType.POST);
		request.setSysDomain("dysmsapi.aliyuncs.com");
		request.setSysVersion("2017-05-25");
		request.setSysAction("SendSms");
		request.putQueryParameter("PhoneNumbers", String.join(",", smsTemplate.getPhoneNumbers()));
		request.putQueryParameter("SignName", smsTemplate.getSignName());
		request.putQueryParameter("TemplateCode", smsTemplate.getTemplateCode());
		request.putQueryParameter("TemplateParam", Utils.toJsonStr(smsTemplate.getTemplateParam()));
		try {
			IAcsClient iAcsClient = acsClients.get(smsTemplate.getTemplateCode());
			final CommonResponse response = iAcsClient.getCommonResponse(request);
			checkSmsResponse(response);
		}
		catch (final ClientException e) {
			throw new SmsException(e);
		}
	}

}
