# Techniques

## Streaming Multiple Messages
If you want to write multiple messages to a single file or stream, it is up to you to keep track of where one message ends and the next begins. The easiest way to solved this problem is to write the size of each message before you write the message itself. When you read the message nack in, you read the size, then read the bytes into a separate buffer, then parse from that buffer. 
* **CodedInputStream** from C++ and Java can limit reads to a certain number of bytes to avoid copying bytes to a separate buffer.