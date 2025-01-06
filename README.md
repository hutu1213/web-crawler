# Distributed Web Crawler

A high-performance, distributed web crawler designed to efficiently crawl, index, and process web pages while adhering to domain-specific rate limits, avoiding duplicates, and respecting `robots.txt` files.

Reference: https://www.hellointerview.com/learn/system-design/problem-breakdowns/web-crawler
---

## Features

1. **Distributed Architecture**
    - Scalable design with support for multiple workers processing URLs concurrently.
    - Centralized queue management using RabbitMQ.

2. **Rate Limiting**
    - Domain-specific rate limits implemented with Redis.
    - Exponential backoff with jitter to avoid synchronized behavior among workers.

3. **Respect for `robots.txt`**
    - Fully respects `robots.txt` directives, including crawl delays and disallowed paths.

4. **Duplicate Content Detection**
    - Content hash-based deduplication ensures unique processing of pages.

5. **Storage**
    - HTML and stripped text content uploaded to Amazon S3 for scalable storage.
    - Metadata and URL information stored in MongoDB.

6. **Queue Management**
    - RabbitMQ-based message queues for URL management:
        - Frontier Queue: For URLs to be crawled.
        - Parsing Queue: For URLs to be parsed after crawling.

7. **Error Handling**
    - Robust retry mechanisms with jitter.
    - Detailed logging for debugging and monitoring.

8. **Extendable**
    - Modular design allows easy integration with other systems or services, such as search engines or data analytics.

---

## System Design Overview



### Workflow
1. **URL Fetching**:
    - URLs are consumed from the Frontier Queue.
    - Worker fetches HTML content using `WebClient`.
    - HTML is stripped to text and uploaded to S3.

2. **Metadata Processing**:
    - URL metadata (hash, status, last crawl time) is stored in MongoDB.

3. **Rate Limiting**:
    - Redis enforces domain-specific rate limits.
    - Jitter prevents synchronized requests among workers.

4. **Duplicate Detection**:
    - Content hash is calculated for every page.
    - Pages with duplicate hashes are skipped.

5. **Queue Management**:
    - After processing, the URL ID is pushed to the Parsing Queue for further handling.

---

## Setup and Installation

### Prerequisites
- **Java 17** or higher
- **Maven 3.9.9** or higher
- **RabbitMQ** (Message Broker)
- **Redis** (Caching and Rate Limiting)
- **MongoDB** (Metadata Storage)
- **Amazon S3** (Data Storage)

### Steps to Run
1. Clone the repository:
   ```bash
   git clone https://github.com/hutu1213/web-crawler.git
   cd web-crawler
   ```

2. Configure environment variables in `.env`:

3. Build the project:
   ```bash
   mvn clean install
   ```

4. Run the application:
   ```bash
   java -jar target/web-crawler-0.0.1-SNAPSHOT.jar
   ```

---

---

## Future Enhancements
1. Integrate with ElasticSearch for full-text indexing.
2. Add priority-based URL scheduling.
3. Support for additional content types (e.g., PDFs, images).
4. Use AWS SQS instead of RabbitMQ to leverage built-in exponential backoff mechanisms.
---

## License
This project is licensed under the MIT License. See the `LICENSE` file for details.

