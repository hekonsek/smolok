docker rm spark_worker
        docker run --net=host -d --name spark_worker \
          -v /tmp/jobs:/tmp/jobs -P \
          -t smolok/spark-standalone-worker:0.0.0-SNAPSHOT