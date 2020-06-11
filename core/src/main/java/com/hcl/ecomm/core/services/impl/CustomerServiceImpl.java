package com.hcl.ecomm.core.services.impl;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hcl.ecomm.core.config.MagentoServiceConfig;
import com.hcl.ecomm.core.services.AddToCartService;
import com.hcl.ecomm.core.services.CustomerService;
import com.hcl.ecomm.core.services.LoginService;


@Component(
		immediate = true,
		enabled = true, 
		service = CustomerService.class)
public class CustomerServiceImpl implements CustomerService{

	private static final Logger LOG = LoggerFactory.getLogger(CustomerServiceImpl.class);
	

	@Reference
	LoginService loginService;
	
	@Activate
	private MagentoServiceConfig config;

	@Override
	public String getDomainName() {
		return loginService.getDomainName();
	}
	
	@Override
	public String customerSignupServicePath() {
		return config.customerService_signupPath();
	}

	@Override
	public String customerSigninServicePath() {
		return config.customerService_signinPath();
	}

	@Override
	public String customerProfileServicePath() {
		return config.customerService_profilePath();
	}

	@Override
	public JSONObject customerSignup(JSONObject signupObject) {
		LOG.debug("customerSignup method start  signupObject={}: " + signupObject);
		String scheme = "http";
		JSONObject customerSignupResponse = new JSONObject();

		try {
			String authToken = loginService.getToken();
			String domainName = getDomainName();
			String customerSignupPath = customerSignupServicePath();
			String url = scheme + "://" + domainName + customerSignupPath;
			LOG.info("customerSignupPath  : " + url);
			
			Integer statusCode;
			JSONObject response = new JSONObject();
			StringEntity signupInput = new StringEntity(signupObject.toString(),ContentType.APPLICATION_JSON);
			CloseableHttpClient httpClient = HttpClients.createDefault();
			HttpPost httppost = new HttpPost(url);
			httppost.setHeader("Content-Type", "application/json");
			httppost.setHeader("Authorization", "Bearer " +authToken);
			httppost.setEntity(signupInput);
			CloseableHttpResponse httpResponse = httpClient.execute(httppost);
			statusCode = httpResponse.getStatusLine().getStatusCode();
			
			LOG.info("customer Signup: magento statusCode ={}",statusCode);
			
			if(HttpStatus.SC_OK == statusCode){
				BufferedReader br = new BufferedReader(new InputStreamReader((httpResponse.getEntity().getContent())));
				String output;
				while ((output = br.readLine()) != null) {
					response = new JSONObject(output);
				}
				customerSignupResponse.put("statusCode", statusCode);
				customerSignupResponse.put("message", response);
			}else if(HttpStatus.SC_BAD_REQUEST == statusCode){
				customerSignupResponse.put("statusCode", statusCode);
				customerSignupResponse.put("message", httpResponse.getEntity().getContent().toString());
				LOG.error("Error while customer Signup. status code:{} and message={}",statusCode,httpResponse.getEntity().getContent().toString());
			}else{
				LOG.error("Error while customer Signup. status code:{}",statusCode);
			}
		} catch (Exception e) {
			LOG.error("Error while executing customerSignup() method. Error={} ",e);
		}
		LOG.debug("signupObject method end  customerSignupResponse={}: ", customerSignupResponse);
		return customerSignupResponse;
	}

	@Override
	public JSONObject customerSignin(JSONObject signinObject) {
		LOG.debug("customerSignin() method start. signObject={}",signinObject);
		String scheme = "http";
		JSONObject customerSigninRes = new JSONObject();
		

		try {
			String authToken = loginService.getToken();
			String domainName = getDomainName();
			String customerSigninPath = customerSigninServicePath();
			String url = scheme + "://" + domainName + customerSigninPath;
			LOG.info("customerSigninPath  : " + url);
			
			Integer statusCode;
			StringEntity signinInput = new StringEntity(signinObject.toString(),ContentType.APPLICATION_JSON);
			CloseableHttpClient httpClient = HttpClients.createDefault();
			HttpPost httpPost = new HttpPost(url);
			httpPost.setHeader("Content-Type", "application/json");
			httpPost.setHeader("Authorization", "Bearer " +authToken);
			httpPost.setEntity(signinInput);
			CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
			statusCode = httpResponse.getStatusLine().getStatusCode();
			
			LOG.info("customer Signin: magento statusCode ={}",statusCode);
			
			if(HttpStatus.SC_OK == statusCode){
				BufferedReader br = new BufferedReader(new InputStreamReader((httpResponse.getEntity().getContent())));
				String str = "";
				String output;
				while ((output = br.readLine()) != null) {
					str += output;
				}
				if (StringUtils.isNotEmpty("str")) {
					str = str.replace("\"", "");
				}
				customerSigninRes.put("statusCode", statusCode);
				customerSigninRes.put("customerToken", str);
			}else if(HttpStatus.SC_BAD_REQUEST == statusCode){
				customerSigninRes.put("statusCode", statusCode);
				customerSigninRes.put("message", httpResponse.getEntity().getContent().toString());
				LOG.error("Error while  customer Signin. status code:{} and message={}",statusCode,httpResponse.getEntity().getContent().toString());
			}else{
				LOG.error("Error while customer Signin. status code:{}",statusCode);
			}
		} catch (Exception e) {
			LOG.error("Error while executing customerSignin. Error={} ", e);
		}
		LOG.debug("customerSignin method end  customerSigninRes={}: " + customerSigninRes);
		return customerSigninRes;
	}

	
	@Override
	public JSONObject customerProfile(String customerToken) {
		LOG.debug("customerProfile method start  customerToken={}: " + customerToken);
		String scheme = "http";
		JSONObject customerProfileResponse = new JSONObject();

		try {
			String domainName = getDomainName();
			String customerProfilePath = customerProfileServicePath();
			String url = scheme + "://" + domainName + customerProfilePath;
			LOG.info("customerProfilePath  : " + url);
			
			Integer statusCode;
			JSONObject response = new JSONObject();
			CloseableHttpClient httpClient = HttpClients.createDefault();
			HttpGet httpGet = new HttpGet(url);
			httpGet.setHeader("Content-Type", "application/json");
			httpGet.setHeader("Authorization", "Bearer " +customerToken);
			CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
			statusCode = httpResponse.getStatusLine().getStatusCode();
			
			LOG.info("customer profile: magento statusCode ={}",statusCode);
			
			if(HttpStatus.SC_OK == statusCode){
				BufferedReader br = new BufferedReader(new InputStreamReader((httpResponse.getEntity().getContent())));
				String output;
				while ((output = br.readLine()) != null) {
					response = new JSONObject(output);
				}
				customerProfileResponse.put("statusCode", statusCode);
				customerProfileResponse.put("message", response);
			}else if(HttpStatus.SC_BAD_REQUEST == statusCode){
				customerProfileResponse.put("statusCode", statusCode);
				customerProfileResponse.put("message", httpResponse.getEntity().getContent().toString());
				LOG.error("Error while customer profile. status code:{} and message={}",statusCode,httpResponse.getEntity().getContent().toString());
			}else{
				LOG.error("Error while customer profile. status code:{}",statusCode);
			}
		} catch (Exception e) {
			LOG.error("Error while executing customerProfile() method. Error={} ",e);
		}
		LOG.debug("customerProfile method end  customerProfileResponse={}", customerProfileResponse);
		return customerProfileResponse;
	}
	

}