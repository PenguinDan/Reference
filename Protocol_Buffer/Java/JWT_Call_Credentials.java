// Adding CallCredentials for JWT

// Create the class, in the documentation, it implemented CallCredentials, and extending CallCredentials2 might just be a temporary thing, so double check
public class JwtCallCredential extends CallCredentials2 {
    // The jwt token to be carried into the credentials
    private final String jwt;

    public JwtCallCredential(String jwt) {
        this.jwt = jwt;
    }


    @Override
    public void applyRequestMetadata(RequestInfo requestInfo, Executor executor, final MetadataApplier metadataApplier) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Metadata headers = new Metadata();
                    Metadata.Key<String> jwtKey = Metadata.Key.of("jwt", Metadata.ASCII_STRING_MARSHALLER);
                    headers.put(jwtKey, jwt);
                    metadataApplier.apply(headers);
                } catch (Throwable e) {
                    metadataApplier.fail(Status.UNAUTHENTICATED.withCause(e));
                }
            }
        });
    }

    // Documentation states that this should be left unused
    @Override 
    public void thisUsesUnstableApi() {
    }
}

// Apply the following to wherever you are going to use this implementation
CoreGrpc.CoreBlockingStub stub = CoreGrpc.newBlockingStub(channel).withCallCallCredentials(new JwtCallCredential(USER_JWT));
// Create a Message
TextMessage request = TextMessage.newBuilder().setText(textMessage).build();
// Send request and retrieve response
JobResp response = stub.textReq(request);
String url = response.getUrl();