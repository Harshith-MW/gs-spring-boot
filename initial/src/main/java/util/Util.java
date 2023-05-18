package util;

import io.restassured.response.Response;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {

    public static void pushJsonResponseAttibutesToPropFile(Response response, String endpoint) {
        String fileName = endpoint.replaceFirst("/", "").replace("/", "_");
        try (OutputStream output = new FileOutputStream("src/main/java/dto/response/" + fileName + ".properties")) {
            Properties prop = new Properties();
            // set the properties value
            Map<String, Object> jsonObjectMap = response.getBody().jsonPath().getJsonObject("");
            jsonObjectMap.entrySet().forEach(entry -> {
//                checkLinkedHashMap(jsonObjectMap, entry, prop);
                String path = null;
                if (entry.getValue() instanceof LinkedHashMap) {
//                    System.out.println(entry.getKey() + " ----- DATATYPE OF1 VALUE -------> " + entry.getValue().getClass().getName());

                    path = entry.getKey();
//                    System.out.println("JSON path LMP ---> " + path);

                    prop.setProperty(path, entry.getValue().getClass().getName());
                    Util.loopLinkedHashMap(jsonObjectMap, entry, prop, path);
                } else if (entry.getValue() instanceof ArrayList) {
//                    System.out.println(entry.getKey() + " ----- DATATYPE OF VALUE -------> " + entry.getValue().getClass().getName());

                    path = entry.getKey();
//                    System.out.println("JSON path AL ---> " + path);

                    prop.setProperty(path, entry.getValue().getClass().getName());
                    if (((ArrayList<?>) entry.getValue()).size()>0){
                    Map<String, Object> listItem = (Map<String, Object>) ((ArrayList<LinkedHashMap>) entry.getValue()).get(0);
                    String finalPath = path;
                    listItem.entrySet().forEach(entryVar -> {
                        checkLinkedHashMap(listItem, entryVar, prop, finalPath);
//                        System.out.println(entryVar.getKey() + " ----- DATATYPE OF VALUE -------> " + entryVar.getValue().getClass().getName());
                        String pathBuilder1 = finalPath + "[0]" + "." + entryVar.getKey();
//                        System.out.println(pathBuilder1);
                        prop.setProperty(pathBuilder1, entryVar.getValue().getClass().getName());
                    });
                    }
                } else {
//                    System.out.println(entry.getKey() + " ----- DATATYPE OF VALUE -------> " + entry.getValue().getClass().getName());

                    path = entry.getKey();
//                    System.out.println("JSON path OTHER ---> " + path);

                    prop.setProperty(path, entry.getValue().getClass().getName());
                }
            });

            // save properties to project root folder
            prop.store(output, null);
//            System.out.println(prop);

        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    public static void loopLinkedHashMap(Map<String, Object> jsonObjectMap, Map.Entry<String, Object> entry, Properties prop, String path) {
        boolean jsonObjectMapFlag = true;
        Map<String, Object> jsonObjectMapData = (Map<String, Object>) jsonObjectMap.get(entry.getKey());
//        String pathBuilder = path;
        while (jsonObjectMapFlag) {
            jsonObjectMapData.entrySet().forEach(entry1 -> {
                if (entry1.getValue() instanceof LinkedHashMap) {
//                    System.out.println(entry1.getKey() + " ----- DATATYPE OF2 VALUE -------> " + entry1.getValue().getClass().getName());

                    String pathBuilder = path + "." + entry1.getKey();
//                    System.out.println(pathBuilder);

                    prop.setProperty(pathBuilder, entry1.getValue().getClass().getName());
                    loopLinkedHashMap(jsonObjectMapData, entry1, prop, pathBuilder);
                } else if (entry1.getValue() instanceof ArrayList) {
//                    System.out.println(entry1.getKey() + " ----- DATATYPE OF3 VALUE -------> " + entry1.getValue().getClass().getName());

                    String pathBuilder = path + "." + entry1.getKey();
//                    System.out.println(pathBuilder);

                    prop.setProperty(pathBuilder, entry1.getValue().getClass().getName());
                    if (((ArrayList<?>) entry1.getValue()).size()>0){
                    Map<String, Object> listItem = (Map<String, Object>) ((ArrayList<LinkedHashMap>) entry1.getValue()).get(0);

                    listItem.entrySet().forEach(entryVar -> {
                        checkLinkedHashMap(listItem, entryVar, prop, pathBuilder);
//                        System.out.println(entryVar.getKey() + " ----- DATATYPE OF4 VALUE -------> " + entryVar.getValue().getClass().getName());

                        String pathBuilder1 = pathBuilder + "[0]" + "." + entryVar.getKey();
//                        System.out.println(pathBuilder1);

                        prop.setProperty(pathBuilder1, entryVar.getValue().getClass().getName());
                    });
                    }
                } else {
//                    System.out.println(entry1.getKey() + " ----- DATATYPE OF5 VALUE -------> " + entry1.getValue().getClass().getName());

                    String pathBuilder = path + "." + entry1.getKey();
//                    System.out.println(pathBuilder);

                    prop.setProperty(pathBuilder, entry1.getValue().getClass().getName());
                }
            });
            jsonObjectMapFlag = false;
        }
    }

    public static void checkLinkedHashMap(Map<String, Object> jsonObjectMap, Map.Entry<String, Object> entry, Properties prop, String path) {
        if (entry.getValue() instanceof LinkedHashMap) {
            String pathBuilder = "." + path + "." + entry.getKey();
            prop.setProperty(pathBuilder, entry.getValue().getClass().getName());
            Util.loopLinkedHashMap(jsonObjectMap, entry, prop, pathBuilder);
        }
    }


    public static void checkInstanceOfEntryInList1(Map.Entry<String, Object> entry, BufferedWriter fileWriter, String concatStr) {
        String arrayItemTcStr;
        if(entry.getValue() instanceof String) {
            arrayItemTcStr = concatStr + entry.getKey()  + ").that.is.a('string');\n";
            writeFile(fileWriter, arrayItemTcStr);
        }
        else if(entry.getValue() instanceof Integer){
            arrayItemTcStr = concatStr + entry.getKey() + ").that.is.a('number');\n";
            writeFile(fileWriter, arrayItemTcStr);
        }
        else if(entry.getValue() instanceof Boolean){
            arrayItemTcStr = concatStr + entry.getKey() + ").that.is.a('boolean');\n";
            writeFile(fileWriter, arrayItemTcStr);
        }
        else if(entry.getValue() instanceof ArrayList){
            arrayItemTcStr = concatStr + entry.getKey() + ").that.is.a('array');\n";
            writeFile(fileWriter, arrayItemTcStr);
            try {
                if(((ArrayList) entry.getValue()).size()>0){
                    Map<String, Object> listItem = (Map<String, Object>) ((ArrayList<LinkedHashMap>) entry.getValue()).get(0);
                    String commonArrayStr = concatStr + entry.getKey() + "[0].";
                    listItem.entrySet().forEach(entryVar -> {
                        checkHashMap(listItem, entryVar, fileWriter, commonArrayStr);
                        checkInstanceOfEntryInList(entryVar, fileWriter, commonArrayStr);
                    });
                }

            }
            catch (IndexOutOfBoundsException e){
                e.printStackTrace();
            }
        }
    }


    public static void getJSONValueUsingPath(Response response) {
        String data = response.getBody().jsonPath().getString("data");
    }

    public static Properties loadAndGetPropertiesFile(String filePath){
        try {
            // All API endpoints fetched from a config file
            File file = new File(filePath);
            FileInputStream fileInput = new FileInputStream(file);
            Properties properties = new Properties();
            properties.load(fileInput);
            fileInput.close();
            return properties;
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Properties createPropertiesFile(String filePath) {
        try {
            FileOutputStream output = new FileOutputStream(filePath);
            Properties properties = new Properties();
            return properties;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void closePropertiesFile(String filePath, Properties properties){
        try{
            FileOutputStream fileOutputStream = new FileOutputStream(filePath);
            properties.store(fileOutputStream, null);
        }catch (IOException e){
            e.printStackTrace();
        }

    }

    public static String pushJSTestCasesToPropFile(Response response, String endpoint) {
        // Create a new text file for every API to push all JS test cases
        String fileName = endpoint.replaceFirst("/", "").replace("/", "_");
        try {
            File file = new File("src/test/javaScriptTests/" + fileName + ".txt");
            //FileWriter fileWriter = new FileWriter("src/test/javaScriptTests/" + file.getName());
            BufferedWriter fileWriter = new BufferedWriter(new FileWriter("src/test/javaScriptTests/" + file.getName()));
            fileWriter.write("pm.test(\"Verify data object has correct properties and types\", function () {\n");
            String testcases = "";

            // Store the API json response in a map object
            Map<String, Object> jsonObjectMap = response.getBody().jsonPath().getJsonObject("");

            String initialStr = "pm.expect(pm.response.json().";
            jsonObjectMap.entrySet().forEach(entry -> {
                if (entry.getValue() instanceof LinkedHashMap) {
                    String tcStr = initialStr + entry.getKey() + ").to.be.an('object');\n";
                    writeFile(fileWriter, tcStr);
                    String concatStr = "pm.expect(pm.response.json()." + entry.getKey() + ".";
                    Util.loopHashMap(jsonObjectMap, entry, fileWriter, concatStr);
                } else if (entry.getValue() instanceof ArrayList) {
                    String tcStr = initialStr + entry.getKey() + ").to.be.an('array');\n";
                    writeFile(fileWriter, tcStr);
                    if (((ArrayList<?>) entry.getValue()).size()>0){
                    Map<String, Object> listItem = (Map<String, Object>) ((ArrayList<LinkedHashMap>) entry.getValue()).get(0);
                    String commonArrayStr = "pm.expect(pm.response.json()." + entry.getKey() + "[0].";
                    listItem.entrySet().forEach(entryVar -> {
                        checkHashMap(listItem, entryVar, fileWriter, commonArrayStr);
                        checkInstanceOfEntryInList(entryVar,fileWriter,commonArrayStr);
                    });
                    }
                } else {
                    checkInstanceOfEntry(entry,fileWriter,initialStr);
                }

            });
            fileWriter.write("});");
            fileWriter.close();
        } catch (IOException io) {
            io.printStackTrace();
        }
        return fileName;
    }

    public static void loopHashMap(Map<String, Object> jsonObjectMap, Map.Entry<String, Object> entry, BufferedWriter fileWriter, String concatStr) {
        boolean jsonObjectMapFlag = true;
        Map<String, Object> jsonObjectMapData = (Map<String, Object>) jsonObjectMap.get(entry.getKey());
        while (jsonObjectMapFlag) {
            jsonObjectMapData.entrySet().forEach(entry1 -> {
                String tcStr1;
                if (entry1.getValue() instanceof LinkedHashMap) {
                    tcStr1 = concatStr + entry1.getKey() + ").to.be.an('object');\n";
                    writeFile(fileWriter, tcStr1);
                    String concatStr1 = concatStr + entry1.getKey() + ".";
                    loopHashMap(jsonObjectMapData, entry1, fileWriter, concatStr1);
                } else if (entry1.getValue() instanceof ArrayList) {
                    tcStr1 = concatStr + entry1.getKey() + ").to.be.an('array');\n";
                    writeFile(fileWriter, tcStr1);
                    try {
                        if(((ArrayList<?>) entry1.getValue()).size()>0){
                        Map<String, Object> listItem = (Map<String, Object>) ((ArrayList<LinkedHashMap>) entry1.getValue()).get(0);
                        String commonArrayStr = concatStr + entry1.getKey() + "[0].";
                        listItem.entrySet().forEach(entryVar -> {
                            checkHashMap(listItem, entryVar, fileWriter, commonArrayStr);
                            checkInstanceOfEntryInList(entryVar, fileWriter, commonArrayStr);
                        });
                        }
                    }
                    catch (IndexOutOfBoundsException e){
                        e.printStackTrace();
                    }

                } else {
                    checkInstanceOfEntry(entry1, fileWriter, concatStr);
                }
            });
            jsonObjectMapFlag = false;
        }
    }

    public static void checkHashMap(Map<String, Object> jsonObjectMap, Map.Entry<String, Object> entry, BufferedWriter fileWriter, String tcStr) {
        if (entry.getValue() instanceof LinkedHashMap) {
            Util.loopHashMap(jsonObjectMap, entry, fileWriter, tcStr);
        }
    }

    public static void checkInstanceOfEntry(Map.Entry<String, Object> entry, BufferedWriter fileWriter, String concatStr){
        if(entry.getValue() instanceof String) {
            String tcStr = concatStr + entry.getKey() + ").that.is.a('string');\n";
            writeFile(fileWriter, tcStr);
        }
        else if(entry.getValue() instanceof Integer){
            String tcStr = concatStr + entry.getKey() + ").that.is.a('number');\n";
            writeFile(fileWriter, tcStr);
        }
        else if(entry.getValue() instanceof Boolean){
            String tcStr = concatStr + entry.getKey() + ").that.is.a('boolean');\n";
            writeFile(fileWriter, tcStr);
        }
    }

    public static void checkInstanceOfEntryInList(Map.Entry<String, Object> entry, BufferedWriter fileWriter, String concatStr) {
        String arrayItemTcStr;
        if(entry.getValue() instanceof String) {
            arrayItemTcStr = concatStr + entry.getKey()  + ").that.is.a('string');\n";
            writeFile(fileWriter, arrayItemTcStr);
        }
        else if(entry.getValue() instanceof Integer){
            arrayItemTcStr = concatStr + entry.getKey() + ").that.is.a('number');\n";
            writeFile(fileWriter, arrayItemTcStr);
        }
        else if(entry.getValue() instanceof Boolean){
            arrayItemTcStr = concatStr + entry.getKey() + ").that.is.a('boolean');\n";
            writeFile(fileWriter, arrayItemTcStr);
        }
        else if(entry.getValue() instanceof ArrayList){
            arrayItemTcStr = concatStr + entry.getKey() + ").that.is.a('array');\n";
            writeFile(fileWriter, arrayItemTcStr);
            try {
                if(((ArrayList) entry.getValue()).size()>0){
                Map<String, Object> listItem = (Map<String, Object>) ((ArrayList<LinkedHashMap>) entry.getValue()).get(0);
                    String commonArrayStr = concatStr + entry.getKey() + "[0].";
                    listItem.entrySet().forEach(entryVar -> {
                        checkHashMap(listItem, entryVar, fileWriter, commonArrayStr);
                        checkInstanceOfEntryInList(entryVar, fileWriter, commonArrayStr);
                    });
                }

            }
            catch (IndexOutOfBoundsException e){
                e.printStackTrace();
            }
        }
    }

    public static void writeFile(BufferedWriter fileWriter, String content){
        try {
            fileWriter.write(content);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String patternMatch(String sequence, String targetString){
        Pattern pattern = Pattern.compile(sequence);
        Matcher match = pattern.matcher(targetString);
        String matchedString = "";
        while (match.find()) {
            //System.out.println(match.group(0));
            matchedString = match.group(0);
        }
        return matchedString;
    }

    public static String generateUrl(String curl){
        String url = Util.patternMatch("(?<=location\\s).*",curl);
        System.out.println("gvbhjnk "+url);
        String[] url1 = url.split("'");
        url = url1[0];
        System.out.println("h jkl");
        if(!url.contains("http")){
            url = "https://" + url;
        }
        return url;
    }
}
