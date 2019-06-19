package hung.com.http.client;


import java.util.List;
import java.util.Map;

import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Using OkHttp to test Http client Synchronous
 */
public class App51_HttpClientSynchronous {

	public static void main(String[] args) throws Exception {
		test_OkHttp_Get_Synchronous();
	}
	
    public static void test_OkHttp_Get_Synchronous() throws Exception {

        // avoid creating several instances, should be singleon
        OkHttpClient client = new OkHttpClient();  

        // url is
        Request request = new Request.Builder()
                .url("http://localhost:81/index.html")
                .build();


        System.out.println("====================== request header ====================");
        System.out.println(request.toString());  //show url here
        System.out.println(request.headers().toString()); //error: not show all header for example: url in header here

        //start send request here.
        Response response = client.newCall(request).execute();

        // synchronous receive response here
        // https://en.wikipedia.org/wiki/List_of_HTTP_status_codes
        System.out.println("====================== response code ================== " );
        System.out.println("response code = " + response.code());
        if( response.code() == 200){ //200 = 0k
            //do something here
        }

        if( response.isSuccessful() == true){ //response code = 200 to 300
            //do something here
        }

        if( response.isRedirect()){ //response code = 3xx
            //do something here
        }

        //================================ response header =================
        Headers headers = response.headers();
        Map<String, List<String>> map = headers.toMultimap();
        System.out.println("====================== response header ====================");
        System.out.print(headers.toString());

        //body of response
        String body = response.body().string();
        System.out.print(body);
        
    }
}
