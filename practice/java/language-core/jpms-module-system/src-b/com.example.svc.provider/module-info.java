module com.example.svc.provider {
    requires com.example.svc.api;
    provides com.example.svc.api.Greeter with com.example.svc.provider.internal.EnglishGreeter;
}
