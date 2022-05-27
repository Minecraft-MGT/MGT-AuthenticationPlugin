package mc.authplugin;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class NetworkAdapter {

    public static final String webServerApiAdress = "http://127.0.0.1:31313/api/";
    public static final String ApiAccessToken = "3OfbKnsQgQEXzcJGu8cuIGgHN9Ftg3bfu8IWr32P6JTe7Ffl0W";

    public static JsonObject buildApiRequst(JsonObject args){
        JsonObject requestObject = new JsonObject();
        requestObject.addProperty("cmd", "MCAUTHENTICATION");
        args.addProperty("authenticationToken", ApiAccessToken);
        requestObject.add("args", args);
        return  requestObject;
    }

    public static String getTokenForPlayer(String name){
        JsonObject args = new JsonObject();
        args.addProperty("method", "get_token");
        args.addProperty("playername", name);

        String response = sendApiRq(webServerApiAdress, buildApiRequst(args));

        return new JsonParser().parse(response).getAsJsonObject().get("token").getAsString();

    }

    /*
    Notifies the WebServer that uses the authentication that this Minecraft Server is able to authenticate Players.
     */
    public static void registerAuthenticator(){

        JsonObject args = new JsonObject();
        args.addProperty("method", "register");

        System.out.println(sendApiRq(webServerApiAdress, buildApiRequst(args)));
    }
    /*
    Notifies the WebServer that uses the authentication that this Minecraft Server is no longer available as an Authenticator.
     */
    public static void deregisterAuthenticator() {

        JsonObject args = new JsonObject();
        args.addProperty("method", "deregister");

        System.out.println(sendApiRq(webServerApiAdress, buildApiRequst(args)));

    }



    public static String sendApiRq(String url, JsonObject data){
        String body = new Gson().toJson(data);
        System.out.println(url+" --> "+body);

        HttpURLConnection connection = null;

        try {
            //Create connection
            URL curl = new URL(url);
            connection = (HttpURLConnection) curl.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type",
                    "invaldtofallbacktodatas");

            connection.setRequestProperty("Content-Length",
                    Integer.toString(body.getBytes().length));
            connection.setRequestProperty("Content-Language", "en-US");

            connection.setUseCaches(false);
            connection.setDoOutput(true);

            //Send request
            DataOutputStream wr = new DataOutputStream (
                    connection.getOutputStream());
            wr.writeBytes(body);
            wr.close();

            //Get Response
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String response = "";
            String line;
            while ((line = rd.readLine()) != null) {
                response+=line.replaceAll(" ","");
            }
            rd.close();
            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

}