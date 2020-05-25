package com.hcl.ecomm.core.servlets;

import javax.servlet.Servlet;

import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.json.HTTP;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hcl.ecomm.core.services.AddToCartService;
import com.hcl.ecomm.core.services.ProductService;
import com.hcl.ecomm.core.utility.ProductUtility;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;
import org.osgi.framework.Constants;

@Component(
		service = Servlet.class, 
		property = { 
				 Constants.SERVICE_DESCRIPTION + "= Add to Cart Servlet",
				"sling.servlet.paths=/bin/hclecomm/addtocart",
				"sling.servlet.method=" + HttpConstants.METHOD_POST,
				"sling.servlet.extensions=json"})
public class AddToCartServlet extends SlingAllMethodsServlet{

	private static final long serialVersionUID = 175460834851290225L;
	private static final Logger LOG = LoggerFactory.getLogger(AddToCartServlet.class);
	
	@Reference
	private AddToCartService addToCartService;
	
	@Override
	protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) {
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		JSONObject responseObject = new JSONObject();

		try {
			responseObject.put("message", "Request failed");
			responseObject.put("status", Boolean.FALSE);
			
			StringBuilder buffer = new StringBuilder();
			BufferedReader reader = request.getReader();
			String line;
			while ((line = reader.readLine()) != null) {
				buffer.append(line);
			}
			String payload = buffer.toString();
			if (StringUtils.isNotEmpty(payload)) {
				JSONObject jsonPayload =  new JSONObject(payload);
				if (isValidPayload(jsonPayload)) {
					JSONObject cartItem = jsonItemObj( jsonPayload);
					JSONObject addToCartResponse = addToCartService.addToCart(cartItem);
					if (addToCartResponse.has("statusCode") && addToCartResponse.getInt("statusCode") == 200) {
						responseObject.put("message", addToCartResponse.getJSONObject("message"));
						responseObject.put("status", Boolean.TRUE);
					} else {
						responseObject.put("message", "something went wrong while add to cart.");
					}
				}
			} else {
				responseObject.put("message", "Missing Parameter in payload.");
				responseObject.put("status", Boolean.FALSE);
			}
			response.getWriter().print(responseObject);
		} catch (Exception e) {
			LOG.error("Error Occured while executing AddToCartServlet Post - add item in cart. responseObject={} ", responseObject);
			LOG.error("Full Error={} ", e);
		}
	}
	
	@Override
	protected void doPut(SlingHttpServletRequest request, SlingHttpServletResponse response) {
		LOG.debug("addtocart PUT()  method start.");
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		JSONObject responseObject = new JSONObject();
		try {
			responseObject.put("message", "Request failed");
			responseObject.put("status", Boolean.FALSE);
			
			StringBuilder buffer = new StringBuilder();
			BufferedReader reader = request.getReader();
			String line;
			while ((line = reader.readLine()) != null) {
				buffer.append(line);
			}
			String payload = buffer.toString();
			if (StringUtils.isNotEmpty(payload)) {
				JSONObject jsonPayload =  new JSONObject(payload);
				LOG.info("addtocart PUT()  payload={}",jsonPayload);
				if (isValidPayload(jsonPayload) && jsonPayload.has("itemid")) {
					JSONObject cartItem = jsonItemObj( jsonPayload);
					JSONObject addToCartResponse = addToCartService.updateCartItem(cartItem, jsonPayload.getString("itemid"));
					if (addToCartResponse.has("statusCode") && addToCartResponse.getInt("statusCode") == 200) {
						responseObject.put("message", addToCartResponse.getJSONObject("message"));
						responseObject.put("status", Boolean.TRUE);
					} else {
						responseObject.put("message", "something went wrong while add to cart.");
					}
				}
			} else {
				responseObject.put("message", "Missing Parameter in payload.");
				responseObject.put("status", Boolean.FALSE);
			}
			response.getWriter().print(responseObject);
		} catch (Exception e) {
			LOG.error("Error Occured while executing AddToCartServlet Put- update item quantity. responseObject={}", responseObject);
			LOG.error("Full Error={} ", e);
		}
	}
	
	
	private boolean isValidPayload(JSONObject jsonPayload) {
		boolean isValidData=Boolean.TRUE;
		if(!jsonPayload.has("cartid") && !jsonPayload.has("sku") && !jsonPayload.has("qty")){
			isValidData = Boolean.FALSE;
		}
		return isValidData;
	}
	
	private JSONObject jsonItemObj(JSONObject itemData) {
		JSONObject item = new JSONObject();
		JSONObject cartItem = new JSONObject();
		try {
			cartItem.put("quote_id", itemData.getString("cartid"));
			cartItem.put("sku", itemData.getString("sku"));
			cartItem.put("qty", itemData.getInt("qty"));
			item.put("cartItem", cartItem);
		} catch (JSONException e) {
			LOG.error("Error while executing. Error={}",e);
		}
		return item;
	}


}