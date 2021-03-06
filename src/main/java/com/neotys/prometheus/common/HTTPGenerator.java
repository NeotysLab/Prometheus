package com.neotys.prometheus.common;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.neotys.extensions.action.engine.Proxy;
import org.apache.http.Consts;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.neotys.prometheus.common.HTTPGeneratorUtils.*;


public class HTTPGenerator {
	public static final String HTTP_GET_METHOD = "GET";
	public static final String HTTP_POST_METHOD = "POST";
	public static final String HTTP_OPTION_METHOD = "OPTION";
	public static final String HTTP_PUT_METHOD = "PUT";
	public static final String HTTP_DELETE_METHOD = "DELETE";

	private final DefaultHttpClient httpClient;

	private final HttpRequestBase request;

	public HTTPGenerator(final String httpMethod,
						 final String url,
						 final Map<String, String> headers,
						 final Map<String, String> params,
						 final Optional<Proxy> proxy,final Optional<String> user, final Optional<String> password)
			throws UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, MalformedURLException, URISyntaxException {
		this.request = generateHttpRequest(httpMethod, url);
		final boolean isHttps = url.contains("https");
		this.httpClient = newHttpClient(isHttps,user,password);

		if (proxy.isPresent()) {
			initProxy(proxy.get());
		}
		addHeaders(request, headers);
		if (params != null && !params.isEmpty()) {
			setRequestUrl(request, url, params);
		}
	}
	public static HTTPGenerator deleteHttpGenerator(final String url,
													final Map<String, String> headers,
													final Map<String, String> params,
													final Optional<Proxy> proxy,Optional<String> user,Optional<String> password) throws Exception {
		final HTTPGenerator httpGenerator = new HTTPGenerator(HTTP_DELETE_METHOD, url, headers, params, proxy,user,password);
		return httpGenerator;

	}

	public static HTTPGenerator newJsonHttpGenerator(final String httpMethod,
													 final String url,
													 final Map<String, String> headers,
													 final Map<String, String> params,
													 final Optional<Proxy> proxy,Optional<String> user,Optional<String> password,
													 final String bodyJson)
			throws Exception {
		final HTTPGenerator httpGenerator = new HTTPGenerator(httpMethod, url, headers, params, proxy,user,password);
		final StringEntity requestEntity = new StringEntity(bodyJson, "application/json","utf8");
		addJsonParameters(httpGenerator.request, requestEntity, httpMethod);
		return httpGenerator;
	}


	public static HTTPGenerator newPostParametersHttpGenerator(
													 final String url,
													 final Map<String, String> headers,
													 final Map<String, String> params,
													 final Optional<Proxy> proxy,Optional<String> user,Optional<String> password)
			throws Exception {
		final HTTPGenerator httpGenerator = new HTTPGenerator(HTTP_POST_METHOD, url, headers, null, proxy,user,password);
		List<NameValuePair> parameters = new ArrayList<NameValuePair>();


		params.entrySet().stream().forEach(stringStringEntry -> {
			parameters.add(new BasicNameValuePair(stringStringEntry.getKey(), stringStringEntry.getValue()));
		});
		((HttpPost) httpGenerator.request).setEntity(new UrlEncodedFormEntity(parameters, Consts.UTF_8));

		//final StringEnity requestEntity = new StringEntity(bodyJson, "application/json","utf8");
		//addJsonParameters(httpGenerator.request, requestEntity, httpMethod);
		return httpGenerator;
	}
	private void initProxy(final Proxy proxy) {
		final HttpHost proxyHttpHost = new HttpHost(proxy.getHost(), proxy.getPort(), "http");
		httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxyHttpHost);
		if (!Strings.isNullOrEmpty(proxy.getLogin())) {
			httpClient.getCredentialsProvider().setCredentials(
					new AuthScope(proxy.getHost(), proxy.getPort()),
					new UsernamePasswordCredentials(proxy.getLogin(), proxy.getPassword()));
		}
	}

	public void closeHttpClient() {
		httpClient.getConnectionManager().shutdown();
	}

	public HttpResponse execute() throws IOException {
		return httpClient.execute(request);
	}

	public JSONArray executeAndGetJsonArrayResponse() throws IOException, PrometheusException {
		final HttpResponse httpResponse = httpClient.execute(request);
		if (!HttpResponseUtils.isSuccessHttpCode(httpResponse.getStatusLine().getStatusCode())) {
			final String stringResponse = HttpResponseUtils.getStringResponse(httpResponse);
			throw new PrometheusException(httpResponse.getStatusLine().getReasonPhrase() + " - "+ request + " - " + stringResponse);
		}
		return HttpResponseUtils.getJsonArrayResponse(httpResponse);
	}

	public int executeAndGetResponseCode() throws IOException {
		final HttpResponse response = httpClient.execute(request);
		return response.getStatusLine().getStatusCode();
	}

	public HttpRequestBase getRequest() {
		return request;
	}
}
