package org.example;

import io.restassured.http.ContentType;
import org.example.api.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;

public class AppTest {

    private final static String URL = "https://reqres.in/";

    @Test
    public void checkAvatarAndIdTest() {
        Specifications.installSpecification(Specifications.requestSpec(URL),Specifications.responseSpecOK200());
        List<UserData> users = given()
                .when()
                .get("api/users/?page=2")
                .then().log().all()
                .extract().body().jsonPath().getList("data", UserData.class);
       // users.stream().forEach(x-> Assert.assertTrue(x.getAvatar().contains(x.getId().toString())));
       // Assert.assertTrue(users.stream().allMatch(x-> x.getEmail().endsWith("@regres.in")));

        List<String> avatars = users.stream().map(UserData::getAvatar).collect(Collectors.toList());
        List<String> ids = users.stream().map(x->x.getId().toString()).collect(Collectors.toList());

        for (int i = 0; i < avatars.size(); i++){
            Assert.assertTrue(avatars.get(i).contains(ids.get(i)));
        }
    }
    @Test
    public void successReg(){
        Specifications.installSpecification(Specifications.requestSpec(URL),Specifications.responseSpecOK200());
        Integer id = 4;
        String token = "QpwL5tke4Pnpja7X4";

        UserRegistration user = new UserRegistration("eve.holt@reqres.in", "pistol");
        SuccessReg successReg = given()
                .body(user)
                .when()
                .post("api/register")
                .then().log().all()
                .extract().as(SuccessReg.class);
        Assert.assertNotNull(successReg.getId());
        Assert.assertNotNull(successReg.getToken());
        Assert.assertEquals(id,successReg.getId());
        Assert.assertEquals(token,successReg.getToken());
    }

    @Test
    public void unSuccessReg(){
        Specifications.installSpecification(Specifications.requestSpec(URL),Specifications.responseSpecError400());

        UserRegistration user = new UserRegistration("sydney@fife", "");
        UnSuccessReg unSuccessReg = given()
                .body(user)
                .when()
                .post("api/register")
                .then().log().all()
                .extract().as(UnSuccessReg.class);
        Assert.assertEquals("Missing password",unSuccessReg.getError());
    }

    @Test
    public void ResourseListDescenceSorting(){
        Specifications.installSpecification(Specifications.requestSpec(URL),Specifications.responseSpecOK200());

        List<ResourseList> resourseList = given()
                .when()
                .get("api/unknown")
                .then().log().all()
                .extract().body().jsonPath().getList("data", ResourseList.class);


        List<Integer> years = resourseList.stream()
                .map(ResourseList::getYear)
                .collect(Collectors.toList());

        List<Integer> sortedYears = years.stream()
                .sorted()
                .collect(Collectors.toList());

        Assert.assertEquals(sortedYears, years);
    }


    @Test
    public void deleteSecondUser(){
        Specifications.installSpecification(Specifications.requestSpec(URL),Specifications.responseSpecUnique(204));

        given()
                .when()
                .delete("api/users/2")
                .then().log().all();
    }

    @Test
    public void UpdateUserInfo(){
        Specifications.installSpecification(Specifications.requestSpec(URL),Specifications.responseSpecOK200());

        UserJob userJob = new UserJob("morpheus","zion resident");
         UserJobInfo userJobInfo = given()
                 .body(userJob)
                 .when()
                 .put("api/users/2")
                .then().log().all()
                .extract().as(UserJobInfo.class);
        //  Assert.assertEquals(now, userJobInfo.getUpdatedAt());
    }
}
