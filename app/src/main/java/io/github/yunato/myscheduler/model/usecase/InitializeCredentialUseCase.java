package io.github.yunato.myscheduler.model.usecase;

import android.content.Context;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.CalendarScopes;

import java.util.Arrays;

/**
 * 認証情報を初期化するユースケース
 */
public class InitializeCredentialUseCase {

    private final Context context;
    private static final String[] SCOPES = {CalendarScopes.CALENDAR};

    public InitializeCredentialUseCase(Context context) {
        this.context = context;
    }

    public GoogleAccountCredential run() {
        return GoogleAccountCredential.usingOAuth2(context.getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());
    }
}
