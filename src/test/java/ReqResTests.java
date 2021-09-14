import io.restassured.http.Headers;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;
import org.hamcrest.Matchers;
import org.hamcrest.MatcherAssert;
import io.restassured.path.json.JsonPath;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class ReqResTests extends BaseTest{

    @Test
    public void loginTest() {
                    given()
                    .body("{\n" +
                            "    \"email\": \"eve.holt@reqres.in\",\n" +
                            "    \"password\": \"cityslicka\"\n" +
                            "}")
                    .post("login")
                    .then()
                    .statusCode(HttpStatus.SC_OK)
                    .body("token", Matchers.notNullValue());
    }

    @Test
    public void getSingleUserTest() {
                    given()
                    .get("users/2")
                    .then()
                    .statusCode(HttpStatus.SC_OK)
                    .body("data.id",Matchers.equalTo(2));
    }

    @Test
    public void deleteUserTest() {
                given()
                .delete("users/2")
                .then()
                .statusCode(HttpStatus.SC_NO_CONTENT);
    }

    @Test
    public void patchUserTest() {
        String nameUpdated = given()
                .when()
                .body("{\n" +
                        "    \"name\": \"morpheus\",\n" +
                        "    \"job\": \"zion resident\"\n" +
                        "}")
                .patch("users/2")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .jsonPath().getString("name");
                MatcherAssert.assertThat(nameUpdated, Matchers.equalTo("morpheus"));

    }

    @Test
    public void putUserTest() {
        String nameUpdated = given()
                .when()
                .body("{\n" +
                        "    \"name\": \"morpheus\",\n" +
                        "    \"job\": \"zion resident\"\n" +
                        "}")
                .put("users/2")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .jsonPath().getString("job");
        MatcherAssert.assertThat(nameUpdated, Matchers.equalTo("zion resident"));

    }

    @Test
    public void getAllUsersTest(){
        Response response = given()
                .get("users?page=2");
        Headers headers = response.getHeaders();
        int statusCode = response.getStatusCode();
        String body = response.getBody().asString();
        String contentType = response.getContentType();

        MatcherAssert.assertThat(statusCode, Matchers.equalTo(HttpStatus.SC_OK));
        System.out.println("body: " + body);
        System.out.println("content type: " + contentType);
        System.out.println("headers: " + headers.toString());


    }

    @Test
    public void getAllUsersTest2(){

        String response = given()
                .when()
                .get("users?page=2")
                .then().extract()
                .body()
                .asString();

        int page = JsonPath.from(response).get("page");
        int totalPages = JsonPath.from(response).get("total_pages");
        int idFirstUser = JsonPath.from(response).get("data[0].id");

        System.out.println("page: " + page);
        System.out.println("totalPage: " + totalPages);
        System.out.println("IdFirstUser: " + idFirstUser);

        List<Map> userMayorA10 = JsonPath.from(response).get("data.findAll {user -> user.id > 10}");

        String email = userMayorA10.get(0).get("email").toString();

        System.out.println(email);

        List<Map> user = JsonPath.from(response).get("data.findAll {user -> user.id > 10 && user.last_name == 'Howell'}");
        int id = Integer.valueOf(user.get(0).get("id").toString());

    }

    @Test
    public void createUserTest(){

        String response = given()
                .when()
                .body("{\n" +
                        "    \"name\": \"morpheus\",\n" +
                        "    \"job\": \"leader\"\n" +
                        "}")
                .post("users")
                .then().extract()
                .body()
                .asString();
        User user = JsonPath.from(response).getObject("",User.class);
        System.out.println(user.getId());
        System.out.println(user.getJob());
    }

    @Test
    public void registerUserTest(){
        CreateUserRequest user = new CreateUserRequest();
        user.setEmail("eve.holt@reqres.in");
        user.setPassword("pistol");
        CreateUserResponse userResponse = given()
                .when()
                .body(user)
                .post("register")
                .then()
                .statusCode(200)
                .contentType(Matchers.equalTo("application/json; charset=utf-8"))
                .extract()
                .body()
                .as(CreateUserResponse.class);

        MatcherAssert.assertThat(userResponse.getId(), Matchers.equalTo(4));
        MatcherAssert.assertThat(userResponse.getToken(), Matchers.equalTo("QpwL5tke4Pnpja7X4"));
    }

}
