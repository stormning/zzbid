### 准备工作

``` bash
make dir -p /opt/tesseract \
	&& cd /opt/tesseract \
	&& wget https://raw.githubusercontent.com/stormning/zzbid/master/entrypoint.sh
	&& chmod +x /opt/tesseract/entrypoint.sh
```

### 运行docker镜像

``` bash
docker run -idt -p 8080:8080 \
    -v /opt/tesseract/entrypoint.sh:/entrypoint.sh \
    -v /opt/tesseract/tessdata:/usr/local/share/tessdata \
    --restart always --name zzbid slyak/tesseract
```    