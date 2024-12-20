resource "kafka_topic" "my_topic" {
  name = "my-topic"
  partitions = 31
  replication_factor = 1
  config = {
    "retention.ms" = "86400000"
  }
}