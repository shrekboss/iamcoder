package org.coder.err.programming._2_design_chapter.asynprocess.compensation;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    private List<User> users = new ArrayList<>();

    public User register() {
        User user = new User();
        users.add(user);
        return user;
    }

    public List<User> getUsersAfterIdWithLimit(long id, int limit) {
        return users.stream()
                .filter(user -> user.getId() >= id)
                .limit(limit)
                .collect(Collectors.toList());
    }
}
