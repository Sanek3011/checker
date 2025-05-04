package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.model.State;
import org.example.model.User;
import org.example.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public void saveUser(Long tgId) {
        User build = User.builder()
                .telegramId(tgId)
                .state(State.NO)
                .build();
        userRepository.save(build);
    }

    public User getUserByTgId(Long chatId) {
        return userRepository.getByTelegramId(chatId).orElse(null);
    }

    public void updateUserState(Long chatId, State state) {
        userRepository.updateUserState(chatId, state);
    }
 }
