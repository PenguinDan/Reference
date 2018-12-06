// Create the custom header
Metadata header = new Metadata();
Metadata.Key<String> key = Metadata.Key.of("Authentication", Metadata.ASCII_STRING_MARSHALLER);
header.put(key, USER_JWT_TOKEN);

// Create a client stub and attach a header
CoreGrpc.CoreBlockingStub stub = CoreGrpc.newBlockingStub(channel);
stub = MetadataUtils.attachHeaders(stub, header);
// Call the request
TextMessage request = TextMessage.newBuilder().setText(textMessage).build();
JobResp response = stub.textReq(request);
String url = response.getUrl();
Log.d(TAG, "Retrieved url: " + url);