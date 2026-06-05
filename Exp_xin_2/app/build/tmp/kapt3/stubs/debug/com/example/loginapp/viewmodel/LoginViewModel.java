package com.example.loginapp.viewmodel;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\u000e\n\u0002\b\u000b\n\u0002\u0010\u0002\n\u0002\b\u0004\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0006\u0010\u0019\u001a\u00020\u001aJ\u000e\u0010\u001b\u001a\u00020\u001a2\u0006\u0010\u001c\u001a\u00020\u000eJ\u000e\u0010\u001d\u001a\u00020\u001a2\u0006\u0010\u001c\u001a\u00020\u000eR+\u0010\u0007\u001a\u00020\u00062\u0006\u0010\u0005\u001a\u00020\u00068F@BX\u0086\u008e\u0002\u00a2\u0006\u0012\n\u0004\b\f\u0010\r\u001a\u0004\b\b\u0010\t\"\u0004\b\n\u0010\u000bR+\u0010\u000f\u001a\u00020\u000e2\u0006\u0010\u0005\u001a\u00020\u000e8F@BX\u0086\u008e\u0002\u00a2\u0006\u0012\n\u0004\b\u0014\u0010\r\u001a\u0004\b\u0010\u0010\u0011\"\u0004\b\u0012\u0010\u0013R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R+\u0010\u0015\u001a\u00020\u000e2\u0006\u0010\u0005\u001a\u00020\u000e8F@BX\u0086\u008e\u0002\u00a2\u0006\u0012\n\u0004\b\u0018\u0010\r\u001a\u0004\b\u0016\u0010\u0011\"\u0004\b\u0017\u0010\u0013\u00a8\u0006\u001e"}, d2 = {"Lcom/example/loginapp/viewmodel/LoginViewModel;", "Landroidx/lifecycle/ViewModel;", "repository", "Lcom/example/loginapp/data/repository/UserRepository;", "(Lcom/example/loginapp/data/repository/UserRepository;)V", "<set-?>", "Lcom/example/loginapp/viewmodel/LoginResult;", "loginResult", "getLoginResult", "()Lcom/example/loginapp/viewmodel/LoginResult;", "setLoginResult", "(Lcom/example/loginapp/viewmodel/LoginResult;)V", "loginResult$delegate", "Landroidx/compose/runtime/MutableState;", "", "password", "getPassword", "()Ljava/lang/String;", "setPassword", "(Ljava/lang/String;)V", "password$delegate", "username", "getUsername", "setUsername", "username$delegate", "login", "", "onPasswordChanged", "value", "onUsernameChanged", "app_debug"})
public final class LoginViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final com.example.loginapp.data.repository.UserRepository repository = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.compose.runtime.MutableState username$delegate = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.compose.runtime.MutableState password$delegate = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.compose.runtime.MutableState loginResult$delegate = null;
    
    public LoginViewModel(@org.jetbrains.annotations.NotNull()
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
    public final com.example.loginapp.viewmodel.LoginResult getLoginResult() {
        return null;
    }
    
    private final void setLoginResult(com.example.loginapp.viewmodel.LoginResult p0) {
    }
    
    public final void onUsernameChanged(@org.jetbrains.annotations.NotNull()
    java.lang.String value) {
    }
    
    public final void onPasswordChanged(@org.jetbrains.annotations.NotNull()
    java.lang.String value) {
    }
    
    public final void login() {
    }
}