package com.hcl.ecomm.core.services.impl;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.eclipse.jetty.http.HttpStatus;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hcl.ecomm.core.config.MagentoServiceConfig;
import com.hcl.ecomm.core.services.CreateCartService;
import com.hcl.ecomm.core.services.LoginService;

@Component(
		immediate = true,
		enabled = true, 
		service = CreateCartService.class)
public class CreateCartServiceImpl implements CreateCartService{

	private static final Logger LOG = LoggerFactory.getLogger(CreateCartServiceImpl.class);
	
	@Reference
	LoginService loginService;
	
	@Activate
	private MagentoServiceConfig config;

	@Override
	public String getDomainName() {
		return loginService.getDomainName();
	}

	@Override
	public String getEmptyCartPath() {
		return config.magentoService_emptyCartPath();
	}
	

	@Override
	public JSONObject createCart(String customerToken) {
		String scheme = "http";
		String authToken = "";
		String createCartPath = "";
		JSONObject createCartRes = new JSONObject();

		try {
			if(customerToken != null && !customerToken.isEmpty()) {
				authToken = customerToken;
				createCartPath = config.customer_createCart_string();
			}
			else {
				authToken = loginService.getToken();
				createCartPath = getEmptyCartPath();
			}
			String domainName = getDomainName();
			String url = scheme + "://" + domainName + createCartPath;
			LOG.debug("createGuestCartPath  : " + url);
			
			Integer statusCode;
			CloseableHttpClient httpClient = HttpClients.createDefault();
			HttpPost httppost = new HttpPost(url);
			httppost.setHeader("Content-Type", "application/json");
			httppost.setHeader("Authorization", "Bearer " +authToken);
			CloseableHttpResponse httpResponse = httpClient.execute(httppost);
			statusCode = httpResponse.getStatusLine().getStatusCode();
			
			LOG.info("Create cart: magento statusCode ={}",statusCode);
			
			if(HttpStatus.OK_200 == statusCode){
				BufferedReader br = new BufferedReader(new InputStreamReader((httpResponse.getEntity().getContent())));
				String str = "";
				String output;
				while ((output = br.readLine()) != null) {
					str += output;
				}
				JSONObject cartid = new JSONObject();
				if (StringUtils.isNotEmpty("str")) {
					str = str.replace("\"", "");
				}
				cartid.put("cartid", str);
				createCartRes.put("statusCode", statusCode);
				createCartRes.put("message", cartid);
			}else if(HttpStatus.BAD_REQUEST_400 == statusCode){
				createCartRes.put("statusCode", statusCode);
				createCartRes.put("message", httpResponse.getEntity().getContent().toString());
				LOG.error("Error while  create cart. status code:{} and message={}",statusCode,httpResponse.getEntity().getContent().toString());
			}else{
				LOG.error("Error while ceate cart. status code:{}",statusCode);
			}
		} catch (Exception e) {
			LOG.error("createGuestCart method caught an exception " + e);
		}
		LOG.debug("createGuestCart method end  createGuestCartRes={}: " + createCartRes);
		return createCartRes;
	}





}
