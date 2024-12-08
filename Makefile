back:
	cd back && mvn spring-boot:run -X

front:
	cd front && npm run dev

minio:
	cd minio && minio server . --console-address :9001

.PHONY: back front minio
