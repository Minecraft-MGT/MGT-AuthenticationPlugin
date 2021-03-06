package mc.authplugin;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class NetworkAdapter {



    public static String getCurrentServerAddress(){
        String externalIP;
        try {externalIP = new BufferedReader(new InputStreamReader(new URL("http://checkip.amazonaws.com").openStream())).readLine();}
        catch (IOException e) {externalIP = "INVALID";}
        return externalIP+":"+Main.plugin.getServer().getPort();
    }

    public static JsonObject buildApiRequst(JsonObject args){
        JsonObject requestObject = new JsonObject();
        requestObject.addProperty("cmd", "MCAUTHENTICATION");
        args.addProperty("authenticationToken", Main.apiAccessToken);
        requestObject.add("args", args);
        return  requestObject;
    }

    public static String getTokenForPlayer(String name){
        JsonObject args = new JsonObject();
        args.addProperty("method", "get_token");
        args.addProperty("playername", name);

        String response = sendApiRq(Main.webServerApiAdress, buildApiRequst(args));

        if(new JsonParser().parse(response).getAsJsonObject().get("token").isJsonNull()) return null;
        return new JsonParser().parse(response).getAsJsonObject().get("token").getAsString();

    }

    /*
    Notifies the WebServer that uses the authentication that this Minecraft Server is able to authenticate Players.
     */
    public static void registerAuthenticator(){
        Bukkit.getScheduler().scheduleAsyncDelayedTask(Main.plugin, ()-> {
            JsonObject args = new JsonObject();
            args.addProperty("method", "register");
            args.addProperty("address", getCurrentServerAddress());

            System.out.println(sendApiRq(Main.webServerApiAdress, buildApiRequst(args)));
        });
    }
    /*
    Notifies the WebServer that uses the authentication that this Minecraft Server is no longer available as an Authenticator.
     */
    public static void deregisterAuthenticator() {
        JsonObject args = new JsonObject();
        args.addProperty("method", "deregister");
        args.addProperty("address", getCurrentServerAddress());
        System.out.println(sendApiRq(Main.webServerApiAdress, buildApiRequst(args)));
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
