package sn.farmerai.config;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import sn.farmerai.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String telephone) throws UsernameNotFoundException {
        var user = userRepository.findByTelephone(telephone)
                .orElseThrow(() -> new UsernameNotFoundException("Aucun utilisateur avec ce numéro : " + telephone));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getTelephone())
                .password(user.getMotDePasseHash())
                .authorities("ROLE_" + user.getRole().name())
                .build();
    }
}
