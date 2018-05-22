import requests
# 演示用，一般随便搞个就可以，此地址会返回404，但不影响观看请求体
url = "http://127.0.0.1:8080/file/upload2path"

# 折中方案，参数按如下方式组织，也是模拟multipart/form-data的核心
files = {'file': ('MultipartFileUpload.java', open('src/main/java/net/mooncloud/fileupload/MultipartFileUpload.java', 'rb'), 'application/octet-stream'), 'path': (None, "/tmp/upload/", 'text/plain')}

res = requests.post(url, files=files)
# 查看请求体是否符合要求，有具体接口可以直接用具体接口，成功则符合要求，此处主要是演示用
print res.request.body
# 查看请求头
print res.request.headers