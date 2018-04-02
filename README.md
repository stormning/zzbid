### 准备工作

``` bash
make dir -p /opt/tesseract \ 
	&& curl -o /opt/tesseract/entrypoint.sh https://raw.githubusercontent.com/stormning/zzbid/master/entrypoint.sh 
```

### 运行docker镜像

``` bash
docker run -idt -p 8080:8080 \
    -v /opt/tesseract/entrypoint.sh:/entrypoint.sh \
    -v /opt/tesseract/tessdata:/usr/local/share/tessdata \
    -v /opt/zzbid/data:/root/.h2
    --restart always --name zzbid slyak/tesseract
```    