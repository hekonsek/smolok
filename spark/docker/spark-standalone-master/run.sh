docker rm spark_master
docker run -d --net=host --name spark_master -p 8081:8080 -P -t smolok/spark-standalone:0.0.0-SNAPSHOT /start-master.sh "$@"