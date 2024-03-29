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

package io.springboot.sms;

import io.springboot.sms.core.SmsClient;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 阿里云 SMS 自动配置.
 *
 * @author cn-src
 */
@AutoConfiguration
@Configuration(proxyBeanMethods = false)
@RequiredArgsConstructor
@ConditionalOnClass(name = "com.aliyuncs.IAcsClient")
@EnableConfigurationProperties(SmsProperties.class)
public class SmsAutoConfiguration {

	private final SmsProperties smsProperties;

	/**
	 * Configuration SmsClient bean.
	 * @return the sms client
	 */
	@Bean
	@ConditionalOnMissingBean
	public SmsClient smsClient() {
		return new SmsClient(smsProperties);
	}

}
