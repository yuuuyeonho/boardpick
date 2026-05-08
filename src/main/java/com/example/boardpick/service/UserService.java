package com.example.boardpick.service;

import com.example.boardpick.entity.CollectionType;
import com.example.boardpick.entity.GameCollection;
import com.example.boardpick.entity.User;
import com.example.boardpick.repository.GameCollectionRepository;
import com.example.boardpick.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final GameCollectionRepository gameCollectionRepository;
    private final PasswordEncoder passwordEncoder;

    public User createUser(String username, String password){
        return createUser(username, username, password);
    }

    public User createUser(String username, String displayName, String password){
        User user = new User();
        user.setUsername(username);
        user.setDisplayName(displayName == null || displayName.isBlank() ? username : displayName);
        user.setPassword(passwordEncoder.encode(password));
        User savedUser = this.userRepository.save(user);

        GameCollection collection = new GameCollection();
        collection.setName(savedUser.getDisplayName() + "의 보드게임");
        collection.setSlug(toSlug(savedUser.getUsername()));
        collection.setType(CollectionType.PERSONAL);
        collection.setOwner(savedUser);
        gameCollectionRepository.save(collection);

        return savedUser;
    }

    private String toSlug(String value) {
        return value.trim().toLowerCase().replaceAll("[^a-z0-9가-힣]+", "-").replaceAll("(^-|-$)", "");
    }
}
