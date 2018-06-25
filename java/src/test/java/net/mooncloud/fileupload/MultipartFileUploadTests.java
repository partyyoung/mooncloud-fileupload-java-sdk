package net.mooncloud.fileupload;

import java.io.IOException;

public class MultipartFileUploadTests {
	public static String HOST = "192.168.10.101:12121"; // "127.0.0.1:8080";// "172.16.1.78:2121"; //

	public static void main(String[] args) throws IOException {
		MultipartFileUpload.HOST(HOST);
		String filePath = "/home/yangjd/Documents/git/mooncloud/mooncloud-fileupload-service/target/mooncloud-fileupload-service-0.0.1-SNAPSHOT.jar";
		// MultipartFileUpload.upload(filePath);
		MultipartFileUpload.upload2http(filePath, "/static/img", false, false);
	}
}
