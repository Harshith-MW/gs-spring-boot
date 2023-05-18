package base;

import enumsAndConstants.Constants;
import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import util.Util;

import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Base {
    static String jwtHeader = "jwt";
    static String refreshTokenHeader = "refreshToken";

    public static RequestSpecification setBaseUri(Properties baseUriproperties){
        RestAssured.baseURI = baseUriproperties.getProperty("lj");
        return RestAssured.given();
    }

    public static String setCookie(Properties authProperties){
        String cookie = "mwjwt=" + authProperties.getProperty(jwtHeader) + ";mwrefreshtoken=" + authProperties.getProperty(refreshTokenHeader);
        return cookie;
    }

    public static String buildRequestFromCurl(Map.Entry<Object, Object> entry){
        Properties requestProperties = Util.loadAndGetPropertiesFile(Constants.REQUEST_BODY_FILEPATH);
        String curl = requestProperties.get(entry.getKey()).toString();
        Pattern pattern = Pattern.compile("\\{([^}]+)\\}");
        Matcher match = pattern.matcher(curl);
        String requestBody = "";
        while (match.find()) {
            //System.out.println(match.group(0));
            requestBody = match.group(0);
        }
        return requestBody;
    }

    public static String buildRequestFromCurl(String endpointName){
        Properties requestProperties = Util.loadAndGetPropertiesFile(Constants.REQUEST_BODY_FILEPATH);
        String curl = requestProperties.get(endpointName).toString();
        Pattern pattern = Pattern.compile("\\{([^}]+)\\}");
        Matcher match = pattern.matcher(curl);
        String requestBody = "";
        while (match.find()) {
            //System.out.println(match.group(0));
            requestBody = match.group(0);
        }
        return requestBody;
    }
}
