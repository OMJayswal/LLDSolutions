package com.test;


import lombok.Data;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Data
class MessageQueueBroker{
    private static MessageQueueBroker instance;
    Map<String,Topic> topicMap = new ConcurrentHashMap<>();
    private MessageQueueBroker(){

    }
    public static MessageQueueBroker getInstance(){
        if(instance == null){
            synchronized (MessageQueueBroker.class){
                if(instance ==null) {
                    instance = new MessageQueueBroker();
                    return instance;
                }
            }
        }
        return instance;
    }

    public void createTopic(String name,int numberOfPartitions){
        if(topicMap.containsKey(name)){
            System.out.println("Topic already exists");
            return;
        }
        Topic topic = new Topic(name,numberOfPartitions,new RoundRobinPartitionStrategy());
        topicMap.put(name,topic);
    }

    public void publish(String name,String message){
        if(!topicMap.containsKey(name)){
            System.out.println("No such topic exists");
            return;
        }
        Topic topic = topicMap.get(name);
        topic.sendMessage(message);
    }

    public void subscribe(String topic, String groupId, Consumer consumer){
        if(!topicMap.containsKey(topic)){
            System.out.println("Topic doesn't exist");
            return;
        }
        Topic t = topicMap.get(topic);
        t.addConsumer(groupId,consumer);
    }



}

@Data
class Topic{
    private String name;
    private List<Partition> partitions = new ArrayList<>();
    PartitionStrategy partitionStrategy;
    private int numberOfPartitions;
    private Map<String,ConsumerGroup> consumerGroups = new ConcurrentHashMap<>();

    public Topic(String name,int numberOfPartitions,PartitionStrategy partitionStrategy){
        this.name = name;
        this.numberOfPartitions = numberOfPartitions;
        for(int i=0;i<numberOfPartitions;i++){
            partitions.add(new Partition(String.valueOf(i), consumerGroups));
        }
        this.partitionStrategy = partitionStrategy;
    }

    public void sendMessage(String message){
        int partition = partitionStrategy.selectPartition(message,numberOfPartitions);
        Partition p = partitions.get(partition);
        p.addMessage(message);
    }

    public void addConsumer(String id,Consumer consumer){
         consumerGroups.putIfAbsent(id,new ConsumerGroup(id));
         consumerGroups.get(id).addConsumer(consumer);
    }

}

class Partition{
    private String id;
    private BlockingQueue<String> queue = new LinkedBlockingQueue<>();
    private Map<String,ConsumerGroup> consumerGroupMap;
    private ExecutorService dispatcher = Executors.newSingleThreadExecutor();

    public Partition(String id,Map<String,ConsumerGroup> consumerGroupMap){
        this.id = id;
        this.consumerGroupMap = consumerGroupMap;
        startDispatcher();
    }

    public void addMessage(String message){
        queue.add(message);
    }

    private void startDispatcher(){
       dispatcher.submit(() ->{
           while(true){
               try{
                   String message = queue.poll();
                   if(message!= null) {
                       for (ConsumerGroup consumerGroup : consumerGroupMap.values()) {
                           consumerGroup.deliver("Partition:" + id + " " + message);
                       }
                   }
               }catch (Exception e){
                   Thread.currentThread().interrupt();
                   break;
               }
           }
       });
    }
}

class Consumer{
    String id;
    public Consumer(String id){
        this.id = id;
    }
    public void consume(String message){
        System.out.println("Message is consumed :"+message);
    }
}

class ConsumerGroup{
    private String id;
    private List<Consumer> consumers = new CopyOnWriteArrayList<>();
    ExecutorService executorService = Executors.newFixedThreadPool(5);
    private AtomicInteger atomicInteger = new AtomicInteger(0);

    public ConsumerGroup(String id){
        this.id = id;
    }

    public void addConsumer(Consumer consumer){
        consumers.add(consumer);
    }

    public void deliver(String message){
        if(consumers.isEmpty()){
            System.out.println("Consumers are empty");
            return;
        }
        int index = atomicInteger.addAndGet(1)%consumers.size();
        Consumer consumer = consumers.get(index);
        executorService.submit(() -> consumer.consume(message));
    }

}

interface PartitionStrategy{
    int selectPartition(String message,int numberOfPartitions);
}

class RoundRobinPartitionStrategy implements PartitionStrategy{
    private AtomicInteger counter = new AtomicInteger(0);
    public int selectPartition(String name,int numberOfPartitions){
       return counter.addAndGet(1)%numberOfPartitions;
    }
}

public class KafkaLLD {
    public static void main(String []args) throws InterruptedException {
        MessageQueueBroker messageQueueBroker = MessageQueueBroker.getInstance();
        messageQueueBroker.createTopic("topic-1",3);

        messageQueueBroker.subscribe("topic-1","group-1",new Consumer("consumer-1"));
        messageQueueBroker.subscribe("topic-1","group-1",new Consumer("consumer-2"));
        messageQueueBroker.subscribe("topic-1","group-1",new Consumer("consumer-3"));

        for(int i=0;i<10;i++){
            messageQueueBroker.publish("topic-1","Message-"+i);
        }
        Thread.sleep(2000);
    }
}
