package org.api.regres;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.api.regres.spec.Specifications;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.IsEqual.equalTo;

public class RegresNoPojoTest {

    private final static String URL = "https://reqres.in/";
    @Test
    public void checkAvatarsNoPojoTest(){
        Specifications.installSpecification(Specifications.requestSpec(URL),Specifications.responseSpecOK200());
        Response response = given()
                .when()
                .get("api/users?page=2")
                .then().log().all()
                .body("page",equalTo(2))
                .body("data.id",notNullValue())
                .extract().response();
        JsonPath jsonPath = response.jsonPath();
        List<String> emails = jsonPath.get("data.email");
        List<Integer> ids = jsonPath.get("data.id");
        List<String> avatars = jsonPath.get("data.avatar");
        for (int i = 0; i< avatars.size(); i++){
            Assert.assertTrue(avatars.get(i).contains(ids.get(i).toString()));
            Assert.assertTrue(emails.get(i).contains("@regres.in"));
        }
       // Assert.assertTrue(emails.stream().allMatch(x -> x.endsWith("@regres.in")));
    }

    @Test
    public void successUserRegNoPojo(){
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpecOK200());
        Map<String,String> user = new HashMap<>();
        user.put("email","eve.holt@reqres.in");
        user.put("password","pistol");

        Response response = given()
                .body(user)
                .when()
                .post("api/register")
                .then().log().all()
               // .body("id", equalTo(4))
               // .body("token", equalTo("QpwL5tke4Pnpja7X4"))
                .extract().response();
        JsonPath jsonPath = response.jsonPath();
        int id = jsonPath.get("id");
        String token = jsonPath.get("token");
        Assert.assertEquals(4,id);
        Assert.assertEquals("QpwL5tke4Pnpja7X4",token);
    }

    @Test
    public void unSuccessRegNoPojo(){
        Specifications.installSpecification(Specifications.requestSpec(URL),Specifications.responseSpecError400());
        Map<String,String> user = new HashMap<>();
        user.put("email","sydney@fife");

        Response response = given()
                .body(user)
                .when()
                .post("api/register")
                .then().log().all()
                //.body("error", equalTo("Missing password"))
                .extract().response();
        JsonPath jsonPath  = response.jsonPath();
        String error = jsonPath.get("error");
        Assert.assertEquals("Missing password", error);
    }
}
