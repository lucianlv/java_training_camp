package org.geektimes.rest.demo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.Charsets;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.nio.charset.Charset;

public class RestClientDemo {

    public static void main(String[] args) throws JsonProcessingException {
        testGet();
        testPost();
    }

    private static void testGet() {
        Client client = ClientBuilder.newClient();
        Response response = client
                .target("http://127.0.0.1:8080/hello/world")      // WebTarget
                .request() // Invocation.Builder
                .get();                                     //  Response

        String content = response.readEntity(String.class);
        System.out.println("get response："+content);
    }

    private static void testPost() throws JsonProcessingException {
        User user = new User();
        user.setAge(11);
        user.setName("爱丽诗");
        MediaType mediaType = new MediaType("application", "json", "utf-8");
        Entity entity =Entity.entity(user, mediaType);
        Client client = ClientBuilder.newClient();
        Response response = client
                .target("http://127.0.0.1:8080/hello/world")      // WebTarget
                .request() // Invocation.Builder
                .post(entity);                     //  Response
        User content = response.readEntity(User.class);
        System.out.println("post response："+content);
    }





    static class User {
        private String name;
        private int age;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        @Override
        public String toString() {
            return "User{" +
                    "name='" + name + '\'' +
                    ", age=" + age +
                    '}';
        }
    }
}
