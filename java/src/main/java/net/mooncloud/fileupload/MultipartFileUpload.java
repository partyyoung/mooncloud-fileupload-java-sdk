package net.mooncloud.fileupload;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.sun.tools.javac.util.Assert;

public class MultipartFileUpload {

	public static String HOST = "127.0.0.1:8080";
	private static String URL = "http://" + HOST + "/file/upload";
	private static String URL2PATH = "http://" + HOST + "/file/upload2path";
	private static String URL2HTTP = "http://" + HOST + "/file/upload2http";

	public static void HOST(final String host) {
		HOST = host;
		URL = "http://" + HOST + "/file/upload";
		URL2PATH = "http://" + HOST + "/file/upload2path";
		URL2HTTP = "http://" + HOST + "/file/upload2http";
	}

	/**
	 * @param filePath
	 * @return
	 * @throws IOException
	 */
	public static Object upload(String filePath) throws IOException {
		// 1. 创建上传需要的元素类型
		// 1.1 装载本地上传图片的文件
		File file = new File(filePath);
		Assert.check(file.exists(), "文件 not exists");
		FileBody fileBody = new FileBody(file);
		// 2. 将所有需要上传元素打包成HttpEntity对象
		MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
		multipartEntityBuilder.addPart("file", fileBody);
		HttpEntity reqEntity = multipartEntityBuilder.build();
		System.out.println("打包数据完成");
		return _upload(reqEntity, URL);
	}

	/**
	 * @param filePath
	 * @param remotePath
	 * @return
	 * @throws IOException
	 */
	public static Object upload2path(String filePath, String remotePath) throws IOException {
		// 1. 创建上传需要的元素类型
		// 1.1 装载本地上传图片的文件
		File file = new File(filePath);
		Assert.check(file.exists(), "文件 not exists");
		FileBody fileBody = new FileBody(file);
		// 2. 将所有需要上传元素打包成HttpEntity对象
		MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
		multipartEntityBuilder.addPart("file", fileBody);
		if (remotePath != null) {
			multipartEntityBuilder.addPart("path", new StringBody(remotePath, ContentType.DEFAULT_TEXT));
		}
		HttpEntity reqEntity = multipartEntityBuilder.build();
		System.out.println("打包数据完成");
		return _upload(reqEntity, URL2PATH);
	}

	/**
	 * @param filePath
	 * @param remotePath
	 * @return
	 * @throws IOException
	 */
	public static Object upload2http(String filePath, String path, boolean rename, boolean overwrite)
			throws IOException {
		// 1. 创建上传需要的元素类型
		// 1.1 装载本地上传图片的文件
		File file = new File(filePath);
		Assert.check(file.exists(), "文件 not exists");
		FileBody fileBody = new FileBody(file);
		// 2. 将所有需要上传元素打包成HttpEntity对象
		MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
		multipartEntityBuilder.addPart("file", fileBody);
		if (path != null) {
			multipartEntityBuilder.addPart("path", new StringBody(path, ContentType.DEFAULT_TEXT));
		}
		multipartEntityBuilder.addPart("rename", new StringBody(String.valueOf(rename), ContentType.DEFAULT_TEXT));
		multipartEntityBuilder.addPart("overwrite",
				new StringBody(String.valueOf(overwrite), ContentType.DEFAULT_TEXT));
		HttpEntity reqEntity = multipartEntityBuilder.build();
		System.out.println("打包数据完成");
		return _upload(reqEntity, URL2HTTP);
	}

	private static Object _upload(final HttpEntity reqEntity, final String url) throws IOException {
		// 3. 创建HttpPost对象，用于包含信息发送post消息
		HttpPost httpPost = new HttpPost(url);
		httpPost.setEntity(reqEntity);
		System.out.println("创建post请求并装载好打包数据");
		// 4. 创建HttpClient对象，传入httpPost执行发送网络请求的动作
		CloseableHttpClient httpClient = HttpClients.createDefault();
		CloseableHttpResponse response = httpClient.execute(httpPost);
		System.out.println("发送post请求并获取结果");
		// 5. 获取返回的实体内容对象并解析内容
		HttpEntity resultEntity = response.getEntity();
		String responseMessage = "";
		try {
			System.out.println("开始解析结果");
			if (resultEntity != null) {
				InputStream is = resultEntity.getContent();
				BufferedReader br = new BufferedReader(new InputStreamReader(is));
				StringBuffer sb = new StringBuffer();
				String line = "";
				while ((line = br.readLine()) != null) {
					sb.append(line);
				}
				responseMessage = sb.toString();
				System.out.println("解析完成，解析内容为" + responseMessage);
			}
			EntityUtils.consume(resultEntity);
		} finally {
			if (null != response) {
				response.close();
			}
		}
		return responseMessage;
	}
}
