Upload
export S3_KEY="minio"
export S3_SECRET="minio123"
export RESOURCE="/test/hoge.txt"
export CONTENT_TYPE="application/octet-stream"
export DATE=`date -R`
export _SIGNATURE="PUT\n\n${CONTENT_TYPE}\n${DATE}\n${RESOURCE}"
export SIGNATURE=`echo -en ${_SIGNATURE} | openssl sha1 -hmac ${S3_SECRET} -binary | base64`
curl -v -X PUT http://127.0.0.1:9090${RESOURCE} \
-T /tmp/hoge.txt \
-H "Host: 127.0.0.1:9090" \
-H "Date: ${DATE}" \
-H "Content-Type: ${CONTENT_TYPE}" \
-H "Authorization: AWS ${S3_KEY}:${SIGNATURE}"



curl -v -X PUT http://127.0.0.1:9090/test/hoge.txt \
-T /tmp/hoge.txt \
-H "Host: 127.0.0.1:9090" \
-H "Date: Sat, 26 Feb 2022 21:34:56 +0900" \
-H "Content-Type: ${CONTENT_TYPE}" \
-H "Authorization: AWS minio:XS4KHVc7NXAafuSiIUCJ99JM3Ew="


    curl -v -X PUT http://192.168.11.11:9090/test/hoge.txt \
-T /tmp/hoge.txt \
-H "Host: 192.168.11.11:9090" \
-H "Date: Sat, 26 Feb 2022 21:34:56 +0900" \
-H "Content-Type: ${CONTENT_TYPE}" \
-H "Authorization: AWS minio:XS4KHVc7NXAafuSiIUCJ99JM3Ew="




Download
export S3_KEY="minio"
export S3_SECRET="minio123"
export RESOURCE="/test/testfile.txt"
export CONTENT_TYPE="application/zstd"
export DATE=`date -R`
export _SIGNATURE="GET\n\n${CONTENT_TYPE}\n${DATE}\n${RESOURCE}"
export SIGNATURE=`echo -en ${_SIGNATURE} | openssl sha1 -hmac ${S3_SECRET} -binary | base64`

export OUT_FILE="aa.txt"
curl -v -o "${OUT_FILE}"  http://127.0.0.1:9090${RESOURCE} \
-H "Host: 127.0.0.1:9090" \
-H "Date: ${DATE}" \
-H "Content-Type: ${CONTENT_TYPE}" \
-H "Authorization: AWS ${S3_KEY}:${SIGNATURE}"