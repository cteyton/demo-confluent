const producerConfig = {
  'bootstrap.servers': 'localhost:9092', // Kafka broker(s)
  'partitioner': 'murmur2_random', // Default partitioning strategy
};



const producer = new Kafka.Producer({
  'bootstrap.servers': 'localhost:9092', // Kafka broker(s)
  'partitioner': 'murmur2_random', // Default partitioning strategy
});