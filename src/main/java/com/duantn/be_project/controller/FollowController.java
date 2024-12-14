
package com.duantn.be_project.controller;

import com.duantn.be_project.Repository.FollowRepository;
import com.duantn.be_project.Repository.StoreRepository;
import com.duantn.be_project.Repository.UserRepository;
import com.duantn.be_project.model.Follow;
import com.duantn.be_project.model.Store;
import com.duantn.be_project.model.User;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin("*")
public class FollowController {
    @Autowired
    FollowRepository followRepository;
    @Autowired
    StoreRepository storeRepository;
    @Autowired
    UserRepository userRepository;

    @GetMapping("/follow/user")
    public boolean checkIfUserFollowStore(@RequestParam("userId") Integer userId,
            @RequestParam("storeId") Integer storeId) {
        User user = userRepository.findById(userId).orElseThrow();
        Store store = storeRepository.findById(storeId).orElseThrow();
        Follow follow = followRepository.findByUserAndStore(user, store);
        return follow != null;
    }

    @GetMapping("/follow/count")
    public int countFollowerOfStore(@RequestParam("storeId") Integer storeId) {
        Store store = storeRepository.findById(storeId).orElseThrow();
        return followRepository.countByStore(store);
    }

    @PostMapping("/follow")
    public ResponseEntity<?> followStore(@RequestBody Map<String, Object> data) {
        User user = userRepository.findById((Integer) data.get("userId")).orElseThrow();
        Store store = storeRepository.findById((Integer) data.get("storeId")).orElseThrow();
        Follow follow = new Follow(1, user, store);
        followRepository.save(follow);
        return ResponseEntity.ok(follow);
    }

    @PostMapping("/unfollow")
    public ResponseEntity<?> unfollowStore(@RequestBody Map<String, Object> data) {
        User user = userRepository.findById((Integer) data.get("userId")).orElseThrow();
        Store store = storeRepository.findById((Integer) data.get("storeId")).orElseThrow();
        Follow follow = followRepository.findByUserAndStore(user, store);
        followRepository.delete(follow);
        return ResponseEntity.ok(follow);
    }
}
