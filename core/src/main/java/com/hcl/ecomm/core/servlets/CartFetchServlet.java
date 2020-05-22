package com.hcl.ecomm.core.servlets;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.hcl.ecomm.core.services.ProductService;
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component(service = Servlet.class, property = { "sling.servlet.paths=/bin/hclecomm/cartproducts",
		"sling.servlet.method=" + HttpConstants.METHOD_GET, "sling.servlet.extensions=json" })
public class CartFetchServlet extends SlingSafeMethodsServlet {

	private static final long serialVersionUID = 4016057296495129474L;
	private static final Logger LOG = LoggerFactory.getLogger(CartFetchServlet.class);

	@Reference
    CartService cartService;

	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
			throws ServletException, IOException {

		LOG.info("inside ProductServlet doGET method");
		try {

			String cartItems = null;
			String cartId = request.getParameter("cartId");
			JsonArray responseStream = cartService.getCartDetails(cartId);
			LOG.info("responseStream is {}", responseStream.toString());

			JsonArray itemsarr= responseStream.getAsJsonArray();

			List<HashMap<String, Object>> list = new ArrayList<>();
			JsonArray cartArray=new JsonArray();

			for(int i=0; i<itemsarr.size();i++){
				HashMap<String, Object> cartMap = new HashMap<String, Object>();
				JsonObject cartObj = itemsarr.get(i).getAsJsonObject();
				LOG.info("cartObj is {}", cartObj);
				cartMap.put("item_id",itemsarr.get(i).getAsJsonObject().get("item_id").getAsInt());
				cartMap.put("sku", itemsarr.get(i).getAsJsonObject().get("sku").getAsString());
				cartMap.put("qty",itemsarr.get(i).getAsJsonObject().get("qty").getAsInt());
				cartMap.put("name", itemsarr.get(i).getAsJsonObject().get("name").getAsString());
				cartMap.put("price",itemsarr.get(i).getAsJsonObject().get("price").getAsInt());
				cartMap.put("image_url", itemsarr.get(i).getAsJsonObject().get("extension_attributes").getAsJsonObject().get("image_url").getAsString());
				list.add(cartMap);
				 cartArray = new Gson().toJsonTree(list).getAsJsonArray();


			}



			response.setContentType("application/json");
			response.getWriter().write(cartArray.toString());
			response.setStatus(200);
		}
		catch (Exception e){
			LOG.error("error in product servlet {} ",e.getMessage());
			response.setStatus(500);
		}

	}



}