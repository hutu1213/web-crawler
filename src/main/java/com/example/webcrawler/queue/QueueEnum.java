package com.example.webcrawler.queue;

public class QueueEnum {
    public enum Name {
        FRONTIER_QUEUE("frontierQueue"),
        PARSING_QUEUE("parsingQueue");

        private final String queueName;

        Name(String queueName) {
            this.queueName = queueName;
        }

        public String getQueueName() {
            return queueName;
        }
    }
}
