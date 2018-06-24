package net.mooncloud.fileupload;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.tools.javac.util.Assert;

public class MultipartFileUpload {

	private final static Logger LOGGER = LoggerFactory.getLogger(MultipartFileUpload.class);

	public static String HOST = "127.0.0.1:8080";
	private static String URL = "http://" + HOST + "/upload/oss/upload";
	private static String URL2PATH = "http://" + HOST + "/upload/oss/upload2path";
	private static String URL2HTTP = "http://" + HOST + "/upload/oss/upload2http";

	public static void HOST(final String host) {
		HOST = host;
		URL = "http://" + HOST + "/upload/oss/upload";
		URL2PATH = "http://" + HOST + "/upload/oss/upload2path";
		URL2HTTP = "http://" + HOST + "/upload/oss/upload2http";
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
		return _upload(reqEntity, URL, file.length());
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
		return _upload(reqEntity, URL2PATH, file.length());
	}

	/**
	 * @param filePath
	 * @param path
	 * @param rename
	 * @param overwrite
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
		LOGGER.debug("打包数据完成");
		return _upload(reqEntity, URL2HTTP, file.length());
	}

	private static Object _upload(final HttpEntity reqEntity, final String url, final long length) throws IOException {
		Long start = System.currentTimeMillis();
		Map<String, Object> ret = new HashMap<String, Object>();
		ret.put("start", start);
		// 3. 创建HttpPost对象，用于包含信息发送post消息
		HttpPost httpPost = new HttpPost(url);
		httpPost.setEntity(reqEntity);
		LOGGER.debug("创建post请求并装载好打包数据");
		// System.out.println("创建post请求并装载好打包数据");
		// 4. 创建HttpClient对象，传入httpPost执行发送网络请求的动作
		CloseableHttpClient httpClient = HttpClients.createDefault();
		CloseableHttpResponse response = httpClient.execute(httpPost);
		LOGGER.debug("发送post请求并获取结果");
		// System.out.println("发送post请求并获取结果");
		// 5. 获取返回的实体内容对象并解析内容
		HttpEntity resultEntity = response.getEntity();
		String responseMessage = "";
		try {
			LOGGER.debug("开始解析结果");
			// System.out.println("开始解析结果");
			if (resultEntity != null) {
				InputStream is = resultEntity.getContent();
				BufferedReader br = new BufferedReader(new InputStreamReader(is));
				StringBuffer sb = new StringBuffer();
				String line = "";
				while ((line = br.readLine()) != null) {
					sb.append(line);
				}
				responseMessage = sb.toString();
				LOGGER.debug("解析完成，解析内容为" + responseMessage);
				// System.out.println("解析完成，解析内容为" + responseMessage);
			}
			EntityUtils.consume(resultEntity);
		} finally {
			if (null != response) {
				response.close();
			}
		}
		Long end = System.currentTimeMillis();
		ret.put("length", length);
		ret.put("end", end);
		ret.put("taken", end - start);
		ret.put("response", responseMessage);
		return ret;
	}
}
