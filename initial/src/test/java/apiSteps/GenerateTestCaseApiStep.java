package apiSteps;

import base.Base;
import enumsAndConstants.Constants;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import util.Util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;


public class GenerateTestCaseApiStep {

    Properties authProperties = Util.loadAndGetPropertiesFile(Constants.AUTH_TOKEN_FILEPATH);
    static String jwtHeader = "jwt";
    static String refreshTokenHeader = "refreshToken";

    public String generateSpecificTestCaseForAPI(String methodType, String curl) {

        String testcases = "";
        if(methodType.equalsIgnoreCase("GET"))
            testcases = generateTestCasesforGetAPIs(curl);
        else if (methodType.equalsIgnoreCase("POST"))
            testcases = generateTestCasesforPostAPIs(curl);
        else if (methodType.equalsIgnoreCase("PUT"))
            testcases = generateTestCasesforPostAPIs(curl);
        else if (methodType.equalsIgnoreCase("DELETE"))
            testcases = generateTestCasesforPostAPIs(curl);
        return testcases;
    }


    /**
     * One-time execution script for any given new GET API to
     * Generate JavaScript (PostMan) Test Cases
     */
    public String generateTestCasesforGetAPIs(String curl) {

        String url = generateUrl(curl);
        System.out.println(url);

        // Set base URI - can be Data driven for Environment and Apps
        RequestSpecification request = RestAssured.given();

        // Set Cookie for Authentication
        String cookie = null;
        String userCookie = Util.patternMatch("'Cookie(.*?)(?=')", curl).replace("Cookie: ", "")
                .replace("'", "");
        if(userCookie.length()!=0){
            cookie = userCookie;
            System.out.println("In IF Cookie --- " + cookie);
        }
        else{
            generateTokens();
            cookie = Base.setCookie(authProperties);
            System.out.println("In Else Cookie --- " + cookie);
        }

        // GET API call
        Response response = request
                .cookie(cookie)
                .get(url);

        // Traverse to every nested level and generate javascript(Postman-like) testcases
        String endpoint = url.replace("https://", "");
        String fileName = Util.pushJSTestCasesToPropFile(response, endpoint);
        try {
            // Read the file content into a byte array
            byte[] fileBytes = Files.readAllBytes(Paths.get("src/test/javaScriptTests/" + fileName +".txt"));

            // Convert the byte array to a string
            String fileContent = new String(fileBytes);
            System.out.println("File content: " + fileContent);
            return fileContent;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }

    /**
     * One-time execution script for any given new POST API to
     * Generate JavaScript (PostMan) Test Cases
     */
    public String generateTestCasesforPostAPIs(String curl) {

        String url = generateUrl(curl);
        System.out.println(url);


        // Set base URI - can be Data driven for Environment and Apps
        RequestSpecification request = RestAssured.given();

        // Set Cookie for Authentication
        String cookie = null;
        String userCookie = Util.patternMatch("'Cookie(.*?)(?=')", curl).replace("Cookie: ", "")
                .replace("'", "");
        if(userCookie.length()!=0){
            cookie = userCookie;
            System.out.println("In IF Cookie --- " + cookie);
        }
        else{
            generateTokens();
            cookie = Base.setCookie(authProperties);
            System.out.println("In Else Cookie --- " + cookie);
        }


        // Build request body
        String requestBody = Util.patternMatch("\\{([^}]+)\\}", curl);
        System.out.println(requestBody);

        // POST API call
        Response response = request
                .cookie(cookie)
                .contentType("application/json").body(requestBody).post(url);
        response.prettyPrint();

        // Traverse to every nested level and generate javascript(Postman-like) testcases
        String endpoint = url.replace("https://", "");
        String fileName = Util.pushJSTestCasesToPropFile(response, endpoint);
        try {
            // Read the file content into a byte array
            byte[] fileBytes = Files.readAllBytes(Paths.get("src/test/javaScriptTests/" + fileName +".txt"));

            // Convert the byte array to a string
            String fileContent = new String(fileBytes);

            System.out.println("File content: " + fileContent);
            return fileContent;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * One-time execution script for any given new PUT API to
     * Generate JavaScript (PostMan) Test Cases
     */
    public String generateTestCasesforPutAPIs(String curl) {

        String url = generateUrl(curl);
        System.out.println(url);


        // Set base URI - can be Data driven for Environment and Apps
        RequestSpecification request = RestAssured.given();

        // Set Cookie for Authentication
        String cookie = null;
        String userCookie = Util.patternMatch("'Cookie(.*?)(?=')", curl).replace("Cookie: ", "")
                .replace("'", "");
        if(userCookie.length()!=0){
            cookie = userCookie;
            System.out.println("In IF Cookie --- " + cookie);
        }
        else{
            generateTokens();
            cookie = Base.setCookie(authProperties);
            System.out.println("In Else Cookie --- " + cookie);
        }


        // Build request body
        String requestBody = Util.patternMatch("\\{([^}]+)\\}", curl);
        System.out.println(requestBody);

        // POST API call
        Response response = request
                .cookie(cookie)
                .contentType("application/json").body(requestBody).put(url);
        response.prettyPrint();

        // Traverse to every nested level and generate javascript(Postman-like) testcases
        String endpoint = url.replace("https://", "");
        String fileName = Util.pushJSTestCasesToPropFile(response, endpoint);
        try {
            // Read the file content into a byte array
            byte[] fileBytes = Files.readAllBytes(Paths.get("src/test/javaScriptTests/" + fileName +".txt"));

            // Convert the byte array to a string
            String fileContent = new String(fileBytes);

            System.out.println("File content: " + fileContent);
            return fileContent;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * One-time execution script for any given new Delete API to
     * Generate JavaScript (PostMan) Test Cases
     */
    public String generateTestCasesforDeleteAPIs(String curl) {

        String url = generateUrl(curl);
        System.out.println(url);

        // Set base URI - can be Data driven for Environment and Apps
        RequestSpecification request = RestAssured.given();

        // Set Cookie for Authentication
        String cookie = null;
        String userCookie = Util.patternMatch("'Cookie(.*?)(?=')", curl).replace("Cookie: ", "")
                .replace("'", "");
        if(userCookie.length()!=0){
            cookie = userCookie;
            System.out.println("In IF Cookie --- " + cookie);
        }
        else{
            generateTokens();
            cookie = Base.setCookie(authProperties);
            System.out.println("In Else Cookie --- " + cookie);
        }

        // GET API call
        Response response = request
                .cookie(cookie)
                .delete(url);

        // Traverse to every nested level and generate javascript(Postman-like) testcases
        String endpoint = url.replace("https://", "");
        String fileName = Util.pushJSTestCasesToPropFile(response, endpoint);
        try {
            // Read the file content into a byte array
            byte[] fileBytes = Files.readAllBytes(Paths.get("src/test/javaScriptTests/" + fileName +".txt"));

            // Convert the byte array to a string
            String fileContent = new String(fileBytes);
            System.out.println("File content: " + fileContent);
            return fileContent;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String generateUrl(String curl){
        String url = Util.patternMatch("(?<=location\\s).*", curl);
        url = Util.patternMatch("'([^']*)'", url).replace("'", "");
        if(!url.contains("http")){
            url = "https://" + url;
        }
        return url;
    }

    public static void generateTokens()  {
        // Set base URI - can be Data driven for Environment and Apps
        Properties baseUriproperties = Util.loadAndGetPropertiesFile(Constants.BASE_URI_FILEPATH);
        RequestSpecification request = Base.setBaseUri(baseUriproperties);

        // Prepare request body from Curl stored in requestBody.properties file;
        String requestBody = Base.buildRequestFromCurl("VerifyOTP");

        // VERIFY-OTP POST API CALL
        Response response = request.contentType("application/json").body(requestBody).post("/portal/auth/V2/verify-otp");

        // Retrieve and store JWT and RefreshToken from VerifyOTP API
        String jwt = response.getBody().jsonPath().getJsonObject("data.jwt").toString();
        String refreshToken = response.getBody().jsonPath().getJsonObject("data.refreshToken").toString();


        // Save JWT and RefreshToken to AuthToken.properties file
        Properties properties = Util.loadAndGetPropertiesFile(Constants.AUTH_TOKEN_FILEPATH);
        properties.setProperty(jwtHeader, jwt);
        properties.setProperty(refreshTokenHeader, refreshToken);
        Util.closePropertiesFile(Constants.AUTH_TOKEN_FILEPATH,properties);
    }
}
