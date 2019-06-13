package com.maaksoft.saml;

import org.apache.http.HttpResponse;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import org.apache.http.impl.client.DefaultHttpClient;

import org.apache.http.util.EntityUtils;

import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class IndexController {

    @RequestMapping("/matrices")
    @ResponseBody
    public String testMatrices() throws Exception {

        HttpClient h = new DefaultHttpClient();
        HttpResponse httpResponse = h.execute(new HttpGet("http://127.0.0.1:5000/matrices"));//Pointing to (to be private) REST API server
        String getResponse = EntityUtils.toString(httpResponse.getEntity());

        return getResponse;

    }

}