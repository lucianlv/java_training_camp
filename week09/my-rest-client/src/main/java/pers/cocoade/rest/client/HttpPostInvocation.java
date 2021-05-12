/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package pers.cocoade.rest.client;


import com.fasterxml.jackson.databind.ObjectMapper;
import pers.cocoade.rest.core.DefaultResponse;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.InvocationCallback;
import javax.ws.rs.core.*;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * HTTP POST Method {@link Invocation}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since
 */
class HttpPostInvocation implements Invocation {

    private final URI uri;

    private final URL url;

    private final MultivaluedMap<String, Object> headers;

    private final Entity<?> entity;

    private ObjectMapper objectMapper;

    HttpPostInvocation(URI uri, MultivaluedMap<String, Object> headers, Entity<?> entity) {
        this.uri = uri;
        this.headers = headers;
        this.headers.add("Accept-Encoding",entity.getEncoding());
        this.headers.add("Accept-Language",entity.getLanguage());
        String contentType = entity.getMediaType().getType() + "/" + entity.getMediaType().getSubtype();
        this.headers.add("Content-Type",contentType);
        this.entity = entity;
        try {
            this.url = uri.toURL();
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public Invocation property(String name, Object value) {
        return this;
    }

    @Override
    public Response invoke() {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);   //需要输出
            connection.setDoInput(true);   //需要输入
            connection.setRequestMethod(HttpMethod.POST);
            setRequestHeaders(connection);
            connection.connect();

            post(connection, entity);
            // TODO Set the cookies
            int statusCode = connection.getResponseCode();

            DefaultResponse response = new DefaultResponse();
            response.setConnection(connection);
            response.setStatus(statusCode);
            return response;
        } catch (Exception e) {
            // TODO Error handler
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据 MediaType 选择 Post 类型
     */
    private void post(HttpURLConnection connection, Entity<?> entity) throws IOException {
        MediaType mediaType = entity.getMediaType();
        if (MediaType.APPLICATION_JSON_TYPE.equals(mediaType)) {
            postJson(connection, entity);
        } else if (MediaType.APPLICATION_FORM_URLENCODED_TYPE.equals(mediaType)) {
            postForm(connection, entity);
        } else {
            throw new UnsupportedOperationException("not support mediaType: " + mediaType);
        }
    }

    /**
     * POST application/json 类型
     * @see Entity#json(Object)
     */
    private void postJson(HttpURLConnection connection, Entity<?> entity) throws IOException {
        byte[] bytes = getObjectMapper().writeValueAsString(entity.getEntity()).getBytes();
        this.headers.add("Content-Length", bytes.length);
        DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
        dataOutputStream.write(bytes);
        dataOutputStream.close();
    }

    private ObjectMapper getObjectMapper() {
        if (objectMapper == null) {
            synchronized (HttpPostInvocation.class) {
                if (objectMapper == null) {
                    objectMapper = new ObjectMapper();
                }
            }
        }
        return objectMapper;
    }

    /**
     * post application/x-www-form-urlencoded 类型
     * @see Entity#form(Form)
     */
    private void postForm(HttpURLConnection connection, Entity<?> entity) throws IOException {
        Form form = (Form) entity.getEntity();
        MultivaluedMap<String, String> stringStringMultivaluedMap = form.asMap();
        List<String> list = new LinkedList<>();
        stringStringMultivaluedMap.forEach((key, v) -> {
            list.add(key + "=" + v);
        });
        if (list.size() > 0) {
            String params = String.join("&", list);
            byte[] bytes = params.getBytes();
            this.headers.add("Content-Length", bytes.length);
            DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
            dataOutputStream.write(bytes);
            dataOutputStream.close();
        }
    }


    private void setRequestHeaders(HttpURLConnection connection) {
        for (Map.Entry<String, List<Object>> entry : headers.entrySet()) {
            String headerName = entry.getKey();
            for (Object headerValue : entry.getValue()) {
                connection.setRequestProperty(headerName, headerValue.toString());
            }
        }
    }

    @Override
    public <T> T invoke(Class<T> responseType) {
        Response response = invoke();
        return response.readEntity(responseType);
    }

    @Override
    public <T> T invoke(GenericType<T> responseType) {
        Response response = invoke();
        return response.readEntity(responseType);
    }

    @Override
    public Future<Response> submit() {
        return null;
    }

    @Override
    public <T> Future<T> submit(Class<T> responseType) {
        return null;
    }

    @Override
    public <T> Future<T> submit(GenericType<T> responseType) {
        return null;
    }

    @Override
    public <T> Future<T> submit(InvocationCallback<T> callback) {
        return null;
    }


}
