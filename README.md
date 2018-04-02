### 准备工作

``` bash
mkdir /opt/tesseract \
	&& cd /opt/tesseract \
	&& wget https://raw.githubusercontent.com/stormning/zzbid/master/entrypoint.sh \
	&& chmod +x /opt/tesseract/entrypoint.sh
```

### 运行docker镜像

``` bash
docker run -idt -p 8080:8080 \
    -v /opt/tesseract/entrypoint.sh:/entrypoint.sh \
    -v /opt/tesseract/tessdata:/usr/local/share/tessdata \
    -v /opt/zzbid/source:/opt/zzbid/source \
    -v /opt/zzbid/data:/root/.h2 \
    --restart always --name zzbid slyak/tesseract
```    