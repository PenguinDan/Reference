public class HeaderClientInterceptor implements ClientInterceptor {
    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> method, CallOptions callOptions, Channel next) {
        return new SimpleForwardingClientCall<ReqT, RespT>(next.newCall(method, callOptions)) {

            @Override
            public void start(Listener<RespT> responseListener, Metadata headers)       {
                /* put custom header */
                Timber.d("header sending to server:");


                Metadata fixedHeaders=new Metadata();
                Metadata.Key<String> key =
                    Metadata.Key.of("Grps-Matches-Key", Metadata.ASCII_STRING_MARSHALLER);
                fixedHeaders.put(key, "primary.secondary");

                headers.merge(fixedHeaders);

                super.start(new SimpleForwardingClientCallListener<RespT>(responseListener) {
                    @Override
                    public void onHeaders(Metadata headers) {
                        /**
                         * if you don't need receive header from server,
                        * you can use {@link io.grpc.stub.MetadataUtils attachHeaders}
                        * directly to send header
                        */

                        Timber.e("header received from server:" + headers.toString());
                        super.onHeaders(headers);
                    }
                 }, headers);
            }
        };
    }
}  


// Then in the inline code
ClientInterceptor interceptor = new HeaderClientInterceptor();
Channel channel = ManagedChannelBuilder.forAddress(BuildConfig.HOST, BuildConfig.PORT).build();
Channel channelWithHeader = ClientInterceptors.intercept(channel, interceptor);
ServiceGrpc.ServiceBlockingStub service = ServiceGrpc.newBlockingStub(channelWithHeader);