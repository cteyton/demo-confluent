const producerConfig = {
  'bootstrap.servers': 'localhost:9092', // Kafka broker(s)
  'partitioner': 'consistent_random', // Default partitioning strategy
};



const producer = new Kafka.Producer({
  'bootstrap.servers': 'localhost:9092', // Kafka broker(s)
  'partitioner': 'random', // Default partitioning strategy
});




const producer = new Kafka.Producer({
  'bootstrap.servers': 'localhost:9092', // Kafka broker(s)
  'partitioner': 'murmur2', // Default partitioning strategy
});