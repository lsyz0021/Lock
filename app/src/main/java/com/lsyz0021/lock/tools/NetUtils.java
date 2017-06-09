package com.lsyz0021.lock.tools;

import android.accounts.NetworkErrorException;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Author：Li ChuanWu on 2016/9/27
 * Blog  ：http://blog.csdn.net/lsyz0021/
 */
public class NetUtils {
    final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {

        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };


    /**
     * post网络请求
     *
     * @param baseUrl 基本的请求地址
     * @param param   请求参数
     * @return 请求的数据
     * @throws NetworkErrorException
     */
    public static String post(String baseUrl, String param) throws NetworkErrorException {
        HttpURLConnection con = null;
        int responseCode = 0;
        try {
            URL url = new URL(baseUrl);

            if ("https".equals(url.getProtocol().toLowerCase())) {
                // trust all hosts
                trustAllHosts();
                HttpsURLConnection https = (HttpsURLConnection) url.openConnection();
                https.setHostnameVerifier(DO_NOT_VERIFY);
                con = https;
            } else {
                con = (HttpURLConnection) url.openConnection();
            }

            con.setRequestMethod("POST");
            con.setConnectTimeout(10 * 1000);
            con.setReadTimeout(5 * 1000);
            con.setDoOutput(true);
            OutputStream os = con.getOutputStream();
            os.write(param.getBytes());
            os.flush();
            os.close();

            responseCode = con.getResponseCode();
            if (responseCode == 200) {
                InputStream is = con.getInputStream();
                return StreamToStr(is);
            } else {
                throw new NetworkErrorException("response status is " + responseCode);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }

        return "网络请求失败,状态码 = " + responseCode;
    }

    /**
     * @param httpUrl get的请求地址
     * @return 请求数据
     * @throws NetworkErrorException
     */
    public static String get(String httpUrl) throws NetworkErrorException {
        HttpURLConnection con = null;
        int responseCode = 0;
        try {
            URL url = new URL(httpUrl);

            if ("https".equals(url.getProtocol().toLowerCase())) {
                // trust all hosts
                trustAllHosts();
                HttpsURLConnection https = (HttpsURLConnection) url.openConnection();
                https.setHostnameVerifier(DO_NOT_VERIFY);
                con = https;
            } else {
                con = (HttpURLConnection) url.openConnection();
            }

            con.setRequestMethod("GET");
            con.setConnectTimeout(10 * 1000);
            con.setReadTimeout(5 * 1000);
            responseCode = con.getResponseCode();

            if (responseCode == 200) {
                return StreamToStr(con.getInputStream());
            } else {
                throw new NetworkErrorException("response status is " + responseCode);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // disconnecting releases the resources held by a connection so they may be closed or reused
            if (con != null) {
                con.disconnect();
            }
        }

        return "网络请求失败,状态码 = " + responseCode;
    }

    /**
     * 用于拼接网址参数，最后得到数据格式：index=0&age=47&name=zhangsan
     *
     * @param params 含有请求参数的HashMap集合
     */
    public static String createPostParmas(HashMap<String, String> params) {
        Set<String> keySet = params.keySet();                           // Set是无序的，无法排序，所以转换为ArrayList
        ArrayList<String> keys = new ArrayList<String>(keySet);
        Collections.sort(keys);                                         // 对集体排序

        // 把所有的参数拼接成index=0&age=47&name=zs
        StringBuilder sb = new StringBuilder();
        for (String key : keys) {
            sb.append("&").append(key).append("=").append(params.get(key));

        }
        // 上面for循环走完得到这样的字符串：&index=0&age=47&name=zs
        sb.deleteCharAt(0);// 删除第0位置的符号：&，得到：index=0&age=47&name=zhangsan
        return sb.toString();
    }


    /**
     * Trust every server - dont check for any certificate
     */
    private static void trustAllHosts() {
        final String TAG = "trustAllHosts";
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {

            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[]{};
            }

            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                Log.i(TAG, "checkClientTrusted");
            }

            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                Log.i(TAG, "checkServerTrusted");
            }
        }};

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 流转字符串
     *
     * @return
     * @throws IOException
     */
    private static String StreamToStr(InputStream is) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = is.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        String result = bos.toString();
        is.close();
        bos.close();
        return result;
    }

}
