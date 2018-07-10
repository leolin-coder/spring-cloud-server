package com.github.util;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;


@Configuration
public class RestTemplateConfig {

	@Bean
	public ClientHttpRequestFactory clientHttpRequestFactory() {

		ClientHttpRequestFactory clientHttpRequestFactory = new SimpleClientHttpRequestFactory() {
			@Override
			protected void prepareConnection(HttpURLConnection connection, String httpMethod) throws IOException {

				// 支持https访问
				if (connection instanceof HttpsURLConnection) {

					HttpsURLConnection httpsConnection = (HttpsURLConnection) connection;
					TrustManager[] trustManagers = new TrustManager[]{
							new X509TrustManager() {
								// 检查客户端证书
								@Override
								public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
								}
								// 检查服务器端证书
								@Override
								public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
								}
								// 返回受信任的X509证书数组
								@Override
								public X509Certificate[] getAcceptedIssuers() {
									return null;
								}
							}
					};

					try {
						SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
						sslContext.init(null, trustManagers, new SecureRandom());
						httpsConnection.setSSLSocketFactory(sslContext.getSocketFactory());
					} catch (Exception e) {
						e.printStackTrace();
					}

				} else {
					super.prepareConnection(connection, httpMethod);
				}

			}
		};

		((SimpleClientHttpRequestFactory) clientHttpRequestFactory).setConnectTimeout(6 * 1000);
		((SimpleClientHttpRequestFactory) clientHttpRequestFactory).setReadTimeout(1 * 60 * 1000);

		return clientHttpRequestFactory;
	}

}

