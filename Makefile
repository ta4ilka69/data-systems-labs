back:
	cd back && mvn spring-boot:run -X

front:
	cd front && npm run dev

minio:
	cd minio && minio server . --console-address :9001

k6:
	k6 run script.js

.PHONY: back front minio k6
