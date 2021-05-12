package pers.cocoadel.demo.rest.demo;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;
import java.util.Properties;

public class RestClientDemo {

    public static void main(String[] args) {
        Client client = ClientBuilder.newClient();
        testGet(client);
        testPostJson(client);
        testPostForm(client);
    }

    /**
     * get 方法测试
     */
    private static void testGet(Client client) {
        Response response = client
                .target("http://127.0.0.1:8080/hello/world")      // WebTarget
                .request() // Invocation.Builder
                .get();                                     //  Response
        String content = response.readEntity(String.class);
        System.out.println("get response: " + content);
    }

    /**
     * post json 测试
     */
    private static void testPostJson(Client client) {
        Properties properties = new Properties();
        properties.setProperty("name", "rwby");
        properties.setProperty("age", "15");
        Response response = client
                .target("http://localhost:8080/post/json")      // WebTarget
                .request() // Invocation.Builder

                .post(Entity.json(properties));                                    //  Response

        String content = response.readEntity(String.class);
        System.out.println("post json response: " + content);
    }

    /**
     * post form 测试
     */
    private static void testPostForm(Client client) {
        Form form = new Form();
        form.param("name","weiss");
        form.param("age","14");
        Response response = client
                .target("http://localhost:8080/post/form")      // WebTarget
                .request() // Invocation.Builder
                .post(Entity.form(form));                                    //  Response

        String content = response.readEntity(String.class);
        System.out.println("post form response: " + content);
    }

}
