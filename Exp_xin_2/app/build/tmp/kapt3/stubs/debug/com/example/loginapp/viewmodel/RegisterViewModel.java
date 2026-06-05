package com.example.loginapp.viewmodel;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u000b\n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0010\u0002\n\u0002\b\u0005\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u000e\u0010\u001d\u001a\u00020\u001e2\u0006\u0010\u001f\u001a\u00020\u0006J\u000e\u0010 \u001a\u00020\u001e2\u0006\u0010\u001f\u001a\u00020\u0006J\u000e\u0010!\u001a\u00020\u001e2\u0006\u0010\u001f\u001a\u00020\u0006J\u0006\u0010\"\u001a\u00020\u001eR+\u0010\u0007\u001a\u00020\u00062\u0006\u0010\u0005\u001a\u00020\u00068F@BX\u0086\u008e\u0002\u00a2\u0006\u0012\n\u0004\b\f\u0010\r\u001a\u0004\b\b\u0010\t\"\u0004\b\n\u0010\u000bR+\u0010\u000e\u001a\u00020\u00062\u0006\u0010\u0005\u001a\u00020\u00068F@BX\u0086\u008e\u0002\u00a2\u0006\u0012\n\u0004\b\u0011\u0010\r\u001a\u0004\b\u000f\u0010\t\"\u0004\b\u0010\u0010\u000bR+\u0010\u0013\u001a\u00020\u00122\u0006\u0010\u0005\u001a\u00020\u00128F@BX\u0086\u008e\u0002\u00a2\u0006\u0012\n\u0004\b\u0018\u0010\r\u001a\u0004\b\u0014\u0010\u0015\"\u0004\b\u0016\u0010\u0017R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R+\u0010\u0019\u001a\u00020\u00062\u0006\u0010\u0005\u001a\u00020\u00068F@BX\u0086\u008e\u0002\u00a2\u0006\u0012\n\u0004\b\u001c\u0010\r\u001a\u0004\b\u001a\u0010\t\"\u0004\b\u001b\u0010\u000b\u00a8\u0006#"}, d2 = {"Lcom/example/loginapp/viewmodel/RegisterViewModel;", "Landroidx/lifecycle/ViewModel;", "repository", "Lcom/example/loginapp/data/repository/UserRepository;", "(Lcom/example/loginapp/data/repository/UserRepository;)V", "<set-?>", "", "confirmPassword", "getConfirmPassword", "()Ljava/lang/String;", "setConfirmPassword", "(Ljava/lang/String;)V", "confirmPassword$delegate", "Landroidx/compose/runtime/MutableState;", "password", "getPassword", "setPassword", "password$delegate", "Lcom/example/loginapp/viewmodel/RegisterResult;", "registerResult", "getRegisterResult", "()Lcom/example/loginapp/viewmodel/RegisterResult;", "setRegisterResult", "(Lcom/example/loginapp/viewmodel/RegisterResult;)V", "registerResult$delegate", "username", "getUsername", "setUsername", "username$delegate", "onConfirmPasswordChanged", "", "value", "onPasswordChanged", "onUsernameChanged", "register", "app_debug"})
public final class RegisterViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final com.example.loginapp.data.repository.UserRepository repository = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.compose.runtime.MutableState username$delegate = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.compose.runtime.MutableState password$delegate = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.compose.runtime.MutableState confirmPassword$delegate = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.compose.runtime.MutableState registerResult$delegate = null;
    
    public RegisterViewModel(@org.jetbrains.annotations.NotNull()
    com.example.loginapp.data.repository.UserRepository repository) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getUsername() {
        return null;
    }
    
    private final void setUsername(java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getPassword() {
        return null;
    }
    
    private final void setPassword(java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getConfirmPassword() {
        return null;
    }
    
    private final void setConfirmPassword(java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.loginapp.viewmodel.RegisterResult getRegisterResult() {
        return null;
    }
    
    private final void setRegisterResult(com.example.loginapp.viewmodel.RegisterResult p0) {
    }
    
    public final void onUsernameChanged(@org.jetbrains.annotations.NotNull()
    java.lang.String value) {
    }
    
    public final void onPasswordChanged(@org.jetbrains.annotations.NotNull()
    java.lang.String value) {
    }
    
    public final void onConfirmPasswordChanged(@org.jetbrains.annotations.NotNull()
    java.lang.String value) {
    }
    
    public final void register() {
    }
}