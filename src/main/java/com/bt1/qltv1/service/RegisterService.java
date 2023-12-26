package com.bt1.qltv1.service;

import com.bt1.qltv1.dto.register.RegisterRequest;
import com.bt1.qltv1.entity.Role;
import com.bt1.qltv1.entity.User;
import com.bt1.qltv1.enumeration.UserStatus;
import com.bt1.qltv1.exception.BadRequest;
import com.bt1.qltv1.repository.RoleRepository;
import com.bt1.qltv1.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Log4j
@RequiredArgsConstructor
public class RegisterService {
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final UserRepository userRepository;
    public void register(RegisterRequest registerRequest){
        Optional<User> userByEmail = userRepository.findByEmail(registerRequest.getEmail());
        if (userByEmail.isPresent()) {
            throw new BadRequest("Email duplicate, Please retype!");
        }
        if (!validatePassword(registerRequest.getPassword())){
            throw new BadRequest("""
                    Password must be longer than 7 character.
                    At least 1 uppercase letter.
                    At least 1 special character.
                    At least 1 number.""");

        }

        Optional<Role> roleUserOptional = roleRepository.findById(2L);
        log.info(roleUserOptional);
        List<Role> roleUserList = new ArrayList<>();
        roleUserOptional.ifPresent(roleUserList::add);
        Set<Role> roleUserSet = new HashSet<>(roleUserList);

        User newUser = User.builder().fullName(registerRequest.getFullname())
                .status(UserStatus.ACTIVE)
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .email(registerRequest.getEmail())
                .roleSet(roleUserSet).build();

        userRepository.save(newUser);

        userService.sendEmailToActivatedAccount(registerRequest.getEmail(),
                registerRequest.getMailType());
    }

    public boolean validatePassword(String password){
        if (password == null || password.length() < 8) {
            return false;
//            throw new IllegalArgumentException("Password must be longer than 7 character and can't be null");
        }
        String regex = "^(?=.*[A-Z])(?=.*\\d)(?=.*[#$@!%&*?])[A-Za-z\\d#$@!%&*?]{8,}$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(password);
        return m.matches();
    }

}
