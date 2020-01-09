package org.king2.trm.client;

import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.king2.trm.cache.TransactionCache;
import org.king2.trm.pojo.TransactionPojo;

public class HttpClient {

    public static String get(String url) {
        String result = "";
        try {
            CloseableHttpClient httpClient = HttpClients.createDefault ();
            TransactionPojo transactionPojo = TransactionCache.CURRENT_TD.get ();
            HttpGet httpGet = new HttpGet (url);
            httpGet.addHeader ("Content-type", "application/json");
            httpGet.addHeader ("GTM_GROUP_ID", transactionPojo.getGroupId ());
            CloseableHttpResponse response = httpClient.execute (httpGet);

            if (response.getStatusLine ().getStatusCode () == HttpStatus.SC_OK) {
                result = EntityUtils.toString (response.getEntity (), "utf-8");
            }
            response.close ();

        } catch (Exception e) {
            e.printStackTrace ();
        }

        return result;
    }
}
