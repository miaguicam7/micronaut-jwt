package example.micronaut.authentication

import io.micronaut.security.authentication.AuthenticationProvider
import io.micronaut.security.authentication.AuthenticationRequest
import io.micronaut.security.authentication.AuthenticationResponse
import org.reactivestreams.Publisher
import javax.inject.Singleton
import io.micronaut.security.authentication.AuthenticationFailed
import io.reactivex.Flowable
import java.util.ArrayList
import io.micronaut.security.authentication.UserDetails


@Singleton
class AuthenticationProviderUserPassword : AuthenticationProvider {

    override fun authenticate(authenticationRequest: AuthenticationRequest<*, *>?): Publisher<AuthenticationResponse> {
        if (authenticationRequest != null && authenticationRequest.identity != null && authenticationRequest.secret != null) {
            if (authenticationRequest.identity == "sherlock" && authenticationRequest.secret == "password") {
                return Flowable.just<AuthenticationResponse>(UserDetails(authenticationRequest.identity as String, ArrayList()))
            }
        }
        return Flowable.just<AuthenticationResponse>(AuthenticationFailed())

    }
}